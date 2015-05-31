package edu.berkeley.cs.sdb.bosswave;

import java.io.IOException;
import java.io.OutputStream;

public class RoutingObject {
    private final byte routingObjNum;
    private final byte[] content;

    public RoutingObject(byte routingObjNum, byte[] content) {
        this.routingObjNum = routingObjNum;
        this.content = content;
    }

    void writeToStream(OutputStream stream) throws IOException {
        
    }
}
