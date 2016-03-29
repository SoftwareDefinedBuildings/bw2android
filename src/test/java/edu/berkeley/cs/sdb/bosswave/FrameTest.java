package edu.berkeley.cs.sdb.bosswave;

import org.apache.commons.lang3.CharEncoding;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FrameTest {

    @Test
    public void testGetFirstValue() throws UnsupportedEncodingException {
        Frame.Builder builder = new Frame.Builder(Command.PUBLISH, 410);
        builder.addKVPair("testKey", "testValue1");
        builder.addKVPair("testKey", "testValue2"); // Redundant key/value pair

        Frame frame = builder.build();
        String firstValue = new String(frame.getFirstValue("testKey"), CharEncoding.UTF_8);
        assertEquals("testValue1", firstValue);
    }

    @Test(expected = InvalidFrameException.class)
    public void testInvalidHeader() throws IOException {
        byte[] frameContent = "helo 00000000000 0000000410 foobar\nend\n".getBytes(CharEncoding.UTF_8);
        ByteArrayInputStream in = new ByteArrayInputStream(frameContent);
        Frame.readFromStream(in);
    }

    @Test
    public void testReadEmptyFrame() throws IOException {
        byte[] frameContent = "helo 0000000000 0000000410\nend\n".getBytes(CharEncoding.UTF_8);
        ByteArrayInputStream in = new ByteArrayInputStream(frameContent);
        Frame frame = Frame.readFromStream(in);

        assertEquals(Command.HELLO, frame.getCommand());
        assertEquals(410, frame.getSeqNo());
        assertTrue(frame.getKVPairs().isEmpty());
        assertTrue(frame.getRoutingObjects().isEmpty());
        assertTrue(frame.getPayloadObjects().isEmpty());
    }

    @Test
    public void testReadKvPairFrame() throws IOException {
        String frameStr = "publ 0000000099 0000000410\n" +
                "kv testKey 9\n" +
                "testValue\n" +
                "kv testKey2 10\n" +
                "testValue2\n" +
                "kv testKey 6\n" +
                "foobar\n" +
                "end\n";
        byte[] frameContent = frameStr.getBytes(CharEncoding.UTF_8);
        ByteArrayInputStream in = new ByteArrayInputStream(frameContent);
        Frame frame = Frame.readFromStream(in);

        assertEquals(Command.PUBLISH, frame.getCommand());
        assertEquals(410, frame.getSeqNo());
        assertEquals(3, frame.getKVPairs().size());
        assertEquals("testValue", new String(frame.getFirstValue("testKey"), CharEncoding.UTF_8));
        assertEquals("testValue2", new String(frame.getFirstValue("testKey2"), CharEncoding.UTF_8));
        assertTrue(frame.getRoutingObjects().isEmpty());
        assertTrue(frame.getPayloadObjects().isEmpty());
    }

    @Test
    public void testReadPoFrame() throws IOException {
        String frameStr = "publ 0000000059 0000000410\n" +
                "po 1.2.3.4: 11\n" +
                "testPayload\n" +
                "end\n";
        byte[] frameContent = frameStr.getBytes(CharEncoding.UTF_8);
        ByteArrayInputStream in = new ByteArrayInputStream(frameContent);
        Frame frame = Frame.readFromStream(in);

        assertEquals(Command.PUBLISH, frame.getCommand());
        assertEquals(410, frame.getSeqNo());
        assertTrue(frame.getKVPairs().isEmpty());
        assertTrue(frame.getRoutingObjects().isEmpty());
        assertEquals(frame.getPayloadObjects().size(), 1);

        PayloadObject.Type expectedType = new PayloadObject.Type(new byte[]{1, 2, 3, 4});
        byte[] expectedContents = "testPayload".getBytes(CharEncoding.UTF_8);
        PayloadObject expectedPayload = new PayloadObject(expectedType, expectedContents);
        assertEquals(expectedPayload, frame.getPayloadObjects().get(0));
    }

    @Test
    public void testReadRoFrame() throws IOException {
        String frameStr = "pers 0000000046 0000000410\n" +
                "ro 255 6\n" +
                "testRO\n" +
                "end\n";
        byte[] frameContent = frameStr.getBytes(CharEncoding.UTF_8);
        ByteArrayInputStream in = new ByteArrayInputStream(frameContent);
        Frame frame = Frame.readFromStream(in);

        assertEquals(Command.PERSIST, frame.getCommand());
        assertEquals(410, frame.getSeqNo());
        assertTrue(frame.getKVPairs().isEmpty());
        assertTrue(frame.getPayloadObjects().isEmpty());
        assertEquals(frame.getRoutingObjects().size(), 1);

        RoutingObject expectedRo = new RoutingObject(255, "testRO".getBytes(CharEncoding.UTF_8));
        assertEquals(expectedRo, frame.getRoutingObjects().get(0));
    }

    @Test
    public void testWriteEmptyFrame() throws IOException {
        Frame.Builder builder = new Frame.Builder(Command.SUBSCRIBE, 1840);
        Frame frame = builder.build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        frame.writeToStream(out);

        String actualFrameContents = out.toString(CharEncoding.UTF_8);
        String expectedFrameContents = "subs 0000000000 0000001840\nend\n";
        assertEquals(expectedFrameContents, actualFrameContents);
    }

    @Test
    public void testWriteKvPairFrame() throws IOException {
        Frame.Builder builder = new Frame.Builder(Command.PUBLISH, 1600);
        builder.addKVPair("testKey1", "testValue1");
        builder.addKVPair("testKey2", "testValue2");
        Frame frame = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        frame.writeToStream(out);
        String frameStr = out.toString(CharEncoding.UTF_8);

        String expectedFrameStr = "publ 0000000000 0000001600\n" +
                "kv testKey1 10\n" +
                "testValue1\n" +
                "kv testKey2 10\n" +
                "testValue2\n" +
                "end\n";
        assertEquals(expectedFrameStr, frameStr);
    }

    @Test
    public void testWritePoFrame() throws IOException {
        Frame.Builder builder = new Frame.Builder(Command.SUBSCRIBE, 1840);
        PayloadObject.Type type = new PayloadObject.Type(42);
        PayloadObject po = new PayloadObject(type, "testPayload".getBytes(CharEncoding.UTF_8));
        builder.addPayloadObject(po);
        Frame frame = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        frame.writeToStream(out);
        String frameStr = out.toString(CharEncoding.UTF_8);

        String expectedFrameStr = "subs 0000000000 0000001840\n" +
                "po :42 11\n" +
                "testPayload\n" +
                "end\n";
        assertEquals(expectedFrameStr, frameStr);
    }

    @Test
    public void testWriteRoFrame() throws IOException {
        Frame.Builder builder = new Frame.Builder(Command.PUBLISH, 1234);
        RoutingObject ro = new RoutingObject(99, "testRO".getBytes(CharEncoding.UTF_8));
        builder.addRoutingObject(ro);
        Frame frame = builder.build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        frame.writeToStream(out);
        String frameStr = out.toString(CharEncoding.UTF_8);

        String expectedFrameStr = "publ 0000000000 0000001234\n" +
                "ro 99 6\n" +
                "testRO\n" +
                "end\n";
        assertEquals(expectedFrameStr, frameStr);
    }
}