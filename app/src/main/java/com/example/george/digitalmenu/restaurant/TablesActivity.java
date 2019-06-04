package com.example.george.digitalmenu.restaurant;

import android.content.Intent;
import android.support.v4.util.Consumer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.utils.Restaurant;
import com.example.george.digitalmenu.utils.RestaurantDatabase;
import com.example.george.digitalmenu.utils.ServiceRegistry;

public class TablesActivity extends AppCompatActivity {

    private RestaurantDatabase db;
    private String restaurantName;
    private LinearLayout tableEntry;

    public TablesActivity() {
        this.db = ServiceRegistry.getInstance().getService(RestaurantDatabase.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables);

        Intent intent = getIntent();
        this.restaurantName = intent.getStringExtra(LoginActivity.TABLES_INTENT_KEY);

        db.getRestaurant(restaurantName, this::fetchThemePicture);

        db.getNumberOfTables(restaurantName, new Consumer<Integer>() {
            @Override
            public void accept(Integer numberOfTables) {
                displayTables(numberOfTables);
            }
        });
    }

    private void displayTables(Integer numberOfTables) {

        for (int i = 0; i < numberOfTables; i++) {
            displayTable(i);
        }

    }

    private void displayTable(Integer tableNumber) {

        LinearLayout tableList = findViewById(R.id.table_list);

        LayoutInflater inflater = getLayoutInflater();
        this.tableEntry = (LinearLayout) inflater.inflate(R.layout.table_entry, tableList, false);

        TextView tableNumberText = tableEntry.findViewById(R.id.table);
        tableNumberText.setText("Table " + tableNumber);

        db.listenForOrders(restaurantName, this::notifyAndStartTime);

        tableEntry.setId(View.generateViewId());
        tableList.addView(tableEntry);
    }

    private void notifyAndStartTime(Integer numberOfItems) {

        TextView itemsText = tableEntry.findViewById(R.id.number_of_items);
        itemsText.setText(numberOfItems + " items");

        TextView notification = tableEntry.findViewById(R.id.notification_order);
        notification.setVisibility(View.VISIBLE);
        hideNotificationOnClick();

        Chronometer chronometer = findViewById(R.id.simpleChronometer);
        chronometer.setVisibility(View.VISIBLE);
        chronometer.start();

    }

    private void hideNotificationOnClick() {
        tableEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableEntry.findViewById(R.id.notification_order).setVisibility(View.INVISIBLE);
            }
        });
    }

    private void fetchThemePicture(Restaurant r) {

        ImageView restaurantLogo = findViewById(R.id.restaurantLogo);
        db.downloadThemePicture(r, bm -> restaurantLogo.setImageBitmap(bm));

    }
}
