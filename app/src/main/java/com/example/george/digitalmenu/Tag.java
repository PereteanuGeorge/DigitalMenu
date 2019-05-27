package com.example.george.digitalmenu;

enum Tag {
    GLUTEN_FREE("Gluten Free"),
    HALAL("Halal"),
    KOSHER("Kosher"),
    VEGAN("Vegan"),
    VEGETARIAN("Vegetarian"),
    MILD("Mild"),
    HOT("Hot"),
    K_HOT("K-Hot"),
    BLAZING("Blazing"),
    Atomic("Atomic");

    private String text;

    Tag(String text) {
        this.text = text;
    }
}
