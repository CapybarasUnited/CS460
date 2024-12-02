package com.cs460.finalprojectfirstdraft;

/**
 * Represents an item in a list, including details such as the title, type, and progress percentage.
 */
public class ListItem {
    private String title;
    private String type;
    private Integer progress; // Optional field for progress

    /**
     * Constructs a new ListItem with the specified title, type, and progress.
     *
     * @param title    The title of the list item.
     * @param type     The type or category of the list item (e.g., "Task", "Shopping").
     * @param progress The progress percentage for the list item (can be null if not applicable).
     */
    public ListItem(String title, String type, Integer progress) {
        this.title = title;
        this.type = type;
        this.progress = progress;
    }

    /**
     * Gets the title of the list item.
     *
     * @return The title of the list item.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the list item.
     *
     * @param title The new title of the list item.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the type or category of the list item.
     *
     * @return The type of the list item.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type or category of the list item.
     *
     * @param type The new type of the list item.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the progress percentage of the list item.
     *
     * @return The progress percentage, or null if not applicable.
     */
    public Integer getProgress() {
        return progress;
    }

    /**
     * Sets the progress percentage of the list item.
     *
     * @param progress The new progress percentage.
     */
    public void setProgress(Integer progress) {
        this.progress = progress;
    }
}
