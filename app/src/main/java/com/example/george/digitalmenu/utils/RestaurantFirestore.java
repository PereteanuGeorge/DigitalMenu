package com.example.george.digitalmenu.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.util.Consumer;
import android.util.Log;

import com.example.george.digitalmenu.restaurant.TablesActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.AsyncEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.annotation.Nullable;


// Adapter implementation for firebase solution.
public class RestaurantFirestore implements RestaurantDatabase {

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private String restaurantName = null;
    public final static int MAX_DOWNLOAD_SIZE_BYTES = 1024 * 1024;
    private FirebaseAuth mAuth;

    private final String TAG = "Firestore";

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
        StorageReference ref = storage.getReferenceFromUrl(dish.getPic_url());
        ref.getBytes(MAX_DOWNLOAD_SIZE_BYTES).addOnSuccessListener(bytes -> {
            dish.setPicture(bytes);
            Log.d(TAG, "Download picture for " + dish.getName() + " succeeded");

            callback.accept(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        }).addOnFailureListener(e -> Log.d(TAG, "Download picture for " + dish.getName() + " falied", e));
    }

    @Override
    public void downloadThemePicture(Restaurant restaurant, Consumer<Bitmap> callback) {
        StorageReference ref = storage.getReferenceFromUrl(restaurant.getPic_url());
        ref.getBytes(MAX_DOWNLOAD_SIZE_BYTES).addOnSuccessListener(bytes -> {
            restaurant.setPicture(bytes);
            Log.d(TAG, "Download picture for " + restaurant + " succeeded");

            callback.accept(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Download picture for " + restaurant + " falied", e);
            }
        });
    }

    @Override
    public void downloadTagPicture(Tag tag, Consumer<Bitmap> callback) {
        StorageReference ref = storage.getReferenceFromUrl(tag.getPic_url());
        Log.d(TAG, "Downloading picture for " + tag);
        ref.getBytes(MAX_DOWNLOAD_SIZE_BYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                tag.setPicture(bytes);
                Log.d(TAG, "Download picture for " + tag + " succeeded");
                callback.accept(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Download picture for " + tag + " falied", e);
            }
        });
    }

    @Override
    public void init(Runnable onSuccess, Runnable onFailure) {
        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInAnonymously:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
                            onSuccess.run();

                        } else {
                            Log.d(TAG, "signInAnonymously:failure" + task.getException());
                            onFailure.run();

                        }
                    }
                });
    }

    @Override
    public void listenForOrders(String restaurantName, Consumer<Order> callback) {

        db.collection("restaurantOrders")
            .document(restaurantName)
            .collection("orders")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                    @Nullable FirebaseFirestoreException e) {

                    for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
                        if (change.getNewIndex() == -1) {
                            continue;
                        }

                        if (change.getType() != DocumentChange.Type.ADDED) {
                            continue;
                        }

                        if (change.getDocument().getId().equals("nullOrder")) {
                            continue;
                        }

                        QueryDocumentSnapshot document = change.getDocument();
                        Order order = document.toObject(Order.class);
                        callback.accept(order);
                }
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
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
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
                    }
                });

    }

    @Override
    public void saveOrder(Order order) {
        db.collection("restaurantOrders").document(restaurantName)
                .collection("orders").add(order)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Upload oder successfully");
                    } else {
                        Log.d(TAG, "Uploading failed");
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
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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
            }
        });
    }
}
