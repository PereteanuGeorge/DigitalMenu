package com.example.george.digitalmenu.menu;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.main.MainActivity;
import com.example.george.digitalmenu.utils.Dish;
import com.example.george.digitalmenu.utils.DisplayableDish;
import com.example.george.digitalmenu.utils.OrderedDish;
import com.example.george.digitalmenu.utils.Restaurant;
import com.example.george.digitalmenu.utils.RestaurantFirestore;
import com.example.george.digitalmenu.utils.ServiceRegistry;
import com.example.george.digitalmenu.utils.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.george.digitalmenu.main.MainActivity.INTENT_KEY;

/* Responsible for android-OS specific and UI logic */
public class MenuActivity extends AppCompatActivity implements MenuContract.View, FragmentListener {

    public static DisplayableDish DISH = new Dish();

    private static final String TAG = "MenuActivity";

    private ConstraintLayout rootLayout;

    MenuContract.Presenter presenter;
    private Map<String, Boolean> open = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        rootLayout = findViewById(R.id.rootLayout);

        presenter = ServiceRegistry.getInstance().getService(MenuContract.Presenter.class);
        presenter.registerView(this);

        setScanButton();
        setCheckoutButton();

        presenter.onViewCompleteCreate();
    }

    private void setCheckoutButton() {
        final View button = findViewById(R.id.order_button);
        button.setOnClickListener(v -> {
            OrderPageFragment fragment = new OrderPageFragment();
            fragment.addListener(this);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.order_fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }


    @Override
    public void displayInfoFood(Dish dish) {
        DISH = new OrderedDish(dish,1);
        OrderBoardFragment fragment = new OrderBoardFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.dish_info_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setScanButton() {
       View view = findViewById(R.id.scan_button);
       view.setOnClickListener(v -> {
           presenter.cleanOrder();
           startActivity(new Intent(getApplicationContext(), MainActivity.class));
       });
    }

    // Get the Intent that started this activity and extract the string
    @Override
    public String getRestaurantName() {
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
        runOnUiThread(() -> {
            Toast.makeText(getApplicationContext(), "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        });
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
        open.put(c, false);

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout menuPanel = rootLayout.findViewById(R.id.menu_panel);
        LinearLayout clist = (LinearLayout) inflater.inflate(R.layout.category_list, menuPanel, false);
        menuPanel.addView(clist);
        clist.setId(View.generateViewId());

        TextView infoText = clist.findViewById(R.id.category_text);
        infoText.setText(c);//

        displayDishes(dishes, clist, c);
    }


    private void displayDishes(List<Dish> dishes, LinearLayout clist, String c) {
        clist.setOnClickListener(v -> {
            if (!open.get(c)) {
                for (Dish d : dishes) {
                    displayDish(d, clist);
                }
                Toast.makeText(getApplicationContext(), "Opened a category", Toast.LENGTH_LONG);
            } else {
                clist.removeViews(1, clist.getChildCount()-1);
                Toast.makeText(getApplicationContext(), "Closed a category", Toast.LENGTH_LONG);
            }
            open.put(c, !open.get(c));
        });
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

        ImageView foodImage = dishCard.findViewById(R.id.picture);
        presenter.fetchDishImage(d, bm -> foodImage.setImageBitmap(bm));

        displayTags(d, dishCard.findViewById(R.id.tag_panel));

        // Add to existing list of cards
        clist.addView(dishCard);
        dishCard.setId(View.generateViewId());

        // Register click event to presenter.
        dishCard.setOnClickListener(v -> presenter.onDishItemClick((Dish) d));
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

    @Override
    public void sendOrder() {
        presenter.sendOrder();
    }
}
