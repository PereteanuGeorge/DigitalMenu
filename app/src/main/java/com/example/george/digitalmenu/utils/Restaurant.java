package com.example.george.digitalmenu.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.george.digitalmenu.utils.RestaurantFirestore.MAX_DOWNLOAD_SIZE_BYTES;

public class Restaurant {
    private String address;
    private List<String> categories;
    private String telephone;
    private String website;
    private List<Dish> dishes;
    private String pic_url;
    private Integer numberOfTables;

    private byte[] picture = new byte[MAX_DOWNLOAD_SIZE_BYTES];

    public Restaurant() {}

    public Restaurant(String address, List<String> categories, String telephone, String website,
                      List<Dish> dishes, String pic_url, Integer numberOfTables) {
        this.address = address;
        this.categories = categories;
        this.telephone = telephone;
        this.website = website;
        this.dishes = dishes;
        this.pic_url = pic_url;
        this.numberOfTables = numberOfTables;
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
            List<String> cate = d.getCategories();
            if (cate == null) cate = new ArrayList<>();
            for (String c: cate){
                List<Dish> dishList = map.get(c);
                if (dishList == null) dishList = new ArrayList<>();
                dishList.add(d);
                map.put(c,dishList);
            }
        }
        return map;
    }

    public Integer getNumberOfTables() {
        return numberOfTables;
    }

    public void setNumberOfTables(Integer numberOfTables) {
        this.numberOfTables = numberOfTables;
    }
}
