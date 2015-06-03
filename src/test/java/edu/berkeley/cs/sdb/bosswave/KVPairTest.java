package edu.berkeley.cs.sdb.bosswave;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class KVPairTest {

    @Test
    public void testWriteToStream() throws IOException {
        KVPair pair = new KVPair("testKey", "testValue".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        pair.writeToStream(out);

        String actualOutput = out.toString(StandardCharsets.UTF_8.name());
        String expectedOutput = "kv testKey 9\ntestValue\n";
        assertEquals(expectedOutput, actualOutput);
    }
}