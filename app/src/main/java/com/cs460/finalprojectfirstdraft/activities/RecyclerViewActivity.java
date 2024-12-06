package com.cs460.finalprojectfirstdraft.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.adapter.RecyclerViewAdapter;
import com.cs460.finalprojectfirstdraft.models.ListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Activity class for displaying a RecyclerView with a list of items.
 */
public class RecyclerViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<ListItem> itemList;
    private HashMap<String, ArrayList<String>> checklistMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        initializeChecklistMap();
        initializeRecyclerView();
    }

    /**
     * Initializes the checklist map with predefined checklists for each list item.
     */
    private void initializeChecklistMap() {
        checklistMap = new HashMap<>();

        // Add default checklists for each list item
        checklistMap.put("To Do", new ArrayList<>());
        checklistMap.put("Shopping", new ArrayList<>());
        checklistMap.put("Pixar Movies", new ArrayList<>());

        // Add sample data for demonstration
        checklistMap.get("To Do").add("Grapes of Wrath");
        checklistMap.get("To Do").add("Frankenstein");
        checklistMap.get("Shopping").add("Milk");
        checklistMap.get("Shopping").add("Eggs");
        checklistMap.get("Pixar Movies").add("Toy Story");
        checklistMap.get("Pixar Movies").add("Finding Nemo");
    }

    private void initializeRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize the list and add some sample data
        itemList = new ArrayList<>();
        itemList.add(new ListItem("To Do", "Task", null));
        itemList.add(new ListItem("Shopping", "Shopping", null));
        itemList.add(new ListItem("Pixar Movies", "Movies", null));

        // Inside RecyclerViewActivity
        adapter = new RecyclerViewAdapter(itemList, position -> {
            ListItem clickedItem = itemList.get(position);

            if (checklistMap.containsKey(clickedItem.getTitle())) {
                Intent intent = new Intent(RecyclerViewActivity.this, ChecklistActivity.class);
                intent.putExtra("listTitle", clickedItem.getTitle());
                // Pass checklist items as an array
                intent.putExtra("checklistItems", checklistMap.get(clickedItem.getTitle()).toArray(new String[0]));
                startActivity(intent);
            } else {
                Toast.makeText(this, "No checklist found for: " + clickedItem.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);

    }
}
