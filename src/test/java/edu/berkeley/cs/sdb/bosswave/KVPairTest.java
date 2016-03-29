package edu.berkeley.cs.sdb.bosswave;

import org.apache.commons.lang3.CharEncoding;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class KVPairTest {

    @Test
    public void testWriteToStream() throws IOException {
        KVPair pair = new KVPair("testKey", "testValue".getBytes(CharEncoding.UTF_8));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        pair.writeToStream(out);

        String actualOutput = out.toString(CharEncoding.UTF_8);
        String expectedOutput = "kv testKey 9\ntestValue\n";
        assertEquals(expectedOutput, actualOutput);
    }
}