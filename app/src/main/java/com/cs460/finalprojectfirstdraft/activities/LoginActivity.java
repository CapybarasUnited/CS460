package com.cs460.finalprojectfirstdraft.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cs460.finalprojectfirstdraft.utilities.Constants;
import com.cs460.finalprojectfirstdraft.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

//TODO
//remove reference to profile image as we will not be using one
//use FirebaseHelper instead of in-class methods to check stuff in database

/**
 * Controller class for the sign in activity
 */
public class LoginActivity extends AppCompatActivity {

    private com.cs460.finalprojectfirstdraft.databinding.ActivityLoginBinding binding;
    private PreferenceManager preferenceManager;

    /**
     * Initialization method
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = com.cs460.finalprojectfirstdraft.databinding.ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    /**
     * Set listeners for the buttons in this activity
     */
    private void setListeners() {
        binding.textViewSignUp.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignupActivity.class)));

        binding.buttonSignIn.setOnClickListener(v -> {
            if(isValidSignInDetails()) {
                signIn();
            }
        });

        //press enter while in password field to login
        binding.editTextPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null &&
                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                binding.buttonSignIn.performClick();  // Simulate button click
                return true;
            }
            return false;
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
     * Sign in the user given the text the user has entered, checks to make sure the user provided
     * credentials are present in the database. As per the requirements this method will show a toast
     * for both successful and failed attempts
     */
    private void signIn() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.editTextEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.editTextPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        showToast("Successful Sign-in!");
                    }
                    else {
                        loading(false);
                        showToast("Email or password are incorrect, or account doesn't exist");
                    }
                });
    }

    /**
     * Make sure the user has entered the right data to allow for a login attempt
     * @return boolean true/false depending on if the user can try logging in
     */
    private boolean isValidSignInDetails() {
        if(binding.editTextEmail.getText().toString().trim().isEmpty()) {
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

        return true;
    }

    /**
     * Hide/show the sign up button and progress bar depending on what the app is doing
     * @param isLoading boolean true/false depending on if the app is loading or not
     */
    private void loading (Boolean isLoading) {
        if(isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}