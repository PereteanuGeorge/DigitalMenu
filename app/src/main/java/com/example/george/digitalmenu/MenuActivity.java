package com.example.george.digitalmenu;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.george.digitalmenu.MainActivity.INTENT_KEY;

/* Responsible for android-OS specific and UI logic */
public class MenuActivity extends AppCompatActivity implements MenuContract.View {

    public static final String DISH_KEY = "dish_key";

    private static final String TAG = "MenuActivity";

    private ConstraintLayout rootLayout;
    private Map<Integer, Dish> dishIds = new HashMap<>();

    MenuContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        rootLayout = findViewById(R.id.rootLayout);

        presenter = new MenuPresenter(this, new RestaurantFirestore());
        presenter.onViewCompleteCreate();
        setSlideBackToScanning();
    }

    @Override
    public void displayInfoFood(Dish dish) {
        getIntent().putExtra(DISH_KEY, dish);
        DishInfoFragment fragment = new DishInfoFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.dish_info_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setSlideBackToScanning() {
        final ImageButton scan_button = findViewById(R.id.scan_button);
        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    @Override
    public String getRestaurantName() {
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        return intent.getStringExtra(INTENT_KEY);
    }

    @Override
    public void displayMenu(Restaurant r) {
        displayThemePicture(r);
        displayCategories(r);
    }

    @Override
    public void notifyModelInitFailure() {
        Toast.makeText(getApplicationContext(), "Authentication failed.",
                Toast.LENGTH_SHORT).show();
    }

    private void displayCategories(Restaurant r) {
        List<String> categories = r.getCategories();
        Map<String, List<Dish>> map = r.getDishesForCategories();
        for (String c: categories) {
            displayCategory(c, map.get(c));
        }
    }

    private void displayCategory(String c, List<Dish> dishes) {
        if (dishes == null) dishes = new ArrayList<>();
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout menuPanel = rootLayout.findViewById(R.id.menu_panel);
        LinearLayout clist = (LinearLayout) inflater.inflate(R.layout.category_list, menuPanel, false);
        menuPanel.addView(clist);
        clist.setId(View.generateViewId());

        TextView infoText = clist.findViewById(R.id.category_text);
        infoText.setText(c);//

        displayDishes(dishes, clist);
    }

    private void displayDishes(List<Dish> dishes, LinearLayout clist) {
        for (Dish d: dishes) {
            displayDish(d, clist);
        }
    }

    private void displayThemePicture(Restaurant r) {
        ImageView picture = rootLayout.findViewById(R.id.theme_picture);
        presenter.fetchThemeImage(r, bm -> picture.setImageBitmap(bm));
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

        ImageView foodImage = dishCard.findViewById(R.id.food_picture);
        presenter.fetchDishImage(d, bm -> foodImage.setImageBitmap(bm));

        //displayTags
        // (d, dishCard.findViewById(R.id.tag_panel));

        // Add to existing list of cards
        clist.addView(dishCard);
        dishCard.setId(View.generateViewId());
        dishIds.put(dishCard.getId(), d);
        Log.d(TAG, "The size of dish" + dishIds.size());
        View dishView = findViewById(dishCard.getId());

        // Register click event to presenter.
        dishView.setOnClickListener(v -> presenter.onDishItemClick(d));
    }

    private void displayTags(Dish d, LinearLayout tagPanel) {
        Log.d(TAG, "Downloading picture for tags" + d.getTags() );
        for (Tag t: d.getEnumTags()) {
            displayTag(t, tagPanel);
        }
    }

    private void displayTag(Tag t, LinearLayout tagPanel) {
        /* Create card view with fields. */
        LayoutInflater inflater = getLayoutInflater();
        ConstraintLayout tag = (ConstraintLayout) inflater.inflate(R.layout.tag_card, tagPanel, false);

        ImageView tagIcon = tag.findViewById(R.id.tag_icon);
        presenter.fetchTagImage(t, bm -> tagIcon.setImageBitmap(bm));

        tag.setId(View.generateViewId());
        tagPanel.addView(tag);
    }
}
