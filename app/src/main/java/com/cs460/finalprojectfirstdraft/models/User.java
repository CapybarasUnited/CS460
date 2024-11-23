package com.cs460.finalprojectfirstdraft.models;

import com.cs460.finalprojectfirstdraft.utilities.Constants;

import java.util.HashMap;

//Not really sure if this class is needed!
public class User {
    private int userId;
    private String first_name, last_name, email, password;

    public User(String first_name, String last_name, String email, String password) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }
}
