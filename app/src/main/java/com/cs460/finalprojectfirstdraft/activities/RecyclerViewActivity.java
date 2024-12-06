package com.cs460.finalprojectfirstdraft.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs460.finalprojectfirstdraft.models.ListItem;
import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.adapter.RecyclerViewAdapter;
import com.cs460.finalprojectfirstdraft.models.ListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Activity class for displaying a RecyclerView with a list of items.
 * This class demonstrates the use of a RecyclerView to display a list of {@link ListItem} objects.
 */
public class RecyclerViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<ListItem> itemList;
    private HashMap<String, ArrayList<String>> checklistMap;

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
     * Initializes the checklist map with predefined checklists for each list item.
     */
    private void initializeRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);

        // Set up the RecyclerView with the adapter
        adapter = new RecyclerViewAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
