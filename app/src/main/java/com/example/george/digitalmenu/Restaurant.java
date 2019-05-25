package com.example.george.digitalmenu;

import java.util.List;

class Restaurant {
    private String address;
    private List<String> categories;
    private String telephone;
    private String website;
    private List<Dish> dishes;

    public Restaurant() {}

    public Restaurant(String address, List<String> categories, String telephone, String website, List<Dish> dishes) {
        this.address = address;
        this.categories = categories;
        this.telephone = telephone;
        this.website = website;
        this.dishes = dishes;
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
}
