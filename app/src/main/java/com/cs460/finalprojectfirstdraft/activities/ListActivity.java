package com.cs460.finalprojectfirstdraft.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.PopupMenu;
import android.widget.TextView;
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
import java.util.Map;

public class ListActivity extends AppCompatActivity implements ItemListener {

    private ActivityListBinding binding;
    private boolean showDeleteIcon = false; // Tracks the visibility of the delete icon

    private Bundle extras;
    private String listID;
    private UserList thisList;
    private boolean addingEntries, addPanelVisible, unchecking;
    private int tracker, numChecked;

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
        tracker = 0;
        thisList = new UserList();
        numChecked = 0;
        getListContext(listID);

        getListsAndEntries();
        setListeners();

        Log.d("Debug", "list ID in List Activity: " + listID);
        addingEntries = false;
        addPanelVisible = false;
        unchecking = false;


    }

    private void getListContext(String listID){
        FirebaseHelper.retrieveOneList(CurrentUser.getCurrentUser().getEmail(), listID, new OnCompleteListener<UserList>() {
            @Override
            public void onComplete(@NonNull Task<UserList> task) {
                thisList.setListName(task.getResult().getListName());
                thisList.setColor(task.getResult().getColor());
                setHeaderColor(thisList.getColor());
                thisList.setParentListId(task.getResult().getParentListId());
                thisList.setIsChecklist(task.getResult().getIsChecklist());
                thisList.setIsDelete(task.getResult().getIsDelete());
                ready();

                if (thisList.getIsDelete()){
                    Log.d("Debug", thisList.getListName() + " is delete");
                }else{
                    Log.d("Debug", thisList.getListName() + " is not delete");
                }
            }
        });
    }

    private void getListsAndEntries() {
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

                            for (int i = 0; i < entries.size(); i++){
                                if (entries.get(i).getChecked()){
                                    numChecked++;
                                    if (unchecking){
                                        numChecked = 0;
                                        entries.get(i).setChecked(false);
                                        Map<String, Object> changes = new HashMap<>();
                                        changes.put("isChecked", false);
                                        FirebaseHelper.updateEntry(listID, entries.get(i).getEntryId(), changes, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {}
                                        });
                                    }
                                }
                            }
                            if (unchecking) {unchecking = false;}
                            adapter = new ItemAdapter(lists, entries, ListActivity.this);
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(ListActivity.this));
                            if (showDeleteIcon){adapter.setShowDeleteIcon(true);}
                            binding.recyclerView.setAdapter(adapter);

                            ready();
                    }
                });
            }
        });
    }

    private void updateTitle() {
        binding.title.setText(thisList.getListName());
    }

    private void ready(){
        tracker++;
        if (tracker >= 2){
            binding.progressBar.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        }

        updateTitle();
    }

    public void setHeaderColor(String color) {
        switch (color) {
            case "White":
                binding.header.setBackgroundColor(binding.header.getContext().getResources().getColor(R.color.white));
                break;
            case "Red":
                binding.header.setBackgroundColor(binding.header.getContext().getResources().getColor(R.color.red));
                break;
            case "Orange":
                binding.header.setBackgroundColor(binding.header.getContext().getResources().getColor(R.color.orange));
                break;
            case "Yellow":
                binding.header.setBackgroundColor(binding.header.getContext().getResources().getColor(R.color.yellow));
                break;
            case "Green":
                binding.header.setBackgroundColor(binding.header.getContext().getResources().getColor(R.color.green));
                break;
            case "Blue":
                binding.header.setBackgroundColor(binding.header.getContext().getResources().getColor(R.color.blue));
                break;
            case "Purple":
                binding.header.setBackgroundColor(binding.header.getContext().getResources().getColor(R.color.purple));
                break;
            default:
                binding.header.setBackgroundColor(binding.header.getContext().getResources().getColor(R.color.lightgray));

        }
    }

    private void setListeners() {
        // Floating Action Button
        binding.fabAdd.setOnClickListener(view -> {
            if (showDeleteIcon){
                toggleDeleteOption();
            }
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
        binding.settingsIcon.setOnClickListener(this::showSettingsMenu);

        //User editor action instead of key action, allows for the keyboard to persist after
        //the enter key press and actually allows the keyboard enter key to work.
        binding.editTextAddEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    Entry entry = new Entry(null, listID, binding.editTextAddEntry.getText().toString(), false);
                    FirebaseHelper.addEntry(entry, listID, task -> {
                        if (task.isSuccessful()) {
                            showToast("Entry Added");
                            if (showDeleteIcon){
                                toggleDeleteOption();
                            }
                        } else {
                            showToast("Failed to add entry");
                        }
                    });
                    entries = new ArrayList<>();
                    FirebaseHelper.retrieveEntries(listID, new OnCompleteListener<ArrayList<Entry>>() {
                        @Override
                        public void onComplete(@NonNull Task<ArrayList<Entry>> task) {
                            entries = task.getResult();
                            entries.add(entry);
                            adapter = new ItemAdapter(lists, entries, ListActivity.this);
                            binding.recyclerView.setAdapter(adapter);
                            binding.editTextAddEntry.setText(null);
                        }
                    });
                    return true; // Consume the event and keep the keyboard open
                }
                return false; // Allow default behavior if not handled
            }
        });

        //set up the back button to open this screen with the parent list instead
        binding.backIcon.setOnClickListener(view -> {
            Intent intent;
            if (thisList.getParentListId() != null){
                intent = new Intent(getApplicationContext(), ListActivity.class);
                intent.putExtra("LIST_ID", thisList.getParentListId());
            }else{
                intent = new Intent(getApplicationContext(), MainActivity.class);
            }
            startActivity(intent);
            finish();
        });

        //set up home button to take you to the home screen
        binding.homeIcon.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
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
        if (thisList.getIsChecklist() && !thisList.getIsDelete()) {
            popupMenu.getMenu().findItem(R.id.unCheckAll).setVisible(true);
        }else if (thisList.getIsChecklist()){
            popupMenu.getMenu().findItem(R.id.toggleDelete).setVisible(false);
        }

        popupMenu.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.toggleDelete) {
                        toggleDeleteOption();
                        return true;
                    }
                    else if (menuItem.getItemId() == R.id.unCheckAll){
                        unchecking = true;
                        getListsAndEntries();
                        return true;
                    }
                    else if (menuItem.getItemId() == R.id.logOut) {
                        CurrentUser.logout(this);
                        return true;
                    }
                    return false; // Default case for other menu items
                });

        popupMenu.show();
    }


    private void toggleDeleteOption() {
        showDeleteIcon = !showDeleteIcon;
        if (showDeleteIcon){
            Log.d("Debug", "delete icon shown");

        }else {
            Log.d("Debug", "delete icon not shown");
        }
        if (adapter != null) {
            adapter.setShowDeleteIcon(showDeleteIcon);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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

                FirebaseHelper.deleteEntry(listID, recyclerViewItem.id, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        getListsAndEntries();
                        if (adapter.getItemCount() == 0){
                            toggleDeleteOption();
                        }
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
