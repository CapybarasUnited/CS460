package com.cs460.finalprojectfirstdraft.utilities;

import android.content.Intent;
import android.util.Log;

import com.cs460.finalprojectfirstdraft.activities.MainActivity;
import com.cs460.finalprojectfirstdraft.models.Entry;
//import com.cs460.finalprojectfirstdraft.models.List;
import com.cs460.finalprojectfirstdraft.models.User;
import com.cs460.finalprojectfirstdraft.models.UserList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.util.Listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FirebaseHelper {
    private static final FirebaseHelper firebaseHelper = new FirebaseHelper();
    private static FirebaseFirestore db;

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
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
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
     * Adds new user to Firestore database
     *
     * @param user: map containing user data (keys:values)
     * @param listener: triggered when operation completes; provides a
     *                reference to new document created if successful
     */
    public void addUser(User user, OnCompleteListener<DocumentReference> listener) {
        //extract emails from user map
        HashMap<String, String> userHashMap = new HashMap<>();

        userHashMap.put(Constants.KEY_FIRST_NAME, user.getFirstName());
        userHashMap.put(Constants.KEY_LAST_NAME, user.getLastName());
        userHashMap.put(Constants.KEY_EMAIL, user.getEmail());
        userHashMap.put(Constants.KEY_PASSWORD, user.getPassword());
        String email = userHashMap.get(Constants.KEY_EMAIL);

        //check if email already exists
        db.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .get()
                .addOnCompleteListener(task -> {
                    //if query was successful and result is not null and query did not return a document
                    if(task.isSuccessful() && task.getResult() != null && task.getResult().isEmpty()) {
                        //email is unique proceed
                        db.collection(Constants.KEY_COLLECTION_USERS)
                                .add(userHashMap) //auto generate document id
                                .addOnCompleteListener(listener);
                    } else {
                        listener.onComplete(null);
                    }
                });
    }

    /**
     * Adds a new list to the database
     * @param list :  a list object contains fields userId, listId, listName,
     *                  color, and listType
     * @param listener :a listener to handle success or failure after operation completes
     */
    public static void addList(UserList list, OnCompleteListener<Void> listener) {
        //ensure that userEmail is not null or empty
        if (list.getUserEmail() == null || list.getUserEmail().isEmpty()) {
            FirebaseFirestoreException exception = new FirebaseFirestoreException(
                    //set exception to indicate the email is missing
                    "User email is missing for the list",
                    FirebaseFirestoreException.Code.INVALID_ARGUMENT
            ); //specific firestore error code
            //return early
            return;
        }
        //create a reference to a document int the Lists collection
        DocumentReference documentReference = db.collection(Constants.KEY_COLLECTION_LISTS).document();

        //generate a new document id
        String documentId= documentReference.getId();

        //set the list id in the list object
        list.setListId(documentId);

        //add list to firestore
        documentReference.set(list.toHashMap()) //convert List to HashMap and add to Firestore
                .addOnCompleteListener(listener);
    }

    /**
     * Updates an existing list in the database
     * @param documentId: unique ID of the list document to update
     * @param updates: a map of fields and their new values to update in the document
     * @param listener: a listener to handle success or failure after operation completes
     */
    public static void updateList(String documentId, Map<String, Object> updates, OnCompleteListener<Void> listener) {
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
    public static void deleteList(String documentId, OnCompleteListener<Void> listener){
        //use document id to find specific list
        db.collection("Lists")
                .document(documentId) //point to a specific list document by its Id
                .collection("entries") //access the entries subcollection
                .get()//retrieve all entries in the subcollection
                .addOnCompleteListener(entriesTask -> { //listener to hamdle result of the entries query
                    if (entriesTask.isSuccessful()) {
                        //loop through all entries in the subcollection and delete each one
                        for (DocumentSnapshot entry : entriesTask.getResult()) {
                            entry.getReference().delete();
                        }

                        //after deleting entries, delete any sublists
                        db.collection("Lists")
                                .whereEqualTo("parentListId", documentId)//find sublists that have the current list as their parent
                                .get()//retrieve all sublists
                                .addOnCompleteListener(sublistsTask -> { //listener to hamdle result of the entries query
                                    if (sublistsTask.isSuccessful()) { //if query is successful
                                        //loop through all sublists and delete each one
                                        for (DocumentSnapshot sublist : sublistsTask.getResult()) {
                                            sublist.getReference().delete();
                                        }
                                        //delete main list
                                        db.collection("Lists")//reference to main list
                                                .document(documentId) //reference to main list
                                                .delete() //delete main document
                                                .addOnCompleteListener(listener); //notify listener once complete
                                    } else {
                                        //if deleting sublist fails createTaskCompletionSource
                                        listener.onComplete(Tasks.forException(sublistsTask.getException())); //notify of failed operation
                                    }
                                });
                    } else {
                        //if deleting entries fails createTaskCompletionSource
                        listener.onComplete(Tasks.forException(entriesTask.getException())); //notify of failed operation
                    }
                });
    }

    /**
     *Retrieves all lists belonging to a user from database
     * @param userEmail: user email
     * @param listener:  A listener to handle success or failure after operation completes
     */
    public static void retrieveAllLists(String userEmail, OnCompleteListener<List<UserList>> listener) {
        //access the lists collection
        db.collection("Lists")
                //filter by email
                .whereEqualTo("userEmail", userEmail)
                .get()
                //listener to handle the result of fetch operation
                .addOnCompleteListener(task -> {
                    //check if task is successful and result is not null
                    if(task.isSuccessful() && task.getResult() != null) {
                        //create an array list to hold retrieved UserList objects
                        List<UserList> userLists = new ArrayList<>();
                        //loop through each document in the result set
                        for(DocumentSnapshot document : task.getResult()) {
                            //covert document to list object
                            UserList list = document.toObject(UserList.class);
                            //ensure conversion was successful
                            if (list != null) {
                                userLists.add(list);//add List objects to the list
                            }
                        }
                        //notify listener and pass the list of List objects
                        listener.onComplete(Tasks.forResult(userLists));
                    } else {
                        //notify listener with exception
                        listener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }

    /**
     * Retrieve a specific list
     * @param listId: of desired individual list
     * @param listener A listener to handle success or failure after operation completes
     */
    public static void retrieveAList(String listId, OnCompleteListener<QuerySnapshot> listener){

    }

    /**
     * Add a new entry to a a list in the database
     * @param entry : entry object contains fields userId, username and password
     * @param listener: A listener to handle success or failure after operation completes
     */
    //public void addEntry(Entry entry, String listID, OnCompleteListener<DocumentReference> listener) {
        //ensure that list is not null
      //  if (listID == null || listID.isEmpty()) {
        //    FirebaseFirestoreException exception = new FirebaseFirestoreException(
                    //set exception to indicate the list id  is missing
          //          "List id is missing for the list",
            //        FirebaseFirestoreException.Code.INVALID_ARGUMENT
            //);
            //return;
        //}
        //check if list exists
        //db.collection(Constants.KEY_COLLECTION_LISTS).document(listID).get().addOnSuccessListener(document -> {
          //  if(document.exists()) {
                //create a reference to the entries sub collection
            //    CollectionReference entryCollections = db.collection(Constants.KEY_COLLECTION_LISTS)
                //        .document(listID)
              //          .collection("Entry");
                //generate a new document id
                //DocumentReference newEntryRef = entryCollections.document();
                //String documentId= newEntryRef.getId();

                //set the entry id in the entry object
                //entry.setEntryId(documentId);

                //add entry to entry subcollection
                //newEntryRef.set(entry.toHashMap())
                  //      .addOnCompleteListener(task -> {
                    //        listener.onComplete(Tasks.forResult(null));
                      //  });
            //} else {
              //  listener.onComplete(Tasks.forException(new Exception()));
            //}
        //});





   // }

    /**
     * Update existing entry in the database
     * @param entryID: the id of the document to update
     * @param updates: a map containing fields to update and their new values
     * @param listener: A listener to handle success or failure after operation completes
     */
    public void updateEntry(String entryID, Map<String, Object> updates, OnCompleteListener<Void> listener) {
        db.collection("Entries")
                .document(entryID) //find using entry id / document id
                .update(updates) //perform partial update
                .addOnCompleteListener(listener);
    }

    /**
     * Delete an entry from the database
     * @param entryID: the id of the document to delete
     * @param listener: A listener to handle success or failure after operation completes
     */
    public void deleteEntry(String entryID, OnCompleteListener<Void> listener) {
        db.collection("Entries")
                .document(entryID) //find using document id
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