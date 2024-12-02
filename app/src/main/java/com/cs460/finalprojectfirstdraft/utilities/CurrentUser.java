package com.cs460.finalprojectfirstdraft.utilities;

import com.cs460.finalprojectfirstdraft.models.User;

public class CurrentUser {
    private User user;
    private static CurrentUser currentUser;
    private CurrentUser(User user) {
        this.user = user;
    }

    public static void setCurrentUser(User user) {
        if (currentUser == null) {
            currentUser = new CurrentUser(user);
        }
    }
    public static User getCurrentUser() {
        return currentUser.getUser();
    }

    private User getUser() {
        return this.user;
    }
}
