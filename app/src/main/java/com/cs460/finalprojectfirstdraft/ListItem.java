package com.cs460.finalprojectfirstdraft;

/**
 * Represents a single item in the RecyclerView.
 */
public class ListItem {
    private String title;

    /**
     * Constructor for ListItem.
     *
     * @param title The title of the item.
     */
    public ListItem(String title) {
        this.title = title;
    }

    /**
     * Gets the title of the item.
     *
     * @return The title of the item.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the item.
     *
     * @param title The new title of the item.
     */
    public void setTitle(String title) {
        this.title = title;
    }
}


