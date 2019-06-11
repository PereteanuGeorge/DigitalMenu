package com.example.george.digitalmenu.utils;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

import static com.example.george.digitalmenu.utils.Utils.roundDouble;

public class Order {
    private Double totalPrice = 0.0;
    private List<OrderedDish> dishes = new ArrayList<>();
    public static Integer tableNumber;
    private String id;
    private Boolean isAskingForBill = false;

    public Order() {}

    public Order(Double totalPrice, List<OrderedDish> dishes, Integer tableNumber, Boolean isAskingForBill) {
        this.totalPrice = totalPrice;
        this.dishes = dishes;
        this.tableNumber = tableNumber;
        this.isAskingForBill = isAskingForBill;
    }


    public void clean() {
        dishes.clear();
    }

    public Boolean getAskingForBill() {
        return isAskingForBill;
    }

    public void setAskingForBill(Boolean askingForBill) {
        isAskingForBill = askingForBill;
    }

    @Exclude
    public List<OrderedDish> getOrderedDishes() {
        return dishes;
    }

    public Double getTotalPrice() {
        Double price = 0.0;
        for (OrderedDish dish : dishes) {
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

    public void add(OrderedDish dish) {
        dishes.add(dish);
        dish.setIsOrdered();
    }

    @Exclude
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public boolean isEmpty() {
        return dishes.isEmpty();
    }
}
