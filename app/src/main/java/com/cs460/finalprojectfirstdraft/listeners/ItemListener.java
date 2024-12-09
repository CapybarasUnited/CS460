package com.cs460.finalprojectfirstdraft.listeners;

import com.cs460.finalprojectfirstdraft.models.RecyclerViewItem;

/**
 * Interface for activity controllers
 */
public interface ItemListener {

    /**
     * Given a recyclerViewItem that was clicked, open the list activity with the given recyclerViewItem (this only works on lists)
     * @param recyclerViewItem List to open list activity with
     */
    void onItemClicked(RecyclerViewItem recyclerViewItem);
}
