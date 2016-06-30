# bw2Android - Android Bindings for Bosswave

## Obtaining a JAR File
This project uses Gradle for compilation and dependency management. After
cloning the project, run the following from the top level directory:
```
$ ./gradlew shadowJar
```

After Gradle has finished, a fat JAR file will be available under `build/libs`.

## Basic Usage
Here's a rough example of how to initialize the client, publish, and subscribe.
```java
// Many client methods throw IOExceptions
BosswaveClient client;
try {
    // Connect to a Bosswave agent running locally
    client = new BosswaveClient("localhost", BosswaveClient.DEFAULT_PORT);

    // Set the Bosswave entity to be used for subsequent operations
    client.setEntityFromFile("myKey.ent");

    // Enable auto chain by default
    client.overrideAutoChainTo(true);

    // Define a callback to handle Bosswave errors
    private class ResponseErrorHandler implements ResponseHandler {
        @Override
        public void onResponseReceived(BosswaveResponse resp) {
            if (!resp.getStatus().equals("okay")) {
                throw new RuntimeException(resp.getReason()));
            }
        }
    }

    // Publish a simple text message
    PublishRequest.Builder builder = new PublishRequest.Builder(BW_URI);
    PayloadObject.Type poType = new PayloadObject.Type(POAllocations.PODFText);
    String message = "Hello, World!";
    byte[] poContents = message.getBytes(StandardCharsets.UTF_8);
    PayloadObject po = new PayloadObject(poType, poContents);
    builder.addPayloadObject(po);
    PublishRequest request = builder.build();
    client.publish(request, new ResponseErrorHandler());

    // Define a callback to handle incoming text messages
    private class TextResultHandler implements ResultHandler {
        @Override
        public void onResultReceived(BosswaveResult rslt) {
            byte[] messageContent = rslt.getPayloadObjects().get(0).getContent();
            String msg = new String(messageContent, StandardCharsets.UTF_8);
            System.out.println(msg);
        }
    }

    // Subscribe to a Bosswave URI
	SubscribeRequest.Builder builder = new SubscribeRequest.Builder("scratch.ns/foo/bar");
    SubscribeRequest request = builder.build();
    client.subscribe(request, new ResponseErrorHandler(), new TextResultHandler());

    // Additional application logic...
} finally {
   if (client != null) {
       client.close();
   }
}
```
