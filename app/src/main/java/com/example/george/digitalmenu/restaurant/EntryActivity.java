package com.example.george.digitalmenu.restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.main.MainActivity;
import com.example.george.digitalmenu.utils.ServiceRegistry;

import static com.example.george.digitalmenu.restaurant.LoginActivity.TABLES_INTENT_KEY;

public class EntryActivity extends AppCompatActivity implements EntryContract.View {

    private EntryContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loading_screen);

        presenter = ServiceRegistry.getInstance().getService(EntryContract.Presenter.class);
        presenter.registerView(this);

        presenter.onViewCompleteCreate();
    }

    @Override
    public void displayCustomerRestaurantOptions() {
        setContentView(R.layout.activity_entry);
        setUpButtons();
    }

    private void setUpButtons() {
        Button customerButton = findViewById(R.id.customerButton);
        Button restaurantButton = findViewById(R.id.restaurantButton);

        customerButton.setOnClickListener(v -> presenter.onCustomerButtonClick());

        restaurantButton.setOnClickListener(v -> presenter.onRestauantButtonClick());
    }

    @Override
    public void switchToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void switchToLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void switchToTablesActivity(String restaurantName) {
        Intent intent = new Intent(this, TablesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        /* Dynamically fetch restaurant name. */
        intent.putExtra(TABLES_INTENT_KEY, restaurantName);
        startActivity(intent);
    }
}
