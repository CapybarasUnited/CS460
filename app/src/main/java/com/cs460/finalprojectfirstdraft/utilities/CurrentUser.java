package com.cs460.finalprojectfirstdraft.utilities;

import androidx.annotation.NonNull;

import com.cs460.finalprojectfirstdraft.models.User;

/**
 * Single instance class to make the current user a globally accessible object
 */
public class CurrentUser {
    private User user;
    private static CurrentUser currentUser;

    /**
     * Private constructor
     * @param user User instance created by login or sign up
     */
    private CurrentUser(User user) {
        this.user = user;
    }

    /**
     * Private method to get the currently logged in user
     * @return User currentUser
     */
    private User getUser() {
        return this.user;
    }

    /**
     * Set the currently logged in user
     * @param user User newUser
     */
    public static void setCurrentUser(User user) {
        if (currentUser == null) {
            currentUser = new CurrentUser(user);
        }
    }

    /**
     * Get the currently logged in user
     * @return User currentUser
     */
    public static User getCurrentUser() {
        return currentUser.getUser();
    }

    /**
     * Print info about the current user
     */
    public static void printUserInfo() {
        System.out.println(currentUser.toString());
    }

    @NonNull
    @Override
    /**
     * toString override, checks to make sure current user is not null
     */
    public String toString() {
        if(user != null) {
            return ("Name: " + user.getFirstName() + " " + user.getLastName() +
                    "\nEmail: " + user.getEmail() +
                    "\nPassword: " + user.getPassword());
        }
        else {
            return "There is no current user";
        }
    }
}
