package com.example.george.digitalmenu.menu;

import com.example.george.digitalmenu.utils.OrderedDish;

import java.util.List;
import android.support.v4.util.Consumer;

interface FragmentListener {

    void sendOrder();

    void createNewOrder();

    void listenForOrderedDishUpdate(String id, Consumer<List<OrderedDish>> callback);
}
