package com.cs460.finalprojectfirstdraft;

/**
 * Entry class extends Item
 * This class represents a list entry, list entries do not contain entries or lists but do store text
 */
public class Entry extends Item{
    private Boolean isChecked;
    private int entryId, listId;
    private String entryContent;

    /**
     * Constructor
     * @param entryId This entries ID
     * @param listId This entries parent list ID
     * @param entryContent String content of this entry
     */
    public Entry(int entryId, int listId, String entryContent) {
        this.entryId = entryId;
        this.listId = listId;
        this.entryContent = entryContent;
    }

    /**
     * Set the value of isChecked
     * @param checked Boolean checked or unchecked
     */
    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    /**
     * Set the contents of this entry
     * @param entryContent String content
     */
    public void setEntryContent(String entryContent) {
        //depending on how we implement the database, this may need to be limited to 100 chars
        this.entryContent = entryContent;
    }

    /**
     * Get the value of isChecked
     * @return Boolean value of isChecked
     */
    public Boolean getChecked() {
        return isChecked;
    }

    /**
     * Get the contents of entryContent
     * @return String value entryContent
     */
    public String getEntryContent() {
        return entryContent;
    }
}