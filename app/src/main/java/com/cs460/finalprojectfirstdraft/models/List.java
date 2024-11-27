package com.cs460.finalprojectfirstdraft.models;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * List class used to contain info about this list and an ArrayList storing Items
 */
public class List extends Item{
    private ArrayList<Item> list;
    private String listID; //parentListId;
    private String parentListId;
    private String listName;
    private int color;
    private boolean isChecklist;
    private boolean deleteWhenChecked;
    private String userEmail;

    /**
     * Constructor
     * @param listID This lists ID
     * @param parentListId this lists parent list ID
     * @param listName this lists name
     * @param color this lists color
     */
    public List(String listID, String parentListId, String listName, int color, boolean isChecklist, boolean deleteWhenChecked, String userEmail) {
        this.listID = listID;
        this.parentListId = parentListId;
        this.listName = listName;
        this.color = color;
        this.isChecklist = isChecklist;
        this.deleteWhenChecked = deleteWhenChecked;
        this.userEmail = userEmail;
    }

    /**
     * Add a List or Entry to this list
     * @param item Item to add
     */
    public void addItem(Item item) {
        list.add(item);
    }

    /**
     * remove a List or Entry from this list
     * @param item Item to remove
     */
    public void removeItem(Item item) {
        list.remove(item);
    }

    /**
     * Get this list as an array, to be used to display list items
     * @return This lists contents as an array of Items
     */
    public Item[] getList() {
        return (Item[]) list.toArray();
    }

    /**
     * merhod to set user email
     * @param userEmail
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * Get the size of this list
     * @return int List size
     */
    public int size() {
        return list.size();
    }

    /**
     * Set this Lists name
     * @param listName String new name of this list
     */
    public void setListName(String listName) {
        this.listName = listName;
    }

    /**
     * Set the color of this list
     * @param color Color new color of this list
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Get the name of this list
     * @return String list name
     */
    public String getListName() {
        return listName;
    }

    /**
     * Get the color of this list
     * @return Color of this list
     */
    public int getColor() {
        return color;
    }

    /**
     * getter for email
     * @return email associated with the list
     */
    public String getUserEmail() { return userEmail; }

    public boolean getIsChecklist() {
        return isChecklist;
    }

    /**
     * method to convert list into a hashmap for firebase
     * @return a hashmap with list fields
     */
    public HashMap<String, Object> toHashMap(){
        HashMap<String, Object> map = new HashMap<>();

        map.put("listId", listID);
        map.put("parentListId", parentListId);
        map.put("listName", listName);
        map.put("color", color);
        map.put("isChecklist", isChecklist);
        map.put("deleteWhenChecked", deleteWhenChecked);
        map.put("userEmail", userEmail);

    return  map;
    }
}


