package com.example.george.digitalmenu.utils;

import java.util.List;
import java.util.Map;

public interface DisplayableDish {
    List<String> getTags();

    String getName();

    String getPic_url();

    String getDescription();

    double getPrice();

    List<String> getCategories();

    void setPicture(byte[] picture);

    byte[] getPicture();

    String getCurrency();

    List<Tag> getEnumTags();

    Integer getPortion();

    boolean isOrdered();

    Map<String, Boolean> getOptions();

    void put(String text, boolean checked);
}
