package com.sunit.groceryplus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;

import java.util.List;

/** SearchHistoryAdapter - Displays previous search queries with options to re-search or delete. */
public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    private List<String> historyItems;
    private OnItemClickListener listener;
    private OnDeleteClickListener deleteListener;

    /** Interface for re-triggering a previous search query. */
    public interface OnItemClickListener {
        void onItemClick(String query);
    }

    /** Interface for removing a query from history. */
    public interface OnDeleteClickListener {
        void onDeleteClick(String query);
    }

    /** Constructor. */
    public SearchHistoryAdapter(List<String> historyItems, OnItemClickListener listener) {
        this.historyItems = historyItems;
        this.listener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    /** Updates the data source and refreshes the UI. */
    public void updateItems(List<String> items) {
        this.historyItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String query = historyItems.get(position);
        holder.queryTv.setText(query);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(query);
        });

        if (deleteListener != null) {
            holder.deleteIv.setVisibility(View.VISIBLE);
            holder.deleteIv.setOnClickListener(v -> deleteListener.onDeleteClick(query));
        } else {
            holder.deleteIv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return historyItems != null ? historyItems.size() : 0;
    }

    /** ViewHolder for search history prompts. */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // UI Components
        TextView queryTv;
        ImageView deleteIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            queryTv = itemView.findViewById(R.id.historyQueryTv);
            deleteIv = itemView.findViewById(R.id.historyDeleteIv);
        }
    }
}
