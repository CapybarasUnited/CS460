package com.cs460.finalprojectfirstdraft.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.models.ListItem;

import java.util.List;

/**
 * RecyclerViewAdapter is responsible for binding data from a list of {@link ListItem} objects
 * to views that are displayed within a {@link RecyclerView}.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final List<ListItem> itemList;
    private final OnItemClickListener listener;

    /**
     * Interface for item click callbacks.
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * Constructor for RecyclerViewAdapter.
     *
     * @param itemList The list of {@link ListItem} objects to be displayed in the RecyclerView.
     * @param listener The listener for handling item click events.
     */
    public RecyclerViewAdapter(List<ListItem> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListItem item = itemList.get(position);

        // Set the title
        holder.titleTextView.setText(item.getTitle());

        // Display progress if available
        if (item.getProgress() != null) {
            holder.progressTextView.setVisibility(View.VISIBLE);
            holder.progressTextView.setText(item.getProgress() + "%");
        } else {
            holder.progressTextView.setVisibility(View.GONE);
        }

        // Set the background color based on the position
        int backgroundColor;
        switch (position % 3) {
            case 0:
                backgroundColor = holder.itemView.getContext().getResources().getColor(R.color.red);
                break;
            case 1:
                backgroundColor = holder.itemView.getContext().getResources().getColor(R.color.orange);
                break;
            case 2:
                backgroundColor = holder.itemView.getContext().getResources().getColor(R.color.yellow);
                break;
            default:
                backgroundColor = holder.itemView.getContext().getResources().getColor(R.color.defaultBackground);
        }
        holder.itemView.setBackgroundColor(backgroundColor);

        // Set the click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(currentPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /**
     * ViewHolder class to hold the views for each list item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;  // Displays the title of the list item
        TextView progressTextView;  // Displays the progress of the list item (optional)

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The view for a single list item.
         * @param listener The listener for item click events.
         */
        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.itemTitle);
            progressTextView = itemView.findViewById(R.id.itemProgress);

            // Click event handler
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
