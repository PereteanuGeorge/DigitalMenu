package com.example.george.digitalmenu.menu;

import com.example.george.digitalmenu.utils.OrderedDish;

import java.util.List;
import java.util.Map;

interface BoardFragmentListener {
    void addDish(OrderedDish dish);

    void deleteOrderedDish(OrderedDish dish);

    void updateOrderedDish(OrderedDish orderedDish);

    void shareToFriends(OrderedDish orderedDish, Map<String, Boolean> nameMap);

    List<String> getFriends();
}
