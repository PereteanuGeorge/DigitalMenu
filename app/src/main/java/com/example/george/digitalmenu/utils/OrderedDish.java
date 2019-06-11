package com.example.george.digitalmenu.utils;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

import static com.example.george.digitalmenu.utils.Utils.roundDouble;
import static java.lang.StrictMath.max;

@IgnoreExtraProperties
public class OrderedDish {
    private Dish dish = new Dish();

    private String name;
    private Integer number;
    private Map<String, Boolean> options = new HashMap<>();
    private Double price;
    private boolean ordered = false;
    private boolean isServed = false;
    private boolean isSent = false;
    private Integer id;

    public OrderedDish() {}

    public OrderedDish(String name, Integer number, Map<String, Boolean> options,
                       Double price, Boolean isServed, Integer id) {
        this.name = name;
        this.number = number;
        this.options = options;
        this.price = price;
        this.isServed = isServed;
        this.id = id;
    }

    // Adding order
    public OrderedDish(Dish dish) {
        this.dish = dish;
        this.name = dish.getName();
        this.number = 1;
        this.options = dish.getOptions();
        this.price = dish.getPrice();
        this.ordered = false;
        this.id = IdGenerator.generate();
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
        return roundDouble(price, 2);
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

    public boolean isServed() {
        return isServed;
    }

    public void setServed(boolean isServed) {
        this.isServed = isServed;
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

    @Exclude
    public void setIsSent(Boolean isSent) {
        this.isSent = isSent;
    }

    @Exclude
    public Boolean isSent() {
        return this.isSent;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Exclude
    public Integer getStatus() {
        if (isServed) {
            return 2;
        }
        if (isSent) {
            return 1;
        }
        return 0;
    }

    public static class IdGenerator {

        private static int count;

        public static int generate() {
            return ++count;
        }
    }
}
