package com.cs460.finalprojectfirstdraft;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity class for displaying a RecyclerView with a list of items.
 * This class demonstrates the use of a RecyclerView to display a list of {@link ListItem} objects.
 */
public class RecyclerViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<ListItem> itemList;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        // Initialize the RecyclerView
        initializeRecyclerView();
    }

    /**
     * Initializes the RecyclerView by setting up its layout manager, adapter, and populating
     * it with a list of sample data.
     */
    private void initializeRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize the list and add some sample data
        itemList = new ArrayList<>();
        itemList.add(new ListItem("To Do", "Task", null));
        itemList.add(new ListItem("Shopping", "Shopping", null));
        itemList.add(new ListItem("Pixar Movies", "Movies", 34)); // 34% progress

        // Set up the RecyclerView with the adapter
        adapter = new RecyclerViewAdapter(itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
