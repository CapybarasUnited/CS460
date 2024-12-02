package com.cs460.finalprojectfirstdraft.utilities;

import com.cs460.finalprojectfirstdraft.models.User;

public class CurrentUser {
    private User user;
    private static CurrentUser currentUser;
    private CurrentUser(User user) {
        this.user = user;
        System.out.println("User created from database: " + user.getEmail());
    }

    private User getUser() {
        return this.user;
    }
    public static void setCurrentUser(User user) {
        if (currentUser == null) {
            currentUser = new CurrentUser(user);
        }

        printUserInfo();
    }

    public static User getCurrentUser() {
        return currentUser.getUser();
    }

    public static void printUserInfo() {
        User temp = currentUser.getUser();
        System.out.println("Name: " + temp.getFirstName() + " " + temp.getLastName() +
                "\nEmail: " + temp.getEmail() +
                "\nPassword: " + temp.getPassword());
    }
}
