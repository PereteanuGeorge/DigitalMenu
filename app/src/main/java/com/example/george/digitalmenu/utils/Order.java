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

    public List<OrderedDish> getOrderedDishes() {
        return dishes;
    }

    public Double getTotalPrice() {
        Double price = 0.0;
        for (OrderedDish dish: dishes) {
            price += dish.getPrice();
        }
        return price;
    }

    public void delete(OrderedDish dish) {
        dishes.remove(dish);
    }
}
