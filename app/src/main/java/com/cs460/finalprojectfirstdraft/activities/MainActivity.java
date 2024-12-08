package com.cs460.finalprojectfirstdraft.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs460.finalprojectfirstdraft.adapter.ItemAdapter;
import com.cs460.finalprojectfirstdraft.listeners.ItemListener;
import com.cs460.finalprojectfirstdraft.models.Item;
import com.cs460.finalprojectfirstdraft.models.ListItem;
import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.adapter.RecyclerViewAdapter;
import com.cs460.finalprojectfirstdraft.databinding.ActivityMainBinding;
import com.cs460.finalprojectfirstdraft.models.RecyclerViewItem;
import com.cs460.finalprojectfirstdraft.models.UserList;
import com.cs460.finalprojectfirstdraft.utilities.CurrentUser;
import com.cs460.finalprojectfirstdraft.utilities.FirebaseHelper;

import java.util.ArrayList;

/**
 * MainActivity serves as the home screen of the application, displaying a list of user-defined tasks
 * using a RecyclerView and providing navigation to create new lists.
 */
public class MainActivity extends AppCompatActivity implements ItemListener {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private ArrayList<UserList> lists;
    private ActivityMainBinding binding;
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
        Log.d("Debug","1");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        Log.d("Debug","2");
        firebaseHelper = new FirebaseHelper();
        Log.d("Debug","3");
        setContentView(binding.getRoot());
        Log.d("Debug","4");
        String newTitle = "Welcome " + CurrentUser.getCurrentUser().getFirstName() + " " + CurrentUser.getCurrentUser().getLastName() + "!";
        Log.d("Debug","5");
        binding.title.setText(newTitle);
        Log.d("Debug","6");
        getLists();
        Log.d("Debug","7");
        setListeners();
        Log.d("Debug","8");
    }

    private void getLists() {
        lists = new ArrayList<>(firebaseHelper.getUserListsbyParentID(null));
        Log.d("Debug","num lists pulled " + lists.size());
        if(!lists.isEmpty()){
            adapter = new ItemAdapter(lists, null, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
        }else{
            showNoListMessage();
        }
    }

    /**
     * Sets up the Floating Action Button (FAB) to navigate to the NewListActivity
     * when clicked.
     */
    private void setListeners() {
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Debug", "fabClicked");
                // Navigate to the NewListActivity
                Intent intent = new Intent(getApplicationContext(), NewListActivity.class);
                intent.putExtra("PARENT_LIST_ID", (String) null);
                Log.d("Debug","intent created");
                startActivity(intent);
                Log.d("Debug","Activity Started");
                finish();
                Log.d("Debug","finished");
            }
        });
    }

    private void showNoListMessage(){

        binding.recyclerView.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);
        binding.textNoLists.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClicked(RecyclerViewItem item) {
        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        intent.putExtra("LIST_ID", item.listID);
        startActivity(intent);
        finish();
    }
}
