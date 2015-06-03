package edu.berkeley.cs.sdb.bosswave;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RoutingObject {
    private final int routingObjNum;
    private final byte[] content;

    public RoutingObject(int routingObjNum, byte[] content) {
        if (routingObjNum < 0 || routingObjNum > 255) {
            throw new IllegalArgumentException("Routing object number must be between 0 and 255");
        }
        this.routingObjNum = routingObjNum;
        this.content = content;
    }

    void writeToStream(OutputStream out) throws IOException {
        String header = String.format("ro %d %d\n", routingObjNum, content.length);
        out.write(header.getBytes(StandardCharsets.UTF_8));
        out.write(content);
        out.write('\n');
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (!(o instanceof RoutingObject)) {
            return false;
        } else {
            RoutingObject other = (RoutingObject) o;
            return this.routingObjNum == other.routingObjNum &&
                   Arrays.equals(this.content, other.content);
        }
    }
}
