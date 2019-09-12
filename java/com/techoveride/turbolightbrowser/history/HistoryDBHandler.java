package com.techoveride.turbolightbrowser.history;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.techoveride.turbolightbrowser.history.MyHistory.*;

public class HistoryDBHandler extends SQLiteOpenHelper {
    public static final String DATABSE_NAME = "history.db";
    public static final int DATABASE_VERSION = 1;

    public HistoryDBHandler(Context context) {
        super(context, DATABSE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_HISTORY_TABLE = "CREATE TABLE " +
                HistoryEntry.TABLE_NAME + " (" +
                HistoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                HistoryEntry.COL_TITLE + " TEXT NOT NULL, " +
                HistoryEntry.COL_URL + " TEXT NOT NULL, " +
                HistoryEntry.COL_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        db.execSQL(SQL_CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ HistoryEntry.TABLE_NAME);
        onCreate(db);
    }
}
