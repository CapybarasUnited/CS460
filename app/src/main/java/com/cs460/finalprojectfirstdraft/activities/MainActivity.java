package com.cs460.finalprojectfirstdraft.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.adapter.RecyclerViewAdapter;
import com.cs460.finalprojectfirstdraft.databinding.ActivityMainBinding;
import com.cs460.finalprojectfirstdraft.models.UserList;
import com.cs460.finalprojectfirstdraft.utilities.CurrentUser;
import com.cs460.finalprojectfirstdraft.utilities.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity serves as the home screen of the application, displaying a list of user-defined tasks
 * using a RecyclerView and providing navigation to create new lists.
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
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

         //Initialize the list and add some sample data

        ArrayList<UserList> userLists = new ArrayList<>();

//        FirebaseHelper.retrieveAllLists(CurrentUser.getCurrentUser().getEmail(), new OnCompleteListener<List<UserList>>() {
//            @Override
//            public void onComplete(@NonNull Task<List<UserList>> task) {
//                while(task.getResult().iterator().hasNext()) {
//                    userLists.add(task.getResult().iterator().next());
//                }
//            }
//        });
//
//        for (UserList ul : userLists.iterator()) {
//            System.out.println(ul.toString());
//        }
//
//         //Set up the RecyclerView with the adapter
//        adapter = new RecyclerViewAdapter(userLists);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(adapter);
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
