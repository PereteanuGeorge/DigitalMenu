package com.example.george.digitalmenu.utils;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

import static com.example.george.digitalmenu.utils.Utils.roundDouble;

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

    public void add(OrderedDish dish) {
        dishes.add(dish);
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

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = roundDouble(totalPrice, 2);
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
