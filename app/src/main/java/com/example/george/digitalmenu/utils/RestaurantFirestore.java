package com.example.george.digitalmenu.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.nsd.NsdManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Consumer;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// Adapter implementation for firebase solution.
public class RestaurantFirestore implements RestaurantDatabase {

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private String restaurantName = null;
    public final static int MAX_DOWNLOAD_SIZE_BYTES = 1024 * 1024;
    private FirebaseAuth mAuth;

    private final String TAG = "Firestore";

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private Map<String, ListenerRegistration> listenerMap = new HashMap<>();

    private HashMap<Order, String> orderToId = new HashMap<>();

    public static List<String> users;
    private ListenerRegistration sharedOrderListener;
    private ListenerRegistration removedSharedDishListenr;
    private ListenerRegistration newUserListenr;

    public RestaurantFirestore() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void getRestaurant(final String restaurant, final Consumer<Restaurant> callback) {
        this.restaurantName = restaurant;
        DocumentReference ref = db.collection("restaurants").document(restaurant);
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Log.d(TAG, "Cached document data " + restaurant + " " + document.getData());
                Restaurant restaurant1 = document.toObject(Restaurant.class);
                callback.accept(restaurant1);
            } else {
                Log.d(TAG, "Cached get failed ", task.getException());
            }
        });
    }

    public void downloadDishPicture(Dish dish, final Consumer<Bitmap> callback) {
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_CORES);
        for (int i = 0; i < NUMBER_OF_CORES; i++) {
            executorService.submit((Callable<Object>) () -> {
                StorageReference ref = storage.getReferenceFromUrl(dish.getPic_url());
                ref.getBytes(MAX_DOWNLOAD_SIZE_BYTES).addOnSuccessListener(bytes -> {

                    dish.setPicture(bytes);
                    Log.d(TAG, "Download picture for " + dish.getName() + " succeeded");

                    callback.accept(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                }).addOnFailureListener(e -> Log.d(TAG, "Download picture for " + dish.getName() + " falied", e));
                return dish;
            });
        }
    }

    @Override
    public void downloadThemePicture(Restaurant restaurant, Consumer<Bitmap> callback) {
        StorageReference ref = storage.getReferenceFromUrl(restaurant.getPic_url());
        ref.getBytes(MAX_DOWNLOAD_SIZE_BYTES).addOnSuccessListener(bytes -> {

            restaurant.setPicture(bytes);
            Log.d(TAG, "Download picture for " + restaurant + " succeeded");
            callback.accept(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

        }).addOnFailureListener(e -> Log.d(TAG, "Download picture for " + restaurant + " falied", e));
    }

    @Override
    public void downloadTagPicture(Tag tag, Consumer<Bitmap> callback) {
        StorageReference ref = storage.getReferenceFromUrl(tag.getPic_url());
        Log.d(TAG, "Downloading picture for " + tag);
        ref.getBytes(MAX_DOWNLOAD_SIZE_BYTES).addOnSuccessListener(bytes -> {

            tag.setPicture(bytes);
            Log.d(TAG, "Download picture for " + tag + " succeeded");
            callback.accept(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

        }).addOnFailureListener(e -> Log.d(TAG, "Download picture for " + tag + " falied", e));
    }

    @Override
    public void init(Runnable onSuccess, Runnable onFailure) {
        mAuth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInAnonymously:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
                        onSuccess.run();

                    } else {
                        Log.d(TAG, "signInAnonymously:failure" + task.getException());
                        onFailure.run();

                    }
                });
    }

    public void updateOrderedDishes(String restaurantName, List<Order> orders) {

        WriteBatch batch = db.batch();

        for (Order order : orders) {
            /* Find order to update. */
            String orderId = order.getId();

            DocumentReference docRef = db.collection("restaurantOrders")
                    .document(restaurantName)
                    .collection("orders")
                    .document(orderId);

            batch.set(docRef, order);

//        docRef.update("dishes", Arrays.asList(newDishes)).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Log.d(TAG, "updateOrderedDishes:success");
//            } else {
//                Log.w(TAG, "updateOrderedDishes:failure", task.getException());
//            }
//        });
        }

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "updateOrderedDishes:success");
            } else {
                Log.w(TAG, "updateOrderedDishes:failure", task.getException());
            }
        });

    }

    @Override
    public void updateOrderedDishes(List<Order> orders, Consumer<List<Order>> callback) {

        WriteBatch batch = db.batch();

        for (Order order : orders) {
            /* Find order to update. */
            String orderId = order.getId();
            DocumentReference docRef = db.collection("restaurantOrders")
                    .document(restaurantName)
                    .collection("orders")
                    .document(orderId);
            batch.set(docRef, order);
        }

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "updateOrderedDishes:success");
                callback.accept(orders);
            } else {
                Log.w(TAG, "updateOrderedDishes:failure", task.getException());
            }
        });

    }

    @Override
    public void listenForCustomerOrders(String restaurantName, Consumer<Order> callback) {

        db.collection("restaurantOrders")
                .document(restaurantName)
                .collection("orders")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {

                    for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
                        if (change.getNewIndex() == -1) {
                            continue;
                        }

                        if (change.getType() != DocumentChange.Type.ADDED) {
                            continue;
                        }

                        String id = change.getDocument().getId();
                        if (id.equals("nullOrder")) {
                            continue;
                        }

                        QueryDocumentSnapshot document = change.getDocument();
                        Order order = document.toObject(Order.class);
                        order.setId(document.getId());
//                    orderToId.put(order, id);
                        callback.accept(order);
                    }
                });
    }

    @Override
    public boolean alreadySignedIn() {
        return mAuth.getCurrentUser() != null;
    }

    @Override
    public void signInWithEmailAndPassword(String email, String password,
                                           Runnable success, Runnable failure) {
        if (mAuth.getCurrentUser() != null) {
            success.run();
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        success.run();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        failure.run();
                    }

                    // ...
                });

    }

    @Override
    public void saveOrder(Order order, Consumer<Order> callback) {
        db.collection("restaurantOrders").document(restaurantName)
                .collection("orders").add(order)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        order.setId(task.getResult().getId());
                        callback.accept(order);
                        Log.d(TAG, "Upload oder successfully");
                    } else {
                        Log.d(TAG, "Uploading failed");
                    }
                });
    }

    public void listenForSentOrder(String id, Consumer<Order> callback) {

        ListenerRegistration registration = db.collection("restaurantOrders")
                .document(restaurantName)
                .collection("orders")
                .document(id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "Current data: " + snapshot.getData());

                            //function)
                            callback.accept(snapshot.toObject(Order.class));
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
        listenerMap.put(id, registration);
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    @Override
    public void removeListener(String id) {
        listenerMap.get(id).remove();
        listenerMap.remove(id);
    }

    public void listenForTableWithId(Integer tableNumber, Consumer<Table> callback) {
        Log.d(TAG,"Numaru in db e " + tableNumber);
        newUserListenr = db.collection("restaurantOrders")
                .document(restaurantName)
                .collection("tables")
                .document(String.valueOf(tableNumber))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        Log.d(TAG, "Snapshot e " + snapshot.getData());
                        if (snapshot != null && snapshot.exists()) {
                            users = (List<String>) snapshot.getData().get("users");

                            Log.d(TAG, "Current data: " + snapshot.getData());

                            //function)
                            callback.accept(snapshot.toObject(Table.class));
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }

    @Override
    public void uploadSharedDish(Integer tableID, SharedDish sharedDish) {
        db.collection("restaurantOrders")
                .document(restaurantName)
                .collection("tables")
                .document(String.valueOf(tableID))
                .collection("sharedOrders")
                .add(sharedDish)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG,"Succeed to upload sharing dish");
                        } else {
                            Log.d(TAG, "Failed to upload sharing dish");
                        }
                    }
                });
    }

    @Override
    public void listenForTableSharedDish(Integer tableID, Consumer<SharedDish> callback) {
        sharedOrderListener = db.collection("restaurantOrders")
                .document(restaurantName)
                .collection("tables")
                .document(String.valueOf(tableID))
                .collection("sharedOrders")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    for (DocumentChange change: queryDocumentSnapshots.getDocumentChanges()) {
                        if (change.getNewIndex() == -1) {
                            continue;
                        }

                        if (change.getType() != DocumentChange.Type.ADDED) {
                            continue;
                        }

                        String id = change.getDocument().getId();
                        if (id.equals("nullOrder")) {
                            continue;
                        }

                        QueryDocumentSnapshot document = change.getDocument();
                        SharedDish sharedDish = document.toObject(SharedDish.class);
                        sharedDish.getOrderedDish().setId(document.getId());
                        callback.accept(sharedDish);
                    }
                });
    }


    /* Figure out what will happen if tableID not exist, or the path does not contain "users" field*/
    @Override
    public void removeUserFromTable(String userName, Integer tableID) {
        db.collection("restaurantOrders")
                .document(restaurantName)
                .collection("tables")
                .document(String.valueOf(tableID))
                .update("users", FieldValue.arrayRemove(userName))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Succeed to remove " + userName + " in Table " + tableID);
                    } else {
                        Log.d(TAG, "Failed to remove " + userName + " in Table " + tableID);
                    }
                });
    }

    @Override
    public void removeSharedOrderListener() {
        sharedOrderListener.remove();
    }


    //Need to use callback
    @Override
    public void removeSharedDishWithId(OrderedDish dish, Integer tableID, Consumer<OrderedDish> callback) {
        db.collection("restaurantOrders")
                .document(restaurantName)
                .collection("tables")
                .document(String.valueOf(tableID))
                .collection("sharedOrders")
                .document(dish.getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Succeed to delete shared dish" + dish);
                            dish.setIsShared(false);
                            callback.accept(dish);
                        } else {
                            Log.d(TAG, "Failed to delete shared dish" + dish);
                        }
                    }
                });
    }

    @Override
    public void listenForRemovedSharedDishes(Integer tableID, Consumer<String> callback) {
        removedSharedDishListenr = db.collection("restaurantOrders")
                .document(restaurantName)
                .collection("tables")
                .document(String.valueOf(tableID))
                .collection("sharedOrders")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                        for (DocumentChange change : documentChanges) {
                            if (change.getType() == DocumentChange.Type.REMOVED) {
                                callback.accept(change.getDocument().getId());
                            }
                        }

                    }
                });

    }

    @Override
    public void removeRemovingShareOrderListener() {
        removedSharedDishListenr.remove();
    }

    @Override
    public void removeNewUserListener() {
        newUserListenr.remove();
    }

    @Override
    public void saveTable(String username, Integer tableNumber) {
        DocumentReference ref = db.collection("restaurantOrders")
                .document(restaurantName).collection("tables").document(String.valueOf(tableNumber));
        ref.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Table table;
                if(document.exists()) {
                    Log.d(TAG, "Catched table" + tableNumber);
                    table = document.toObject(Table.class);
                    table.add(username);
                } else {
                    Log.d(TAG, "Table" + tableNumber + " is not exist");
                    table = new Table(tableNumber);
                    table.add(username);
                }
                ref.set(table).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Successfully saved table" + tableNumber);
                        } else {
                            Log.d(TAG, "Failed to save table" + tableNumber);
                        }
                    }
                });
            } else {
                Log.d(TAG, "Failed to catch table" + tableNumber);
            }
        });
    }

    @Override
    public void removeOrders(List<Order> orders, Runnable callback) {
        if (orders.isEmpty()) {
            callback.run();
        }

        WriteBatch batch = db.batch();

        for (Order order : orders) {
            DocumentReference docRef = db.collection("restaurantOrders")
                    .document(restaurantName)
                    .collection("orders")
                    .document(order.getId());
            batch.delete(docRef);
        }


        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.run();
                } else {
                    Log.d(TAG, "Orders deletion failed ", task.getException());
                }
            }
        });
    }

    @Override
    public void getSignedInUserRestaurantName(Consumer<String> success, Runnable failure) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            failure.run();
        }

        DocumentReference ref = db.collection("restaurantNames").document(user.getUid());
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {

                    Log.d(TAG, "Cached document data " + user.getUid() + " " + document.getData());
                    Restaurant restaurant = document.toObject(Restaurant.class);
                    success.accept((String) document.get("name"));

                } else {

                    Log.d(TAG, "Document doesn't exist:   " + user.getUid());
                    failure.run();

                }

            } else {
                Log.d(TAG, "Cached get failed ", task.getException());
                failure.run();
            }
        });
    }
}
