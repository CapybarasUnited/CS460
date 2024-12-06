package com.cs460.finalprojectfirstdraft.utilities;

import com.cs460.finalprojectfirstdraft.models.User;
import com.cs460.finalprojectfirstdraft.models.UserList;

public class CurrentRootList {
    private UserList userList;
    private static CurrentRootList currentRootList;
    private CurrentRootList(UserList userList) {
        this.userList = userList;
    }

    private UserList getUserList() {
        return this.userList;
    }
    public static void setCurrentRootList(UserList userList) {
        if (currentRootList == null) {
            currentRootList = new CurrentRootList(userList);
        }
    }

    public static UserList getCurrentRootList() {
        return currentRootList.getUserList();
    }

    public static void printUserInfo() {
        UserList temp = currentRootList.getUserList();
        System.out.println(temp.toString());
    }
}
