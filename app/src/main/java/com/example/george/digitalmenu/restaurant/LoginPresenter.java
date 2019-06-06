package com.example.george.digitalmenu.restaurant;


import android.support.v4.util.Consumer;

import com.example.george.digitalmenu.utils.RestaurantDatabase;
import com.example.george.digitalmenu.utils.ServiceRegistry;

public class LoginPresenter implements LoginContract.Presenter {

    RestaurantDatabase db;
    LoginContract.View view;

    public LoginPresenter() {
        db = ServiceRegistry.getInstance().getService(RestaurantDatabase.class);
    }

    @Override
    public void registerView(LoginContract.View view) {
        this.view = view;
    }

    @Override
    public void onConfirmInputClicked() {

        view.disableButtonPress();

        String email = view.getEmailInput();
        String password = view.getPasswordInput();

        if (email.length() < 1) {
            view.enableButtonPress();
            view.promptEmailInputIsEmpty();
            return;
        }

        if (password.length() < 1) {
            view.enableButtonPress();
            view.promptPasswordInputIsEmpty();
            return;
        }

        Runnable success = this::onSignInSuccess;

        Runnable failure = () -> {
            view.enableButtonPress();
            view.reportSignInError();
        };

        db.signInWithEmailAndPassword(email, password, success, failure);
    }

    private void onSignInSuccess() {

        Consumer<String> success = s -> view.switchToTablesActivity(s);

        Runnable failure = () -> { };

        db.getSignedInUserRestaurantName(success, failure);
    }
}
