package com.example.george.digitalmenu.restaurant;

import android.support.v4.util.Consumer;

import com.example.george.digitalmenu.utils.RestaurantDatabase;
import com.example.george.digitalmenu.utils.ServiceRegistry;

public class EntryPresenter implements EntryContract.Presenter {

    private EntryContract.View view;
    private RestaurantDatabase db;

    public EntryPresenter() {
        db = ServiceRegistry.getInstance().getService(RestaurantDatabase.class);
    }

    @Override
    public void registerView(EntryContract.View view) {
        this.view = view;
    }

    @Override
    public void onViewCompleteCreate() {

//        if (db.alreadySignedIn()) {
//            checkUserTypeAndGoNextScreen();
//        } else {
            view.displayCustomerRestaurantOptions();
//        }
    }

    private void checkUserTypeAndGoNextScreen() {

        Consumer<String> success = s -> view.switchToTablesActivity(s);

        Runnable failure = () -> view.switchToMainActivity();

        db.getSignedInUserRestaurantName(success, failure);
    }

    @Override
    public void onCustomerButtonClick() {
        view.switchToFakeNameActivity();
//        view.switchToMainActivity();
    }

    @Override
    public void onRestauantButtonClick() {
        view.switchToLoginActivity();
    }
}
