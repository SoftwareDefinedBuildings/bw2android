package edu.berkeley.cs.sdb.bosswave;

public interface ListResultHandler {
    void onResult(String result);

    void finish();
}
