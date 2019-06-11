package com.example.george.digitalmenu.menu;

import com.example.george.digitalmenu.utils.Order;
import com.example.george.digitalmenu.utils.OrderedDish;

interface FragmentListener {

    void sendOrder(Order order);

    void createNewOrder();

    void goBack();

    void deleteOrderedDish(OrderedDish dish);
}
