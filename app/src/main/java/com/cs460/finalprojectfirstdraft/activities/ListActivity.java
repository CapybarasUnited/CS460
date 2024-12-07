package com.cs460.finalprojectfirstdraft.activities;

import android.content.Intent;
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
import com.cs460.finalprojectfirstdraft.adapter.RecyclerViewAdapter;
import com.cs460.finalprojectfirstdraft.databinding.ActivityListBinding;
import com.cs460.finalprojectfirstdraft.models.Entry;
import com.cs460.finalprojectfirstdraft.models.ListItem;
import com.cs460.finalprojectfirstdraft.utilities.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ActivityListBinding binding;
    private FirebaseHelper firebaseHelper;
    private Bundle extras;
    private String listID;
    private boolean isChecklist;
    private boolean deleteWhenChecked;
    private boolean addingEntries;
    private boolean addPanelVisible;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<ListItem> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();

        // Step 1: Bind RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        // Step 2: Initialize data for the RecyclerView
        itemList = new ArrayList<>();
        itemList.add(new ListItem("Shopping List", "List", 0));
        itemList.add(new ListItem("Groceries", "Entry", 0));
        itemList.add(new ListItem("Pixar Movies", "Entry", 34)); // 34% progress

        // Step 3: Set up RecyclerView
        adapter = new RecyclerViewAdapter(itemList, position -> {
            // Handle item clicks here (e.g., open new activity or mark item as completed)
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Linear Layout
        recyclerView.setAdapter(adapter);

        extras = getIntent().getExtras();
        listID = extras.getString("LIST_ID");
        //isChecklist = extras.getBoolean("IS_CHECKLIST");
        //deleteWhenChecked = extras.getBoolean("DELETE_WHEN_CHECKED");
        addingEntries = false;
        addPanelVisible = false;
    }

    private void setListeners() {
        binding.fabAdd.setOnClickListener(view -> {
            if(addingEntries){
                addingEntries = false;
                binding.editTextAddEntry.setText(null);
                binding.editTextAddEntry.setVisibility(View.GONE);
                binding.fabAdd.setImageDrawable(getDrawable(R.drawable.ic_add));
            }else {
                if(addPanelVisible) {
                    addPanelVisible = false;
                    binding.fabAdd.setImageDrawable(getDrawable(R.drawable.ic_add));
                    binding.addPanel.setVisibility(View.GONE);
                    binding.btnAddEntry.setVisibility(View.GONE);
                    binding.btnAddSublist.setVisibility(View.GONE);
                }else{
                    addPanelVisible = true;
                    binding.fabAdd.setImageDrawable(getDrawable(R.drawable.ic_down_arrow));
                    binding.addPanel.setVisibility(View.VISIBLE);
                    binding.btnAddEntry.setVisibility(View.VISIBLE);
                    binding.btnAddSublist.setVisibility(View.VISIBLE);
                }
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
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    Entry entry = new Entry(null, listID, binding.editTextAddEntry.getText().toString());
                    FirebaseHelper.addEntry(entry, listID, new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            showToast("Entry Added");
                        }
                    });
                    //(add entry to recyclerview)
                    binding.editTextAddEntry.setText(null);
                    return true;
                }else{
                    return false;
                }
            }
        });
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}