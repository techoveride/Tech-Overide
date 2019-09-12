package com.techoveride.turbolightbrowser.bookmarks;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.techoveride.turbolightbrowser.R;
import com.techoveride.turbolightbrowser.bookmarks.MyBookmarks.BookmarkEntry;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private OnItemClickListener mListener;
    private OnItemClickListenerVisit dListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public interface OnItemClickListenerVisit {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }public void setOnItemClickListenerVisit(OnItemClickListenerVisit visitlistener) {
        dListener = visitlistener;
    }

    public BookmarkAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.bookmarks_list_item, viewGroup, false);

        return new BookmarkViewHolder(view, mListener,dListener);
    }
    @Override
    public void onBindViewHolder(@NonNull final BookmarkViewHolder bookmarkViewHolder, final int position) {

        if (!mCursor.moveToPosition(position)) {
            return;
        }
        String title = mCursor.getString(mCursor.getColumnIndex(BookmarkEntry.COL_TITLE));
        String url = mCursor.getString(mCursor.getColumnIndex(BookmarkEntry.COL_URL));
        bookmarkViewHolder.txtTitle.setText(title);
        bookmarkViewHolder.txtURL.setText(url);
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

    public class BookmarkViewHolder extends RecyclerView.ViewHolder {
        public ImageButton btnDel;
        public TextView txtTitle;
        public TextView txtURL;

        public BookmarkViewHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener,final OnItemClickListenerVisit onItemClickListenerVisit) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.url_title);
            txtURL = itemView.findViewById(R.id.url_link);
            btnDel = itemView.findViewById(R.id.btn_del_bookmark);
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(position);
                            //notifyItemRemoved(position);
                        }
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListenerVisit != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListenerVisit.onItemClick(position);
                            //notifyItemRemoved(position);
                        }
                    }
                }
            });
        }
    }
}

