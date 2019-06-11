package com.example.george.digitalmenu.restaurant;

public interface EntryContract {
    interface View {

        void displayCustomerRestaurantOptions();

        void switchToMainActivity();

        void switchToLoginActivity();

        void switchToTablesActivity(String s);

        void switchToFakeNameActivity();
    }

    interface Presenter {

        void registerView(EntryContract.View view);

        void onViewCompleteCreate();

        void onCustomerButtonClick();

        void onRestauantButtonClick();
    }
}
