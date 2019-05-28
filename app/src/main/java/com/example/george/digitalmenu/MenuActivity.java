package com.example.george.digitalmenu;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivity";
    private RestaurantDatabase db;

    private ConstraintLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        rootLayout = findViewById(R.id.rootLayout);

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
        Map<String, List<Dish>> map = r.getDishesForCategories();
        for (String c: categories) {
            displayCategory(c, map.getOrDefault(c, new ArrayList<>()));
        }
    }

    private void displayCategory(String c, List<Dish> dishes) {
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout menuPanel = rootLayout.findViewById(R.id.menu_panel);
        LinearLayout clist = (LinearLayout) inflater.inflate(R.layout.category_list, menuPanel, false);

        TextView infoText = clist.findViewById(R.id.category_text);
        infoText.setText(c);//

        displayDishes(dishes, clist);
        clist.setId(View.generateViewId());
        menuPanel.addView(clist);
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
        ConstraintLayout dishCard = (ConstraintLayout) inflater.inflate(R.layout.dish_card, clist, false);

        TextView infoText = dishCard.findViewById(R.id.description);
        infoText.setText(d.getDescription());

        TextView nameText = dishCard.findViewById(R.id.name);
        nameText.setText(d.getName());

        TextView priceText = dishCard.findViewById(R.id.price);
        priceText.setText(String.valueOf(d.getPrice()));

        TextView currencyText = dishCard.findViewById(R.id.currency);
        currencyText.setText(String.valueOf(d.getCurrency()));

        ImageView foodImage = dishCard.findViewById(R.id.foodPicture);
        db.downloadDishPicture(d, bm -> foodImage.setImageBitmap(bm));

        displayTags(d, dishCard);

        dishCard.setId(View.generateViewId());

        // Add to existing list of cards
        clist.addView(dishCard);
    }

    private void displayTags(Dish d, ConstraintLayout dishCard) {
        for (Tag t: d.getEnumTags()) {
            displayTag(t, dishCard.findViewById(R.id.tags));
        }
    }

    private void displayTag(Tag t, LinearLayout tagPanel) {
        /* Create card view with fields. */
        LayoutInflater inflater = getLayoutInflater();
        ConstraintLayout tag = (ConstraintLayout) inflater.inflate(R.layout.tag_card, tagPanel, false);

        ImageView tagIcon = tag.findViewById(R.id.tag_icon);
        if (t.iconDownload()) {
            byte[] bytes = t.getPicture();
            tagIcon.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        }
        db.downloadTagPicture(t, bm -> tagIcon.setImageBitmap(bm));

        tag.setId(View.generateViewId());
        tagPanel.addView(tag);
    }
}
