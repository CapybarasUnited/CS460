package com.cs460.finalprojectfirstdraft.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs460.finalprojectfirstdraft.databinding.ItemContainerItemBinding;
import com.cs460.finalprojectfirstdraft.listeners.ItemListener;
import com.cs460.finalprojectfirstdraft.models.Entry;
import com.cs460.finalprojectfirstdraft.models.RecyclerViewItem;
import com.cs460.finalprojectfirstdraft.models.UserList;
import com.cs460.finalprojectfirstdraft.utilities.FirebaseHelper;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{
    private ArrayList<RecyclerViewItem> items;
    private final ItemListener itemListener;
    public ItemAdapter(ArrayList<UserList> lists, ArrayList<Entry> entries, ItemListener itemListener){
        for (int i = 0; i < lists.size();i++){
            RecyclerViewItem item = new RecyclerViewItem(
                    true,
                    lists.get(i).getIsDelete(),
                    lists.get(i).getListName(),
                    lists.get(i).getListId(),
                    lists.get(i).getColor());
            items.add(item);
        }
        for (int i = 0; i < entries.size(); i++){
            RecyclerViewItem item = new RecyclerViewItem(
                    false,
                    false,
                    entries.get(i).getEntryContent(),
                    null,
                    "white");
            items.add(item);
        }
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerItemBinding itemContainerItemBinding = ItemContainerItemBinding
                .inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ItemViewHolder(itemContainerItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ItemViewHolder holder, int position) {
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
            if (item.isNormalChecklist){
                binding.textPercent.setText(item.percentChecked);
                binding.textPercent.setVisibility(View.VISIBLE);
                binding.textPercentSymbol.setVisibility(View.VISIBLE);
            }
        }
    }
}
