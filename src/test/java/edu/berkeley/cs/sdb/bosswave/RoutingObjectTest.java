package edu.berkeley.cs.sdb.bosswave;

import org.apache.commons.lang3.CharEncoding;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class RoutingObjectTest {

    @Test
    public void testWriteToStream() throws IOException {
        String content = "testRoutingObject";
        RoutingObject ro = new RoutingObject(210, content.getBytes(CharEncoding.UTF_8));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ro.writeToStream(out);

        String expectedOutput = "ro 210 17\ntestRoutingObject\n";
        String actualOutput = out.toString(CharEncoding.UTF_8);
        assertEquals(expectedOutput, actualOutput);
    }
}