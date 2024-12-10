package com.cs460.finalprojectfirstdraft.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.adapter.ItemAdapter;
import com.cs460.finalprojectfirstdraft.databinding.ActivityMainBinding;
import com.cs460.finalprojectfirstdraft.listeners.ItemListener;
import com.cs460.finalprojectfirstdraft.models.Entry;
import com.cs460.finalprojectfirstdraft.models.UserList;
import com.cs460.finalprojectfirstdraft.models.RecyclerViewItem;
import com.cs460.finalprojectfirstdraft.utilities.CurrentUser;
import com.cs460.finalprojectfirstdraft.utilities.FirebaseHelper;

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
        loading(true);
        lists = new ArrayList<>();
        UserList userList1 = new UserList("1", "0", "Books", "Purple", false, false, "sam@sam.com");
        UserList userList2 = new UserList("1", "0", "Other Stuff", "Red", true, false, "sam@sam.com");
        lists.add(userList1);
        lists.add(userList2);

        if (!lists.isEmpty()) {
            adapter = new ItemAdapter(lists, new ArrayList<Entry>(), this);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            binding.recyclerView.setAdapter(adapter);
            loading(false);
            binding.recyclerView.setVisibility(View.VISIBLE);
        } else {
            showNoListMessage();
        }
    }

    /**
     * Sets up listeners for UI components.
     */
    private void setListeners() {
        // Floating Action Button (FAB) to navigate to NewListActivity
        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), NewListActivity.class);
            intent.putExtra("PARENT_LIST_ID", (String) null);
            startActivity(intent);
            finish();
        });

        // Open settings menu when the settings icon is clicked
        binding.settingsIcon.setOnClickListener(view -> showSettingsMenu(view));
    }

    /**
     * Shows a popup menu when the settings icon is clicked.
     *
     * @param view The view to attach the popup menu to.
     */
    private void showSettingsMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.settings_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.toggleDelete) {
                toggleDeleteOption();
                return true;
            }
            return false; // Default case for other menu items
        });

        popupMenu.show();
    }


    /**
     * Toggles the visibility of the delete icon in the list items.
     */
    private void toggleDeleteOption() {
        showDeleteIcon = !showDeleteIcon;
        if (adapter != null) {
            adapter.setShowDeleteIcon(showDeleteIcon);
        }
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showNoListMessage() {
        binding.textNoLists.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClicked(RecyclerViewItem item) {
        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        intent.putExtra("LIST_ID", item.id);
        startActivity(intent);
        finish();
    }
}
