package edu.berkeley.cs.sdb.bosswave;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BosswaveClient implements AutoCloseable {
    private static final SimpleDateFormat Rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    private final String hostName;
    private final int port;
    private final Thread listenerThread;

    private final Map<Integer, ResponseHandler> responseHandlers;
    private final Object responseHandlerLock;
    private final Map<Integer, MessageHandler> messageHandlers;
    private final Object messageHandlersLock;
    private final Map<Integer, ListResultHandler> listResultHandlers;
    private final Object listResultHandlersLock;

    private Socket socket;
    private BufferedInputStream inStream;
    private BufferedOutputStream outStream;

    public BosswaveClient(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
        listenerThread = new Thread(new BWListener());

        responseHandlers = new HashMap<>();
        responseHandlerLock = new Object();
        messageHandlers = new HashMap<>();
        messageHandlersLock = new Object();
        listResultHandlers  = new HashMap<>();
        listResultHandlersLock = new Object();
    }

    public void connect() throws IOException {
        socket = new Socket(hostName, port);
        inStream = new BufferedInputStream(socket.getInputStream());
        outStream = new BufferedOutputStream(socket.getOutputStream());

        // Check that we receive a well-formed acknowledgment
        try {
            Frame frame = Frame.readFromStream(inStream);
            if (frame.getCommand() != Command.HELLO) {
                close();
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
        inStream.close();
        outStream.close();
        socket.close();
    }

    public void setEntityFile(File f, ResponseHandler handler) throws IOException {
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(f));
        byte[] keyFile = new byte[(int) (f.length() - 1)];
        stream.read(); // Strip the first byte
        stream.read(keyFile, 0, keyFile.length);
        stream.close();

        setEntity(keyFile, handler);
    }

    private void setEntity(byte[] keyFile, ResponseHandler handler) throws IOException {
        int seqNo = Frame.generateSequenceNumber();
        Frame.Builder builder = new Frame.Builder(Command.SET_ENTITY, seqNo);
        PayloadObject.Type type = new PayloadObject.Type(new byte[]{1, 0, 1, 2});
        PayloadObject po = new PayloadObject(type, keyFile);
        builder.addPayloadObject(po);

        Frame frame = builder.build();
        frame.writeToStream(outStream);
        outStream.flush();
        installResponseHandler(seqNo, handler);
    }

    public void publish(PublishRequest request, ResponseHandler handler) throws IOException {
        Command command = Command.PUBLISH;
        if (request.isPersist()) {
            command = Command.PERSIST;
        }
        int seqNo = Frame.generateSequenceNumber();
        Frame.Builder builder = new Frame.Builder(command, seqNo);

        String uri = request.getUri();
        builder.addKVPair("uri", uri);

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

        if (request.autoChain()) {
            builder.addKVPair("autochain", "true");
        }

        for (RoutingObject ro : request.getRoutingObjects()) {
            builder.addRoutingObject(ro);
        }
        for (PayloadObject po : request.getPayloadObjects()) {
            builder.addPayloadObject(po);
        }

        Frame f = builder.build();
        f.writeToStream(outStream);
        outStream.flush();
        installResponseHandler(seqNo, handler);
    }

    public void subscribe(SubscribeRequest request, ResponseHandler rh, MessageHandler mh) throws IOException {
        int seqNo = Frame.generateSequenceNumber();
        Frame.Builder builder = new Frame.Builder(Command.SUBSCRIBE, seqNo);

        String uri = request.getUri();
        builder.addKVPair("uri", uri);

        Date expiryTime = request.getExpiry();
        if (expiryTime != null) {
            builder.addKVPair("expiry", Rfc3339.format(expiryTime));
        }

        Long expiryDelta = request.getExpiryDelta();
        if (expiryDelta != null) {
            builder.addKVPair("expirydelta", String.format("%dms", expiryDelta));
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

        if (request.autoChain()) {
            builder.addKVPair("autochain", "true");
        }

        if (!request.leavePacked()) {
            builder.addKVPair("unpack", "true");
        }

        for (RoutingObject ro : request.getRoutingObjects()) {
            builder.addRoutingObject(ro);
        }

        Frame f = builder.build();
        f.writeToStream(outStream);
        outStream.flush();

        if (rh != null) {
            installResponseHandler(seqNo, rh);
        }
        if (mh != null) {
            installMessageHandler(seqNo, mh);
        }
    }

    public void list(ListRequest request, ResponseHandler rh, ListResultHandler lrh) throws IOException {
        int seqNo = Frame.generateSequenceNumber();
        Frame.Builder builder = new Frame.Builder(Command.LIST, seqNo);

        builder.addKVPair("uri", request.getUri());

        String pac = request.getPrimaryAccessChain();
        if (pac != null) {
            builder.addKVPair("primary_access_chain", pac);
        }

        Date expiry = request.getExpiry();
        if (expiry != null) {
            builder.addKVPair("expiry", Rfc3339.format(expiry));
        }
        Long expiryDelta = request.getExpiryDelta();
        if (expiryDelta != null) {
            builder.addKVPair("expirydelta", String.format("%dms", expiryDelta));
        }

        ChainElaborationLevel level = request.getElabLevel();
        if (level != ChainElaborationLevel.UNSPECIFIED) {
            builder.addKVPair("elaborate_pac", level.toString().toLowerCase());
        }

        if (request.autoChain()) {
            builder.addKVPair("autochain", "true");
        }

        for (RoutingObject ro : request.getRoutingObjects()) {
            builder.addRoutingObject(ro);
        }

        Frame f = builder.build();
        f.writeToStream(outStream);
        outStream.flush();
        if (rh != null) {
            installResponseHandler(seqNo, rh);
        }
        if (lrh != null) {
            installListResponseHandler(seqNo, lrh);
        }
    }

    public void query(QueryRequest request, ResponseHandler rh, MessageHandler mh) throws IOException {
        int seqNo = Frame.generateSequenceNumber();
        Frame.Builder builder = new Frame.Builder(Command.QUERY, seqNo);

        builder.addKVPair("uri", request.getUri());

        String pac = request.getPrimaryAccessChain();
        if (pac != null) {
            builder.addKVPair("primary_access_chain", pac);
        }

        Date expiry = request.getExpiry();
        if (expiry != null) {
            builder.addKVPair("expiry", Rfc3339.format(expiry));
        }
        Long expiryDelta = request.getExpiryDelta();
        if (expiryDelta != null) {
            builder.addKVPair("expirydelta", String.format("%dms", expiryDelta));
        }

        ChainElaborationLevel level = request.getElabLevel();
        if (level != ChainElaborationLevel.UNSPECIFIED) {
            builder.addKVPair("elaborate_pac", level.toString().toLowerCase());
        }

        if (request.autoChain()) {
            builder.addKVPair("autochain", "true");
        }

        if (!request.leavePacked()) {
            builder.addKVPair("unpack", "true");
        }

        for (RoutingObject ro : request.getRoutingObjects()) {
            builder.addRoutingObject(ro);
        }

        Frame f = builder.build();
        f.writeToStream(outStream);
        outStream.flush();
        if (rh != null) {
            installResponseHandler(seqNo, rh);
        }
        if (mh != null) {
            installMessageHandler(seqNo, mh);
        }
    }

    public void makeEntity(MakeEntityRequest request, ResponseHandler rh, MessageHandler mh) throws IOException {
        int seqNo = Frame.generateSequenceNumber();
        Frame.Builder builder = new Frame.Builder(Command.MAKE_ENTITY, seqNo);

        String contact = request.getContact();
        if (contact != null) {
            builder.addKVPair("contact", contact);
        }

        String comment = request.getComment();
        if (comment != null) {
            builder.addKVPair("comment", comment);
        }

        Date expiry = request.getExpiry();
        if (expiry != null) {
            builder.addKVPair("expiry", Rfc3339.format(expiry));
        }

        Long expiryDelta = request.getExpiryDelta();
        if (expiryDelta != null) {
            builder.addKVPair("expirydelta", String.format("%dms", expiryDelta));
        }

        for (String revoker : request.getRevokers()) {
            builder.addKVPair("revoker", revoker);
        }

        builder.addKVPair("omitcreationdate", Boolean.toString(request.omitCreationDate()));

        Frame f = builder.build();
        f.writeToStream(outStream);
        outStream.flush();
        if (rh != null) {
            installResponseHandler(seqNo, rh);
        }
        if (mh != null) {
            installMessageHandler(seqNo, mh);
        }
    }

    public void makeDot(MakeDotRequest request, ResponseHandler rh, MessageHandler mh) throws IOException {
        int seqNo = Frame.generateSequenceNumber();
        Frame.Builder builder = new Frame.Builder(Command.MAKE_DOT, seqNo);

        builder.addKVPair("to", request.getTo());

        Integer ttl = request.getTimeToLive();
        if (ttl != null) {
            builder.addKVPair("ttl", Integer.toString(ttl));
        }

        builder.addKVPair("ispermission", Boolean.toString(request.isPermission()));

        Date expiry = request.getExpiry();
        if (expiry != null) {
            builder.addKVPair("expiry", Rfc3339.format(expiry));
        }

        Long expiryDelta = request.getExpiryDelta();
        if (expiryDelta != null) {
            builder.addKVPair("expirydelta", String.format("%dms", expiryDelta));
        }

        String contact = request.getContact();
        if (contact != null) {
            builder.addKVPair("contact", contact);
        }

        String comment = request.getComment();
        if (comment != null) {
            builder.addKVPair("comment", comment);
        }

        for (String revoker : request.getRevokers()) {
            builder.addKVPair("revoker", revoker);
        }

        builder.addKVPair("omitcreationdate", Boolean.toString(request.omitCreationDate()));

        String accessPermissions = request.getAccessPermissions();
        if (accessPermissions != null) {
            builder.addKVPair("accesspermissions", accessPermissions);
        }

        String uri = request.getUri();
        if (uri != null) {
            builder.addKVPair("uri", uri);
        }
    }

    public void makeChain(boolean isPermission, boolean unelaborate, List<String> dots,
                          ResponseHandler rh, MessageHandler mh) throws IOException {
        int seqNo = Frame.generateSequenceNumber();
        Frame.Builder builder = new Frame.Builder(Command.MAKE_CHAIN, seqNo);

        builder.addKVPair("ispermission", Boolean.toString(isPermission));
        builder.addKVPair("unelaborate", Boolean.toString(unelaborate));
        for (String dot : dots) {
            builder.addKVPair("dot", dot);
        }

        Frame f = builder.build();
        f.writeToStream(outStream);
        outStream.flush();
        if (rh != null) {
            installResponseHandler(seqNo, rh);
        }
        if (mh != null) {
            installMessageHandler(seqNo, mh);
        }
    }

    private void installResponseHandler(int seqNo, ResponseHandler rh) {
        synchronized (responseHandlerLock) {
            responseHandlers.put(seqNo, rh);
        }
    }

    private void installMessageHandler(int seqNo, MessageHandler mh) {
        synchronized (messageHandlersLock) {
            messageHandlers.put(seqNo, mh);
        }
    }

    private void installListResponseHandler(int seqNo, ListResultHandler lrh) {
        synchronized (listResultHandlersLock) {
            listResultHandlers.put(seqNo, lrh);
        }
    }

    private class BWListener implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    Frame frame = Frame.readFromStream(inStream);
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
                            MessageHandler messageHandler;
                            synchronized (messageHandlersLock) {
                                messageHandler = messageHandlers.get(seqNo);
                            }
                            ListResultHandler listResultHandler;
                            synchronized (listResultHandlersLock) {
                                listResultHandler = listResultHandlers.get(seqNo);
                            }

                            if (messageHandler != null) {
                                String uri = new String(frame.getFirstValue("uri"), StandardCharsets.UTF_8);
                                String from = new String(frame.getFirstValue("from"), StandardCharsets.UTF_8);

                                boolean unpack = true;
                                byte[] unpackBytes = frame.getFirstValue("unpack");
                                if (unpackBytes != null) {
                                    unpack = Boolean.parseBoolean(new String(unpackBytes, StandardCharsets.UTF_8));
                                }

                                Message msg;
                                if (unpack) {
                                    msg = new Message(from, uri, frame.getRoutingObjects(), frame.getPayloadObjects());
                                } else {
                                    msg = new Message(from, uri, null, null);
                                }
                                messageHandler.onResultReceived(msg);
                            } else if (listResultHandler != null) {
                                String finishedStr = new String(frame.getFirstValue("finished"), StandardCharsets.UTF_8);
                                boolean finished = Boolean.parseBoolean(finishedStr);
                                if (finished) {
                                    listResultHandler.finish();
                                } else {
                                    String child = new String(frame.getFirstValue("child"), StandardCharsets.UTF_8);
                                    listResultHandler.onResult(child);
                                }
                            }
                            break;
                        }

                        default:
                            // Ignore frames with any other commands
                    }
                }
            } catch (InvalidFrameException e) {
                // Ignore invalid frames
            } catch (SocketException e) {
                // This should only occur when we are terminating the client and is safe to ignore
            } catch (IOException e) {
                throw new RuntimeException("Failed to read frame", e);
            }
        }
    }
}
