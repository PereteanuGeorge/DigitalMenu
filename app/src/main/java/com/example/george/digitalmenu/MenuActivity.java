package com.example.george.digitalmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivity";
    private RestaurantDatabase db;

    private ConstraintLayout previousCard;
    private ConstraintLayout rootLayout;
    private ConstraintSet constraintSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_1);

        rootLayout = findViewById(R.id.rootLayout);
        constraintSet = new ConstraintSet();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String restaurantName = intent.getStringExtra(MainActivity.INTENT_KEY);

        createMenu(restaurantName);
    }

    private void createMenu(String restaurantName) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInAnonymously:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        db = new RestaurantFirestore();
                        db.getRestaurant(restaurantName, r -> displayMenu(r));
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInAnonymously:failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayMenu(Restaurant r) {
        displayThemePicture(r);
        displayCategories(r);
    }

    private void displayCategories(Restaurant r) {
        List<String> categories = r.getCategories();
        for (String c: categories) {
            displayCategory(r, c);
        }
    }

    private void displayCategory(Restaurant r, String c) {
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout clist = (LinearLayout) inflater.inflate(R.layout.category_list, rootLayout, false);

        TextView infoText = clist.findViewById(R.id.category_text);
        infoText.setText(c);

        displayDishes(r.getDishes(c), clist);
        clist.setId(View.generateViewId());
        rootLayout.addView(clist);
    }

    private void displayDishes(List<Dish> dishes, LinearLayout clist) {
        for (Dish d: dishes) {
            displayDish(d, clist);
        }
    }

    private void displayThemePicture(Restaurant r) {
        ImageView picture = rootLayout.findViewById(R.id.theme_picture);
        db.downloadThemePicture(r, bm -> picture.setImageBitmap(bm));
    }

    private void displayDish(Dish d, LinearLayout clist) {

        /* Create card view with fields. */
        LayoutInflater inflater = getLayoutInflater();
        ConstraintLayout dish_card = (ConstraintLayout) inflater.inflate(R.layout.dish_card, clist, false);

        TextView infoText = dish_card.findViewById(R.id.description);
        infoText.setText(d.getDescription());

        TextView nameText = dish_card.findViewById(R.id.name);
        nameText.setText(d.getName());

        TextView priceText = dish_card.findViewById(R.id.price);
        priceText.setText(String.valueOf(d.getPrice()));

        ImageView foodImage = dish_card.findViewById(R.id.foodPicture);
        db.downloadDishPicture(d, bm -> foodImage.setImageBitmap(bm));

        dish_card.setId(View.generateViewId());

        // Add to existing list of cards
        clist.addView(dish_card);
    }
}
