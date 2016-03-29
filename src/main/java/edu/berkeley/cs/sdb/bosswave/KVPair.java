package edu.berkeley.cs.sdb.bosswave;

import org.apache.commons.lang3.CharEncoding;

import java.io.IOException;
import java.io.OutputStream;

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
        String header = String.format("kv %s %d\n", key, value.length);
        out.write(header.getBytes(CharEncoding.UTF_8));
        out.write(value);
        out.write('\n');
    }
}
