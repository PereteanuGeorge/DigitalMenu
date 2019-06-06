package com.example.george.digitalmenu.restaurant;

import com.example.george.digitalmenu.utils.OrderedDish;

import java.util.List;

public interface TableOrdersFragmentListener {

    void onOrderDishesServed(int tableNumber, List<OrderedDish> orderedDishes);

    void onAllOrdersServed(int tableNumber);

}