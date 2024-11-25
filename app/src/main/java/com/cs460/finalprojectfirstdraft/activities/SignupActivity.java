package com.cs460.finalprojectfirstdraft.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cs460.finalprojectfirstdraft.R;
import com.cs460.finalprojectfirstdraft.databinding.ActivitySignupBinding;
import com.cs460.finalprojectfirstdraft.utilities.Constants;
import com.cs460.finalprojectfirstdraft.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

//TODO
//add last name to items added to database and pref manager
//use FirebaseHelper instead of in-class methods to store stuff in database


public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private String encodeImage;
    private PreferenceManager preferenceManager;

    /**
     * Initialization method
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        preferenceManager = new PreferenceManager(getApplicationContext());
    }

    /**
     * Set listeners for the buttons in this activity
     */
    private void setListeners() {
        binding.buttonBack.setOnClickListener(v ->
                onBackPressed());

        binding.buttonSignUp.setOnClickListener(v -> {
            if(isValidSignUpDetails()) {
                signUp();
            }
        });
    }

    /**
     * Show a toast message
     * @param message String message to show
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Create a new account given the data stored in the EditTexts and ImageView
     */
    private void signUp() {
        //check loading
        loading(true);

        //post to firestore
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, String> user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.editTextFirstName.getText().toString());
        user.put(Constants.KEY_NAME_LAST, binding.editTextLastName.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.editTextEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.editTextPassword.getText().toString());

        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_NAME, binding.editTextFirstName.getText().toString());
                    preferenceManager.putString(Constants.KEY_NAME_LAST, binding.editTextLastName.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }).addOnFailureListener(exception -> {
                    loading(false);
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Make sure the user has entered everything necessary to create a new account,
     * and send a toast message if there is something missing
     * @return boolean true/false depending on the state of the elements
     */
    private boolean isValidSignUpDetails() {
        if(binding.editTextFirstName.getText().toString().trim().isEmpty()) {
            showToast("Please enter your first name");
            return false;
        }
        else if(binding.editTextLastName.getText().toString().trim().isEmpty()) {
            showToast("Please enter your last name");
            return false;
        }
        else if(binding.editTextEmail.getText().toString().trim().isEmpty()) {
            showToast("Please enter your email address");
            return false;
        }

        //make sure the email follows the valid email pattern
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.editTextEmail.getText().toString()).matches()) {
            showToast("Please enter a valid email address");
            return false;
        }
        else if(binding.editTextPassword.getText().toString().trim().isEmpty()) {
            showToast("Please enter your password");
            return false;
        }
        else if(binding.editTextReenterPassword.getText().toString().trim().isEmpty()) {
            showToast("Please confirm your password");
            return false;
        }

        //make sure the password is equal to the confirm password
        if(!binding.editTextPassword.getText().toString().equals(binding.editTextReenterPassword.getText().toString())) {
            showToast("the two passwords do not match!");
            return false;
        }

        return true;
    }

    /**
     * Hide/show the sign up button and progress bar depending on what the app is doing
     * @param isLoading boolean true/false depending on if the app is loading or not
     */
    private void loading (Boolean isLoading) {
        if(isLoading) {
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.buttonSignUp.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}