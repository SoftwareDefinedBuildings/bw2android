package edu.berkeley.cs.sdb.bosswave;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RoutingObject {
    private final byte routingObjNum;
    private final byte[] content;

    public RoutingObject(byte routingObjNum, byte[] content) {
        this.routingObjNum = routingObjNum;
        this.content = content;
    }

    void writeToStream(OutputStream out) throws IOException {
        String header = String.format("ro %d %d\n", routingObjNum, content.length);
        out.write(header.getBytes(StandardCharsets.UTF_8));
        out.write(content);
        out.write('\n');
    }
}
