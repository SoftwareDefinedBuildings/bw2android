package edu.berkeley.cs.sdb.bosswave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MakeDotRequest {
    private final String to;
    private final Integer timeToLive;
    private final boolean isPermission;
    private final Long expiry;
    private final Long expiryDelta;
    private final String contact;
    private final String comment;
    private final List<String> revokers;
    private final boolean omitCreationDate;
    private final String accessPermissions;
    private final String uri;

    // Instantiate this class with MakeDotRequest.Builder
    private MakeDotRequest(String to, Integer timeToLive, boolean isPermission, Date expiry, Long expiryDelta,
                           String contact, String comment, List<String> revokers, boolean ocd,
                           String accessPermissions, String uri) {
        this.to = to;
        this.timeToLive = timeToLive;
        this.isPermission = isPermission;
        this.expiry = (expiry == null ? null : expiry.getTime());
        this.expiryDelta = expiryDelta;
        this.contact = contact;
        this.comment = comment;
        this.revokers = Collections.unmodifiableList(revokers);
        omitCreationDate = ocd;
        this.accessPermissions = accessPermissions;
        this.uri = uri;
    }

    public String getTo() {
        return to;
    }

    public Integer getTimeToLive() {
        return timeToLive;
    }

    public boolean isPermission() {
        return isPermission;
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

    public String getContact() {
        return contact;
    }

    public String getComment() {
        return comment;
    }

    public List<String> getRevokers() {
        return revokers;
    }

    public boolean omitCreationDate() {
        return omitCreationDate;
    }

    public String getAccessPermissions() {
        return accessPermissions;
    }

    public String getUri() {
        return uri;
    }

    public static class Builder {
        private String to;
        private Integer timeToLive;
        private boolean isPermission;
        private String contact;
        private String comment;
        private Date expiry;
        private Long expiryDelta;
        private List<String> revokers;
        private boolean omitCreationDate;
        private String accessPermissions;
        private String uri;

        public Builder(String to) {
            this.to = to;
            isPermission = false;
            revokers = new ArrayList<>();
        }

        public Builder setTo(String to) {
            this.to = to;
            return this;
        }

        public Builder setTimeToLive(int ttl) {
            if (ttl >= 0) {
                timeToLive = ttl;
            }
            return this;
        }

        public Builder setContact(String contact) {
            this.contact = contact;
            return this;
        }

        public Builder setComment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder setExpiry(Date expiry) {
            this.expiry = new Date(expiry.getTime());
            return this;
        }

        public Builder setExpiryDelta(long delta) {
            expiryDelta = delta;
            return this;
        }

        public Builder addRevoker(String revoker) {
            revokers.add(revoker);
            return this;
        }

        public Builder setOmitCreationDate(boolean ocd) {
            omitCreationDate = ocd;
            return this;
        }

        public Builder setAccessPermissions(String permissions) {
            accessPermissions = permissions;
            return this;
        }

        public Builder setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public void clearRevokers() {
            revokers.clear();
        }

        public void clearAll() {
            isPermission = false;
            revokers.clear();
        }

        public MakeDotRequest build() {
            return new MakeDotRequest(to, timeToLive, isPermission, expiry, expiryDelta, contact, comment, revokers,
                                      omitCreationDate, accessPermissions, uri);
        }
    }
}
