package com.cs460.finalprojectfirstdraft.utilities;

import android.content.Intent;
import com.cs460.finalprojectfirstdraft.activities.MainActivity;
import com.cs460.finalprojectfirstdraft.models.Entry;
import com.cs460.finalprojectfirstdraft.models.List;
import com.cs460.finalprojectfirstdraft.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.util.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FirebaseHelper {
    private static final FirebaseHelper firebaseHelper = new FirebaseHelper();
    private FirebaseFirestore db;

    /**
     * Constructor to initialize FireStore instance
     */
    public FirebaseHelper() {
        //initialize FireStore
        db = FirebaseFirestore.getInstance();
    }

    public static FirebaseHelper getInstance() {
        return firebaseHelper;
    }

    /**
     * Adds new user to FireStore database
     * Temporarily commented out to avoid errors
     * @param user: the user object contains fields like userId, userName, etc.
     */
    public void createUser(User user, OnSuccessListener successListener, OnFailureListener failureListener) {
        //post to firestore
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, String> map = new HashMap<>();
        map.put(Constants.KEY_FIRST_NAME, user.getFirstName());
        map.put(Constants.KEY_LAST_NAME, user.getLastName());
        map.put(Constants.KEY_EMAIL, user.getEmail());
        map.put(Constants.KEY_PASSWORD, user.getPassword());

        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(map)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public boolean userExists(String email, String password) {
        AtomicBoolean returnBoolean = new AtomicBoolean(false);
        db.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .whereEqualTo(Constants.KEY_PASSWORD, password)
                .get()
                .addOnCompleteListener(task -> {
                    returnBoolean.set(true);
                });
        return returnBoolean.get();
    }

    public User getUser(String email, String password) {
        AtomicReference<User> user = null;
        db.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .whereEqualTo(Constants.KEY_PASSWORD, password)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                        user.set(new User(documentSnapshot.getString(Constants.KEY_EMAIL),
                                documentSnapshot.getString(Constants.KEY_PASSWORD),
                                documentSnapshot.getString(Constants.KEY_FIRST_NAME),
                                documentSnapshot.getString(Constants.KEY_LAST_NAME)));
                    }
                });
        return user.get();
    }

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