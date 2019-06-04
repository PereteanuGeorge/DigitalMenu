package com.example.george.digitalmenu.utils;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private Double totalPrice = 0.0;
    private List<OrderedDish> dishes = new ArrayList<>();
    private Integer tableNumber = 5;

    public Order() {}

    public Order(Double totalPrice, List<OrderedDish> dishes, Integer tableNumber) {
        this.totalPrice = totalPrice;
        this.dishes = dishes;
        this.tableNumber = tableNumber;
    }

    public void add(OrderedDish dish, int numberOfPortions) {
        for(int i = 0; i < numberOfPortions; i++) {
            dishes.add(dish);
        }
    }

    public void clean() {
        dishes.clear();
    }

    @Exclude
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

    public void delete(OrderedDish dish) {
        dishes.remove(dish);
    }

    public static double roundDouble(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<OrderedDish> getDishes() {
        return dishes;
    }

    public void setDishes(List<OrderedDish> dishes) {
        this.dishes = dishes;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }
}
