package com.example.george.digitalmenu;

import com.google.type.DayOfWeek;

import java.util.List;

class Dish {
    private String name;
    private String pic_url;
    private String description;
    private Double price;
    private List<String> categories;
    private List<Tag> tags; // ? do not to if it can be converted to this type

    /*
    private List<String> sides; // ? create side class
    private String time_start; // ? use local variable
    private String time_end; // ? same as above
    private List<DayOfWeek> days;// ? is it usable
    */

    private byte[] picture;

    public Dish() {}

    public Dish(String name, String pic_url, String description, Double price, List<String> categories, List<Tag> tags) {
        this.name = name;
        this.pic_url = pic_url;
        this.description = description;
        this.price = price;
        this.categories = categories;
        this.tags = tags;
    }

    public List<Tag> getTags() {
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

    public Double getPrice() {
        return price;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }
}
