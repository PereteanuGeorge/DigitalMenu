package com.example.george.digitalmenu.utils;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.george.digitalmenu.utils.Utils.roundDouble;
import static com.google.common.primitives.Ints.max;

@IgnoreExtraProperties
public class OrderedDish implements DisplayableDish{
    private DisplayableDish dish;
    private Integer number;
    private Map<String, Boolean> options = new HashMap<>();
    private Boolean ordered = false;

    public OrderedDish() {}

    public OrderedDish(DisplayableDish dish, Integer number, Map<String, Boolean> options, Boolean ordered) {
        this.dish = dish;
        this.number = number;
        this.options = options;
        this.ordered = ordered;
    }

    public OrderedDish(DisplayableDish dish, Integer number) {
        this.dish = dish;
        this.number = number;
        this.options = dish.getOptions();
    }

    public void setDish(DisplayableDish dish) {
        this.dish = dish;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setOptions(Map<String, Boolean> options) {
        this.options = options;
    }

    public void setOrdered(Boolean ordered) {
        this.ordered = ordered;
    }

    @Exclude
    public DisplayableDish getDish() {
        return dish;
    }

    @Exclude @Override
    public List<String> getTags() {
        return null;
    }

    @Exclude @Override
    public String getPic_url() {
        return dish.getPic_url();
    }

    @Exclude @Override
    public String getDescription() {
        return dish.getDescription();
    }

    @Exclude @Override
    public void setPicture(byte[] picture) {
        dish.setPicture(picture);
    }

    @Exclude @Override
    public List<Tag> getEnumTags() {
        return dish.getEnumTags();
    }

    @Exclude @Override
    public boolean isOrdered() {
        return ordered;
    }

    @Exclude
    public byte[] getPicture() {
        return dish.getPicture();
    }


    public String getName() {
        return dish.getName();
    }

    @Override
    public double getPrice() {
        return roundDouble(number*dish.getPrice(), 2);
    }

    @Override
    public List<String> getCategories() {
        return dish.getCategories();
    }

    @Override
    public String getCurrency() {
        return dish.getCurrency();
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

    public Integer getNumber() {
        return this.number;
    }

    public void setIsOrdered(boolean b) {
        this.ordered = b;
    }

    public void setNumber(int nr) {
        this.number = nr;
    }
}
