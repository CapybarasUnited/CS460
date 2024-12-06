package com.cs460.finalprojectfirstdraft.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.adapter.RecyclerViewAdapter;
import com.cs460.finalprojectfirstdraft.models.UserList;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ArrayList<UserList> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Step 1: Bind RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        // Step 2: Initialize data for the RecyclerView
        itemList = new ArrayList<>();
//        itemList.add(new UserList("Shopping List", "List", null));
//        itemList.add(new UserList("Groceries", "Entry", null));
//        itemList.add(new UserList("Pixar Movies", "Entry", 34)); // 34% progress

        // Step 3: Set up RecyclerView
        adapter = new RecyclerViewAdapter(itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Linear Layout
        recyclerView.setAdapter(adapter);
    }
}

