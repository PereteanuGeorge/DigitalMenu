package com.example.george.digitalmenu;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

// Adapter implementation for firebase solution.
public class RestaurantFirestore implements RestaurantDatabase {

    private FirebaseFirestore db;

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

}
