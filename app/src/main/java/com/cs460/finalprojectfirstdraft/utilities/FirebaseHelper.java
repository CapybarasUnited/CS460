package com.cs460.finalprojectfirstdraft.utilities;

import android.util.Log;

import com.cs460.finalprojectfirstdraft.models.Entry;
import com.cs460.finalprojectfirstdraft.models.Item;
//import com.cs460.finalprojectfirstdraft.models.List;
import com.cs460.finalprojectfirstdraft.models.User;
import com.cs460.finalprojectfirstdraft.models.UserList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import kotlin.contracts.Returns;

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
     * Updates an existing list in the database
     * @param documentId: unique ID of the list document to update
     * @param updates: a map of fields and their new values to update in the document
     * @param listener: a listener to handle success or failure after operation completes
     */
    public static void updateList(String documentId, Map<String, Object> updates, OnCompleteListener<Void> listener) {
        db.collection(Constants.KEY_COLLECTION_LISTS)
                .document(documentId) //point to specific document to update
                .update(updates) // apply the updates
                .addOnCompleteListener(listener); //trigger listener when operation completes
    }

    /**
     *Deletes a list from the database
     * @param listId: a unique id of the list document to delete
     * @param listener: A listener to handle success or failure after operation completes
     */
    public static void deleteList(String listId, OnCompleteListener<Void> listener) {
        //use document id to find specific entries
        db.collection(Constants.KEY_COLLECTION_ENTRIES)
                .whereEqualTo("listId", listId) //filter by list id
                .get()//retrieve all entries in the collection
                .addOnCompleteListener(entriesTask -> { //listener to hamdle result of the entries query
                    if (entriesTask.isSuccessful()) {
                        //loop through all entries in the subcollection and delete each one
                        for (DocumentSnapshot entry : entriesTask.getResult()) {
                            entry.getReference().delete();
                        }

                        //after deleting entries, delete any sublists
                        db.collection(Constants.KEY_COLLECTION_LISTS)
                                .whereEqualTo(Constants.KEY_PARENT_LIST_ID, listId)//find sublists that have the current list as their parent
                                .get()//retrieve all sublists
                                .addOnCompleteListener(sublistsTask -> { //listener to hamdle result of the sublist query
                                    if (sublistsTask.isSuccessful()) { //if query is successful
                                        //loop through all sublists and delete each one
                                        for (DocumentSnapshot sublist : sublistsTask.getResult()) {
                                            //recursively delete each sublist
                                            deleteList(sublist.getId(), task -> {
                                                if (!task.isSuccessful()) {
                                                    listener.onComplete(task);
                                                    return;
                                                }
                                            });
                                        }
                                        //delete list
                                        db.collection(Constants.KEY_COLLECTION_LISTS)//reference to list collection
                                                .document(listId) //reference to list
                                                .delete() //delete document
                                                .addOnCompleteListener(listener); //notify listener once complete
                                    } else {

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
    public static void retrieveAllSubLists(String userEmail, String parentListId, OnCompleteListener<ArrayList<UserList>> listener) {
        //access the lists collection
        db.collection(Constants.KEY_COLLECTION_LISTS)
                //filter by email
                .whereEqualTo("userEmail", userEmail)
                .whereEqualTo("parentListId", parentListId)
                .get()
                //listener to handle the result of fetch operation
                .addOnCompleteListener(task -> {
                    //check if task is successful and result is not null
                    if(task.isSuccessful() && task.getResult() != null) {
                        //create an array list to hold retrieved UserList objects
                        ArrayList<UserList> userLists = new ArrayList<>();
                        //loop through each document in the result set
                        for(DocumentSnapshot document : task.getResult()) {
                            //covert document to list object
                            UserList list = document.toObject(UserList.class);
                            list.setIsDelete(document.get("deleteWhenChecked", Boolean.class));
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
     *Retrieves one list belonging to a user from database
     * @param userEmail: user email
     * @param listener:  A listener to handle success or failure after operation completes
     */
    public static void retrieveOneList(String userEmail, String listId, OnCompleteListener<UserList> listener) {
        //access the lists collection
        db.collection(Constants.KEY_COLLECTION_LISTS)
                //filter by email
                .whereEqualTo("userEmail", userEmail)
                .whereEqualTo("listId", listId)
                .get()
                //listener to handle the result of fetch operation
                .addOnCompleteListener(task -> {
                    //check if task is successful and result is not null
                    if(task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        UserList list = doc.toObject(UserList.class);
                        assert list != null;
                        list.setIsDelete(task.getResult().getDocuments().get(0).get("deleteWhenChecked", Boolean.class));
                        //notify listener and pass the list of List objects
                        listener.onComplete(Tasks.forResult(list));
                    } else {
                        //notify listener with exception
                        listener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }



    public ArrayList<UserList> getRootLists() {
        ArrayList<UserList> returnList = new ArrayList<>();
        db.collection(Constants.KEY_COLLECTION_LISTS)
                .whereEqualTo(Constants.KEY_USER_EMAIL, CurrentUser.getCurrentUser().getEmail())
                .whereEqualTo(Constants.KEY_PARENT_LIST_ID, null)
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                    returnList.add(new UserList(
                            (String) ds.get(Constants.KEY_LIST_ID),
                            (String) ds.get(Constants.KEY_PARENT_LIST_ID),
                            (String) ds.get(Constants.KEY_LIST_NAME),
                            (String) ds.get(Constants.KEY_COLOR),
                            (boolean) ds.get(Constants.KEY_IS_CHECK_LIST),
                            (boolean) ds.get(Constants.KEY_DELETE_WHEN_CHECKED),
                            (String) ds.get(Constants.KEY_USER_EMAIL)
                            ));
                });
        return returnList;
    }


    public static ArrayList<Item> getItemsWithParentListId(String id) {
        ArrayList<Item> items = new ArrayList<>();

        db.collection(Constants.KEY_COLLECTION_LISTS)
                .whereEqualTo(Constants.KEY_PARENT_LIST_ID, id)
                .get()
                .addOnCompleteListener(task -> {
                    for (DocumentSnapshot ds : task.getResult().getDocuments()) {
                        items.add(new UserList(
                                (String) ds.get(Constants.KEY_LIST_ID),
                                (String) ds.get(Constants.KEY_PARENT_LIST_ID),
                                (String) ds.get(Constants.KEY_LIST_NAME),
                                (String) ds.get(Constants.KEY_COLOR),
                                (boolean) ds.get(Constants.KEY_IS_CHECK_LIST),
                                (boolean) ds.get(Constants.KEY_DELETE_WHEN_CHECKED),
                                (String) ds.get(Constants.KEY_USER_EMAIL)));
                    }
                });

        db.collection(Constants.KEY_COLLECTION_ENTRIES)
                .whereEqualTo(Constants.KEY_PARENT_LIST_ID, id)
                .get()
                .addOnCompleteListener(task -> {
                    for (DocumentSnapshot ds : task.getResult().getDocuments()) {
                        items.add(new Entry(
                                (String) ds.get(Constants.KEY_ENTRY_ID),
                                (String) ds.get(Constants.KEY_PARENT_LIST_ID),
                                (String) ds.get(Constants.KEY_ENTRY_CONTENT),
                                false)); //change last line to db result if we use isChecked

                    }
                });
        return items;
    }

    /**
     *
     * @param id List ID of the parent list
     * @return Array list of UserLists with a specified parent id
     */
    public ArrayList<UserList> getUserListsbyParentID(String id){
        ArrayList<UserList> lists = new ArrayList<>();

        db.collection(Constants.KEY_COLLECTION_LISTS)
                .whereEqualTo(Constants.KEY_PARENT_LIST_ID, id)
                .whereEqualTo(Constants.KEY_USER_EMAIL, id)
                .get()
                .addOnCompleteListener(task -> {
                    for (DocumentSnapshot ds : task.getResult().getDocuments()) {
                        lists.add(new UserList(
                                (String) ds.get(Constants.KEY_LIST_ID),
                                (String) ds.get(Constants.KEY_PARENT_LIST_ID),
                                (String) ds.get(Constants.KEY_LIST_NAME),
                                (String) ds.get(Constants.KEY_COLOR),
                                (boolean) ds.get(Constants.KEY_IS_CHECK_LIST),
                                (boolean) ds.get(Constants.KEY_DELETE_WHEN_CHECKED),
                                (String) ds.get(Constants.KEY_USER_EMAIL)));
                    }
                });
        return lists;
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
     * Add a new entry to a a list in the database
     * @param entry : entry object contains fields userId, username and password
     * @param listener: A listener to handle success or failure after operation completes
     */
    public static void addEntry(Entry entry, String listID, OnCompleteListener<DocumentReference> listener) {
        //ensure that list is not null
        if (listID == null || listID.isEmpty()) {
            FirebaseFirestoreException exception = new FirebaseFirestoreException(
                    //set exception to indicate the list id  is missing
                    "List id is missing for the list",
                    FirebaseFirestoreException.Code.INVALID_ARGUMENT
            );
            return;
        }

        //check if list exists
        db.collection(Constants.KEY_COLLECTION_LISTS)
                .document(listID)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {

                        //generate a new document id for entry
                        DocumentReference newEntryRef = db.collection(Constants.KEY_COLLECTION_ENTRIES).document();
                        String documentId = newEntryRef.getId();

                        //set the entry id
                        entry.setEntryId(documentId);

                        //associate entry with its list
                        entry.setListId(listID);

                        //add entry to entry subcollection
                        newEntryRef.set(entry.entryToHashMap())
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful())
                                    {
                                        Log.d("AddEntry", "Entry successfully added");
                                        listener.onComplete(Tasks.forResult(newEntryRef));
                                    }else {
                                        Log.d("AddEntry", "Failed to add entry");
                                    }
                                });
            } else {
                Log.d("AddEntry", "List document does not exist");
                listener.onComplete(Tasks.forException(new Exception()));
            }
        });
    }

    /**
     * Update existing entry in the database
     * @param entryID: the id of the document to update
     * @param updates: a map containing fields to update and their new values
     * @param listener: A listener to handle success or failure after operation completes
     */
    public static void updateEntry(String listID, String entryID, Map<String, Object> updates, OnCompleteListener<Void> listener) {
        //validate listId and entryId
        if (listID == null || listID.isEmpty()||entryID == null||entryID.isEmpty()) {
            listener.onComplete(Tasks.forException(new FirebaseFirestoreException(
                    "List ID or Entry ID is missing or invalid",
                    FirebaseFirestoreException.Code.INVALID_ARGUMENT
            )));
            return;
        }
        //validate updates
        if (updates == null || updates.isEmpty()) {
            listener.onComplete(Tasks.forException(new FirebaseFirestoreException(
                    "No updates provided",
                    FirebaseFirestoreException.Code.INVALID_ARGUMENT
            )));
        }
        //update entry
        db.collection(Constants.KEY_COLLECTION_ENTRIES)
                .document(entryID)
                .update(updates) //perform partial update
                .addOnCompleteListener(listener)
                .addOnFailureListener(e -> listener.onComplete(Tasks.forException(e)));
    }

    /**
     * Delete an entry from the database
     * @param entryID: the id of the document to delete
     * @param listener: A listener to handle success or failure after operation completes
     */
    public static void deleteEntry(String listID, String entryID, OnCompleteListener<Void> listener) {
        //validate listId and entryId
        if (listID == null || listID.isEmpty()||entryID == null||entryID.isEmpty()) {
            listener.onComplete(Tasks.forException(new FirebaseFirestoreException(
                    "List ID or Entry ID is missing or invalid",
                    FirebaseFirestoreException.Code.INVALID_ARGUMENT
            )));
            return;
        }
        //delete entry
        db.collection(Constants.KEY_COLLECTION_ENTRIES)
                .document(entryID)
                .delete() //deletes document
                .addOnCompleteListener(listener)
                .addOnFailureListener(e -> listener.onComplete(Tasks.forException(e)));
    }

    /**
     * Retrieves all entries for a specific list
     * @param listId: the id of the list whose entries will be retrieved
     * @param listener: A listener to handle success or failure after operation completes
     */
    public static void retrieveEntries(String listId, OnCompleteListener<ArrayList<Entry>> listener) {
        //ensure the list id is valid
        if (listId == null || listId.isEmpty()) {
            FirebaseFirestoreException exception = new FirebaseFirestoreException(
                    "List ID is missing or invalid",
                    FirebaseFirestoreException.Code.INVALID_ARGUMENT
            );
            listener.onComplete(Tasks.forException(exception));
            return;
        }

        //reference entries collection
        db.collection(Constants.KEY_COLLECTION_ENTRIES)
                .whereEqualTo("listId", listId)
                .get() //fetch entries
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        //Log.d("retrieveEntries", "Query result size: " + task.getResult().size());
                        //create a list to store the retrieved enetries
                        ArrayList<Entry> entries = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            //Log.d("retrieveEntries", "Document found: " + document.getData());
                            Entry entry = document.toObject(Entry.class);
                            entry.setChecked(document.get("isChecked", Boolean.class));
                            if(entry != null) {
                                entries.add(entry);

                            }
                        }
                        //notify the listener of the success
                        listener.onComplete(Tasks.forResult(entries));
                    } else {
                        Log.e("retrieveEntries", "Query failed or no results", task.getException());
                        //notify listener of failure
                        listener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }

    /**
     * Method to calculate the progress of a given list
     * based on the number of checked off entries
     *
     * @param listID
     * @param callback
     */
    public static void calculateListProgress(String listID, ProgressCallback callback) {
        Log.d("ProgressCalculation", "Fetching progress for listID: " + listID);

        //query the entries collection filtering by list id
        db.collection(Constants.KEY_COLLECTION_ENTRIES)
                .whereEqualTo("listId", listID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int totalEntries = querySnapshot.size();
                    int checkedEntries = 0;
                    Log.d("ProgressCalculation", "Total entries fetched: " + totalEntries);

                    //iterate through entries to find number of checked entries
                    for (DocumentSnapshot doc : querySnapshot) {
                        boolean isChecked = doc.contains("isChecked") && Boolean.TRUE.equals(doc.getBoolean("isChecked"));
                        Log.d("ProgressCalculation", "Document ID: " + doc.getId() + ", isChecked: " + isChecked);

                        if (isChecked) {
                            checkedEntries++;
                        }
                    }
                    //calculate progress percentage
                    int progress;
                    if (totalEntries > 0) {
                        progress = (checkedEntries * 100) / totalEntries;
                    } else {
                        progress = 0;
                    }

                    Log.d("ProgressCalculation", "Progress calculated for listID: " + listID + " is " + progress + "%");
                    callback.onProgressCalculated(progress);
                })
                .addOnFailureListener(e -> {
                    Log.e("ProgressCalculation", "Error fetching progress for listID: " + listID, e);
                    callback.onError(e);
                });
    }

    /**
     * Callback interface for progress calculations.
     */
    public interface ProgressCallback {
        void onProgressCalculated(int progress); //called when progress is calculated
        void onError(Exception e); //called when there is an error
    }




}