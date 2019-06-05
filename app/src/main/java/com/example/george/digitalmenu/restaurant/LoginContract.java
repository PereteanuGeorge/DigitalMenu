package com.example.george.digitalmenu.restaurant;

public interface LoginContract {
    interface View {

        void switchToTablesActivity(String restaurantName);

        String getEmailInput();

        String getPasswordInput();

        void reportSignInError();

        void promptPasswordInputIsEmpty();

        void promptEmailInputIsEmpty();

        void promptEmailInputNotValid();

        void disableButtonPress();

        void enableButtonPress();
    }

    interface Presenter {

        void registerView(View view);

        void onConfirmInputClicked();

    }
}
