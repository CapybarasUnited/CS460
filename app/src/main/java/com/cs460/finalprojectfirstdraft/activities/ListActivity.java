package com.cs460.finalprojectfirstdraft.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.adapter.ItemAdapter;
import com.cs460.finalprojectfirstdraft.databinding.ActivityListBinding;
import com.cs460.finalprojectfirstdraft.listeners.ItemListener;
import com.cs460.finalprojectfirstdraft.models.Entry;
import com.cs460.finalprojectfirstdraft.models.RecyclerViewItem;
import com.cs460.finalprojectfirstdraft.models.UserList;
import com.cs460.finalprojectfirstdraft.utilities.CurrentUser;
import com.cs460.finalprojectfirstdraft.utilities.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListActivity extends AppCompatActivity implements ItemListener {

    private ActivityListBinding binding;
    private boolean showDeleteIcon = false; // Tracks the visibility of the delete icon

    private FirebaseHelper firebaseHelper;
    private Bundle extras;
    private String listID;
    private UserList thisList;
    private boolean addingEntries;
    private boolean addPanelVisible;

    private ItemAdapter adapter;
    private ArrayList<UserList> lists;
    private ArrayList<Entry> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        extras = getIntent().getExtras();
        listID = extras.getString("LIST_ID");
        thisList = new UserList();
        getListContext(listID);

        getListsAndEntries();
        setListeners();

        Log.d("Debug", "list ID in List Activity: " + listID);
        addingEntries = false;
        addPanelVisible = false;
    }

    private void getListsAndEntries() {
        loading(true);

        lists = new ArrayList<>();
        entries = new ArrayList<>();

        FirebaseHelper.retrieveAllSubLists(CurrentUser.getCurrentUser().getEmail(), listID, new OnCompleteListener<ArrayList<UserList>>() {
            @Override
            public void onComplete(@NonNull Task<ArrayList<UserList>> task) {
                lists = task.getResult();
                FirebaseHelper.retrieveEntries(listID, new OnCompleteListener<ArrayList<Entry>>() {
                    @Override
                    public void onComplete(@NonNull Task<ArrayList<Entry>> task) {
                            entries = task.getResult();
                            adapter = new ItemAdapter(lists, entries, ListActivity.this);
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(ListActivity.this));
                            binding.recyclerView.setAdapter(adapter);
                    }
                });
            }
        });
        binding.progressBar.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
    }

    private void setListeners() {
        // Existing Floating Action Button functionality
        binding.fabAdd.setOnClickListener(view -> {
            if (addingEntries) {
                addingEntries = false;
                binding.editTextAddEntry.setText(null);
                binding.editTextAddEntry.setVisibility(View.GONE);
                binding.fabAdd.setImageDrawable(getDrawable(R.drawable.ic_add));
            } else {
                if (addPanelVisible) {
                    addPanelVisible = false;
                    binding.fabAdd.setImageDrawable(getDrawable(R.drawable.ic_add));
                    binding.addPanel.setVisibility(View.GONE);
                    binding.btnAddEntry.setVisibility(View.GONE);
                    binding.btnAddSublist.setVisibility(View.GONE);
                } else {
                    addPanelVisible = true;
                    binding.fabAdd.setImageDrawable(getDrawable(R.drawable.ic_down_arrow));
                    binding.addPanel.setVisibility(View.VISIBLE);
                    binding.btnAddEntry.setVisibility(View.VISIBLE);
                    binding.btnAddSublist.setVisibility(View.VISIBLE);
                }
            }
        });

        // Listener for the banner button to toggle delete icon
        binding.bannerButton.setOnClickListener(view -> {
            showDeleteIcon = !showDeleteIcon; // Toggle the visibility state
            if (adapter != null) {
                adapter.setShowDeleteIcon(showDeleteIcon); // Update the adapter
            }
        });

        // Add entry
        binding.btnAddEntry.setOnClickListener(view -> {
            addingEntries = true;
            addPanelVisible = false;
            binding.addPanel.setVisibility(View.GONE);
            binding.btnAddEntry.setVisibility(View.GONE);
            binding.btnAddSublist.setVisibility(View.GONE);
            binding.editTextAddEntry.setVisibility(View.VISIBLE);
            binding.fabAdd.setImageDrawable(getDrawable(R.drawable.ic_check));
        });

        // Add sublist
        binding.btnAddSublist.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), NewListActivity.class);
            intent.putExtra("PARENT_LIST_ID", listID);
            startActivity(intent);
            finish();
        });

        // Add settings icon click listener
        binding.settingsIcon.setOnClickListener(view -> showSettingsMenu(view));

        // Handle "Enter" key in the entry text field
        binding.editTextAddEntry.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                Entry entry = new Entry(null, listID, binding.editTextAddEntry.getText().toString(), false);
                entries.add(entry);
                adapter = new ItemAdapter(lists, entries, ListActivity.this);
                binding.recyclerView.setAdapter(adapter);
                binding.editTextAddEntry.setText(null);
                showToast("Entry Added");


                FirebaseHelper.addEntry(entry, listID, task -> {
                    if (task.isSuccessful()) {
                        showToast("Entry Added");
                    } else {
                        showToast("Failed to add entry");
                    }
                });
//                entries = new ArrayList<>();
//                FirebaseHelper.retrieveEntries(listID, new OnCompleteListener<ArrayList<Entry>>() {
//                    @Override
//                    public void onComplete(@NonNull Task<ArrayList<Entry>> task) {
//                        entries = task.getResult();
//                        entries.add(entry);
//                        adapter = new ItemAdapter(lists, entries, ListActivity.this);
//                        binding.recyclerView.setAdapter(adapter);
//                        binding.editTextAddEntry.setText(null);
//                    }
//                });


                return true;
            }
            else {
                return false;
            }
        });
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


    private void toggleDeleteOption() {
        showDeleteIcon = !showDeleteIcon;
        if (adapter != null) {
            adapter.setShowDeleteIcon(showDeleteIcon);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemClicked(RecyclerViewItem recyclerViewItem) {
        Log.d("Debug", thisList.getListName());
        if(thisList.getIsDelete()){
            Log.d("Debug", "is Delete");
        }else{
            Log.d("Debug", "is not Delete");
        }
        if(thisList.getIsChecklist()){
            Log.d("Debug", "is Checklist");
        }else{
            Log.d("Debug", "is not Checklist");
        }

        if (!recyclerViewItem.isList) {
            Log.d("Debug","Is List");
            if(showDeleteIcon || thisList.getIsDelete()){
                Log.d("Debug","Is Delete");

                FirebaseHelper.deleteEntry(listID, recyclerViewItem.id, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        getListsAndEntries();
                    }
                });
            }else if(thisList.getIsChecklist()){
                //Log.d("Debug","Is Checklist");
                Map<String, Object> changes = new HashMap<>();
                changes.put("isChecked", !recyclerViewItem.isChecked);
                FirebaseHelper.updateEntry(listID, recyclerViewItem.id, changes, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        getListsAndEntries();
                    }
                });
            }
        } else if(showDeleteIcon) {
            FirebaseHelper.deleteList(recyclerViewItem.id, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    getListsAndEntries();
                }
            });
        }else{
            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
            intent.putExtra("LIST_ID", recyclerViewItem.id);
            startActivity(intent);
            finish();
        }
    }
}
