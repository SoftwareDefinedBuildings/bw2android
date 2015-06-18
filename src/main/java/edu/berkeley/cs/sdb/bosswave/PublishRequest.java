package edu.berkeley.cs.sdb.bosswave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PublishRequest {
    private final String uri;
    private final boolean persist;
    private final Date expiry;
    private final Long expiryDelta;
    private final boolean doVerify;
    private final String primaryAccessChain;
    private final ChainElaborationLevel elabLevel;
    private final List<RoutingObject> routingObjects;
    private final List<PayloadObject> payloadObjects;

    // Instantiate this class with PublishRequest.Builder
    private PublishRequest(String uri, boolean persist, Date expiry, Long expiryDelta, String primaryAccessChain,
                           boolean doVerify, ChainElaborationLevel cel, List<RoutingObject> ros,
                           List<PayloadObject> pos) {
        this.uri = uri;
        this.persist = persist;
        this.expiry = new Date(expiry.getTime());
        this.expiryDelta = expiryDelta;
        this.primaryAccessChain = primaryAccessChain;
        this.doVerify = doVerify;
        elabLevel = cel;
        routingObjects = Collections.unmodifiableList(ros);
        payloadObjects = Collections.unmodifiableList(pos);
    }

    public boolean isPersist() {
        return persist;
    }

    public Date getExpiry() {
        return expiry;
    }

    public Long getExpiryDelta() {
        return expiryDelta;
    }

    public String getUri() {
        return uri;
    }

    public String getPrimaryAccessChain() {
        return primaryAccessChain;
    }

    public boolean doVerify() {
        return doVerify;
    }

    public ChainElaborationLevel getChainElaborationLevel() {
        return elabLevel;
    }

    public List<RoutingObject> getRoutingObjects() {
        return routingObjects;
    }

    public List<PayloadObject> getPayloadObjects() {
        return payloadObjects;
    }

    public static class Builder {
        private String uri;
        private boolean persist;
        private Date expiry;
        private Long expiryDelta;
        private String primaryAccessChain;
        private boolean doVerify;
        private ChainElaborationLevel elabLevel;
        private final List<RoutingObject> routingObjects;
        private final List<PayloadObject> payloadObjects;

        public Builder(String uri) {
            this.uri = uri;
            doVerify = false;
            elabLevel = ChainElaborationLevel.UNSPECIFIED;
            routingObjects = new ArrayList<>();
            payloadObjects = new ArrayList<>();
        }

        public Builder setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder setPersist(boolean persist) {
            this.persist = persist;
            return this;
        }

        public Builder setExpiry(Date expiry) {
            this.expiry = expiry;
            return this;
        }

        public Builder setExpiryDelta(long delta) {
            expiryDelta = delta;
            return this;
        }

        public Builder setDoVerify(boolean doVerify) {
            this.doVerify = doVerify;
            return this;
        }

        public Builder setPrimaryAccessChain(String pac) {
            primaryAccessChain = pac;
            return this;
        }

        public Builder setChainElaborationLevel(ChainElaborationLevel level) {
            elabLevel = level;
            return this;
        }

        public Builder addRoutingObject(RoutingObject ro) {
            routingObjects.add(ro);
            return this;
        }

        public Builder addPayloadObject(PayloadObject po) {
            payloadObjects.add(po);
            return this;
        }

        public PublishRequest build() {
            return new PublishRequest(uri, persist, expiry, expiryDelta, primaryAccessChain, doVerify,
                                      elabLevel, routingObjects, payloadObjects);
        }

        public void clearPayloadObjects() {
            payloadObjects.clear();
        }

        public void clearRoutingObjects() {
            routingObjects.clear();
        }

        public void clearAll() {
            doVerify = false;
            elabLevel = ChainElaborationLevel.UNSPECIFIED;
            routingObjects.clear();
            payloadObjects.clear();
        }
    }
}
