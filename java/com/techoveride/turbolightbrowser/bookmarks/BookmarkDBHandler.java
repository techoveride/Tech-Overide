package com.techoveride.turbolightbrowser.bookmarks;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.techoveride.turbolightbrowser.bookmarks.MyBookmarks.*;

public class BookmarkDBHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "bookmark.db";
    public static final int DATABASE_VERSION = 1;

    public BookmarkDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_BOOKMARK_TABLE = "CREATE TABLE " +
                BookmarkEntry.TABLE_NAME + " (" +
                BookmarkEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BookmarkEntry.COL_TITLE + " TEXT NOT NULL, " +
                BookmarkEntry.COL_URL + " TEXT NOT NULL, " +
                BookmarkEntry.COL_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        db.execSQL(SQL_CREATE_BOOKMARK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ BookmarkEntry.TABLE_NAME);
        onCreate(db);
    }
}

