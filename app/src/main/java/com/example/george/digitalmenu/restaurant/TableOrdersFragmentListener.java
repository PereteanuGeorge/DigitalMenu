package com.example.george.digitalmenu.restaurant;

import com.example.george.digitalmenu.utils.OrderedDish;

public interface TableOrdersFragmentListener {

    void onFragmentReady();

    void onOrderServed(OrderedDish orderedDish);
}