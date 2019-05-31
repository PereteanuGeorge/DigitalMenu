package com.example.george.digitalmenu.utils;


import android.graphics.Bitmap;
import android.support.v4.util.Consumer;

// Adapter interface for database.
public interface RestaurantDatabase {

    void getRestaurant(String restaurant, Consumer<Restaurant> callback);
    void downloadDishPicture(Dish dish, final Consumer<Bitmap> callback);
    void downloadThemePicture(Restaurant restaurant, Consumer<Bitmap> callback);
    void downloadTagPicture(Tag tag, Consumer<Bitmap> callback);

    void init(Runnable success, Runnable failure);
}
