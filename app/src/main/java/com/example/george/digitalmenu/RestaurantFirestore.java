package com.example.george.digitalmenu;

import android.support.annotation.NonNull;
import android.util.Log;;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


// Adapter implementation for firebase solution.
public class RestaurantFirestore implements RestaurantDatabase {

    private FirebaseFirestore db;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private final static long MAX_DOWNLOAD_SIZE_BYTES = 1024*1024;

    private final String TAG = "Firestore";

    public RestaurantFirestore() {
        db = FirebaseFirestore.getInstance();
    }

    public void getRestaurantEntry(String key) {
        db.collection(key)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });
    }
    public void getRestaurant(final String restaurant) {
        DocumentReference ref = db.collection("restaurants").document(restaurant);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d(TAG, "Cached document data " + restaurant + " " + document.getData());
                    Restaurant restaurant = document.toObject(Restaurant.class);
                    Log.d(TAG, "Converted " + restaurant.getDishes().get(0).getName());
                    downloadPictures(restaurant);
                } else {
                    Log.d(TAG, "Cached get failed ", task.getException());
                }
            }

            private void downloadPictures(Restaurant restaurant) {
                for (final Dish dish: restaurant.getDishes()) {
                    StorageReference ref = storage.getReferenceFromUrl(dish.getPic_url());
                    ref.getBytes(MAX_DOWNLOAD_SIZE_BYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            dish.setPicture(bytes);
                            Log.d(TAG, "Download picture for " + dish.getName() + " succeeded");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Download picture for " + dish.getName() + " falied", e);
                        }
                    });
                }
            }
        });
    }
}
