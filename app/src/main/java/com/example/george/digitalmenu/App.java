package com.example.george.digitalmenu;

import android.app.Application;
import android.app.Service;
import android.content.Intent;

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
