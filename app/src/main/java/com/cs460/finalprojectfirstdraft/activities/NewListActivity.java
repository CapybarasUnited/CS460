package com.cs460.finalprojectfirstdraft.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.databinding.ActivityNewListBinding;
import com.cs460.finalprojectfirstdraft.models.List;
import com.cs460.finalprojectfirstdraft.models.UserList;
import com.cs460.finalprojectfirstdraft.utilities.Constants;
import com.cs460.finalprojectfirstdraft.utilities.FirebaseHelper;
import com.google.firebase.firestore.DocumentReference;
import com.cs460.finalprojectfirstdraft.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;

import java.lang.reflect.Array;
import java.util.HashMap;

public class NewListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ActivityNewListBinding binding;
    private ArrayAdapter adapter;
    private Boolean isChecklist;
    private String color;

    private String name;
    private Boolean deleteWhenChecked;
    private Boolean typeSelected;
    private Boolean colorSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = ArrayAdapter.createFromResource(this, R.array.color_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerColorPicker.setAdapter(adapter);
        binding.spinnerColorPicker.setOnItemSelectedListener(this);
        typeSelected = false;
        colorSelected = false;
        isChecklist = false;
        deleteWhenChecked = false;
        setListeners();
    }

    private void setListeners() {
        binding.checkListButton.setOnClickListener(view -> {
            if (!binding.checkListButton.isActivated()) {
                typeSelected = true;
                binding.checkListButton.setBackgroundResource(R.drawable.background_type_button_pressed_left);
                binding.standardButton.setBackgroundResource(R.drawable.background_type_button_unpressed_right);
                isChecklist = true;
                binding.layoutDeleteWhenChecked.setVisibility(View.VISIBLE);
                checkIfComplete();
            }

        });
        binding.standardButton.setOnClickListener(view -> {
            if (!binding.standardButton.isActivated()) {
                typeSelected = true;
                binding.checkListButton.setBackgroundResource(R.drawable.background_type_button_unpressed_left);
                binding.standardButton.setBackgroundResource(R.drawable.background_type_button_pressed_right);
                isChecklist = false;
                binding.layoutDeleteWhenChecked.setVisibility(View.GONE);
                checkIfComplete();
            }
        });

        binding.inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkIfComplete();
            }
        });

        binding.layoutDeleteWhenChecked.setOnClickListener(view -> {
            if (deleteWhenChecked) {
                deleteWhenChecked = false;
            } else {
                deleteWhenChecked = true;
            }
        });
        //create a list
        binding.createListButton.setOnClickListener(view -> {

            deleteList(); //delete
            updateList(); //delete

        });
    }



    /**
     * hardcoded example -- delete
     */
    private void deleteList() {
        //to delete call firestoreHelper deleteList method with listId/document id
        FirebaseHelper.deleteList("Mvcz8EvoUWYbSvnUKmdY", task -> {
            if (task.isSuccessful()) {
                showToast("List was deleted successfully");
                finish();
            } else {
                showToast("Error communicating with database. Try again");
            }
        });
    }

    /**
     * hardcoded example -- delete
     */
    private void updateList() {
        //create a hashmap for updates
        HashMap<String, Object> hashMap = new HashMap<>();

        //put field name and new value for the update, ex: changing list name to Books
        hashMap.put("listName", "Books");
        //call firebaseHelper updateList method
        FirebaseHelper.updateList("Ux6dlJ5hiUpW9SO757tf", hashMap, task -> {
            if (task.isSuccessful()) {
                showToast("List was updated successfully");
                finish();
            } else {
                showToast("Error communicating with database. Try again");
            }
        });
    }
    //skeleton method - delete
    private void retrieveAllLists(){
        String email = "email";

        FirebaseHelper.retrieveAllLists(email, task -> {
            if(task.isSuccessful()) {
                java.util.List<UserList> userLists = task.getResult();
                for (UserList list : userLists) {
                    Log.d("retrieveAllLists: ", list.toString());
                    //add each list to the recycler view

                }
            } else {
                showToast("error");
            }
        });
    }



    private void checkIfComplete() {
        if(typeSelected && colorSelected && !binding.inputName.getText().toString().isEmpty()){
            name = binding.inputName.getText().toString();
            //binding.createListButton.setBackgroundColor(Integer.parseInt(color));
            binding.createListButton.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        color = adapterView.getItemAtPosition(i).toString();
        showToast(color);
        colorSelected = true;
        checkIfComplete();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        binding.createListButton.setVisibility(View.GONE);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}