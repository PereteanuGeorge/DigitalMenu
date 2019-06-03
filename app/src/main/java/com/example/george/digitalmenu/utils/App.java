package com.example.george.digitalmenu.utils;

import android.app.Application;
import android.content.Intent;

import com.example.george.digitalmenu.main.MainActivity;
import com.example.george.digitalmenu.menu.MenuContract;
import com.example.george.digitalmenu.menu.MenuPresenter;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initServiceRegistry();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void initServiceRegistry() {
        ServiceRegistry registry = ServiceRegistry.getInstance();

        registry.registerService(RestaurantDatabase.class, new RestaurantFirestore());
        registry.registerService(MenuContract.Presenter.class, new MenuPresenter());

    }
}