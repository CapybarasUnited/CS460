package com.cs460.finalprojectfirstdraft.models;

import com.cs460.finalprojectfirstdraft.utilities.FirebaseHelper;

public class RecyclerViewItem {
    public Boolean isList, isNormalChecklist;
    public String text, backgroundColor, listID;
    public int percentChecked;

    public RecyclerViewItem(Boolean isList,Boolean isNormalChecklist, String text, String listID, String backgroundColor){
        this.isList = isList;
        this.isNormalChecklist = isNormalChecklist;
        if (isNormalChecklist){
            //percentChecked = FirebaseHelper.getPercent(listID);
            percentChecked = 101;
        }
        this.text = text;
        this.listID = listID;
        this.backgroundColor = backgroundColor;
    }
}
