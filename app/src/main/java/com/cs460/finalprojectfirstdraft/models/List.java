package com.cs460.finalprojectfirstdraft.models;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * List class used to contain info about this list and an ArrayList storing Items
 */
public class List extends Item{
    private ArrayList<Item> list;
    private int listID, parentListId;
    private String listName;
    private Color color;
    private boolean isChecklist;
    private boolean deleteWhenChecked;

    /**
     * Constructor
     * @param listID This lists ID
     * @param parentListId this lists parent list ID
     * @param listName this lists name
     * @param color this lists color
     */
    public List(int listID, int parentListId, String listName, Color color, boolean isChecklist, boolean deleteWhenChecked) {
        this.listID = listID;
        this.parentListId = parentListId;
        this.listName = listName;
        this.color = color;
        this.isChecklist = isChecklist;
        this.deleteWhenChecked = deleteWhenChecked;
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
    public void setColor(Color color) {
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
    public Color getColor() {
        return color;
    }

    public boolean getIsChecklist() {
        return isChecklist;
    }
}
