package com.example.george.digitalmenu.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
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

    private HashMap<Order, String> orderToId = new HashMap<>();

    public static List<String> users;

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

        db.collection("restaurantOrders")
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
    }

    @Override
    public void listenForTableWithId(Integer tableNumber, Consumer<Table> callback) {
        Log.d(TAG,"Numaru in db e " + tableNumber);
        db.collection("restaurantOrders")
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
                        Log.d(TAG,"Snapshot e " + snapshot.getData());
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
    public void saveTable(String username, Integer tableNumber) {
        DocumentReference ref = db.collection("restaurantOrders")
                .document(restaurantName).collection("tables").document(String.valueOf(tableNumber));
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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
