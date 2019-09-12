package com.techoveride.turbolightbrowser.bookmarks;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.techoveride.turbolightbrowser.MainActivity;
import com.techoveride.turbolightbrowser.R;

public class BookmarkActivity extends AppCompatActivity {
    private SQLiteDatabase sqLiteDBBookmark;
    private BookmarkAdapter bookmarkAdapter;
    private RecyclerView bookmarkRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        setTitle("Browser Bookmark");

        ImageButton btnGoBack = findViewById(R.id.btnBack);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final BookmarkDBHandler bookmarkDBHandler = new BookmarkDBHandler(this);
        sqLiteDBBookmark = bookmarkDBHandler.getWritableDatabase();
       // bookmarkDBHandler.onCreate(sqLiteDBBookmark);

        bookmarkRecyclerView = findViewById(R.id.bookmarks_recycler);
        bookmarkRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookmarkAdapter = new BookmarkAdapter(this, getAllItems());
        bookmarkRecyclerView.setAdapter(bookmarkAdapter);


        bookmarkAdapter.setOnItemClickListener(new BookmarkAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //String message = String.valueOf(position);
                deleteBookmark(position);
                //customToast(message);
                bookmarkAdapter.notifyItemRemoved(position);
               // refreshBookmark();

            }
        });
        bookmarkAdapter.setOnItemClickListenerVisit(new BookmarkAdapter.OnItemClickListenerVisit() {
            @Override
            public void onItemClick(int position) {
                String message = "Link opened in Background,go back to view";
                String url_new= visitBookmark(position);
                MainActivity.addNewTab(BookmarkActivity.this,url_new);
                customToast(message);
            }
        });

        ImageButton btnBookmark = findViewById(R.id.btn_bookmark);
        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearBookmark();
            }
        });
    }

    private void refreshBookmark() {
        bookmarkRecyclerView.setLayoutManager(null);
        bookmarkRecyclerView.setAdapter(null);
        bookmarkRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookmarkRecyclerView.setAdapter(bookmarkAdapter);
    }

    private void deleteBookmark(int position) {
        position +=1;
        String cursorId = String.valueOf(position);

        //Copy bookmark Table
        String copyDB = "CREATE TABLE techoveride AS SELECT" +
                " (SELECT COUNT(*) FROM bookmark b WHERE a._id >= b._id)" +
                " AS _id,title,url,timestamp FROM bookmark a;";
        Log.i("Delete Bookmark",copyDB);
        sqLiteDBBookmark.execSQL(copyDB);
        //Drop bookmark Table
        String dropDB = "DROP TABLE bookmark";
        sqLiteDBBookmark.execSQL(dropDB);
        Log.i("Delete Bookmark",dropDB);

        //Alter new Bookmark Table
        String newDB = "ALTER TABLE techoveride RENAME TO bookmark";
        sqLiteDBBookmark.execSQL(newDB);
        Log.i("Delete Bookmark",newDB);

        //Deleting bookmark record
        String deleteSelectedQuery = "DELETE FROM bookmark WHERE _id = " + cursorId + " ;";
        Log.i("Delete Bookmark",deleteSelectedQuery);

        Log.i("Delete Bookmark", cursorId);
        sqLiteDBBookmark.execSQL(deleteSelectedQuery);
        //bookmarkRecyclerView.setAdapter(null);
        //bookmarkRecyclerView.setLayoutManager(null);
        customToast(cursorId);
    }
    private String visitBookmark(int position) {
        String cursorId = String.valueOf(position);
        //Copy bookmark Table
        String copyDB = "CREATE TABLE techoveride AS SELECT" +
                " (SELECT COUNT(*) FROM bookmark b WHERE a._id >= b._id)" +
                " AS _id,title,url,timestamp FROM bookmark a;";
        sqLiteDBBookmark.execSQL(copyDB);
        //Drop bookmark Table
        String dropDB = "DROP TABLE bookmark";
        sqLiteDBBookmark.execSQL(dropDB);
        //Alter new Bookmark Table
        String newDB = "ALTER TABLE techoveride RENAME TO bookmark";
        sqLiteDBBookmark.execSQL(newDB);
        Cursor demo = sqLiteDBBookmark.rawQuery("SELECT url FROM bookmark", null);
        demo.moveToPosition(position);
        String url = demo.getString(demo.getColumnIndex(MyBookmarks.BookmarkEntry.COL_URL));
        Log.i("Delete Bookmark",url);
        //customToast(url);
        demo.close();
        return url;
    }

    private void clearBookmark() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Confirm Action");
        alertDialog.setMessage("Select Yes to clear Bookmarks or No to keep them !");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String deleteQuery = "DELETE FROM " + MyBookmarks.BookmarkEntry.TABLE_NAME +
                                " WHERE " + MyBookmarks.BookmarkEntry.COL_TITLE + " IS NOT NULL ;";
                        sqLiteDBBookmark.execSQL(deleteQuery);
                        bookmarkRecyclerView.setAdapter(null);
                        bookmarkRecyclerView.setLayoutManager(null);

                        String message = "Browsing History Cleared !";
                        customToast(message);
                    }
                });
        alertDialog.show();
    }

    private void customToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected Cursor getAllItems() {
        return sqLiteDBBookmark.query(MyBookmarks.BookmarkEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MyBookmarks.BookmarkEntry.COL_TIMESTAMP + " DESC");
    }
}
