package com.example.george.digitalmenu.restaurant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.main.MainActivity;
import com.example.george.digitalmenu.utils.ServiceRegistry;

public class EntryActivity extends AppCompatActivity implements EntryContract.View {

    private EntryContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        presenter = ServiceRegistry.getInstance().getService(EntryContract.Presenter.class);
        presenter.registerView(this);

        setUpButtons();

        presenter.onViewCompleteCreate();
    }

    private void setUpButtons() {
        Button customerButton = findViewById(R.id.customerButton);
        Button restaurantButton = findViewById(R.id.restaurantButton);

        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onCustomerButtonClick();
            }
        });

        restaurantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onRestauantButtonClick();
            }
        });
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
}
