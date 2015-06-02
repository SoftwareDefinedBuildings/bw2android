package edu.berkeley.cs.sdb.bosswave;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class KVPairTest extends TestCase {

    public void testWriteToStream() throws IOException {
        KVPair pair = new KVPair("testKey", "testValue".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        pair.writeToStream(out);

        String actualOutput = out.toString(StandardCharsets.UTF_8.name());
        String expectedOutput = "kv testKey 9\ntestValue\n";
        assertEquals(expectedOutput, actualOutput);
    }
}