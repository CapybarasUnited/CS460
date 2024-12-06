package com.cs460.finalprojectfirstdraft.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.graphics.Paint;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs460.finalprojectfirstdraft.R;

import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    private List<String> checklistItems;
    private OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public ChecklistAdapter(List<String> checklistItems, OnDeleteClickListener deleteClickListener) {
        this.checklistItems = checklistItems;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checklist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = checklistItems.get(position);
        holder.itemText.setText(item);

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> deleteClickListener.onDeleteClick(position));

        // Handle item click to toggle strikethrough
        holder.itemText.setOnClickListener(v -> {
            boolean isCompleted = (holder.itemText.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) != 0;
            if (isCompleted) {
                holder.itemText.setPaintFlags(holder.itemText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.itemText.setPaintFlags(holder.itemText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        });
    }

    @Override
    public int getItemCount() {
        return checklistItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemText;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.itemText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
