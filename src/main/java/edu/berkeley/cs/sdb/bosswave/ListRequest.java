package edu.berkeley.cs.sdb.bosswave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ListRequest {
    private final String uri;
    private final String primaryAccessChain;
    private final Long expiry;
    private final Long expiryDelta;
    private final ChainElaborationLevel elabLevel;
    private final boolean autoChain;
    private final List<RoutingObject> routingObjects;

    // Instantiate this class with ListRequest.Builder
    private ListRequest(String uri, String pac, Date expiry, Long expiryDelta, ChainElaborationLevel cel,
                        boolean autoChain, List<RoutingObject> ros) {
        this.uri = uri;
        primaryAccessChain = pac;
        this.expiry = (expiry == null ? null : expiry.getTime());
        this.expiryDelta = expiryDelta;
        elabLevel = cel;
        this.autoChain = autoChain;
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
        private List<RoutingObject> routingObjects;

        public Builder(String uri) {
            this.uri = uri;
            elabLevel = ChainElaborationLevel.UNSPECIFIED;
            autoChain = false;
            routingObjects = new ArrayList<>();
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

        public Builder addRoutingObject(RoutingObject ro) {
            routingObjects.add(ro);
            return this;
        }

        public ListRequest build() {
            return new ListRequest(uri, primaryAccessChain, expiry, expiryDelta, elabLevel, autoChain, routingObjects);
        }
    }
}
