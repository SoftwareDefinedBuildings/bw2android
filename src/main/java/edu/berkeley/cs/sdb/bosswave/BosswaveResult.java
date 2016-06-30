package edu.berkeley.cs.sdb.bosswave;

import java.util.Collections;
import java.util.List;

public class BosswaveResult {
    private final String from;
    private final String uri;
    private final List<RoutingObject> routingObjects;
    private final List<PayloadObject> payloadObjects;

    public BosswaveResult(String from, String uri, List<RoutingObject> ros, List<PayloadObject> pos) {
        this.from = from;
        this.uri = uri;
        routingObjects = Collections.unmodifiableList(ros);
        payloadObjects = Collections.unmodifiableList(pos);
    }

    public String getFrom() {
        return from;
    }

    public String getUri() {
        return uri;
    }

    public List<RoutingObject> getRoutingObjects() {
        return routingObjects;
    }

    public List<PayloadObject> getPayloadObjects() {
        return payloadObjects;
    }
}
