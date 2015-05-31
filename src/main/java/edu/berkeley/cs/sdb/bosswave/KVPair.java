package edu.berkeley.cs.sdb.bosswave;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class KVPair {
    private final String key;
    private final byte[] value;

    public KVPair(String key, byte[] value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public byte[] getValue() {
        return value.clone();
    }

    void writeToStream(OutputStream out) throws IOException {
        String contentLength = Integer.toString(value.length);
        // 6 extra characters: 2 for "kv", 2 spaces in header, and 2 '\n' chars surrounding content
        int totalLength = key.length() + contentLength.length() + value.length + 6;

        String header = String.format("kv %s %s\n", key, totalLength);
        out.write(header.getBytes(StandardCharsets.UTF_8));
        out.write(value);
        out.write('\n');
    }
}
