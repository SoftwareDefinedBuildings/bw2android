package edu.berkeley.cs.sdb.bosswave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class QueryRequest {
    private final String uri;
    private final String primaryAccessChain;
    private final Long expiry;
    private final Long expiryDelta;
    private final ChainElaborationLevel elabLevel;
    private final boolean autoChain;
    private final boolean leavePacked;
    private final List<RoutingObject> routingObjects;

    // Instantiate this class with QueryRequest.Builder
    private QueryRequest(String uri, String pac, Date expiry, Long expiryDelta, ChainElaborationLevel cel,
                         boolean autoChain, boolean leavePacked, List<RoutingObject> ros) {
        this.uri = uri;
        primaryAccessChain = pac;
        this.expiry = (expiry == null ? null : expiry.getTime());
        this.expiryDelta = expiryDelta;
        elabLevel = cel;
        this.autoChain = autoChain;
        this.leavePacked = leavePacked;
        routingObjects = Collections.unmodifiableList(ros);
    }

    public String getUri() {
        return uri;
    }

    public String getPrimaryAccessChain() {
        return primaryAccessChain;
    }

    public Date getExpiry() {
        if (expiry == null) {
            return null;
        } else {
            return new Date(expiry);
        }
    }

    public Long getExpiryDelta() {
        return expiryDelta;
    }

    public ChainElaborationLevel getElabLevel() {
        return elabLevel;
    }

    public boolean autoChain() {
        return autoChain;
    }

    public boolean leavePacked() {
        return leavePacked;
    }

    public List<RoutingObject> getRoutingObjects() {
        return routingObjects;
    }

    public static class Builder {
        private String uri;
        private String primaryAccessChain;
        private Date expiry;
        private Long expiryDelta;
        private ChainElaborationLevel elabLevel;
        private boolean autoChain;
        private boolean leavePacked;
        private List<RoutingObject> routingObjects;

        public Builder(String uri) {
            this.uri = uri;
            elabLevel = ChainElaborationLevel.PARTIAL;
            autoChain = false;
            leavePacked = false;
            routingObjects = new ArrayList<RoutingObject>();
        }

        public Builder setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder setPrimaryAccessChain(String pac) {
            primaryAccessChain = pac;
            return this;
        }

        public Builder setExpiry(Date expiry) {
            this.expiry = expiry;
            return this;
        }

        public Builder setExiryDelta(long delta) {
            expiryDelta = delta;
            return this;
        }

        public Builder setChainElaborationLevel(ChainElaborationLevel cel) {
            elabLevel = cel;
            return this;
        }

        public Builder setAutoChain(boolean autoChain) {
            this.autoChain = autoChain;
            return this;
        }

        public Builder setLeavePacked(boolean leavePacked) {
            this.leavePacked = leavePacked;
            return this;
        }

        public Builder addRoutingObject(RoutingObject ro) {
            routingObjects.add(ro);
            return this;
        }

        public QueryRequest build() {
            return new QueryRequest(uri, primaryAccessChain, expiry, expiryDelta, elabLevel, leavePacked,
                                    autoChain, routingObjects);
        }

        public void clearRoutingObjects() {
            routingObjects.clear();
        }

        public void clearAll() {
            elabLevel = ChainElaborationLevel.NONE;
            autoChain = false;
            leavePacked = false;
            routingObjects.clear();
        }
    }
}
