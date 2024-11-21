package com.cs460.finalprojectfirstdraft.utilities;


import com.cs460.finalprojectfirstdraft.models.Entry;
import com.cs460.finalprojectfirstdraft.models.List;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class FirebaseHelper {
    private FirebaseFirestore db;

    /**
     * Constructor to initialize Firestore instance
     */
    public FirebaseHelper() {
        //initialize Firestore
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Temporarily commented out to prevent errors (user class missing getUserId method)
     * Adds new user to Firestore database
     * Temporarily commented out to avoid errors
     * @param user: the user object contains fields like userId, userName, etc.
     * @param listener: A listener to handle success or failure after operation completes
     */
    /** public void addUser(User user, OnCompleteListener<Void> listener) {
        db.collection("User")
                .document(user.getUserId()) // uses the userId as the document id
                .set(user) //adds or updates the use document
                .addOnCompleteListener(listener); //triggers the provides listener when operation completes

    }/*

    /**
     * Adds a new list to the database
     * @param list :  a list object contains fields userId, listId, listName,
     *                  color, and listType
     * @param listener :a listener to handle success or failure after operation completes
     */
    public void addList(List list, OnCompleteListener<DocumentReference> listener) {
        db.collection("Lists")
                .add(list)//adds the list to the "list" collection
                .addOnCompleteListener(listener); //trigger listener when operation completes
    }

    /**
     * Updates an existing list in the database
     * @param documentId: unique ID of the list document to update
     * @param updates: a map of fields and their new values to update in the document
     * @param listener: a listener to handle success or failure after operation completes
     */
    public void updateList(String documentId, Map<String, Object> updates, OnCompleteListener<Void> listener) {
        db.collection("Lists")
                .document(documentId) //point to specific document to update
                .update(updates) // apply the updates
                .addOnCompleteListener(listener); //trigger listener when operation completes
    }

    /**
     *Deletes a list from the database
     * @param documentId: a unique id of the list document to delete
     * @param listener: A listener to handle success or failure after operation completes
     */
    public void deleteList(String documentId, OnCompleteListener<Void> listener){
        db.collection("Lists")
                .document(documentId) //point to a specific list document by its Id
                .delete() //delete the document
                .addOnCompleteListener(listener); //triggers listener when the operation completes
    }

    /**
     *Retrieves all lists belonging to a user from database
     * @param userId: unique Id of the user whose lists will be retrieved
     * @param listener:  A listener to handle success or failure after operation completes
     */
    public void retrieveList(int userId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("Lists")
                .whereEqualTo("userId", userId) //filter lists where the userId field matched the provided userId
                .get() //retrieve matching documents
                .addOnCompleteListener(listener); //trigger the provided listener when the operation completes
    }

    /**
     * Add a new entry to the database
     * @param entry : entry object contains fields userId, username and password
     * @param listener: A listener to handle success or failure after operation completes
     */
    public void addEntry(Entry entry, OnCompleteListener<DocumentReference> listener) {
        db.collection("Entries")
                .add(entry) //add entry as a new document
                .addOnCompleteListener(listener);
    }

    /**
     * Update existing entry in the database
     * @param documentId: the id of the document to update
     * @param updates: a map containing fields to update and their new values
     * @param listener: A listener to handle success or failure after operation completes
     */
    public void updateEntry(String documentId, Map<String, Object> updates, OnCompleteListener<Void> listener) {
        db.collection("Entries")
                .document(documentId) //find using document id
                .update(updates) //perform partial update
                .addOnCompleteListener(listener);
    }

    /**
     * Delete an entry from the database
     * @param documentId: the id of the document to delete
     * @param listener: A listener to handle success or failure after operation completes
     */
    public void deleteEntry(String documentId, OnCompleteListener<Void> listener) {
        db.collection("Entries")
                .document(documentId) //find using document id
                .delete() //deletes document
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves all entries for a specific list
     * @param listId: the id of the list whose entries will be retrieved
     * @param listener: A listener to handle success or failure after operation completes
     */
    public void retrieveEntries(String listId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("Entries")
                .whereEqualTo("listId", listId) //filer entries by their list id
                .get() //fetch entries
                .addOnCompleteListener(listener);
    }

}