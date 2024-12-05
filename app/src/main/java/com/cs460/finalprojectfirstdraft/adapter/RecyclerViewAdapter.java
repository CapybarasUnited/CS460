package com.cs460.finalprojectfirstdraft.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.databinding.ActivityRecyclerViewBinding;
import com.cs460.finalprojectfirstdraft.models.Entry;
import com.cs460.finalprojectfirstdraft.models.Item;
import com.cs460.finalprojectfirstdraft.models.List;
import com.cs460.finalprojectfirstdraft.models.ListItem;

import java.util.ArrayList;

/**
 * RecyclerViewAdapter is responsible for binding data from a list of {@link ListItem} objects
 * to views that are displayed within a {@link RecyclerView}.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Item> items;
    private ActivityRecyclerViewBinding binding;

    /**
     * Constructor for RecyclerViewAdapter.
     *
     * @param items The list of {@link ListItem} objects to be displayed in the RecyclerView.
     */
    public RecyclerViewAdapter(ArrayList<Item> items) {
        this.items = items;
    }

    public RecyclerViewAdapter() {

    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    /**
     * Called when a new ViewHolder is created. This inflates the layout for a single list item.
     *
     * @param parent   The parent ViewGroup into which the new view will be added.
     * @param viewType The type of the new view (unused in this implementation).
     * @return A new {@link ViewHolder} that holds a view for a single list item.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called to bind data to the ViewHolder at a specific position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents
     *                 of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);

        if(item.getClass() == Entry.class) {

        }

        if(item.getClass() == List.class) {
            List list = (List) item;
            holder.titleTextView.setText(list.getListName());

            //get progress logic needs to reach into the list (if it is a checklist) and count the number of completed entries and divide by total entries and * 100 + "%"
            // Show progress only if it's not null, otherwise hide the progress text view
//            if (item.getProgress() != null) {
//                holder.progressTextView.setVisibility(View.VISIBLE);
//                holder.progressTextView.setText(item.getProgress() + "%");
//            } else {
//                holder.progressTextView.setVisibility(View.GONE);
//            }

            // Set a background color dynamically based on the position
            int backgroundColor;
            switch (position % 3) {
                case 0:
                    backgroundColor = holder.itemView.getContext().getResources().getColor(R.color.red); // Red background
                    break;
                case 1:
                    backgroundColor = holder.itemView.getContext().getResources().getColor(R.color.orange); // Orange background
                    break;
                case 2:
                    backgroundColor = holder.itemView.getContext().getResources().getColor(R.color.yellow); // Yellow background
                    break;
                default:
                    backgroundColor = holder.itemView.getContext().getResources().getColor(R.color.defaultBackground);
            }
            holder.itemView.setBackgroundColor(backgroundColor);
        }
    }






    /**
     * Returns the total number of items in the adapter's data set.
     *
     * @return The number of items in the data set.
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder class to hold the views for each list item.
     * This class provides a reference to the views for each data item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;  // Displays the title of the list item
        TextView progressTextView;  // Displays the progress of the list item (optional)

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The view for a single list item.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.itemTitle);
            progressTextView = itemView.findViewById(R.id.itemProgress);
        }
    }
}
