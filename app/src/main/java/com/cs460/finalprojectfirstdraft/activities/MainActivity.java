package com.cs460.finalprojectfirstdraft.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs460.finalprojectfirstdraft.models.Item;
import com.cs460.finalprojectfirstdraft.models.ListItem;
import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.adapter.RecyclerViewAdapter;
import com.cs460.finalprojectfirstdraft.databinding.ActivityMainBinding;
import com.cs460.finalprojectfirstdraft.models.List;
import com.cs460.finalprojectfirstdraft.utilities.CurrentUser;
import com.cs460.finalprojectfirstdraft.utilities.FirebaseHelper;

import java.util.ArrayList;

/**
 * MainActivity serves as the home screen of the application, displaying a list of user-defined tasks
 * using a RecyclerView and providing navigation to create new lists.
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ArrayList<ListItem> itemsToAdd;
    private ActivityMainBinding binding;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String newTitle = "Welcome " + CurrentUser.getCurrentUser().getFirstName() + " " + CurrentUser.getCurrentUser().getLastName() + "!";
        binding.title.setText(newTitle);

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
        recyclerView = binding.recyclerView;

        // Initialize the list and add some sample data
        List currentRootList = FirebaseHelper.getRootList();
        ArrayList<Item> items = FirebaseHelper.getItemsWithParentListId(currentRootList.getListID());

//        itemList.add(new ListItem("To Do", "Task", null));
//        itemList.add(new ListItem("Shopping", "Shopping", null));
//        itemList.add(new ListItem("Pixar Movies", "Movies", 34)); // Progress is 34%

        // Set up the RecyclerView with the adapter
        adapter = new RecyclerViewAdapter(items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Sets up the Floating Action Button (FAB) to navigate to the NewListActivity
     * when clicked.
     */
    private void setupFloatingActionButton() {
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the NewListActivity
                startActivity(new Intent(MainActivity.this, NewListActivity.class));
            }
        });
    }
}
