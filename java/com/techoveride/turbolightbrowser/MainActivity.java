package com.techoveride.turbolightbrowser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.techoveride.turbolightbrowser.Tabs.TabMenu;
import com.techoveride.turbolightbrowser.Tabs.TabsDrawerAdapter;
import com.techoveride.turbolightbrowser.bookmarks.BookmarkActivity;
import com.techoveride.turbolightbrowser.bookmarks.BookmarkDBHandler;
import com.techoveride.turbolightbrowser.bookmarks.MyBookmarks;
import com.techoveride.turbolightbrowser.history.HistoryActivity;
import com.techoveride.turbolightbrowser.history.HistoryAdapter;
import com.techoveride.turbolightbrowser.history.HistoryDBHandler;
import com.techoveride.turbolightbrowser.history.MyHistory;

import java.util.ArrayList;
import java.util.Objects;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static TabHost th;
    public static ArrayList<TabHost.TabSpec> tabArrayList = new ArrayList<TabHost.TabSpec>();
    public static ArrayList<WebView> webViewArrayList = new ArrayList<>();
    public static int counter = 0;
    public static boolean javascriptStatus = true;

    public static WebView webViewTabbed;
    protected static ProgressBar progressBar;
    protected static ImageView urlIcon;
    protected static ImageView reloadBtn;
    protected String link;
    protected String activeUrl;
    protected static String pageTitle;
    protected static EditText addressBar;
    private static int progressValue = 0;
    protected static LinearLayout progBarLayout;
    protected BottomNavigationView bottomNavigationView;
    protected int bottomMenu;
    protected static Animation aniRotate;
    protected SwipeRefreshLayout swipeRefreshLayout;
    public static TextView tab_counter;
    protected ImageButton addNewTab;
    //tab drawer
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    public static TabsDrawerAdapter tabAdapter;
    private static String visitPageTitle;
    private static String visitPageURL;
    //History
    private static SQLiteDatabase sqLiteDBHistory;
    private HistoryAdapter historyAdapter;
    private long downloadID;
    //Bookmark
    private SQLiteDatabase sqLiteDBBookmarks;
    protected BookmarkDBHandler bookmarkDBHandler;
    protected static boolean zoomControlStatus = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        DrawerLayout tab_drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle tab_toggle = new ActionBarDrawerToggle(
                this, tab_drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        tab_drawer.addDrawerListener(tab_toggle);
        tab_toggle.syncState();
        //Bookmarks
        bookmarkDBHandler = new BookmarkDBHandler(this);
        sqLiteDBBookmarks = bookmarkDBHandler.getWritableDatabase();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        NavigationView tab_nav_view = findViewById(R.id.tab_nav_view);
        //tab_nav_view.inflateHeaderView(R.layout.tab_main_drawer);


        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        getSupportActionBar().setSubtitle(null);

        th = findViewById(R.id.tabhost);
        th.setup();
        th.getTabWidget().setStripEnabled(false);

        final TabHost.TabSpec ourSpec = th.newTabSpec(Integer.toString(counter));
        ourSpec.setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                final LinearLayout linearLayout = new LinearLayout(MainActivity.this);
                webViewTabbed = new WebView(MainActivity.this);
                webViewTabbed.setId(counter);
                webViewTabbed.getSettings().setJavaScriptEnabled(javascriptStatus);
                webViewTabbed.getSettings().setBuiltInZoomControls(zoomControlStatus);
                webViewTabbed.getSettings().setSupportZoom(zoomControlStatus);
                webViewTabbed.setWebViewClient(new WebViewClient() {
                    @TargetApi(Build.VERSION_CODES.O)
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                        progBarLayout.setVisibility(View.VISIBLE);
                        reloadBtn.setImageResource(R.drawable.ic_sync);
                        String demo = java.time.LocalTime.now().toString();
                        Log.i("Time Zone", demo);
                    }

                    @TargetApi(Build.VERSION_CODES.O)
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        String demo = java.time.LocalTime.now().toString();
                        Log.i("Time Zone", demo);
                        reloadBtn.setImageResource(R.drawable.ic_go);
                        //progBarLayout.setVisibility(View.GONE);
                        //swipeRefreshLayout.setRefreshing(false);
                        int tabID = (th.getCurrentTab());
                        final WebView activeWebview = th.getCurrentView().findViewById(tabID);
                        // url =activeWebview.getUrl();
                        addressBar.setText(url);
                        addressBar.setSelection(0);

                        //History Entry
                        visitPageTitle = activeWebview.getTitle();
                        visitPageURL = activeWebview.getUrl();
                        if (visitPageURL != null) {
                            sqLiteDBHistory = new HistoryDBHandler(MainActivity.this).getWritableDatabase();
                            ContentValues cValues = new ContentValues();
                            cValues.put(MyHistory.HistoryEntry.COL_TITLE, visitPageTitle);
                            cValues.put(MyHistory.HistoryEntry.COL_URL, visitPageURL);
                            sqLiteDBHistory.insert(MyHistory.HistoryEntry.TABLE_NAME, null, cValues);
                        }


                    }
                });
                webViewTabbed.setWebChromeClient(new WebChromeClient() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);

                        aniRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                        reloadBtn.startAnimation(aniRotate);

                        //reloadBtn.setImageBitmap(R.drawable.ic_sync);
                        progressBar.setProgress(newProgress);
                        progressValue = progressBar.getProgress();
                        if (progressValue > 35) {
                            progressBar.setProgressTintList(ColorStateList.valueOf(Color.CYAN));
                            progBarLayout.setBackgroundColor(Color.BLUE);
                        }
                        if (progressValue > 50) {
                            progressBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
                            progBarLayout.setBackgroundColor(Color.YELLOW);

                        }
                        if (progressValue > 70) {
                            progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                            progBarLayout.setBackgroundColor(Color.MAGENTA);

                        }
                        if (progressValue > 90) {
                            reloadBtn.setAnimation(null);
                        }
                    }

                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        super.onReceivedTitle(view, title);
                        pageTitle = title;
                    }

                    @Override
                    public void onReceivedIcon(WebView view, Bitmap icon) {
                        super.onReceivedIcon(view, icon);
                        urlIcon.setImageBitmap(icon);
                    }

                });
                webViewTabbed.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                webViewTabbed.loadUrl("https://www.google.com");

                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(webViewTabbed);
                webViewArrayList.add(webViewTabbed);

                return (linearLayout);
            }
        });
        ourSpec.setIndicator(Integer.toString(counter));
        //counter++;
        tabArrayList.add(ourSpec);
        th.addTab(ourSpec);
        //th.setCurrentTabByTag(ourSpec.getTag());
        th.setCurrentTab(counter);
        counter++;

        progressBar = findViewById(R.id.progressBar);
        urlIcon = findViewById(R.id.urlIcon);
        addressBar = findViewById(R.id.address_bar);
        reloadBtn = findViewById(R.id.reload_btn);
        progBarLayout = findViewById(R.id.progressBarLayout);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        //swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        tab_counter = findViewById(R.id.tab_counter);
        tab_counter.setText(counter + "");
        progressBar.setMax(100);
        link = "https://www.google.com";

        addressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressBar.setSelection(addressBar.getText().length());
                //addressBar.selectAll();
                //addressBar.requestFocus();
                // InputMethodManager board = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //board.showSoftInput(addressBar,0);
                // board.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
        addressBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    String link = addressBar.getText().toString().trim();
                    customKeyboardHide(MainActivity.this);
                    customUrlValidator(link);
                    return true;
                }
                return false;
            }
        });
       /* swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            int tabID = (th.getCurrentTab());
            final WebView activeWebview = th.getCurrentView().findViewById(tabID);
            @Override
            public void onRefresh() {
                activeWebview.reload();
            }
        });*/

        //Download Listener
        webViewTabbed.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                downloadID = manager.enqueue(request);

                Toast.makeText(MainActivity.this, "Your File is Downloading...", Toast.LENGTH_SHORT).show();
            }
        });
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                link = addressBar.getText().toString().trim();
                customUrlValidator(link);
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                bottomMenu = menuItem.getItemId();
                int tabID = (th.getCurrentTab());
                final WebView activeWebview = th.getCurrentView().findViewById(tabID);

                switch (bottomMenu) {
                    case R.id.navigation_exit:
                        finish();
                        break;
                    case R.id.navigation_home:
                        activeWebview.loadUrl("https://www.google.com");
                        break;
                    case R.id.navigation_next:
                        if (activeWebview.canGoForward()) {
                            activeWebview.goForward();
                        } else {
                            Toast.makeText(MainActivity.this, "Can't Go Forward !", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.navigation_previous:
                        if (activeWebview.canGoBack()) {
                            activeWebview.goBack();
                        } else {
                            Toast.makeText(MainActivity.this, "Can't go Back !", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.navigation_share:
                        activeUrl = activeWebview.getUrl();
                        pageTitle = activeWebview.getTitle();
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, activeUrl);
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, pageTitle);
                        startActivity(Intent.createChooser(shareIntent, "Send to..."));
                }
                return true;
            }
        });
        tab_counter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout tab_drawer = findViewById(R.id.drawer_layout);
                tab_drawer.openDrawer(GravityCompat.END);
                setRecyclerView();
            }
        });
        addNewTab = findViewById(R.id.btn_add_tab);
        //history
        //dbHandler();

    }

    private void customKeyboardHide(MainActivity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void customUrlValidator(String link) {
        int tabID = (th.getCurrentTab());
        final WebView activeWebview = th.getCurrentView().findViewById(tabID);
        if (!link.contains(" ")) {
            if (URLUtil.isHttpUrl(link) || URLUtil.isHttpsUrl(link)) {
                activeWebview.loadUrl(link);
            } else if (link.contains(".com")
                    || link.contains(".net")
                    || link.contains(".edu")
                    || link.contains(".org")
                    || link.contains(".gov")) {
                if (!link.contains("www")) {
                    activeWebview.loadUrl("https://www." + link);
                } else {
                    activeWebview.loadUrl("https://" + link);
                }
            }
        } else {
            link = "https://www.google.com/search?q=" + link;
            activeWebview.loadUrl(link);
        }
        InputMethodManager board = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        board.hideSoftInputFromWindow(reloadBtn.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!drawer.isDrawerOpen(GravityCompat.START) && webViewTabbed.canGoBack()) {

            webViewTabbed.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        int tabID = (th.getCurrentTab());
        final WebView activeWebview = th.getCurrentView().findViewById(tabID);

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            Intent settings = new Intent(MainActivity.this, Settings.class);
            startActivity(settings);
        } else if (id == R.id.new_tab) {
            addNewTab();
        } else if (id == R.id.add_bookmark) {
            String title = activeWebview.getTitle();
            String url = activeWebview.getOriginalUrl();

            ContentValues cv = new ContentValues();
            cv.put(MyBookmarks.BookmarkEntry.COL_TITLE, title);
            cv.put(MyBookmarks.BookmarkEntry.COL_URL, url);
            sqLiteDBBookmarks.insert(MyBookmarks.BookmarkEntry.TABLE_NAME, null, cv);

            customToast("Bookmark Added");

        } else if (id == R.id.history) {
            Intent history = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(history);
        } else if (id == R.id.refresh) {
            activeWebview.reload();
        }
        return super.onOptionsItemSelected(item);
    }

    protected Cursor getAllItems() {
        return sqLiteDBBookmarks.query(
                MyBookmarks.BookmarkEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MyBookmarks.BookmarkEntry.COL_TIMESTAMP + " DESC"
        );
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        int tabID = (th.getCurrentTab());
        final WebView activeWebview = th.getCurrentView().findViewById(tabID);


        if (id == R.id.app_bookmarks) {
            Intent bookmarks = new Intent(MainActivity.this, BookmarkActivity.class);
            startActivity(bookmarks);
        } else if (id == R.id.app_downloads) {
            startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
        } else if (id == R.id.app_share) {
            activeUrl = activeWebview.getUrl();
            pageTitle = activeWebview.getTitle();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, activeUrl);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, pageTitle);
            startActivity(Intent.createChooser(shareIntent, "Send to..."));

        } else if (id == R.id.app_about) {
            Intent about_us = new Intent(MainActivity.this, AboutUS.class);
            startActivity(about_us);

        } else if (id == R.id.app_feedback) {
            Intent feedback = new Intent(MainActivity.this, HelpFeedback.class);
            startActivity(feedback);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addNewTab() {
        final TabHost.TabSpec ourSpec = th.newTabSpec(Integer.toString(counter));
        ourSpec.setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                final LinearLayout linearLayout = new LinearLayout(MainActivity.this);
                webViewTabbed = new WebView(MainActivity.this);
                webViewTabbed.setId(counter);
                webViewTabbed.getSettings().setJavaScriptEnabled(javascriptStatus);
                webViewTabbed.getSettings().setBuiltInZoomControls(zoomControlStatus);
                webViewTabbed.getSettings().setSupportZoom(zoomControlStatus);
                webViewTabbed.setWebViewClient(new WebViewClient() {
                    @TargetApi(Build.VERSION_CODES.O)
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                        progBarLayout.setVisibility(View.VISIBLE);
                        reloadBtn.setImageResource(R.drawable.ic_sync);
                        String demo = java.time.LocalTime.now().toString();
                        Log.i("Time Zone", demo);
                    }

                    @TargetApi(Build.VERSION_CODES.O)
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        String demo = java.time.LocalTime.now().toString();
                        Log.i("Time Zone", demo);
                        reloadBtn.setImageResource(R.drawable.ic_go);
                        //progBarLayout.setVisibility(View.GONE);
                        //swipeRefreshLayout.setRefreshing(false);
                        int tabID = (th.getCurrentTab());
                        final WebView activeWebview = th.getCurrentView().findViewById(tabID);
                        // url =activeWebview.getUrl();
                        addressBar.setText(url);
                        addressBar.setSelection(0);

                        //History Entry
                        visitPageTitle = activeWebview.getTitle();
                        visitPageURL = activeWebview.getUrl();
                        if (visitPageURL != null) {
                            sqLiteDBHistory = new HistoryDBHandler(MainActivity.this).getWritableDatabase();
                            ContentValues cValues = new ContentValues();
                            cValues.put(MyHistory.HistoryEntry.COL_TITLE, visitPageTitle);
                            cValues.put(MyHistory.HistoryEntry.COL_URL, visitPageURL);
                            sqLiteDBHistory.insert(MyHistory.HistoryEntry.TABLE_NAME, null, cValues);
                        }


                    }
                });
                webViewTabbed.setWebChromeClient(new WebChromeClient() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);

                        aniRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                        reloadBtn.startAnimation(aniRotate);

                        //reloadBtn.setImageBitmap(R.drawable.ic_sync);
                        progressBar.setProgress(newProgress);
                        progressValue = progressBar.getProgress();
                        if (progressValue > 35) {
                            progressBar.setProgressTintList(ColorStateList.valueOf(Color.CYAN));
                            progBarLayout.setBackgroundColor(Color.BLUE);
                        }
                        if (progressValue > 50) {
                            progressBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
                            progBarLayout.setBackgroundColor(Color.YELLOW);

                        }
                        if (progressValue > 70) {
                            progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                            progBarLayout.setBackgroundColor(Color.MAGENTA);

                        }
                        if (progressValue > 90) {
                            reloadBtn.setAnimation(null);
                        }
                    }

                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        super.onReceivedTitle(view, title);
                        pageTitle = title;
                    }

                    @Override
                    public void onReceivedIcon(WebView view, Bitmap icon) {
                        super.onReceivedIcon(view, icon);
                        urlIcon.setImageBitmap(icon);
                    }

                });
                webViewTabbed.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                webViewTabbed.loadUrl("https://www.google.com");

                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(webViewTabbed);
                webViewArrayList.add(webViewTabbed);

                return (linearLayout);
            }
        });
        ourSpec.setIndicator(Integer.toString(counter));
        //counter++;
        tabArrayList.add(ourSpec);
        th.addTab(ourSpec);
        //th.setCurrentTabByTag(ourSpec.getTag());
        th.setCurrentTab(counter);
        counter++;
        tab_counter.setText(counter + "");
    }


    public void setAddNewTab(View view) {
        Toast.makeText(getApplicationContext(), "Tab Added", Toast.LENGTH_SHORT).show();
        addNewTab();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
    }

    public void setRecyclerView() {
        recyclerView = findViewById(R.id.drawer_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        DrawerLayout tab_drawer = findViewById(R.id.drawer_layout);
        tabAdapter = new TabsDrawerAdapter(tabArrayList, webViewArrayList, th, tab_drawer);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(tabAdapter);


        tabAdapter.setOnItemClickListener(new TabsDrawerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //position++;
                String message = String.valueOf(position);
                //customToast(message);
                tabArrayList.remove(position);
                //tabArrayList.clear();
                //tabAdapter.notifyDataSetChanged();
                counter = tabArrayList.size();
                tab_counter.setText(String.valueOf(counter));
                if (position > 0) {
                    th.setCurrentTab(position - 1);
                    th.getTabWidget()
                            .removeView(th.getTabWidget().getChildTabViewAt(position));
                }
                tabAdapter.notifyItemRemoved(position);
                //recyclerView.setLayoutManager(null);
                //recyclerView.setAdapter(null);
                // setRecyclerView();
            }
        });
    }


    private void customToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                customToast("Download Completed");
            }
        }
    };

    public void tabMenu(View view) {
        Intent openTabMenu = new Intent(MainActivity.this, TabMenu.class);
        startActivity(openTabMenu);
    }

    public static void addNewTab(final Context context, final String url_new) {
        final TabHost.TabSpec ourSpec = th.newTabSpec(Integer.toString(counter));
        ourSpec.setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                final LinearLayout linearLayout = new LinearLayout(context);
                webViewTabbed = new WebView(context);
                webViewTabbed.setId(counter);
                webViewTabbed.getSettings().setJavaScriptEnabled(javascriptStatus);
                webViewTabbed.getSettings().setBuiltInZoomControls(zoomControlStatus);
                webViewTabbed.getSettings().setSupportZoom(zoomControlStatus);
                webViewTabbed.setWebViewClient(new WebViewClient() {
                    @TargetApi(Build.VERSION_CODES.O)
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                        progBarLayout.setVisibility(View.VISIBLE);
                        reloadBtn.setImageResource(R.drawable.ic_sync);
                        String demo = java.time.LocalTime.now().toString();
                        Log.i("Time Zone", demo);
                    }

                    @TargetApi(Build.VERSION_CODES.O)
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        String demo = java.time.LocalTime.now().toString();
                        Log.i("Time Zone", demo);
                        reloadBtn.setImageResource(R.drawable.ic_go);
                        //progBarLayout.setVisibility(View.GONE);
                        //swipeRefreshLayout.setRefreshing(false);
                        int tabID = (th.getCurrentTab());
                        final WebView activeWebview = th.getCurrentView().findViewById(tabID);
                        // url =activeWebview.getUrl();
                        addressBar.setText(url);
                        addressBar.setSelection(0);

                        //History Entry
                        visitPageTitle = activeWebview.getTitle();
                        visitPageURL = activeWebview.getUrl();
                        if (visitPageURL != null) {
                            sqLiteDBHistory = new HistoryDBHandler(context).getWritableDatabase();
                            ContentValues cValues = new ContentValues();
                            cValues.put(MyHistory.HistoryEntry.COL_TITLE, visitPageTitle);
                            cValues.put(MyHistory.HistoryEntry.COL_URL, visitPageURL);
                            sqLiteDBHistory.insert(MyHistory.HistoryEntry.TABLE_NAME, null, cValues);
                        }


                    }
                });
                webViewTabbed.setWebChromeClient(new WebChromeClient() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);

                        aniRotate = AnimationUtils.loadAnimation(context, R.anim.rotate);
                        reloadBtn.startAnimation(aniRotate);

                        //reloadBtn.setImageBitmap(R.drawable.ic_sync);
                        progressBar.setProgress(newProgress);
                        progressValue = progressBar.getProgress();
                        if (progressValue > 35) {
                            progressBar.setProgressTintList(ColorStateList.valueOf(Color.CYAN));
                            progBarLayout.setBackgroundColor(Color.BLUE);
                        }
                        if (progressValue > 50) {
                            progressBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
                            progBarLayout.setBackgroundColor(Color.YELLOW);

                        }
                        if (progressValue > 70) {
                            progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                            progBarLayout.setBackgroundColor(Color.MAGENTA);

                        }
                        if (progressValue > 90) {
                            reloadBtn.setAnimation(null);
                        }
                    }

                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        super.onReceivedTitle(view, title);
                        pageTitle = title;
                    }

                    @Override
                    public void onReceivedIcon(WebView view, Bitmap icon) {
                        super.onReceivedIcon(view, icon);
                        urlIcon.setImageBitmap(icon);
                    }

                });
                webViewTabbed.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                webViewTabbed.loadUrl(url_new);

                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(webViewTabbed);
                webViewArrayList.add(webViewTabbed);

                return (linearLayout);
            }
        });
        ourSpec.setIndicator(Integer.toString(counter));
        //counter++;
        tabArrayList.add(ourSpec);
        //onBackPressed();
        th.addTab(ourSpec);
        //th.setCurrentTabByTag(ourSpec.getTag());
        th.setCurrentTab(counter);
        counter++;
        tab_counter.setText(counter + "");
        Log.i("History Activity", "It added the tab");

        Log.i("History Activity", String.valueOf(counter));
    }

}
