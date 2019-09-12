package com.techoveride.turbolightbrowser.Tabs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.techoveride.turbolightbrowser.MainActivity;
import com.techoveride.turbolightbrowser.R;

import java.util.ArrayList;

import static com.techoveride.turbolightbrowser.MainActivity.*;

public class TabMenu extends AppCompatActivity {
    TextView closeOpen;
    TextView closeOther;
    TextView closeAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_menu);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * .6), (int) (height * .22));

        closeAll = findViewById(R.id.close_all_tabs);
        closeOpen = findViewById(R.id.close_current_tab);
        closeOther = findViewById(R.id.close_other_tabs);

        closeOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabArrayList.remove(th.getCurrentTab());
                counter = tabArrayList.size();
                tab_counter.setText(String.valueOf(counter));
                if(th.getCurrentTab()>1) {
                    th.setCurrentTab(th.getCurrentTab() - 1);
                    th.getTabWidget()
                            .removeView(th.getTabWidget().getChildTabViewAt(th.getCurrentTab()));
                    Log.i("TabMenu", "" + th.getCurrentTab() + "  " + counter);
                    MainActivity.tabAdapter.notifyItemRemoved(th.getCurrentTab()+1);
                    onBackPressed();
                }
            }
        });
        closeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabArrayList.clear();
                counter = 0;
                tab_counter.setText(String.valueOf(counter));
                th.clearAllTabs();
                MainActivity.tabAdapter.notifyDataSetChanged();
                MainActivity.addNewTab(TabMenu.this,"https://www.google.com");
                onBackPressed();
            }
        });
        closeOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tabIndex = th.getCurrentTab();
                //tabArrayList.subList(0, tabIndex).clear();
                TabHost.TabSpec temp= tabArrayList.remove(tabIndex);
                tabArrayList.clear();
                tabArrayList.add(0,temp);
                counter = tabArrayList.size();
                tab_counter.setText(String.valueOf(counter));
                MainActivity.tabAdapter.notifyDataSetChanged();
                onBackPressed();
            }
        });
    }
}
