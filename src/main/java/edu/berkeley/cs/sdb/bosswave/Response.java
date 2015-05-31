package edu.berkeley.cs.sdb.bosswave;

public class Response {
    private final String status;
    private final String reason;

    public Response(String status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }
}
