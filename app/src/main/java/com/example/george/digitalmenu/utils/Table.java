package com.example.george.digitalmenu.utils;

import java.util.ArrayList;
import java.util.List;

public class Table {

    private List<String> users = new ArrayList<>();
    private Integer tableID;

    public Table() {
    }

    public Table(List<String> users, Integer tableID) {
        this.users = users;
        this.tableID = tableID;
    }

    public Table(Integer tableNumber) {
        this.tableID = tableNumber;
    }

    public void add(String username) {
        users.add(username);
    }

    public Integer getTableID() {
        return tableID;
    }

    public void setTableID(Integer tableID) {
        this.tableID = tableID;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
