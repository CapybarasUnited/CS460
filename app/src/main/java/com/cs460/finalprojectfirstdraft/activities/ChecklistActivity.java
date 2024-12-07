package com.cs460.finalprojectfirstdraft.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.adapter.ChecklistAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChecklistActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChecklist;
    private ChecklistAdapter adapter;
    private List<String> checklistItems;
    private EditText inputNewItem;
    private ImageButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        // Retrieve the title and checklist items from the Intent
        String listTitle = getIntent().getStringExtra("listTitle");
        String[] receivedChecklistItems = getIntent().getStringArrayExtra("checklistItems");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(listTitle); // Set the title dynamically
        }

        // Initialize checklist items
        checklistItems = new ArrayList<>();
        if (receivedChecklistItems != null) {
            checklistItems.addAll(List.of(receivedChecklistItems));
        }

        // Initialize views
        recyclerViewChecklist = findViewById(R.id.recyclerViewChecklist);
        inputNewItem = findViewById(R.id.inputNewItem);
        addButton = findViewById(R.id.addButton);

        // Set up RecyclerView with ChecklistAdapter
        adapter = new ChecklistAdapter(checklistItems, this::onDeleteItem);
        recyclerViewChecklist.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChecklist.setAdapter(adapter);

        // Add button click listener
        addButton.setOnClickListener(v -> addNewItem());
    }

    /**
     * Adds a new item to the checklist.
     */
    private void addNewItem() {
        String newItem = inputNewItem.getText().toString().trim();
        if (!newItem.isEmpty()) {
            checklistItems.add(newItem);
            adapter.notifyItemInserted(checklistItems.size() - 1);
            inputNewItem.setText(""); // Clear input field
        }
    }

    /**
     * Deletes an item from the checklist.
     *
     * @param position The position of the item to delete.
     */
    private void onDeleteItem(int position) {
        checklistItems.remove(position);
        adapter.notifyItemRemoved(position);
    }
}
