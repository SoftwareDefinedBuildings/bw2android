package edu.berkeley.cs.sdb.bosswave;

import java.io.IOException;
import java.io.OutputStream;

class RoutingObject {
    private final byte routingObjNum;
    private final byte[] content;

    public RoutingObject(byte routingObjNum, byte[] content) {
        this.routingObjNum = routingObjNum;
        this.content = content;
    }

    public void writeToStream(OutputStream stream) throws IOException {
        
    }
}
