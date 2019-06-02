package com.example.george.digitalmenu.utils;

public class OrderedDish {
    Dish dish;
    Integer number;

    public OrderedDish(Dish dish, Integer number) {
        this.dish = dish;
        this.number = number;
    }

    public String getName() {
        return dish.getName();
    }

    public Double getPrice() {
        return number*dish.getPrice();
    }

    public String getCurrency() {
        return dish.getCurrency();
    }

    public byte[] getPicture() {
        return dish.getPicture();
    }

    public Integer getNumber() {
        return this.number;
    }
}
