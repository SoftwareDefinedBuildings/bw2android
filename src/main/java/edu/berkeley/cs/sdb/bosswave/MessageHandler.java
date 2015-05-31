package edu.berkeley.cs.sdb.bosswave;

public interface MessageHandler {
    void onResultReceived(Message message);
}
