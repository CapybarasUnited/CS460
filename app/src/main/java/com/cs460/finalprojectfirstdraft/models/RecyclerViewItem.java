package com.cs460.finalprojectfirstdraft.models;

import android.util.Log;

import com.cs460.finalprojectfirstdraft.utilities.FirebaseHelper;

public class RecyclerViewItem {
    public Boolean isList, isNormalChecklist, isChecked;
    public String text, backgroundColor, id, parentListId;
    public int position, percentChecked;

    public RecyclerViewItem(UserList userList, int position){
        this.isList = true;
        this.isNormalChecklist = userList.getIsDelete();
        this.text = userList.getListName();
        this.id = userList.getListId();
        this.backgroundColor = userList.getColor();
        this.parentListId = userList.getParentListId();
        if(isNormalChecklist){
            percentChecked = 101;
        }
        isChecked = false;
        this.position = position;
    }
    public RecyclerViewItem(Entry entry, int position){
        this.id = entry.getEntryId();
        this.isList = false;
        this.isNormalChecklist = false;
        this.text = entry.getEntryContent();
        backgroundColor = "White";
        this.isChecked = entry.getChecked();
        this.position = position;
    }
}
