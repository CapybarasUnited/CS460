package com.cs460.finalprojectfirstdraft.models;

/**
 * Represents an item to be stored in the recycler view on the main activity. This can either be an item or a list
 */
public class RecyclerViewItem {
    public Boolean isList, isNormalChecklist;
    public String text, backgroundColor, listID, parentListId;
    public int percentChecked;

    /**
     * List constructor
     * @param userList The user list that this class will adapt to work with the recyclerView
     */
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

    /**
     * Entry constructor
     * @param entry The entry that this class will adapt to work with the recyclerView
     */
    public RecyclerViewItem(Entry entry){
        this.isList = false;
        this.isNormalChecklist = false;
        this.text = entry.getEntryContent();
        backgroundColor = "White";

    }
}
