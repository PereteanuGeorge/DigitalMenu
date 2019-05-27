package com.example.george.digitalmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivity";
    private RestaurantDatabase db;

    private CardView previousCard;
    private ConstraintLayout rootLayout;
    private ConstraintSet constraintSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        rootLayout = findViewById(R.id.rootLayout);
        constraintSet = new ConstraintSet();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String restaurantName = intent.getStringExtra(MainActivity.INTENT_KEY);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            db = new RestaurantFirestore();
                            db.getRestaurant(restaurantName, r -> addMenuItems(r));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
