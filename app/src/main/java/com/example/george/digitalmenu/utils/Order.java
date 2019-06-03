package com.example.george.digitalmenu.utils;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<OrderedDish> dishes = new ArrayList<>();

    public void add(OrderedDish dish, int numberOfPortions) {
        for(int i = 0; i < numberOfPortions; i++) {
            dishes.add(dish);
        }
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
        return roundDouble(price, 2);
    }

    public void delete(OrderedDish dish, int counter) {
        for(int i = 0; i < counter; i++) {
            dishes.remove(dish);
        }
    }

    public static double roundDouble(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
