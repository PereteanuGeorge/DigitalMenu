package com.example.george.digitalmenu;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

class Restaurant {
    private String address;
    private List<String> categories;
    private String telephone;
    private String website;
    private List<Dish> dishes;
    private String pic_url;

    private byte[] picture;

    public Restaurant() {}

    public Restaurant(String address, List<String> categories, String telephone, String website,
                      List<Dish> dishes, String pic_url) {
        this.address = address;
        this.categories = categories;
        this.telephone = telephone;
        this.website = website;
        this.dishes = dishes;
        this.pic_url = pic_url;
    }

    public String getAddress() {
        return address;
    }

    public List<String> getCategories() {
        return categories;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getWebsite() {
        return website;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public byte[] getPicture() {
        return picture;
    }

    public Map<String, List<Dish>> getDishesForCategories() {
        Map<String, List<Dish>> map = new HashMap<>();
        for (Dish d: dishes) {
            for (String c: d.getCategories()) {
                map.get(c).add(d);
            }
        }
        return map;
    }
}
