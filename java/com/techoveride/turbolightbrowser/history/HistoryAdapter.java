package com.techoveride.turbolightbrowser.history;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techoveride.turbolightbrowser.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>   {
    private Context mContext;
    private Cursor mCursor;
    private OnItemClickListener historyListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        historyListener = listener;
    }

    public HistoryAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.history_list_item, viewGroup, false);
        return new HistoryViewHolder(view,historyListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder historyViewHolder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        String title = mCursor.getString(mCursor.getColumnIndex(MyHistory.HistoryEntry.COL_TITLE));
        String url = mCursor.getString(mCursor.getColumnIndex(MyHistory.HistoryEntry.COL_URL));
        historyViewHolder.txtTitle.setText(title);
        historyViewHolder.txtURL.setText(url);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle;
        public TextView txtURL;

        public HistoryViewHolder(@NonNull View itemView,final OnItemClickListener onItemClickListener) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.url_title);
            txtURL = itemView.findViewById(R.id.url_link);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(position);
                            //notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }
}

