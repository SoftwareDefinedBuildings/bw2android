package edu.berkeley.cs.sdb.bosswave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MakeEntityRequest {
    private final String contact;
    private final String comment;
    private final Long expiry;
    private final Long expiryDelta;
    private final List<String> revokers;
    private final boolean omitCreationDate;

    // Instantiate this class with MakeEntityRequest.Builder
    private MakeEntityRequest(String contact, String comment, Date expiry, Long expiryDelta, List<String> revokers,
                              boolean ocd) {
        this.contact = contact;
        this.comment = comment;
        this.expiry = (expiry == null ? null : expiry.getTime());
        this.expiryDelta = expiryDelta;
        this.revokers = Collections.unmodifiableList(revokers);
        omitCreationDate = ocd;
    }

    public String getContact() {
        return contact;
    }

    public String getComment() {
        return comment;
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

    public List<String> getRevokers() {
        return revokers;
    }

    public boolean omitCreationDate() {
        return omitCreationDate;
    }

    public static class Builder {
        private String contact;
        private String comment;
        private Date expiry;
        private Long expiryDelta;
        private List<String> revokers;
        private boolean omitCreationDate;

        public Builder() {
            revokers = new ArrayList<>();
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

        public void clearRevokers() {
            revokers.clear();
        }
    }
}
