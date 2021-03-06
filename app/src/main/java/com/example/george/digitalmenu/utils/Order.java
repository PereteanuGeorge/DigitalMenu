package com.example.george.digitalmenu.utils;

import com.example.george.digitalmenu.restaurant.FakeNameActivity;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

import static com.example.george.digitalmenu.utils.Utils.roundDouble;

public class Order {
    private Double totalPrice = 0.0;
    private List<OrderedDish> dishes = new ArrayList<>();
    public static Integer tableNumber;
    private String id;
    private String name;
    private Boolean isAskingForBill = false;

    public Order() {}

    public Order(Double totalPrice, List<OrderedDish> dishes, Integer tableNumber, Boolean isAskingForBill, String name) {
        this.totalPrice = totalPrice;
        this.dishes = dishes;
        this.tableNumber = tableNumber;
        this.isAskingForBill = isAskingForBill;
        this.name = name;
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
            price += (dish.getPrice() / dish.getSharingNumber());
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
        dish.setIsAdded();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void deleteWithId(String id) {
        OrderedDish target = new OrderedDish();
        for (OrderedDish dish: dishes) {
            if (dish.getId().equals(id)) {
                target = dish;
                break;
            }
        }
        delete(target);
    }
}
