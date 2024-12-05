package com.cs460.finalprojectfirstdraft.utilities;

import com.cs460.finalprojectfirstdraft.models.Entry;
import com.cs460.finalprojectfirstdraft.models.Item;
import com.cs460.finalprojectfirstdraft.models.List;
import com.cs460.finalprojectfirstdraft.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
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
    public static void addList(List list, OnCompleteListener<Void> listener) {
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
        list.setListID(documentId);

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
        db.collection(Constants.KEY_COLLECTION_LISTS)
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
        db.collection(Constants.KEY_COLLECTION_LISTS)
                .document(documentId) //point to a specific list document by its Id
                .collection(Constants.KEY_COLLECTION_ENTRIES) //access the entries subcollection
                .get()//retrieve all entries in the subcollection
                .addOnCompleteListener(entriesTask -> { //listener to hamdle result of the entries query
                    if (entriesTask.isSuccessful()) {
                        //loop through all entries in the subcollection and delete each one
                        for (DocumentSnapshot entry : entriesTask.getResult()) {
                            entry.getReference().delete();
                        }

                        //after deleting entries, delete any sublists
                        db.collection(Constants.KEY_COLLECTION_LISTS)
                                .whereEqualTo(Constants.KEY_PARENT_LIST_ID, documentId)//find sublists that have the current list as their parent
                                .get()//retrieve all sublists
                                .addOnCompleteListener(sublistsTask -> { //listener to hamdle result of the entries query
                                    if (sublistsTask.isSuccessful()) { //if query is successful
                                        //loop through all sublists and delete each one
                                        for (DocumentSnapshot sublist : sublistsTask.getResult()) {
                                            sublist.getReference().delete();
                                        }
                                        //delete main list
                                        db.collection(Constants.KEY_COLLECTION_LISTS)//reference to main list
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
    public static void getAllLists(String userEmail, OnCompleteListener<QuerySnapshot> listener) {
        db.collection(Constants.KEY_COLLECTION_LISTS)
                .whereEqualTo(Constants.KEY_USER_EMAIL, userEmail)
                .get()
                .addOnCompleteListener( task -> {
                    if (task.isSuccessful() && task.getResult() != null) {

                    }
                });
    }

    /**
     * Retrieve a specific list
     * @param listId: of desired list
     * @param listener A listener to handle success or failure after operation completes
     */
    public static void getList(String listId, OnCompleteListener<QuerySnapshot> listener){

    }

    public static List getRootList() {
        AtomicReference<List> returnList = new AtomicReference<>();
        db.collection(Constants.KEY_COLLECTION_LISTS)
                .whereEqualTo(Constants.KEY_EMAIL, CurrentUser.getCurrentUser().getEmail())
                .whereEqualTo(Constants.KEY_PARENT_LIST_ID, null)
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                    returnList.set(new List(
                            (String) ds.get(Constants.KEY_LIST_ID),
                            (String) ds.get(Constants.KEY_PARENT_LIST_ID),
                            (String) ds.get(Constants.KEY_LIST_NAME),
                            (String) ds.get(Constants.KEY_COLOR),
                            (boolean) ds.get(Constants.KEY_IS_CHECK_LIST),
                            (boolean) ds.get(Constants.KEY_DELETE_WHEN_CHECKED),
                            (String) ds.get(Constants.KEY_USER_EMAIL)
                            ));
                });
        return returnList.get();
    }

    public static ArrayList<Item> getItemsWithParentListId(String id) {
        ArrayList<Item> items = new ArrayList<>();

        db.collection(Constants.KEY_COLLECTION_LISTS)
                .whereEqualTo(Constants.KEY_PARENT_LIST_ID, id)
                .get()
                .addOnCompleteListener(task -> {
                    for (DocumentSnapshot ds : task.getResult().getDocuments()) {
                        items.add(new List(
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
                                Integer.parseInt((String) ds.get(Constants.KEY_ENTRY_ID)),
                                Integer.parseInt((String) ds.get(Constants.KEY_PARENT_LIST_ID)),
                                (String) ds.get(Constants.KEY_ENTRY_CONTENT)));
                    }
                });
        return items;
    }

    /**
     * Add a new entry to the database
     * @param entry : entry object contains fields userId, username and password
     * @param listener: A listener to handle success or failure after operation completes
     */
    public void addEntry(Entry entry, OnCompleteListener<DocumentReference> listener) {
        db.collection(Constants.KEY_COLLECTION_ENTRIES)
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
        db.collection(Constants.KEY_COLLECTION_ENTRIES)
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
        db.collection(Constants.KEY_COLLECTION_ENTRIES)
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
        db.collection(Constants.KEY_COLLECTION_ENTRIES)
                .whereEqualTo(Constants.KEY_LIST_ID, listId) //filer entries by their list id
                .get() //fetch entries
                .addOnCompleteListener(listener);
    }

}