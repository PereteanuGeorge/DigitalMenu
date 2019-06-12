package com.example.george.digitalmenu.menu;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import com.example.george.digitalmenu.utils.Order;
import com.example.george.digitalmenu.utils.OrderedDish;
import com.example.george.digitalmenu.utils.Restaurant;
import com.example.george.digitalmenu.utils.ServiceRegistry;
import com.example.george.digitalmenu.utils.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.george.digitalmenu.main.MainActivity.INTENT_KEY;
import static com.example.george.digitalmenu.main.MainActivity.USERNAME;

/* Responsible for android-OS specific and UI logic */
public class MenuActivity extends AppCompatActivity implements MenuContract.View, FragmentListener, BoardFragmentListener {

    //public static OrderedDish DISH = new OrderedDish();
    private static final String TAG = "MenuActivity";
    private ConstraintLayout rootLayout;
    MenuContract.Presenter presenter;
    private Map<String, Boolean> open = new HashMap<>();
    private OrderPageFragment orderPageFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        rootLayout = findViewById(R.id.rootLayout);

        presenter = ServiceRegistry.getInstance().getService(MenuContract.Presenter.class);
        presenter.registerView(this);
        presenter.setUserName(getIntent().getStringExtra(USERNAME));

        setScanButton();
        setCheckoutButton();

        presenter.onViewCompleteCreate();
    }

    private void setCheckoutButton() {
        final View button = findViewById(R.id.order_button);
        button.setOnClickListener(v -> {
            orderPageFragment = new OrderPageFragment();
            orderPageFragment.setListener(this);
            orderPageFragment.setOrderedDishes(presenter.getOrderedDishes());
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.order_fragment_container, orderPageFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }


    @Override
    public void displayInfoFood(Dish dish) {
        OrderBoardFragment orderBoardFragment = OrderBoardFragment.newInstance();
        orderBoardFragment.setOrderedDish(new OrderedDish(dish));
        orderBoardFragment.setListener(this);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.dish_info_fragment_container, orderBoardFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setScanButton() {
       View view = findViewById(R.id.scan_button);
       view.setOnClickListener(v -> {
           presenter.cleanOrder();
           presenter.leaveRestaurant();
           Intent intent = new Intent(getApplicationContext(), MainActivity.class);
           intent.putExtra("username", presenter.getUserName());
           startActivity(intent);
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
        //displayThemePicture(r);

        TextView restaurantName = rootLayout.findViewById(R.id.restaurant_Name);
        restaurantName.setText(r.getName());

        displayCategories(r);
    }

    @Override
    public void notifyModelInitFailure() {
        runOnUiThread(() -> {
            Toast.makeText(getApplicationContext(), "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void update(Order order) {
        for (OrderedDish updatedOrderedDish: order.getDishes()) {
            OrderedDish localOrderedDish = presenter.updateOrderedDish(updatedOrderedDish);
            if (orderPageFragment != null) {
                orderPageFragment.updateOrderedDish(localOrderedDish);
            }
        }
    }

    @Override
    public void updateWithEverythingIsServed() {
        if (orderPageFragment != null) {
            orderPageFragment.setGetBillButton();
        }
    }

    @Override
    public void updateWithAddedDish(OrderedDish dish) {
        if (orderPageFragment != null) {
            orderPageFragment.updateWithAddedDish(dish);
        }
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
        CardView categoryCard = (CardView) inflater.inflate(R.layout.new_dish_card, menuPanel, false);
        menuPanel.addView(categoryCard);
        categoryCard.setId(View.generateViewId());

        TextView categoryText = categoryCard.findViewById(R.id.category_name);
        categoryText.setText(c);//

        ImageView categoryPicture = categoryCard.findViewById(R.id.category_picture);
        if (dishes.size() > 0) {
            presenter.fetchDishImage(dishes.get(0), bm -> categoryPicture.setImageBitmap(bm));
        }

        displayDishes(dishes, categoryCard, c);
    }


    private void displayDishes(List<Dish> dishes, CardView categoryCard, String c) {
        View openButton = categoryCard.findViewById(R.id.open_dish_button);
        LinearLayout dishPanel = categoryCard.findViewById(R.id.dish_panel);
        categoryCard.setOnClickListener(v -> {
            if (!open.get(c)) {
                for (Dish d : dishes) {
                    displayDish(d, dishPanel);
                }
                openButton.setBackground(getResources().getDrawable(R.drawable.up_white_arrow));
                Toast.makeText(getApplicationContext(), "Opened a category", Toast.LENGTH_LONG);
            } else {
                dishPanel.removeAllViews();
                openButton.setBackground(getResources().getDrawable(R.drawable.down_white_arrow));
                Toast.makeText(getApplicationContext(), "Closed a category", Toast.LENGTH_LONG);
            }
            open.put(c, !open.get(c));
        });
    }

    private void displayDish(Dish d, LinearLayout dishPanel) {

        /* Create card view with fields. */
        LayoutInflater inflater = getLayoutInflater();
        ConstraintLayout dishCard = (ConstraintLayout) inflater.inflate(R.layout.dish_card, dishPanel, false);

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
        dishPanel.addView(dishCard);
        dishCard.setId(View.generateViewId());

        // Register click event to presenter.
        dishCard.setOnClickListener(v -> presenter.onDishItemClick(d));
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

    @Override
    public void onBackPressed() {
        orderPageFragment = null;
    }

    @Override
    public void goBack() {
        orderPageFragment = null;
        getFragmentManager().popBackStack();
    }

    @Override
    public void deleteOrderedDish(OrderedDish dish) {
        presenter.deleteOrderedDish(dish);
    }

    @Override
    public void askForBill() {
        presenter.askForBill();
    }

    public Double getTotalPrice() {
        return presenter.getTotalPrice();
    }

    @Override
    public boolean isAllServed() {
        return presenter.isAllServed();
    }

    @Override
    public boolean isCannotSent() {
        return presenter.isCannotSent();
    }

    @Override
    public List<String> getFriends() {
        return presenter.getFriends();
    }

    @Override
    public void addDish(OrderedDish dish) {
        presenter.addDish(dish);
    }

    @Override
    public void updateOrderedDish(OrderedDish dish) {}

    @Override
    public void shareToFriends(OrderedDish orderedDish, Map<String, Boolean> nameMap) {
        presenter.shareToFriends(orderedDish, nameMap);
    }

    ;

}
