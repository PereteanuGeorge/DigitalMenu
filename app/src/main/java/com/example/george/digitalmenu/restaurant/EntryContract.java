package com.example.george.digitalmenu.restaurant;

public interface EntryContract {
    interface View {

        void switchToMainActivity();

        void switchToLoginActivity();
    }

    interface Presenter {

        void registerView(EntryContract.View view);

        void onViewCompleteCreate();

        void onCustomerButtonClick();

        void onRestauantButtonClick();
    }
}
