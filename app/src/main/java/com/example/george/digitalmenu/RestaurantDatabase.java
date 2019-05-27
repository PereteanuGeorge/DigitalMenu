package com.example.george.digitalmenu;


import android.graphics.Bitmap;

import java.util.function.Consumer;

// Adapter interface for database.
public interface RestaurantDatabase {

    void getRestaurantEntry(String key);
    void getRestaurant(String restaurant, Consumer<Restaurant> callback);
    void downloadDishPicture(Dish dish, final Consumer<Bitmap> callback);
    void downloadThemePicture(Restaurant restaurant, Consumer<Bitmap> callback);
}
