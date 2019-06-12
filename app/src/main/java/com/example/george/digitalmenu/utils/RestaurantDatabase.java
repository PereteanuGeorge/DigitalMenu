package com.example.george.digitalmenu.utils;


import android.graphics.Bitmap;
import android.support.v4.util.Consumer;

import java.util.List;

// Adapter interface for database.
public interface RestaurantDatabase {

    void getRestaurant(String restaurant, Consumer<Restaurant> callback);

    void downloadDishPicture(Dish dish, final Consumer<Bitmap> callback);

    void downloadThemePicture(Restaurant restaurant, Consumer<Bitmap> callback);

    void downloadTagPicture(Tag tag, Consumer<Bitmap> callback);

    void signInWithEmailAndPassword(String email, String password, Runnable success, Runnable failure);

    void getSignedInUserRestaurantName(Consumer<String> success, Runnable failure);

    void init(Runnable success, Runnable failure);

    void saveOrder(Order order, Consumer<Order> callback);

    void listenForCustomerOrders(String restaurantName, Consumer<Order> callback);
    void updateOrderedDishes(String restaurantName, List<Order> orders);

    void updateOrderedDishes(List<Order> orders, Consumer<List<Order>> callback);

    boolean alreadySignedIn();

    void listenForSentOrder(String id, Consumer<Order> callback);

    void removeOrders(List<Order> orders, Runnable onRemovedOrders);
    String getRestaurantName();

    void removeListener(String id);
    void saveTable(String username, Integer tableNumber);

    void listenForTableWithId(Integer tableNumber, Consumer<Table> callback);
}
