package edu.berkeley.cs.sdb.bosswave;

import org.apache.commons.lang3.CharEncoding;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BosswaveClient implements Closeable {
    public static final int DEFAULT_PORT = 28589;
    private static final int SOCKET_TIMEOUT_MS = 2000;

    private final DateTimeFormatter Rfc3339 = ISODateTimeFormat.dateTime();

    private final BWListener listener;
    private final Thread listenerThread;

    private final Map<Integer, ResponseHandler> responseHandlers;
    private final Object responseHandlerLock;
    private final Map<Integer, ResultHandler> resultHandlers;
    private final Object messageHandlersLock;
    private final Map<Integer, ListResultHandler> listResultHandlers;
    private final Object listResultHandlersLock;

    private Boolean autoChainOverride;

    private Socket socket;
    private BufferedInputStream inStream;
    private BufferedOutputStream outStream;

    public BosswaveClient(String hostName, int port) throws IOException {
        listener = new BWListener();
        listenerThread = new Thread(listener);

        responseHandlers = new HashMap<Integer, ResponseHandler>();
        responseHandlerLock = new Object();
        resultHandlers = new HashMap<Integer, ResultHandler>();
        messageHandlersLock = new Object();
        listResultHandlers = new HashMap<Integer, ListResultHandler>();
        listResultHandlersLock = new Object();

        socket = new Socket(hostName, port);
        socket.setSoTimeout(SOCKET_TIMEOUT_MS);
        inStream = new BufferedInputStream(socket.getInputStream());
        outStream = new BufferedOutputStream(socket.getOutputStream());

        // Check that we receive a well-formed acknowledgment
        try {
            Frame frame = Frame.readFromStream(inStream);
            if (frame.getCommand() != Command.HELLO) {
                throw new InvalidFrameException("Received invalid Bosswave ACK");
            }
        } catch (InvalidFrameException e) {
            close();
            throw new RuntimeException(e);
        }

        listenerThread.start();
    }

    public void overrideAutoChainTo(boolean autoChain) {
        autoChainOverride = autoChain;
    }

    @Override
    public void close() throws IOException {
        listener.stop();
        if (socket != null) {
            inStream.close();
            outStream.close();
            socket.close();
        }

        try {
            listenerThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to join listener thread", e);
        }
    }

    public void setEntityFromFile(File f, ResponseHandler handler) throws IOException {
        BufferedInputStream stream = null;
        try {
            stream = new BufferedInputStream(new FileInputStream(f));
            byte[] keyFile = new byte[(int) (f.length() - 1)];
            stream.read(); // Strip the first byte
            stream.read(keyFile, 0, keyFile.length);
            setEntity(keyFile, handler);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private void setEntity(byte[] keyFile, ResponseHandler handler) throws IOException {
        int seqNo = Frame.generateSequenceNumber();
        Frame.Builder builder = new Frame.Builder(Command.SET_ENTITY, seqNo);
        PayloadObject.Type type = new PayloadObject.Type(new byte[]{0, 0, 0, 50});
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
            builder.addKVPair("expiry", Rfc3339.print(new DateTime(expiryTime)));
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
        if (level != ChainElaborationLevel.NONE) {
            builder.addKVPair("elaborate_pac", level.toString().toLowerCase());
        }

        if (autoChainOverride != null) {
            builder.addKVPair("autochain", autoChainOverride.toString());
        } else if (request.autoChain()) {
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

    public void subscribe(SubscribeRequest request, ResponseHandler rspH, ResultHandler rsltH) throws IOException {
        int seqNo = Frame.generateSequenceNumber();
        Frame.Builder builder = new Frame.Builder(Command.SUBSCRIBE, seqNo);

        String uri = request.getUri();
        builder.addKVPair("uri", uri);

        Date expiryTime = request.getExpiry();
        if (expiryTime != null) {
            builder.addKVPair("expiry", Rfc3339.print(new DateTime(expiryTime)));
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
        if (level != ChainElaborationLevel.NONE) {
            builder.addKVPair("elaborate_pac", level.toString().toLowerCase());
        }

        if (autoChainOverride != null) {
            builder.addKVPair("autochain", autoChainOverride.toString());
        } else if (request.autoChain()) {
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

        if (rspH != null) {
            installResponseHandler(seqNo, rspH);
        }
        if (rsltH != null) {
            installResultHandler(seqNo, rsltH);
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
            builder.addKVPair("expiry", Rfc3339.print(new DateTime(expiry)));
        }
        Long expiryDelta = request.getExpiryDelta();
        if (expiryDelta != null) {
            builder.addKVPair("expirydelta", String.format("%dms", expiryDelta));
        }

        ChainElaborationLevel level = request.getElabLevel();
        if (level != ChainElaborationLevel.NONE) {
            builder.addKVPair("elaborate_pac", level.toString().toLowerCase());
        }

        if (autoChainOverride != null) {
            builder.addKVPair("autochain", autoChainOverride.toString());
        } else if (request.autoChain()) {
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

    public void query(QueryRequest request, ResponseHandler respH, ResultHandler rsltH) throws IOException {
        int seqNo = Frame.generateSequenceNumber();
        Frame.Builder builder = new Frame.Builder(Command.QUERY, seqNo);

        builder.addKVPair("uri", request.getUri());

        String pac = request.getPrimaryAccessChain();
        if (pac != null) {
            builder.addKVPair("primary_access_chain", pac);
        }

        Date expiry = request.getExpiry();
        if (expiry != null) {
            builder.addKVPair("expiry", Rfc3339.print(new DateTime(expiry)));
        }
        Long expiryDelta = request.getExpiryDelta();
        if (expiryDelta != null) {
            builder.addKVPair("expirydelta", String.format("%dms", expiryDelta));
        }

        ChainElaborationLevel level = request.getElabLevel();
        if (level != ChainElaborationLevel.NONE) {
            builder.addKVPair("elaborate_pac", level.toString().toLowerCase());
        }

        if (autoChainOverride != null) {
            builder.addKVPair("autochain", autoChainOverride.toString());
        } else if (request.autoChain()) {
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
        if (respH != null) {
            installResponseHandler(seqNo, respH);
        }
        if (rsltH != null) {
            installResultHandler(seqNo, rsltH);
        }
    }

    public void makeEntity(MakeEntityRequest request, ResponseHandler rh) throws IOException {
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
            builder.addKVPair("expiry", Rfc3339.print(new DateTime(expiry)));
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
    }

    public void makeDot(MakeDotRequest request, ResponseHandler rh) throws IOException {
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
            builder.addKVPair("expiry", Rfc3339.print(new DateTime(expiry)));
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

        Frame f = builder.build();
        f.writeToStream(outStream);
        outStream.flush();
        if (rh != null) {
            installResponseHandler(seqNo, rh);
        }
    }

    public void makeChain(boolean isPermission, boolean unelaborate, List<String> dots,
                          ResponseHandler rh) throws IOException {
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
    }

    private void installResponseHandler(int seqNo, ResponseHandler rh) {
        synchronized (responseHandlerLock) {
            responseHandlers.put(seqNo, rh);
        }
    }

    private void installResultHandler(int seqNo, ResultHandler rh) {
        synchronized (messageHandlersLock) {
            resultHandlers.put(seqNo, rh);
        }
    }

    private void installListResponseHandler(int seqNo, ListResultHandler lrh) {
        synchronized (listResultHandlersLock) {
            listResultHandlers.put(seqNo, lrh);
        }
    }

    private class BWListener implements Runnable {

        private volatile boolean continueRunning;

        public BWListener() {
            continueRunning = true;
        }

        @Override
        public void run() {
            while (continueRunning) {
                try {
                    Frame frame = Frame.readFromStream(inStream);
                    int seqNo = frame.getSeqNo();

                    Command command = frame.getCommand();
                    switch (command) {
                        case RESPONSE: {
                            ResponseHandler responseHandler;
                            synchronized (responseHandlerLock) {
                                responseHandler = responseHandlers.remove(seqNo);
                            }
                            if (responseHandler != null) {
                                String status = new String(frame.getFirstValue("status"), CharEncoding.UTF_8);
                                String reason = null;
                                if (!status.equals("okay")) {
                                    reason = new String(frame.getFirstValue("reason"), CharEncoding.UTF_8);
                                    // Upon error, we also need to clean up any result handlers
                                    synchronized (messageHandlersLock) {
                                        resultHandlers.remove(seqNo);
                                    }
                                    synchronized (listResultHandlersLock) {
                                        listResultHandlers.remove(seqNo);
                                    }
                                }
                                responseHandler.onResponseReceived(new BosswaveResponse(status, reason));
                            }
                            break;
                        }

                        case RESULT: {
                            String finishedStr = new String(frame.getFirstValue("finished"), CharEncoding.UTF_8);
                            boolean finished = Boolean.parseBoolean(finishedStr);
                            ResultHandler resultHandler;
                            synchronized (messageHandlersLock) {
                                if (finished) {
                                    resultHandler = resultHandlers.remove(seqNo);
                                } else {
                                    resultHandler = resultHandlers.get(seqNo);
                                }
                            }
                            ListResultHandler listResultHandler;
                            synchronized (listResultHandlersLock) {
                                if (finished) {
                                    listResultHandler = listResultHandlers.remove(seqNo);
                                } else {
                                    listResultHandler = listResultHandlers.get(seqNo);
                                }
                            }

                            if (resultHandler != null) {
                                String uri = new String(frame.getFirstValue("uri"), CharEncoding.UTF_8);
                                String from = new String(frame.getFirstValue("from"), CharEncoding.UTF_8);

                                boolean unpack = true;
                                byte[] unpackBytes = frame.getFirstValue("unpack");
                                if (unpackBytes != null) {
                                    unpack = Boolean.parseBoolean(new String(unpackBytes, CharEncoding.UTF_8));
                                }

                                BosswaveResult result;
                                if (unpack) {
                                    result = new BosswaveResult(from, uri, frame.getRoutingObjects(), frame.getPayloadObjects());
                                } else {
                                    result = new BosswaveResult(from, uri, null, null);
                                }
                                resultHandler.onResultReceived(result);
                            } else if (listResultHandler != null) {
                                if (finished) {
                                    listResultHandler.finish();
                                } else {
                                    String child = new String(frame.getFirstValue("child"), CharEncoding.UTF_8);
                                    listResultHandler.onResult(child);
                                }
                            }
                            break;
                        }

                        default:
                            // Ignore frames with any other commands
                    }
                } catch (InvalidFrameException e) {
                    // Ignore invalid frames
                } catch (SocketException e) {
                    // This should only occur when we are terminating the client and is safe to ignore
                } catch (SocketTimeoutException e) {
                    // This just happens when the socket times out during normal operation -- ignore
                } catch (IOException e) {
                    e.printStackTrace();
                    // We'll attempt to keep running - use `stop` so safely stop listener
                }
            }
        }

        public void stop() {
            continueRunning = false;
        }
    }
}
