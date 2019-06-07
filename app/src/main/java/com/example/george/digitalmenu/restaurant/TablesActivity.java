package com.example.george.digitalmenu.restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.utils.Order;
import com.example.george.digitalmenu.utils.OrderedDish;
import com.example.george.digitalmenu.utils.Restaurant;
import com.example.george.digitalmenu.utils.RestaurantDatabase;
import com.example.george.digitalmenu.utils.ServiceRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TablesActivity extends AppCompatActivity implements TableOrdersFragmentListener {

    private RestaurantDatabase db;
    private String restaurantName;
    private LinearLayout[] tableEntries;

    private Map<Integer, List<OrderedDish>> tableToOrders = new HashMap<>();
    private TableOrdersFragment fragment;

    public TablesActivity() {
        this.db = ServiceRegistry.getInstance().getService(RestaurantDatabase.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables);

        Intent intent = getIntent();

        this.restaurantName = intent.getStringExtra(LoginActivity.TABLES_INTENT_KEY);

        db.getRestaurant(restaurantName, this::onReceiveRestaurantResponse);
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {

            /* Fragment not in view. */
            super.onBackPressed();

        } else {

            /* Fragment in view. */
            getSupportFragmentManager().popBackStack();
            fragment = null;
        }
    }

    private void displayTables(Integer numberOfTables) {

        for (int i = 1; i <= numberOfTables; i++) {
            displayTable(i);
        }
    }

    private void onTableEntrySelected(View v, Integer tableNumber) {
        v.findViewById(R.id.notification_order).setVisibility(View.INVISIBLE);
        displayFragment(tableNumber);
    }

    private void displayFragment(Integer tableNumber) {
        fragment = TableOrdersFragment.newInstance(tableNumber);
        fragment.registerListener(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.tablesActivityRoot, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        List<OrderedDish> existingOrders = tableToOrders.get(tableNumber);
        for (OrderedDish orderedDish : existingOrders) {
            fragment.addOrderedDish(orderedDish);
        }
    }

    private void displayTable(Integer tableNumber) {

        LinearLayout tableList = findViewById(R.id.table_list);

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout tableEntry = (LinearLayout) inflater.inflate(R.layout.table_entry, tableList, false);

        TextView tableNumberText = tableEntry.findViewById(R.id.table);
        tableNumberText.setText("Table " + tableNumber);

        tableEntries[tableNumber - 1] = tableEntry;

        tableEntry.setOnClickListener(v -> onTableEntrySelected(v, tableNumber));

        tableEntry.setId(View.generateViewId());
        tableList.addView(tableEntry);
    }

    private void onReceiveOrder(Order order) {

//        int numberOfItems = 0;
        int tableNumber = order.getTableNumber();

        for (OrderedDish orderedDish : order.getDishes()) {
            tableToOrders.get(tableNumber).add(orderedDish);

            if (fragment != null && (fragment.getTableNumber() == tableNumber)) {
                fragment.addOrderedDish(orderedDish);
            }

        }

        TextView itemsText = tableEntries[tableNumber - 1].findViewById(R.id.number_of_items);
        itemsText.setVisibility(View.VISIBLE);

//        for (Object ordered : tableToOrders.get(tableNumber)) {
//            numberOfItems += ((Order) ordered).getDishes().size();
//        }

//        itemsText.setText(numberOfItems + " items");

        TextView notification = tableEntries[tableNumber - 1].findViewById(R.id.notification_order);
        notification.setText(tableToOrders.get(tableNumber).size() + "");
        notification.setVisibility(View.VISIBLE);

//        Chronometer chronometer = tableEntries[tableNumber - 1].findViewById(R.id.simpleChronometer);
//        chronometer.setVisibility(View.VISIBLE);
//        chronometer.stop();
//        if (tableToOrders.get(tableNumber).isEmpty()) {
//            chronometer.setBase(SystemClock.elapsedRealtime());
//        }
//        chronometer.start();
    }

    private void onReceiveRestaurantResponse(Restaurant r) {
        initTableOrdersMap(r.getNumberOfTables());
        displayThemePicture(r);
        displayTableEntries(r);
        db.listenForOrders(restaurantName, this::onReceiveOrder);
    }

    private void initTableOrdersMap(int numberOfTables) {
        for (int i = 1; i <= numberOfTables; i++) {
            tableToOrders.put(i, new ArrayList<>());
        }
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

    public void onOrderDishesServed(int tableNumber, List<OrderedDish> servedOrderedDishes) {
        /* TODO: Remove from database. */
        tableToOrders.get(tableNumber).removeAll(servedOrderedDishes);
    }

    @Override
    public void onAllOrdersServed(int tableNumber) {
//        Chronometer c = tableEntries[tableNumber - 1].findViewById(R.id.simpleChronometer);
//        c.stop();
//        c.setBase(SystemClock.elapsedRealtime());
//        c.setVisibility(View.INVISIBLE);
    }
}
