package edu.berkeley.cs.sdb.bosswave;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class RoutingObjectTest extends TestCase {

    public void testWriteToStream() throws Exception {
        String content = "testRoutingObject";
        RoutingObject ro = new RoutingObject((byte)210, content.getBytes(StandardCharsets.UTF_8));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ro.writeToStream(out);

        String expectedOutput = "ro 210 17\ntestRoutingObject";
        String actualOutput = out.toString(StandardCharsets.UTF_8.name());
        assertEquals(expectedOutput, actualOutput);
    }
}