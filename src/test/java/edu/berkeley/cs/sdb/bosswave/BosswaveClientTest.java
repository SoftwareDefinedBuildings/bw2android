package edu.berkeley.cs.sdb.bosswave;

import org.apache.commons.lang3.CharEncoding;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BosswaveClientTest {

    private static final String BW_URI = "jkolb/unitTest";
    private static final String BW_PAC = "OLOiGYupfKwntyktwroOcIdx3oVO6jErLTfBkDUFgCA=";
    private static final int BW_PORT = 28589;
    private static final Set<String> expectedMessages = new HashSet<String>();
    static {
        expectedMessages.add("Hello, World!");
        expectedMessages.add("Bosswave 2");
        expectedMessages.add("Lorem ipsum");
        expectedMessages.add("dolor sit amet");
    }

    private final Semaphore sem = new Semaphore(0);
    private final BosswaveClient client = new BosswaveClient("localhost", BW_PORT);
    private final TestResponseHandler responseHandler = new TestResponseHandler();
    private final TestMessageHandler messageHandler = new TestMessageHandler();

    @Before
    public void setUp() throws IOException {
        // We assume a local Bosswave router is running
        client.connect();
        client.setEntityFile(new File("/home/jack/bosswave/ethereal/unitTests.key"), responseHandler);

        SubscribeRequest.Builder builder = new SubscribeRequest.Builder(BW_URI).setExpiryDelta(3600000);
        builder.setPrimaryAccessChain(BW_PAC);
        builder.setChainElaborationLevel(ChainElaborationLevel.FULL);
        SubscribeRequest request = builder.build();
        client.subscribe(request, new ResponseHandler() {
            @Override
            public void onResponseReceived(Response result) {
                if (result.getStatus().equals("okay")) {
                    sem.release();
                } else {
                    throw new RuntimeException("Failed to subscribe: " + result.getReason());
                }
            }
        }, messageHandler);
    }

    @After
    public void tearDown() throws IOException {
        client.close();
    }

    @Test
    public void testPublish() throws IOException, InterruptedException {
        sem.acquire(); // Block until the subscribe operation is complete

        PublishRequest.Builder builder = new PublishRequest.Builder(BW_URI);
        builder.setPrimaryAccessChain(BW_PAC);
        builder.setChainElaborationLevel(ChainElaborationLevel.FULL);

        for (String msg : expectedMessages) {
            builder.clearPayloadObjects();
            PayloadObject.Type poType = new PayloadObject.Type(new byte[]{64, 0, 0, 0});
            byte[] poContents = msg.getBytes(CharEncoding.UTF_8);
            PayloadObject po = new PayloadObject(poType, poContents);
            builder.addPayloadObject(po);

            PublishRequest request = builder.build();
            client.publish(request, responseHandler);
        }

        sem.acquire(); // Wait until all published messages have been received
    }

    private static class TestResponseHandler implements ResponseHandler {
        @Override
        public void onResponseReceived(Response result) {
            if (!result.getStatus().equals("okay")) {
                throw new RuntimeException("Bosswave operation failed: " + result.getReason());
            }
        }
    }

    private class TestMessageHandler implements MessageHandler {

        private int counter;

        public TestMessageHandler() {
            counter = 0;
        }

        @Override
        public void onResultReceived(Message message) {
            assertEquals(message.getPayloadObjects().size(), 1);
            byte[] messageContent = message.getPayloadObjects().get(0).getContent();
            String messageText;
            try {
                messageText = new String(messageContent, CharEncoding.UTF_8);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Platform doesn't support UTF-8", e);
            }
            assertTrue(expectedMessages.contains(messageText));
            counter++;

            if (counter == expectedMessages.size()) {
                sem.release();
            }
        }
    }
}