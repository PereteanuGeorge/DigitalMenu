package com.example.george.digitalmenu;


// Adapter interface for database.
public interface RestaurantDatabase {

    void getRestaurantEntry(String key);
    void getRestaurant(String restaurant);
}
