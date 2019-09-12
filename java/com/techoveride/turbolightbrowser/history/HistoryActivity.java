package com.techoveride.turbolightbrowser.history;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.techoveride.turbolightbrowser.MainActivity;
import com.techoveride.turbolightbrowser.R;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "HistoryActivity";
    private SQLiteDatabase sqLiteDBHistory;
    private HistoryAdapter historyAdapter;
    protected ImageButton btnGoBack;
    private RecyclerView historyRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("Browser MyHistory");

        btnGoBack = findViewById(R.id.btnBack);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        HistoryDBHandler myDBHandler = new HistoryDBHandler(this);
        sqLiteDBHistory = myDBHandler.getWritableDatabase();

        recyclerInitiate();


    }

    private void recyclerInitiate() {
        historyRecyclerView = findViewById(R.id.history_recycler);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new HistoryAdapter(this, getAllHistory());
        historyRecyclerView.setAdapter(historyAdapter);

        historyAdapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String message = "Link opened in Background,go back to view";
               String url_new= visitHistory(position);
                MainActivity.addNewTab(HistoryActivity.this,url_new);
                customToast(message);
            }
        });
        historyAdapter.notifyDataSetChanged();
    }


    private String visitHistory(int position) {
        String cursorId = String.valueOf(position);
        //Copy history Table
        String copyDB = "CREATE TABLE techoveride AS SELECT" +
                " (SELECT COUNT(*) FROM history b WHERE a._id >= b._id)" +
                " AS _id,title,url,timestamp FROM history a;";
        sqLiteDBHistory.execSQL(copyDB);
        //Drop history Table
        String dropDB = "DROP TABLE history";
        sqLiteDBHistory.execSQL(dropDB);
        //Alter new history Table
        String newDB = "ALTER TABLE techoveride RENAME TO history";
        sqLiteDBHistory.execSQL(newDB);
        Cursor demo = sqLiteDBHistory.rawQuery("SELECT url FROM history", null);
        demo.moveToPosition(position);
        String url = demo.getString(demo.getColumnIndex(MyHistory.HistoryEntry.COL_URL));
        //customToast(url);
        demo.close();
        return url;
    }

    public Cursor getAllHistory() {

        return sqLiteDBHistory.query(
                MyHistory.HistoryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MyHistory.HistoryEntry.COL_TIMESTAMP + " DESC"
        );

    }

    private void customToast(String message) {
        Toast.makeText(HistoryActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void clearHistory(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Confirm Action");
        alertDialog.setMessage("Select Yes to clear Browsing History or No to keep them !");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String deleteQuery = "DELETE FROM " + MyHistory.HistoryEntry.TABLE_NAME +
                                " WHERE " + MyHistory.HistoryEntry.COL_TITLE + " IS NOT NULL ;";
                        sqLiteDBHistory.execSQL(deleteQuery);
                        historyRecyclerView.setAdapter(null);
                        historyRecyclerView.setLayoutManager(null);
                        String message = "Browsing History Cleared !";
                        customToast(message);
                    }
                });
        alertDialog.show();
    }
}
