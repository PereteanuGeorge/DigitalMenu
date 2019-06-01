package com.example.george.digitalmenu.utils;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<OrderedDish> dishes = new ArrayList<>();

    public void add(OrderedDish dish) {
        dishes.add(dish);
    }

    public void clean() {
        dishes.clear();
    }
}
