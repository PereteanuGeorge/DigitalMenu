package com.example.george.digitalmenu.utils;

import android.app.Application;
import android.content.Intent;

import com.example.george.digitalmenu.main.MainActivity;
import com.example.george.digitalmenu.menu.MenuContract;
import com.example.george.digitalmenu.menu.MenuPresenter;
import com.example.george.digitalmenu.restaurant.EntryActivity;
import com.example.george.digitalmenu.restaurant.EntryContract;
import com.example.george.digitalmenu.restaurant.EntryPresenter;
import com.example.george.digitalmenu.restaurant.LoginContract;
import com.example.george.digitalmenu.restaurant.LoginPresenter;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initServiceRegistry();

        Intent intent = new Intent(this, EntryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void initServiceRegistry() {
        ServiceRegistry registry = ServiceRegistry.getInstance();

        RestaurantFirestore firestore = new RestaurantFirestore();

        /* Order is important here. MenuPresenter uses RestaurantDatabase service, etc. */
        registry.registerService(RestaurantDatabase.class, firestore);
        registry.registerService(MenuContract.Presenter.class, new MenuPresenter());

        registry.registerService(EntryContract.Presenter.class, new EntryPresenter());

        registry.registerService(LoginContract.Presenter.class, new LoginPresenter());

    }
}