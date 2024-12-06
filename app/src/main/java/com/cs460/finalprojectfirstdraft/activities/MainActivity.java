package com.cs460.finalprojectfirstdraft.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs460.finalprojectfirstdraft.models.ListItem;
import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.adapter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity serves as the home screen of the application, displaying a list of user-defined tasks
 * using a RecyclerView and providing navigation to create new lists.
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<ListItem> itemList;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the RecyclerView and populate it with data
        initializeRecyclerView();

        // Set up the Floating Action Button to navigate to NewListActivity
        setupFloatingActionButton();
    }

    /**
     * Initializes the RecyclerView by setting up the adapter, layout manager,
     * and populating it with sample data.
     */
    private void initializeRecyclerView() {
        // Reference the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize the list and add some sample data
        itemList = new ArrayList<>();
        itemList.add(new ListItem("To Do", "Task", 0));
        itemList.add(new ListItem("Shopping", "Shopping", 0));
        itemList.add(new ListItem("Pixar Movies", "Movies", 34)); // Progress is 34%

        // Set up the RecyclerView with the adapter and click listener
        adapter = new RecyclerViewAdapter(itemList, position -> {
            ListItem clickedItem = itemList.get(position);

            if (clickedItem.getType().equals("Task")) {
                // Example: Navigate to another activity
                Intent intent = new Intent(MainActivity.this, ChecklistActivity.class);
                intent.putExtra("itemTitle", clickedItem.getTitle());
                startActivity(intent);
            } else {
                // Example: Mark an item as completed by updating its progress
                clickedItem.setProgress(100);
                adapter.notifyItemChanged(position);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Sets up the Floating Action Button (FAB) to navigate to the NewListActivity
     * when clicked.
     */
    private void setupFloatingActionButton() {
        findViewById(R.id.fab).setOnClickListener(view -> {
            // Navigate to the NewListActivity
            startActivity(new Intent(MainActivity.this, NewListActivity.class));
        });
    }
}
