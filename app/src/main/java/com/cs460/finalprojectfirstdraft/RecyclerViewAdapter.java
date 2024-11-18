package com.cs460.finalprojectfirstdraft;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter class for managing the data and binding it to the RecyclerView.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<ListItem> itemList;

    /**
     * Constructor for RecyclerViewAdapter.
     *
     * @param itemList The list of items to be displayed in the RecyclerView.
     */
    public RecyclerViewAdapter(List<ListItem> itemList) {
        this.itemList = itemList;
    }

    /**
     * Called when a new ViewHolder is created.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The type of the new view.
     * @return A new ViewHolder that holds a View for each list item.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called to display data at a specific position.
     *
     * @param holder   The ViewHolder which should be updated to display the contents.
     * @param position The position of the item within the data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListItem item = itemList.get(position);
        holder.titleTextView.setText(item.getTitle());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The number of items.
     */
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /**
     * ViewHolder class to hold the views for each list item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The View of the list item.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.itemTitle);
        }
    }
}
