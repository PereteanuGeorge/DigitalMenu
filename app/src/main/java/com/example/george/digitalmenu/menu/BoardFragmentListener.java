package com.example.george.digitalmenu.menu;

import com.example.george.digitalmenu.utils.OrderedDish;

interface BoardFragmentListener {
    void addDish(OrderedDish dish);

    void deleteOrderedDish(OrderedDish dish);
}
