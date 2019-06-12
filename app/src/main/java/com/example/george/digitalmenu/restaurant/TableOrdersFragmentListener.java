package com.example.george.digitalmenu.restaurant;

import com.example.george.digitalmenu.utils.OrderedDish;

import java.util.List;

public interface TableOrdersFragmentListener {

    void onOrderedDishesServed(int tableNumber, List<OrderedDish> orderedDishes);

    void onAllDishesFromOrderServed(int tableNumber);

    void onClearTable(int tableNumber);
}