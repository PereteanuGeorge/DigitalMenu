package com.example.george.digitalmenu.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.util.Consumer;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


// Adapter implementation for firebase solution.
public class RestaurantFirestore implements RestaurantDatabase {

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    public final static int MAX_DOWNLOAD_SIZE_BYTES = 1024*1024;

    private final String TAG = "Firestore";

    public RestaurantFirestore() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public void getRestaurant(final String restaurant, final Consumer<Restaurant> callback) {
        DocumentReference ref = db.collection("restaurants").document(restaurant);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d(TAG, "Cached document data " + restaurant + " " + document.getData());
                    Restaurant restaurant = document.toObject(Restaurant.class);
                    callback.accept(restaurant);
                } else {
                    Log.d(TAG, "Cached get failed ", task.getException());
                }
            }
        });
    }

    public void downloadDishPicture(Dish dish, final Consumer<Bitmap> callback) {
        StorageReference ref = storage.getReferenceFromUrl(dish.getPic_url());
        ref.getBytes(MAX_DOWNLOAD_SIZE_BYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                dish.setPicture(bytes);
                Log.d(TAG, "Download picture for " + dish.getName() + " succeeded");

                callback.accept(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Download picture for " + dish.getName() + " falied", e);
            }
        });
    }

    @Override
    public void downloadThemePicture(Restaurant restaurant, Consumer<Bitmap> callback) {
        StorageReference ref = storage.getReferenceFromUrl(restaurant.getPic_url());
        ref.getBytes(MAX_DOWNLOAD_SIZE_BYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                restaurant.setPicture(bytes);
                Log.d(TAG, "Download picture for " + restaurant + " succeeded");

                callback.accept(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }

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
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
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

}
