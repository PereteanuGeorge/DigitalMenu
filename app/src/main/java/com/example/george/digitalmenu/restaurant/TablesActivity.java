package com.example.george.digitalmenu.restaurant;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.menu.OrderPageFragment;
import com.example.george.digitalmenu.utils.Dish;
import com.example.george.digitalmenu.utils.Order;
import com.example.george.digitalmenu.utils.OrderedDish;
import com.example.george.digitalmenu.utils.Restaurant;
import com.example.george.digitalmenu.utils.RestaurantDatabase;
import com.example.george.digitalmenu.utils.ServiceRegistry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TablesActivity extends AppCompatActivity implements TableOrdersFragmentListener {

    private RestaurantDatabase db;
    private String restaurantName;
    private LinearLayout[] tableEntries;

    public static Map<Order, Integer> orderToTable = new HashMap<>();

    TableOrdersFragment fragment;

    public TablesActivity() {
        this.db = ServiceRegistry.getInstance().getService(RestaurantDatabase.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables);

        Intent intent = getIntent();
        this.restaurantName = intent.getStringExtra(LoginActivity.TABLES_INTENT_KEY);

//        db.getRestaurant(restaurantName, this::onReceiveRestaurantResponse);

        displayFragment();

//        db.listenForOrders(restaurantName, this::notifyAndStartTime);

    }

    private void displayFragment() {
        fragment = new TableOrdersFragment();
        fragment.registerListener(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.tablesActivityRoot, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void displayTables(Integer numberOfTables) {

        for (int i = 1; i <= numberOfTables; i++) {
            displayTable(i);
        }

    }

    private void displayTable(Integer tableNumber) {

        LinearLayout tableList = findViewById(R.id.table_list);

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout tableEntry = (LinearLayout) inflater.inflate(R.layout.table_entry, tableList, false);

        TextView tableNumberText = tableEntry.findViewById(R.id.table);
        tableNumberText.setText("Table " + tableNumber);

        tableEntries[tableNumber - 1] = tableEntry;

        tableEntry.setId(View.generateViewId());
        tableList.addView(tableEntry);
    }

    private void notifyAndStartTime(Order order) {

        int numberOfItems = order.getOrderedDishes().size();
//        int tableNumber = order.getTableNumber();

//        TextView itemsText = tableEntries[tableNumber - 1].findViewById(R.id.number_of_items);
//        itemsText.setText(numberOfItems + " items");

//        TextView notification = tableEntries[tableNumber - 1].findViewById(R.id.notification_order);
//        notification.setVisibility(View.VISIBLE);
        hideNotificationOnClick();

        Chronometer chronometer = findViewById(R.id.simpleChronometer);
        chronometer.setVisibility(View.VISIBLE);
        chronometer.start();

//        orderToTable.put(order, tableNumber);
    }

    private void hideNotificationOnClick() {
//        tableEntries[tableNumber - 1].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tableEntries[tableNumber - 1].findViewById(R.id.notification_order).setVisibility(View.INVISIBLE);
//            }
//        });
    }

    private void onReceiveRestaurantResponse(Restaurant r) {

        displayThemePicture(r);

        displayTableEntries(r);
    }

    private void displayThemePicture(Restaurant r) {
        ImageView restaurantLogo = findViewById(R.id.restaurantLogo);
        db.downloadThemePicture(r, bm -> restaurantLogo.setImageBitmap(bm));
    }

    private void displayTableEntries(Restaurant r) {
        int numberOfTables = r.getNumberOfTables();
        tableEntries = new LinearLayout[numberOfTables];
        displayTables(numberOfTables);
    }

    @Override
    public void onFragmentReady() {

        OrderedDish orderedDish = new OrderedDish(new Dish("Spicy Fries",
            "test_url",
            "test_description",
            20.0d,
            Arrays.asList("catOne","catTwo"),
            Arrays.asList("tagOne","tagTwo"),
            Arrays.asList("optionOne","optionTwo")), 5);

        orderedDish.put("No spice", true);
        orderedDish.put("No fries", true);

        fragment.addOrderedDish(orderedDish);
    }

    @Override
    public void onOrderServed(OrderedDish orderedDish) {

    }
}
