package com.example.george.digitalmenu;

import java.time.DayOfWeek;
import java.util.List;

class Dish {
    private String name;
    private String pic_url;
    private byte[] picture;

    public Dish() {}

    public Dish(String name, String pic_url) {
        this.name = name;
        this.pic_url = pic_url;
    }

    public String getName() {
        return name;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }
}
