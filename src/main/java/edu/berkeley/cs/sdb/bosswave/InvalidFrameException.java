package edu.berkeley.cs.sdb.bosswave;

class InvalidFrameException extends RuntimeException {
    public InvalidFrameException(String msg) {
        super(msg);
    }

    public InvalidFrameException(String msg, Throwable e) {
        super(msg, e);
    }
}
