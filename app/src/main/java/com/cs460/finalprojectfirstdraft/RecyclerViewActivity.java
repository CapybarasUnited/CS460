package com.cs460.finalprojectfirstdraft;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity class for displaying a RecyclerView with a list of items.
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

        recyclerView = findViewById(R.id.recyclerView);

        // Initialize the list and add some sample data
        itemList = new ArrayList<>();
        itemList.add(new ListItem("Item 1"));
        itemList.add(new ListItem("Item 2"));
        itemList.add(new ListItem("Item 3"));
        itemList.add(new ListItem("Item 4"));

        // Set up the RecyclerView with the adapter
        adapter = new RecyclerViewAdapter(itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
