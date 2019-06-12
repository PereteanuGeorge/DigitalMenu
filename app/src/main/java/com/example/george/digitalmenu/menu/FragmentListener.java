package com.example.george.digitalmenu.menu;

import com.example.george.digitalmenu.utils.Order;
import com.example.george.digitalmenu.utils.OrderedDish;

import java.util.List;

interface FragmentListener {

    void sendOrder();

    void goBack();

    void deleteOrderedDish(OrderedDish dish);

    void askForBill();

    Double getTotalPrice();

    boolean isAllServed();

    boolean isCannotSent();

    List<String> getFriends();
}
