package com.cs460.finalprojectfirstdraft.utilities;

import android.content.Intent;
import com.cs460.finalprojectfirstdraft.activities.MainActivity;
import com.cs460.finalprojectfirstdraft.models.Entry;
import com.cs460.finalprojectfirstdraft.models.List;
import com.cs460.finalprojectfirstdraft.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.FirebaseException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
                        //email already exists
                        //use TaskCompletionSource<DocumentReference> to simulate the completion of a task
                        TaskCompletionSource<DocumentReference> tcs = new TaskCompletionSource<>();
                        //set exception on task
                        tcs.setException(
                                new FirebaseFirestoreException("Email already exists", FirebaseFirestoreException.Code.ALREADY_EXISTS)
                        );
                        listener.onComplete(tcs.getTask()); //invoke listener with task
                    }
                });
    }

    /**
     * Adds a new list to the database
     * @param list :  a list object contains fields userId, listId, listName,
     *                  color, and listType
     * @param listener :a listener to handle success or failure after operation completes
     */
    public void addList(List list, OnCompleteListener<DocumentReference> listener) {
        //ensure that userEmail is not null or empty
        if (list.getUserEmail() == null || list.getUserEmail().isEmpty()) {
            //create a task completion source
            TaskCompletionSource<DocumentReference> tcs = new TaskCompletionSource<>();
            //set exception to indicate the email is missing
            tcs.setException(new FirebaseFirestoreException(
                    "User email is missing for the list",
                    FirebaseFirestoreException.Code.INVALID_ARGUMENT //specific firestore error code
            ));
            //pass the task with the exception to the listener so the caller can handle the failure
            listener.onComplete(tcs.getTask());
            //return early
            return;
        }
        //if email is valid procees
        db.collection(Constants.KEY_COLLECTION_LISTS)
                .add(list.toHashMap()) //convert List to HashMap and add to Firestore
                .addOnCompleteListener(listener); //trigger the listener on completion
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
                                        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
                                        tcs.setException(sublistsTask.getException()); //set the exception
                                        listener.onComplete(tcs.getTask()); //notify of failed operation
                                    }
                                });
                    } else {
                        //if deleting entries fails createTaskCompletionSource
                        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
                        tcs.setException(entriesTask.getException()); //set the exception
                        listener.onComplete(tcs.getTask()); //notify of failed operation
                    }
                });
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