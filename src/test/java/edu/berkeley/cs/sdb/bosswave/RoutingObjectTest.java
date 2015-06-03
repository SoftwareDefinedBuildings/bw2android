package edu.berkeley.cs.sdb.bosswave;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class RoutingObjectTest {

    @Test
    public void testWriteToStream() throws IOException {
        String content = "testRoutingObject";
        RoutingObject ro = new RoutingObject(210, content.getBytes(StandardCharsets.UTF_8));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ro.writeToStream(out);

        String expectedOutput = "ro 210 17\ntestRoutingObject";
        String actualOutput = out.toString(StandardCharsets.UTF_8.name());
        assertEquals(expectedOutput, actualOutput);
    }
}