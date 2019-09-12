package com.techoveride.turbolightbrowser.Tabs;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;

import com.techoveride.turbolightbrowser.R;

import java.util.ArrayList;

public class TabsDrawerAdapter extends RecyclerView.Adapter<TabsDrawerAdapter.ViewHolder> {
    private ArrayList<TabHost.TabSpec> tabArrayList;
    private ArrayList<WebView> webViewArrayList;
    private String tabViewTitle;
    private TabHost tabHost;
    private DrawerLayout tab_drawer;
    private OnItemClickListener tabsListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        tabsListener = listener;
    }


    public TabsDrawerAdapter(ArrayList<TabHost.TabSpec> tabArrayList, ArrayList<WebView> webViewArrayList, TabHost tabHost, DrawerLayout tab_drawer) {
        this.tabArrayList = tabArrayList;
        this.webViewArrayList = webViewArrayList;
        this.tabHost = tabHost;
        this.tab_drawer = tab_drawer;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tab_row, viewGroup, false);
        return new ViewHolder(view,tabsListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        tabViewTitle = webViewArrayList.get(i).getTitle();
        viewHolder.tabList.setText(tabViewTitle);
        viewHolder.tabList.setId(i);
        int activeId = tabHost.getCurrentTab();
        viewHolder.tabList.getId();
        viewHolder.tabList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // viewHolder.tabList.setBackgroundColor(Color.DKGRAY);
                tabHost.setCurrentTab(viewHolder.tabList.getId());
                tab_drawer.closeDrawer(GravityCompat.END);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tabArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tabList;
        ImageButton closeTab;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener itemClickListener) {
            super(itemView);
            tabList = itemView.findViewById(R.id.tab_title);
            closeTab = itemView.findViewById(R.id.tab_close);
            //tabList.findViewById(tabHost.getCurrentTab()).setBackgroundColor(Color.DKGRAY);

            closeTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            itemClickListener.onItemClick(position);
                            notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }
}
