package com.cs460.finalprojectfirstdraft.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.adapter.ItemAdapter;
import com.cs460.finalprojectfirstdraft.databinding.ActivityListBinding;
import com.cs460.finalprojectfirstdraft.listeners.ItemListener;
import com.cs460.finalprojectfirstdraft.models.Entry;
import com.cs460.finalprojectfirstdraft.models.RecyclerViewItem;
import com.cs460.finalprojectfirstdraft.models.UserList;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements ItemListener {

    private ActivityListBinding binding;
    private boolean showDeleteIcon = false; // Tracks the visibility of the delete icon

    private String listID;
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

        getListsAndEntries();
        setListeners();

        listID = getIntent().getExtras().getString("LIST_ID");
        addingEntries = false;
        addPanelVisible = false;
    }

    private void getListsAndEntries() {
        loading(true);

        lists = new ArrayList<>();
        entries = new ArrayList<>();

        UserList list1 = new UserList("1", listID, "Favorites", "Yellow", false, false, "sam@sam.com");
        lists.add(list1);
        Entry entry1 = new Entry("2", listID, "Hunger Games", true);
        Entry entry2 = new Entry("3", listID, "Lord of the Rings", false);
        entries.add(entry1);
        entries.add(entry2);

        adapter = new ItemAdapter(lists, entries, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        loading(false);
        binding.recyclerView.setVisibility(View.VISIBLE);
    }

    private void setListeners() {
        // Floating Action Button
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
                return true;
            }
            return false;
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
        if (!recyclerViewItem.isList) {
            entries.get(recyclerViewItem.position).setChecked(!recyclerViewItem.isChecked);
            adapter = new ItemAdapter(lists, entries, ListActivity.this);
            binding.recyclerView.setAdapter(adapter);
        } else {
            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
            intent.putExtra("LIST_ID", recyclerViewItem.id);
            startActivity(intent);
            finish();
        }
    }
}
