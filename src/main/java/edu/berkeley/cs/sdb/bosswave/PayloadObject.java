package edu.berkeley.cs.sdb.bosswave;

import java.io.IOException;
import java.io.OutputStream;

class PayloadObject {
    private final Type id;
    private final byte[] content;

    public PayloadObject(Type id, byte[] content) {
        this.id = id;
        this.content = content;
    }

    public void writeToStream(OutputStream out) throws IOException {

    }

    public static class Type {
        private final byte[] octet;
        private final int number;

        public Type(byte[] octet) {
            this.octet = octet;
            number = -1;
        }

        public Type(int number) {
            this.number = number;
            this.octet = null;
        }

        public Type(byte[] octet, int number) {
            this.octet = octet;
            this.number = number;
        }

        private static byte[] parseOctet(String str) {
            String[] octetTokens = str.split("\\.");
            if (octetTokens.length != 4) {
                throw new IllegalArgumentException("Octet must contain four elements");
            }
            byte[] octetBytes = new byte[4];
            for (int i = 0; i < 4; i++) {
                try {
                    octetBytes[i] = Byte.parseByte(octetTokens[i]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid octet element: " + octetTokens[i], e);
                }
                if (octetBytes[i] < 0) {
                    throw new IllegalArgumentException("Negative octet element: " + octetTokens[i]);
                }
            }

            return octetBytes;
        }

        public static Type fromString(String str) {
            if (str.startsWith(":")) {
                int poNum;
                try {
                    poNum = Integer.parseInt(str.substring(1));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Payload object type contains invalid number", e);
                }
                if (poNum < 0 || poNum > 99) {
                    throw new IllegalArgumentException("Payload object type number must contain 1 or 2 digits");
                }

                return new Type(poNum);
            } else if (str.endsWith(":")) {
                String octetStr = str.substring(0, str.length() - 1);
                byte[] octet;
                try {
                    octet = parseOctet(octetStr);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Payload object type contains invalid octet", e);
                }

                return new Type(octet);
            } else {
                String[] poTypeTokens = str.split(":");
                if (poTypeTokens.length != 2) {
                    throw new IllegalArgumentException("Malformed payload object type");
                }
                byte[] octet;
                try {
                    octet = parseOctet(poTypeTokens[0]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Payload object type contains invalid octet", e);
                }
                int number;
                try {
                    number = Integer.parseInt(poTypeTokens[1]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Payload object type contains invalid number", e);
                }
                if (number < 0 || number > 99) {
                    throw new IllegalArgumentException("Payload object type number must contain 1 or 2 digits");
                }
                return new Type(octet, number);
            }
        }

        @Override
        public String toString() {
            if (octet != null && number > 0) {
                return String.format("%d.%d.%d,%d:%d", octet[0], octet[1], octet[2], octet[3], number);
            } else if (octet != null) {
                return String.format("%d.%d.%d.%d:", octet[0], octet[1], octet[2], octet[3]);
            } else {
                return String.format(":%d", number);
            }
        }
    }
}
