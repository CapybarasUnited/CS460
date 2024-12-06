package com.cs460.finalprojectfirstdraft.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.databinding.ActivityListBinding;
import com.cs460.finalprojectfirstdraft.utilities.FirebaseHelper;
import com.cs460.finalprojectfirstdraft.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

public class ListActivity extends AppCompatActivity {

    private ActivityListBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseHelper firebaseHelper;
    private Bundle extras;
    private String listID;
    private boolean isChecklist;
    private boolean deleteWhenChecked;
    private boolean addingEntries;
    private boolean addPanelVisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        extras = getIntent().getExtras();
        if (extras != null) {
            //listID = extras.getString("LIST_ID");
            //isChecklist = extras.getBoolean("IS_CHECKLIST");
            //deleteWhenChecked = extras.getBoolean("DELETE_WHEN_CHECKED");
        }
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
            //intent.putExtra("PARENT_LIST_ID", listID);
            startActivity(intent);
            finish();
        });

        binding.editTextAddEntry.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    //FirebaseHelper.addEntry(listID, binding.editTextAddEntry.getText().toString());
                    //(add entry to recyclerview)
                    binding.editTextAddEntry.setText(null);
                    return true;
                }else{
                    return false;
                }
            }
        });
    }


}