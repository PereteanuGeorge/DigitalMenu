package com.example.george.digitalmenu;

import android.icu.util.Currency;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class Dish {
    private String name;
    private String pic_url;
    private String description;
    private double price;
    private List<String> categories;
    private List<String> tags; // ? do not to if it can be converted to this type
    private String currency;

    /*
    private List<String> sides; // ? create side class
    private String time_start; // ? use local variable
    private String time_end; // ? same as above
    private List<DayOfWeek> days;// ? is it usable
    */

    private byte[] picture;


    public Dish() {}

    public Dish(String name, String pic_url, String description, Double price, List<String> categories, List<String> tags) {
        this.name = name;
        this.pic_url = pic_url;
        this.description = description;
        this.price = price;
        this.categories = categories;
        this.tags = tags;
        this.currency = Currency.getInstance(Locale.UK).toString(); //need to change
    }

    public List<String> getTags() {
        return tags;
    }

    public String getName() {
        return name;
    }

    public String getPic_url() {
        return pic_url;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public byte[] getPicture() {
        return picture;
    }

    public String getCurrency() {
        return this.currency;
    }

    public List<Tag> getEnumTags() {
        List<Tag> etags = new ArrayList<>();
        for (String t: tags) {
            try {
                etags.add(Tag.valueOf(t));
            } catch (Exception e) {
                continue;
            }
        }
        return etags;
    }
}
