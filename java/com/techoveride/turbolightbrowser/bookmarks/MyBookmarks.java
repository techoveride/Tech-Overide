package com.techoveride.turbolightbrowser.bookmarks;

import android.provider.BaseColumns;

public class MyBookmarks {
    public MyBookmarks() {
    }
    public static final class BookmarkEntry implements BaseColumns {
        public static final String TABLE_NAME = "bookmark";
        public static final String COL_TITLE = "title";
        public static final String COL_URL = "url";
        public static final String COL_TIMESTAMP = "timestamp";
    }
}