package com.example.george.digitalmenu.utils;

import java.util.List;
import java.util.Map;

import static com.google.common.primitives.Ints.max;

public class OrderedDish implements DisplayableDish{
    private final Map<String, Boolean> options;
    DisplayableDish dish;
    Integer number;
    private boolean ordered = false;

    public OrderedDish(DisplayableDish dish, Integer number) {
        this.dish = dish;
        this.number = number;
        this.options = dish.getOptions();
    }

    @Override
    public List<String> getTags() {
        return null;
    }

    public String getName() {
        return dish.getName();
    }

    @Override
    public String getPic_url() {
        return dish.getPic_url();
    }

    @Override
    public String getDescription() {
        return dish.getDescription();
    }

    @Override
    public double getPrice() {
        return number*dish.getPrice();
    }

    @Override
    public List<String> getCategories() {
        return null;
    }

    @Override
    public void setPicture(byte[] picture) {
        dish.setPicture(picture);
    }

    @Override
    public String getCurrency() {
        return dish.getCurrency();
    }

    @Override
    public List<Tag> getEnumTags() {
        return null;
    }

    @Override
    public Integer getPortion() {
        return this.number;
    }

    @Override
    public boolean isOrdered() {
        return ordered;
    }

    @Override
    public Map<String, Boolean> getOptions() {
        return options;
    }

    @Override
    public void put(String text, boolean checked) {
        options.put(text, checked);
    }

    @Override
    public void increment() {
        number++;
    }

    @Override
    public void decrement() {
        number = max(--number, 1);
    }

    public byte[] getPicture() {
        return dish.getPicture();
    }

    public Integer getNumber() {
        return this.number;
    }

    public void setIsOrdered(boolean b) {
        this.ordered = b;
    }
}
