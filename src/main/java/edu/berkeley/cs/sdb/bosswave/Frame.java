package edu.berkeley.cs.sdb.bosswave;

import org.apache.commons.lang3.CharEncoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class Frame {
    private static final int BW_HEADER_LEN = 27;
    private static final Random random = new Random();

    private final Command command;
    private final int seqNo;
    private final List<KVPair> kvPairs;
    private final List<RoutingObject> routingObjects;
    private final List<PayloadObject> payloadObjects;

    // Frame objects are instantiated using Frame.Builder
    private Frame(Command command, int seqNo, List<KVPair> kvPairs, List<RoutingObject> routingObjects,
                  List<PayloadObject> payloadObjects) {
        this.command = command;
        this.seqNo = seqNo;
        this.kvPairs = Collections.unmodifiableList(kvPairs);
        this.routingObjects = Collections.unmodifiableList(routingObjects);
        this.payloadObjects = Collections.unmodifiableList(payloadObjects);
    }

    public Command getCommand() {
        return command;
    }

    public List<KVPair> getKVPairs() {
        return kvPairs;
    }

    public byte[] getFirstValue(String key) {
        for (KVPair pair : kvPairs) {
            if (pair.getKey().equals(key)) {
                return pair.getValue();
            }
        }
        return null;
    }

    public List<RoutingObject> getRoutingObjects() {
        return routingObjects;
    }

    public List<PayloadObject> getPayloadObjects() {
        return payloadObjects;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public static Frame readFromStream(InputStream stream) throws IOException {
        byte[] frameBytes = new byte[BW_HEADER_LEN];
        stream.read(frameBytes, 0, BW_HEADER_LEN);
        String frameHeader = new String(frameBytes, CharEncoding.UTF_8);

        String[] headerTokens = frameHeader.trim().split(" ");
        if (headerTokens.length != 3) {
            throw new InvalidFrameException("Frame header must contain 3 fields");
        }

        String commandCode = headerTokens[0];
        Command command = Command.fromCode(commandCode);
        if (command == null) {
            throw new InvalidFrameException("Frame header contains invalid command: " + commandCode);
        }

        int frameLength;
        try {
            frameLength = Integer.parseInt(headerTokens[1]);
            if (frameLength < 0) {
                throw new InvalidFrameException("Negative length in frame header");
            }
        } catch (NumberFormatException e) {
            throw new InvalidFrameException("Invalid length field in frame header: " + headerTokens[1], e);
        }

        int seqNo;
        try {
            seqNo = Integer.parseInt(headerTokens[2]);
        } catch (NumberFormatException e) {
            throw new InvalidFrameException("Invalid sequence number in frame header: " + headerTokens[2], e);
        }

        List<KVPair> kvPairs = new ArrayList<KVPair>();
        List<RoutingObject> routingObjects = new ArrayList<RoutingObject>();
        List<PayloadObject> payloadObjects = new ArrayList<PayloadObject>();
        String currentLine;
        while (!(currentLine = readLineFromStream(stream)).equals("end")) {
            String[] tokens = currentLine.split(" ");
            if (tokens.length != 3) {
                throw new InvalidFrameException("Header must contain 3 fields: " + currentLine);
            }

            int length;
            try {
                length = Integer.parseInt(tokens[2]);
                if (length < 0) {
                    throw new InvalidFrameException("Negative length in item header: " + currentLine);
                }
            } catch (NumberFormatException e) {
                throw new InvalidFrameException("Invalid length in item header: " + currentLine, e);
            }

            if (tokens[0].equals("kv")) {
                String key = tokens[1];
                byte[] body = new byte[length];
                stream.read(body, 0, length);
                kvPairs.add(new KVPair(key, body));

                // Remove trailing '\n'
                stream.read();
            } else if (tokens[0].equals("ro")) {
                int routingObjNum;
                try {
                    routingObjNum = Integer.parseInt(tokens[1]);
                    if (routingObjNum < 0 || routingObjNum > 255) {
                        throw new InvalidFrameException("Invalid routing object number: " + currentLine);
                    }
                } catch (NumberFormatException e) {
                    throw new InvalidFrameException("Invalid routing object number: " + tokens[1], e);
                }
                byte[] body = new byte[length];
                stream.read(body, 0, length);
                RoutingObject ro = new RoutingObject(routingObjNum, body);
                routingObjects.add(ro);

                // Remove trailing '\n'
                stream.read();
            } else if (tokens[0].equals("po")) {
                PayloadObject.Type type;
                try {
                    type = PayloadObject.Type.fromString(tokens[1]);
                } catch (IllegalArgumentException e) {
                    throw new InvalidFrameException("Invalid payload object type: " + currentLine, e);
                }

                byte[] body = new byte[length];
                stream.read(body, 0, length);
                payloadObjects.add(new PayloadObject(type, body));

                // Remove trailing '\n'
                stream.read();
            } else {
                throw new InvalidFrameException("Invalid item header: " + currentLine);
            }
        }

        return new Frame(command, seqNo, kvPairs, routingObjects, payloadObjects);
    }

    public void writeToStream(OutputStream out) throws IOException {
        out.write(String.format("%s 0000000000 %010d\n", command.getCode(), seqNo).getBytes(CharEncoding.UTF_8));

        for (KVPair pair : kvPairs) {
            pair.writeToStream(out);
        }
        for (RoutingObject ro : routingObjects) {
            ro.writeToStream(out);
        }
        for (PayloadObject po : payloadObjects) {
            po.writeToStream(out);
        }

        out.write("end\n".getBytes(CharEncoding.UTF_8));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (!(o instanceof Frame)) {
            return false;
        } else {
            Frame other = (Frame)o;
            return this.command == other.command &&
                   this.seqNo == other.seqNo &&
                   this.kvPairs.equals(other.kvPairs) &&
                   this.routingObjects == other.routingObjects &&
                   this.payloadObjects == other.payloadObjects;
        }
    }

    private static byte[] readUntil(InputStream in, byte end) throws IOException {
        List<Byte> bytes = new ArrayList<Byte>();
        int b = in.read();
        while (b != -1 && (byte)b != end) {
            bytes.add((byte)b);
            b = in.read();
        }
        if (b != -1) {
            bytes.add((byte) b); // Add end byte
        }

        byte[] retVal = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            retVal[i] = bytes.get(i);
        }
        return retVal;
    }

    // Returned string does not contain the terminating newline
    private static String readLineFromStream(InputStream stream) throws IOException {
        byte[] bytes = readUntil(stream, (byte)'\n');
        String line = new String(bytes, CharEncoding.UTF_8);
        if (line.endsWith("\n")) {
            return line.substring(0, line.length() - 1);
        } else {
            return line;
        }
    }

    public static int generateSequenceNumber() {
        return Math.abs(random.nextInt());
    }

    public static class Builder {
        private Command command;
        private int seqNo;
        private final List<KVPair> kvPairs;
        private final List<RoutingObject> routingObjects;
        private final List<PayloadObject> payloadObjects;

        public Builder(Command command, int seqNo) {
            this.command = command;
            this.seqNo = seqNo;
            kvPairs = new ArrayList<KVPair>();
            routingObjects = new ArrayList<RoutingObject>();
            payloadObjects = new ArrayList<PayloadObject>();
        }

        public Builder setCommand(Command command) {
            this.command = command;
            return this;
        }

        public Builder setSeqNo(int seqNo) {
            this.seqNo = seqNo;
            return this;
        }

        public Builder addKVPair(KVPair pair) {
            kvPairs.add(pair);
            return this;
        }

        public Builder addKVPair(String key, byte[] value) {
            kvPairs.add(new KVPair(key, value));
            return this;
        }

        public Builder addKVPair(String key, String value) {
            try {
                addKVPair(key, value.getBytes(CharEncoding.UTF_8));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Platform doesn't support UTF-8", e);
            }
            return this;
        }

        public Builder addRoutingObject(RoutingObject ro) {
            routingObjects.add(ro);
            return this;
        }

        public Builder addPayloadObject(PayloadObject po) {
            payloadObjects.add(po);
            return this;
        }

        public void clear() {
            kvPairs.clear();
            routingObjects.clear();
            payloadObjects.clear();
        }

        public Frame build() {
            return new Frame(command, seqNo, kvPairs, routingObjects, payloadObjects);
        }
    }
}