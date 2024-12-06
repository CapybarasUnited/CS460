package com.cs460.finalprojectfirstdraft.utilities;

import android.graphics.Color;

import java.util.HashMap;

/**
 * Utility class to store key name constant strings
 */
public class Constants {
    //User
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USER_ID = "userid";

    //Collections
    public static final String KEY_COLLECTION_USERS = "User";
    public static final String KEY_COLLECTION_LISTS = "Lists";
    public static final String KEY_COLLECTION_ENTRIES = "Entries";

    //Item
    public static final String KEY_PARENT_LIST_ID = "parentListId";

    //List
    public static final String KEY_LIST_ID = "listId";
    public static final String KEY_LIST_NAME = "listName";
    public static final String KEY_COLOR = "color";
    public static final String KEY_IS_CHECK_LIST = "isChecklist";
    public static final String KEY_DELETE_WHEN_CHECKED = "deleteWhenChecked";
    public static final String KEY_USER_EMAIL = "userEmail";

    //Entry
    public static final String KEY_ENTRY_ID = "entryId";
    public static final String KEY_ENTRY_CONTENT = "entryContent";

}
