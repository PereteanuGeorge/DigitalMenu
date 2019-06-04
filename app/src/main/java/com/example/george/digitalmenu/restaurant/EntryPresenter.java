package com.example.george.digitalmenu.restaurant;

public class EntryPresenter implements EntryContract.Presenter {

    EntryContract.View view;

    @Override
    public void registerView(EntryContract.View view) {
        this.view = view;
    }

    @Override
    public void onViewCompleteCreate() {
        
    }

    @Override
    public void onCustomerButtonClick() {
        view.switchToMainActivity();
    }

    @Override
    public void onRestauantButtonClick() {
        view.switchToLoginActivity();
    }
}
