package edu.berkeley.cs.sdb.bosswave;

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

    }
}
