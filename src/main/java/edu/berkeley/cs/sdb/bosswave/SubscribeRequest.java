package edu.berkeley.cs.sdb.bosswave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SubscribeRequest {
    private final String uri;
    private final Date expiry;
    private final Long expiryDelta;
    private final boolean doVerify;
    private final String primaryAccessChain;
    private final ChainElaborationLevel elabLevel;
    private final List<RoutingObject> routingObjects;
    private final boolean leavePacked;

    // Instantiate this class with PublishRequest.Builder
    private SubscribeRequest(String uri, Date expiry, Long expiryDelta, String primaryAccessChain, boolean doVerify,
                             ChainElaborationLevel cel, List<RoutingObject> ros, boolean leavePacked) {
        this.uri = uri;
        this.expiry = expiry;
        this.expiryDelta = expiryDelta;
        this.primaryAccessChain = primaryAccessChain;
        this.doVerify = doVerify;
        elabLevel = cel;
        this.leavePacked = leavePacked;
        routingObjects = Collections.unmodifiableList(ros);
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

    public boolean leavePacked() {
        return leavePacked;
    }

    public class Builder {
        private String uri;
        private Date expiry;
        private Long expiryDelta;
        private boolean doVerify;
        private ChainElaborationLevel elabLevel;
        private final List<RoutingObject> routingObjects;
        private boolean leavePacked;

        public Builder(String uri) {
            this.uri = uri;
            doVerify = false;
            elabLevel = ChainElaborationLevel.UNSPECIFIED;
            routingObjects = new ArrayList<>();
            leavePacked = false;
        }

        public Builder setUri(String uri) {
            this.uri = uri;
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

        public Builder setChainElaborationLevel(ChainElaborationLevel level) {
            elabLevel = level;
            return this;
        }

        public Builder addRoutingObject(RoutingObject ro) {
            routingObjects.add(ro);
            return this;
        }

        public Builder setLeavePacked(boolean leavePacked) {
            this.leavePacked = leavePacked;
            return this;
        }
    }
}
