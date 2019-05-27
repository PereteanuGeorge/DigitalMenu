package com.example.george.digitalmenu;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private QrCodeScanner scanner;

    private RestaurantDatabase db;
    private String TAG = "MainActivity";

    private CardView previousCard;
    private ConstraintLayout rootLayout;
    private ConstraintSet constraintSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Network().checkIfNetworkIsAvailable(this);

        SurfaceView surfaceView = findViewById(R.id.camerapreview);
        TextView textView = findViewById(R.id.textView);

        scanner = new RestaurantQrCodeScanner(surfaceView, textView);

        rootLayout = findViewById(R.id.rootLayout);
        constraintSet = new ConstraintSet();

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInAnonymously:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    db = new RestaurantFirestore();
                    db.getRestaurant("bestmangal", r -> addMenuItems(r));
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInAnonymously:failure", task.getException());
                    Toast.makeText(this.getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            });

        scanner.askForCameraPermissionAndScanQrCode(this);
    }

    private void addMenuItems(Restaurant r) {
        for (Dish d : r.getDishes()) {
            addMenuItem(d);
        }
    }

    private void addMenuItem(Dish d) {

        /* Create card view with fields. */
        LayoutInflater inflater = getLayoutInflater();
        CardView card = (CardView) inflater.inflate(R.layout.menu_item_card, rootLayout, false);

        TextView infoText = card.findViewById(R.id.foodInfo);
        infoText.setText(d.getDescription());

        TextView nameText = card.findViewById(R.id.foodName);
        nameText.setText(d.getName());

        TextView priceText = card.findViewById(R.id.foodPrice);
        priceText.setText(String.valueOf(d.getPrice()));

        ImageView foodImage = card.findViewById(R.id.foodPicture);
        db.downloadDishPicture(d, bm -> foodImage.setImageBitmap(bm));

        card.setId(View.generateViewId());

        /* Add to existing list of cards*/
        rootLayout.addView(card);

        if (previousCard != null) {
            constraintSet.clone(rootLayout);
            constraintSet.connect(card.getId(), ConstraintSet.TOP, previousCard.getId(), ConstraintSet.BOTTOM);
            constraintSet.applyTo(rootLayout);
        }

        previousCard = card;
    }

}
