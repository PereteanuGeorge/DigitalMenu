package com.example.george.digitalmenu.restaurant;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.utils.ServiceRegistry;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    LoginContract.Presenter presenter;

    private static final String TAG = "LoginActivity";
    public static final String TABLES_INTENT_KEY = "LoginActivity";

    private Button confirmButton;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = ServiceRegistry.getInstance().getService(LoginContract.Presenter.class);
        presenter.registerView(this);

        setContentView(R.layout.activity_login);

        confirmButton = findViewById(R.id.confirmButton);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);


        findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onConfirmInputClicked();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void switchToTablesActivity(String restaurantName) {
        Log.d(TAG, restaurantName);
        Intent intent = new Intent(this, TablesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        /* Dynamically fetch restaurant name. */
        intent.putExtra(TABLES_INTENT_KEY, restaurantName);
        startActivity(intent);
    }

    @Override
    public String getEmailInput() {
        return emailEditText.getText().toString().trim();
    }

    @Override
    public String getPasswordInput() {
        return passwordEditText.getText().toString().trim();
    }

    @Override
    public void reportSignInError() {
        passwordEditText.getText().clear();
        new AlertDialog.Builder(LoginActivity.this)
            .setTitle("Log In Failed")
            .setMessage("Please check your email and password.")
            .show();
    }

    @Override
    public void promptPasswordInputIsEmpty() {
        passwordEditText.requestFocus();
    }

    @Override
    public void promptEmailInputIsEmpty() {
        emailEditText.requestFocus();
    }

    @Override
    public void promptEmailInputNotValid() {
        emailEditText.requestFocus();
    }

    @Override
    public void disableButtonPress() {
        confirmButton.setPressed(true);
        confirmButton.setEnabled(false);
    }

    @Override
    public void enableButtonPress() {
        confirmButton.setPressed(false);
        confirmButton.setEnabled(true);
    }
}
