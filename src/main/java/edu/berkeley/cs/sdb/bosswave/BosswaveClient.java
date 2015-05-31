package edu.berkeley.cs.sdb.bosswave;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BosswaveClient implements AutoCloseable {
    private static final SimpleDateFormat Rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    private final String hostName;
    private final int port;
    private final Thread listenerThread;

    private final Map<Integer, ResponseHandler> responseHandlers;
    private final Object responseHandlerLock = new Object();
    private final Map<String, ResultHandler> resultHandlers;
    private final Object resultHandlersLock = new Object();

    private Socket socket;

    public BosswaveClient(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
        listenerThread = new Thread(new BWListener());
        responseHandlers = new HashMap<>();
        resultHandlers = new HashMap<>();
    }

    public void connect() throws IOException {
        socket = new Socket(hostName, port);

        // Check that we receive a well-formed acknowledgment
        try {
            Frame frame = Frame.readFromStream(socket.getInputStream());
            if (frame.getCommand() != Command.HELLO) {
                socket.close();
                throw new RuntimeException("Received invalid Bosswave ACK");
            }
        } catch (InvalidFrameException e) {
            socket.close();
            throw new RuntimeException(e);
        }

        listenerThread.start();
    }

    @Override
    public void close() throws IOException {
        listenerThread.interrupt();
        socket.close();
    }

    public void publish(PublishRequest request, ResponseHandler handler) throws IOException {
        Frame.Builder builder = new Frame.Builder();

        String uri = request.getUri();
        builder.addKVPair("uri", uri);

        int seqNo = Frame.generateSequenceNumber();
        builder.setSeqNo(seqNo);

        if (request.isPersist()) {
            builder.setCommand(Command.PERSIST);
        } else {
            builder.setCommand(Command.PUBLISH);
        }
        builder.addKVPair("persist", Boolean.toString(request.isPersist()));

        Date expiryTime = request.getExpiry();
        if (expiryTime != null) {
            builder.addKVPair("expiry", Rfc3339.format(expiryTime));
        }

        Long expiryDelta = request.getExpiryDelta();
        if (expiryDelta != null) {
            builder.addKVPair("expiryDelta", String.format("%dms", expiryDelta));
        }

        String pac = request.getPrimaryAccessChain();
        if (pac != null) {
            builder.addKVPair("primary_access_chain", pac);
        }

        builder.addKVPair("doverify", Boolean.toString(request.doVerify()));

        ChainElaborationLevel level = request.getChainElaborationLevel();
        if (level != ChainElaborationLevel.UNSPECIFIED) {
            builder.addKVPair("elaborate_pac", level.toString().toLowerCase());
        }

        for (RoutingObject ro : request.getRoutingObjects()) {
            builder.addRoutingObject(ro);
        }
        for (PayloadObject po : request.getPayloadObjects()) {
            builder.addPayloadObject(po);
        }

        Frame f = builder.build();
        f.writeToStream(socket.getOutputStream());
        installResponseHandler(seqNo, handler);
    }

    public void subscribe(SubscribeRequest request, ResponseHandler rh, ResultHandler sh) throws IOException {
        Frame.Builder builder = new Frame.Builder();

        String uri = request.getUri();
        builder.addKVPair("uri", uri);

        int seqNo = Frame.generateSequenceNumber();
        builder.setSeqNo(seqNo);

        Date expiryTime = request.getExpiry();
        if (expiryTime != null) {
            builder.addKVPair("expiry", Rfc3339.format(expiryTime));
        }

        Long expiryDelta = request.getExpiryDelta();
        if (expiryDelta != null) {
            builder.addKVPair("expiryDelta", String.format("%dms", expiryDelta));
        }

        String pac = request.getPrimaryAccessChain();
        if (pac != null) {
            builder.addKVPair("primary_access_chain", pac);
        }

        builder.addKVPair("doverify", Boolean.toString(request.doVerify()));

        ChainElaborationLevel level = request.getChainElaborationLevel();
        if (level != ChainElaborationLevel.UNSPECIFIED) {
            builder.addKVPair("elaborate_pac", level.toString().toLowerCase());
        }

        Boolean leavePacked = request.leavePacked();
        if (!leavePacked) {
            builder.addKVPair("unpack", "true");
        }

        for (RoutingObject ro : request.getRoutingObjects()) {
            builder.addRoutingObject(ro);
        }

        Frame f = builder.build();
        f.writeToStream(socket.getOutputStream());
        if (rh != null) {
            installResponseHandler(seqNo, rh);
        }
        if (sh != null) {
            installResultHandler(uri, sh);
        }
    }

    private void installResponseHandler(int seqNo, ResponseHandler rh) {
        synchronized (responseHandlerLock) {
            responseHandlers.put(seqNo, rh);
        }
    }

    private void installResultHandler(String uri, ResultHandler sh) {
        synchronized (resultHandlersLock) {
            resultHandlers.put(uri, sh);
        }
    }

    private class BWListener implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Frame frame = Frame.readFromStream(socket.getInputStream());
                    int seqNo = frame.getSeqNo();

                    Command command = frame.getCommand();
                    switch (command) {
                        case RESPONSE: {
                            ResponseHandler responseHandler;
                            synchronized (responseHandlerLock) {
                                responseHandler = responseHandlers.get(seqNo);
                            }
                            if (responseHandler != null) {
                                String status = new String(frame.getFirstValue("status"), StandardCharsets.UTF_8);
                                String reason = null;
                                if (!status.equals("okay")) {
                                    reason = new String(frame.getFirstValue("reason"), StandardCharsets.UTF_8);
                                }
                                responseHandler.onResponseReceived(new Response(status, reason));
                            }
                            break;
                        }

                        case RESULT: {
                            String uri = new String(frame.getFirstValue("uri"), StandardCharsets.UTF_8);
                            ResultHandler resultHandler;
                            synchronized (resultHandlersLock) {
                                resultHandler = resultHandlers.get(uri);
                            }
                            if (resultHandler != null) {
                                String from = new String(frame.getFirstValue("from"), StandardCharsets.UTF_8);
                                Result result = new Result(from, uri, frame.getRoutingObjects(),
                                        frame.getPayloadObjects());
                                resultHandler.onResultReceived(result);
                            }
                            break;
                        }

                        default:
                            // Ignore frames with any other commands
                    }
                } catch (InvalidFrameException e) {
                    // Ignore invalid frames
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read frame", e);
                }
            }
        }
    }
}
