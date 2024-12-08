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
        Log.d("Debug","7.1");
        lists = firebaseHelper.getRootLists();
        Log.d("Debug","7.2");
        if(!lists.isEmpty()){
            Log.d("Debug","7.2.1");
            adapter = new ItemAdapter(lists, null, this);
            Log.d("Debug","7.2.2");
            recyclerView.setAdapter(adapter);
            Log.d("Debug","7.2.3");
            recyclerView.setVisibility(View.VISIBLE);
            Log.d("Debug","7.2.4");
        }else{
            Log.d("Debug","7.3");
            showNoListMessage();
            Log.d("Debug","7.3.1");
        }
        Log.d("Debug","7.4");
    }

    /**
     * Sets up the Floating Action Button (FAB) to navigate to the NewListActivity
     * when clicked.
     */
    private void setListeners() {
        Log.d("Debug","8.1");
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the NewListActivity
                Intent intent = new Intent(getApplicationContext(), NewListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Log.d("Debug","8.2");
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
