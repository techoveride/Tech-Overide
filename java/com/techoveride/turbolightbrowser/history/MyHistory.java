package com.techoveride.turbolightbrowser.history;

import android.provider.BaseColumns;

public class MyHistory {
    public MyHistory() {
    }
    public static final class HistoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "history";
        public static final String COL_TITLE = "title";
        public static final String COL_URL = "url";
        public static final String COL_TIMESTAMP = "timestamp";
    }
}