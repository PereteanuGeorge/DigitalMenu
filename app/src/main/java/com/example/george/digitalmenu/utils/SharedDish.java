package com.example.george.digitalmenu.utils;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class SharedDish {
    List<String> users = new ArrayList<>();
    OrderedDish orderedDish = new OrderedDish();
    private String manager;

    public SharedDish() {}

    public SharedDish(OrderedDish orderedDish, List<String> users, String manager) {
        this.orderedDish = orderedDish;
        this.users = users;
        this.manager = manager;
    }

    public  List<String>  getUsers() {
        return users;
    }

    public OrderedDish getOrderedDish() {
        return orderedDish;
    }

    @Exclude
    public Integer getSharingNumber() {
        return users.size();
    }

    @Exclude
    public boolean isShareTo(String userName) {
        for (String string: users) {
            if (string.equals(userName)) {
                return true;
            }
        }
        return false;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public void setOrderedDish(OrderedDish orderedDish) {
        this.orderedDish = orderedDish;
    }

    public String getManager() {
        return this.manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }
}
