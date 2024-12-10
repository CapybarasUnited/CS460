package com.cs460.finalprojectfirstdraft.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs460.finalprojectfirstdraft.adapter.ItemAdapter;
import com.cs460.finalprojectfirstdraft.listeners.ItemListener;
import com.cs460.finalprojectfirstdraft.models.Entry;
import com.cs460.finalprojectfirstdraft.models.Item;
import com.cs460.finalprojectfirstdraft.models.ListItem;
import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.adapter.RecyclerViewAdapter;
import com.cs460.finalprojectfirstdraft.databinding.ActivityMainBinding;
import com.cs460.finalprojectfirstdraft.models.RecyclerViewItem;
import com.cs460.finalprojectfirstdraft.models.UserList;
import com.cs460.finalprojectfirstdraft.utilities.CurrentUser;
import com.cs460.finalprojectfirstdraft.utilities.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

/**
 * MainActivity serves as the home screen of the application, displaying a list of user-defined tasks
 * using a RecyclerView and providing navigation to create new lists.
 */
public class MainActivity extends AppCompatActivity implements ItemListener {

    private ItemAdapter adapter;
    private ArrayList<UserList> lists;
    private ActivityMainBinding binding;

    private boolean showDeleteIcon = false;
    private FirebaseHelper firebaseHelper;

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
        firebaseHelper = new FirebaseHelper();
        setContentView(binding.getRoot());
        String newTitle = "Welcome " + CurrentUser.getCurrentUser().getFirstName() + " " + CurrentUser.getCurrentUser().getLastName() + "!";
        binding.title.setText(newTitle);
        getLists();
        setListeners();
    }

    private void getLists() {
        lists = new ArrayList<>();
        loading(true);
        FirebaseHelper.retrieveAllSubLists(CurrentUser.getCurrentUser().getEmail(),null, new OnCompleteListener<ArrayList<UserList>>(){
                    @Override
                    public void onComplete(@NonNull Task<ArrayList<UserList>> task) {
                        lists = task.getResult();

                        if(!lists.isEmpty()){
                            Log.d("Debug", "Lists is not empty");
                            adapter = new ItemAdapter(lists, new ArrayList<Entry>(), MainActivity.this);
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            binding.recyclerView.setAdapter(adapter);
                            loading(false);
                            binding.recyclerView.setVisibility(View.VISIBLE);
                        }else{
                            loading(false);
                            showNoListMessage();
                        }
                    }
                });

//        UserList userList1 = new UserList("1",null, "Books", "Purple" , false, false, "sam@sam.com");
//        UserList userList2 = new UserList("1",null, "Other Stuff", "Red" , true, false, "sam@sam.com");
//        lists.add(userList1);
//        lists.add(userList2);

    }

    /**
     * Sets up the Floating Action Button (FAB) to navigate to the NewListActivity
     * when clicked.
     */
    private void setListeners() {
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the NewListActivity
                Intent intent = new Intent(getApplicationContext(), NewListActivity.class);
                intent.putExtra("PARENT_LIST_ID", (String) null);
                startActivity(intent);
                finish();
            }
        });

        binding.bannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteIcon = !showDeleteIcon; // Toggle visibility
                if (adapter != null) {
                    adapter.setShowDeleteIcon(showDeleteIcon); // Update the adapter
                }
            }
        });
    }


    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showNoListMessage(){
        binding.textNoLists.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClicked(RecyclerViewItem item) {
        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        Log.d("Debug", "List ID of " + item.text + ": " + item.id);
        intent.putExtra("LIST_ID", item.id);
        startActivity(intent);
        finish();
    }
}
