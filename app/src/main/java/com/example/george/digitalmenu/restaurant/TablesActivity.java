package com.example.george.digitalmenu.restaurant;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.utils.Order;
import com.example.george.digitalmenu.utils.Restaurant;
import com.example.george.digitalmenu.utils.RestaurantDatabase;
import com.example.george.digitalmenu.utils.ServiceRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TablesActivity extends AppCompatActivity {

    private RestaurantDatabase db;
    private String restaurantName;
    private LinearLayout[] tableEntries;

    public static List[] orderToTable;

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

        db.listenForOrders(restaurantName, this::notifyAndStartTime);

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
        int tableNumber = order.getTableNumber();

        orderToTable[tableNumber].add(order);

        TextView itemsText = tableEntries[tableNumber - 1].findViewById(R.id.number_of_items);
        itemsText.setVisibility(View.VISIBLE);

        for (Object ordered : orderToTable[tableNumber]) {
            numberOfItems = ((Order) ordered).getDishes().size();
        }

        itemsText.setText(numberOfItems + " items");

        TextView notification = tableEntries[tableNumber - 1].findViewById(R.id.notification_order);
        notification.setText(orderToTable[tableNumber].size() + "");
        notification.setVisibility(View.VISIBLE);
        hideNotificationOnClick(tableNumber);

        Chronometer chronometer = tableEntries[tableNumber - 1].findViewById(R.id.simpleChronometer);
        chronometer.setVisibility(View.VISIBLE);
        chronometer.stop();
        if (orderToTable[tableNumber].isEmpty()) {
            chronometer.setBase(SystemClock.elapsedRealtime());
        }
        chronometer.start();
    }

    private void hideNotificationOnClick(int tableNumber) {
        tableEntries[tableNumber - 1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableEntries[tableNumber - 1].findViewById(R.id.notification_order).setVisibility(View.INVISIBLE);
            }
        });
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
        orderToTable = new List[numberOfTables];

        for (int i = 0; i < numberOfTables; i++) {
            orderToTable[i] = new ArrayList<Order>();

        }
        displayTables(numberOfTables);
    }

}
