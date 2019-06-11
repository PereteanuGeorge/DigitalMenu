package com.example.george.digitalmenu.menu;

import com.example.george.digitalmenu.utils.OrderedDish;

interface FragmentListener {

    void sendOrder();

    void goBack();

    void deleteOrderedDish(OrderedDish dish);

    Double getTotalPrice();

    Integer getConfirmState();
}
