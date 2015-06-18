package edu.berkeley.cs.sdb.bosswave;

import java.util.List;

public interface ListResultHandler {
    void onResult(String result);

    void finish();
}
