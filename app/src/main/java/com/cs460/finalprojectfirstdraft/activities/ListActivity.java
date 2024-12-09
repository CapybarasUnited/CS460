package com.cs460.finalprojectfirstdraft.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.adapter.ItemAdapter;
import com.cs460.finalprojectfirstdraft.adapter.RecyclerViewAdapter;
import com.cs460.finalprojectfirstdraft.databinding.ActivityListBinding;
import com.cs460.finalprojectfirstdraft.listeners.ItemListener;
import com.cs460.finalprojectfirstdraft.models.Entry;
import com.cs460.finalprojectfirstdraft.models.ListItem;
import com.cs460.finalprojectfirstdraft.models.RecyclerViewItem;
import com.cs460.finalprojectfirstdraft.models.UserList;
import com.cs460.finalprojectfirstdraft.utilities.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements ItemListener {

    private ActivityListBinding binding;
    private boolean showDeleteIcon = false; // Tracks the visibility of the delete icon

    private FirebaseHelper firebaseHelper;
    private Bundle extras;
    private String listID;
    private boolean isChecklist;
    private boolean deleteWhenChecked;
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

        extras = getIntent().getExtras();
        listID = extras.getString("LIST_ID");
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

        binding.btnAddEntry.setOnClickListener(view -> {
            addingEntries = true;
            addPanelVisible = false;
            binding.addPanel.setVisibility(View.GONE);
            binding.btnAddEntry.setVisibility(View.GONE);
            binding.btnAddSublist.setVisibility(View.GONE);
            binding.editTextAddEntry.setVisibility(View.VISIBLE);
            binding.fabAdd.setImageDrawable(getDrawable(R.drawable.ic_check));
        });

        binding.btnAddSublist.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), NewListActivity.class);
            intent.putExtra("PARENT_LIST_ID", listID);
            startActivity(intent);
            finish();
        });

        binding.editTextAddEntry.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                    Entry entry = new Entry(null, listID, binding.editTextAddEntry.getText().toString(), false);
                    FirebaseHelper.addEntry(entry, listID, task -> {
                        if (task.isSuccessful()) {
                            showToast("Entry Added");
                        } else {
                            showToast("Failed to add entry");
                        }
                    });
                    // Add entry to RecyclerView
                    entries.add(entry);
                    adapter = new ItemAdapter(lists, entries, ListActivity.this);
                    binding.recyclerView.setAdapter(adapter);
                    binding.editTextAddEntry.setText(null);
                    return true;
                } else {
                    return false;
                }
            }
        });
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
