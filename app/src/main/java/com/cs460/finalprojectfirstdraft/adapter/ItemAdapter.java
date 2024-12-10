package com.cs460.finalprojectfirstdraft.adapter;

//import static androidx.appcompat.graphics.drawable.DrawableContainerCompat.Api21Impl.getResources;

import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.databinding.ItemContainerItemBinding;
import com.cs460.finalprojectfirstdraft.listeners.ItemListener;
import com.cs460.finalprojectfirstdraft.models.Entry;
import com.cs460.finalprojectfirstdraft.models.RecyclerViewItem;
import com.cs460.finalprojectfirstdraft.models.UserList;
import com.cs460.finalprojectfirstdraft.utilities.FirebaseHelper;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{
    private ArrayList<RecyclerViewItem> items;

    private boolean showDeleteIcon = false; // Default state for the delete icon
    private ItemListener itemListener;
    public ItemAdapter(ArrayList<UserList> lists, ArrayList<Entry> entries, ItemListener itemListener){
        this.itemListener = itemListener;
        items = new ArrayList<>();

        //create RecyclerViewItems from the UserLists and Entries passed in

        for (int i = 0; i < lists.size();i++){
            RecyclerViewItem item = new RecyclerViewItem(lists.get(i), i);
            items.add(item);
        }

        for (int i = 0; i < entries.size(); i++){
            RecyclerViewItem item = new RecyclerViewItem(entries.get(i), i);
            items.add(item);
        }
    }

    public void setShowDeleteIcon(boolean showDeleteIcon) {
        this.showDeleteIcon = showDeleteIcon;
        notifyDataSetChanged(); // Refresh the RecyclerView
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerItemBinding itemContainerItemBinding = ItemContainerItemBinding
                .inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ItemViewHolder(itemContainerItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.setItemData(items.get(position));
    }

    @Override
    public int getItemCount() {return items.size();}

    class ItemViewHolder extends RecyclerView.ViewHolder{
        ItemContainerItemBinding binding;
        public ItemViewHolder(ItemContainerItemBinding itemContainerItemBinding) {
            super(itemContainerItemBinding.getRoot());
            binding = itemContainerItemBinding;
        }

        public void setItemData(RecyclerViewItem item) {
            binding.itemText.setText(item.text);

            // Toggle the visibility of the delete icon
            binding.deleteIcon.setVisibility(showDeleteIcon ? View.VISIBLE : View.GONE);

            if (!item.isList) {
                if (item.isChecked) {
                    binding.itemText.setPaintFlags(binding.itemText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    binding.itemText.setPaintFlags(binding.itemText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
            if (item.isList && item.isCheckList && !item.deleteWhenChecked) {
                binding.textPercent.setText(String.format("%d", item.percentChecked));
                binding.textPercent.setVisibility(View.VISIBLE);
                binding.textPercentSymbol.setVisibility(View.VISIBLE);
            }
            binding.getRoot().setOnClickListener(v -> itemListener.onItemClicked(item));
            setBackgroundColor(item);
        }


        public void setBackgroundColor(RecyclerViewItem item){
            switch (item.backgroundColor){
                case "White":
                    binding.itemContainer.setBackgroundColor(binding.itemContainer.getContext().getResources().getColor(R.color.white));
                    break;
                case "Red":
                    binding.itemContainer.setBackgroundColor(binding.itemContainer.getContext().getResources().getColor(R.color.red));
                    break;
                case "Orange":
                    binding.itemContainer.setBackgroundColor(binding.itemContainer.getContext().getResources().getColor(R.color.orange));
                    break;
                case "Yellow":
                    binding.itemContainer.setBackgroundColor(binding.itemContainer.getContext().getResources().getColor(R.color.yellow));
                    break;
                case "Green":
                    binding.itemContainer.setBackgroundColor(binding.itemContainer.getContext().getResources().getColor(R.color.green));
                    break;
                case "Blue":
                    binding.itemContainer.setBackgroundColor(binding.itemContainer.getContext().getResources().getColor(R.color.blue));
                    break;
                case "Purple":
                    binding.itemContainer.setBackgroundColor(binding.itemContainer.getContext().getResources().getColor(R.color.purple));
                    break;
                default:
                    binding.itemContainer.setBackgroundColor(binding.itemContainer.getContext().getResources().getColor(R.color.lightgray));

            }
        }
    }
}
