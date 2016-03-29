package edu.berkeley.cs.sdb.bosswave;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class PayloadObjectTest {

    @Test
    public void testWriteToStreamDotType() throws IOException {
        PayloadObject.Type type = new PayloadObject.Type(new byte[]{1, 2, 3, 4});
        String content = "testPayloadObject";
        PayloadObject po = new PayloadObject(type, content.getBytes(StandardCharsets.UTF_8));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        po.writeToStream(out);

        String expectedOutput = "po 1.2.3.4: 17\ntestPayloadObject\n";
        String actualOutput = out.toString(StandardCharsets.UTF_8.name());
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testWriteToStreamNumType() throws IOException {
        PayloadObject.Type type = new PayloadObject.Type(410);
        String content = "testPayloadObject";
        PayloadObject po = new PayloadObject(type, content.getBytes(StandardCharsets.UTF_8));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        po.writeToStream(out);

        String expectedOutput = "po :410 17\ntestPayloadObject\n";
        String actualOutput = out.toString(StandardCharsets.UTF_8.name());
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testWriteToStreamBothType() throws IOException {
        PayloadObject.Type type = new PayloadObject.Type(new byte[]{1, 2, 3, 4}, 16909060);
        String content = "testPayloadObject";
        PayloadObject po = new PayloadObject(type, content.getBytes(StandardCharsets.UTF_8));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        po.writeToStream(out);

        String expectedOutput = "po 1.2.3.4:16909060 17\ntestPayloadObject\n";
        String actualOutput = out.toString(StandardCharsets.UTF_8.name());
        assertEquals(expectedOutput, actualOutput);
    }
}