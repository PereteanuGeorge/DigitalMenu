package com.example.george.digitalmenu.utils;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

import static java.lang.StrictMath.max;

@IgnoreExtraProperties
public class OrderedDish {
    private Dish dish;
    private String name;
    private Integer number;
    private Map<String, Boolean> options = new HashMap<>();
    private Double price;
    private boolean ordered;

    public OrderedDish() {}

    public OrderedDish(String name, Integer number, Map<String, Boolean> options, Double price) {
        this.name = name;
        this.number = number;
        this.options = options;
        this.price = price;
    }

    // Adding order
    public OrderedDish(Dish dish) {
        this.dish = dish;
        this.name = dish.getName();
        this.number = 1;
        this.options = dish.getOptions();
        this.price = dish.getPrice();
        this.ordered = false;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Map<String, Boolean> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Boolean> options) {
        this.options = options;
    }

    public Double getPrice() {
        if (dish != null) {
            this.price = this.number * dish.getPrice();
        }
        return Utils.roundDouble(price, 2);
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void increment() {
        number++;
    }

    public void decrement() {
        number = max(0, --number);
    }

    public void put(String text, boolean checked) {
        options.put(text, checked);
    }


    @Exclude
    public void setIsOrdered() {
        this.ordered = true;
    }

    @Exclude
    public Boolean isOrdered() {
        return ordered;
    }

    @Exclude
    public byte[] getPicture() {
        return dish.getPicture();
    }

    @Exclude
    public String getDescription() {
        return dish.getDescription();
    }

    public String getCurrency() {
        return dish.getCurrency();
    }
}
