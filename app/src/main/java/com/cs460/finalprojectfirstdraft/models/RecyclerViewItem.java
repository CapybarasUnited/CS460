package com.cs460.finalprojectfirstdraft.models;

import android.util.Log;

import com.cs460.finalprojectfirstdraft.utilities.FirebaseHelper;

public class RecyclerViewItem {
    public Boolean isList, deleteWhenChecked, isCheckList, isChecked;
    public String text, backgroundColor, id, parentListId;
    public int position;

    public RecyclerViewItem(UserList userList, int position){
        this.isList = true;
        this.text = userList.getListName();
        this.isCheckList = userList.getIsChecklist();
        this.deleteWhenChecked = userList.getIsDelete();
        this.id = userList.getListId();
        this.backgroundColor = userList.getColor();
        this.parentListId = userList.getParentListId();
        isChecked = false;
        this.position = position;
    }
    public RecyclerViewItem(Entry entry, int position){
        this.id = entry.getEntryId();
        this.isList = false;
        this.text = entry.getEntryContent();
        backgroundColor = "White";
        this.isChecked = entry.getChecked();
        this.position = position;
        isCheckList = false;
        deleteWhenChecked = false;
    }
}
