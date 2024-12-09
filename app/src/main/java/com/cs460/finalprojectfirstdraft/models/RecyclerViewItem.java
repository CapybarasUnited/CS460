package com.cs460.finalprojectfirstdraft.models;

import android.util.Log;

import com.cs460.finalprojectfirstdraft.utilities.FirebaseHelper;

public class RecyclerViewItem {
    public Boolean isList, isNormalChecklist;
    public String text, backgroundColor, listID, parentListId;
    public int percentChecked;

    public RecyclerViewItem(UserList userList){
        this.isList = true;
        this.isNormalChecklist = userList.getIsDelete();
        this.text = userList.getListName();
        this.listID = userList.getListId();
        this.backgroundColor = userList.getColor();
        this.parentListId = userList.getParentListId();
        if(isNormalChecklist){
            percentChecked = 101;
        }
    }
    public RecyclerViewItem(Entry entry){
        this.isList = false;
        this.isNormalChecklist = false;
        this.text = entry.getEntryContent();
        backgroundColor = "White";

    }
}
