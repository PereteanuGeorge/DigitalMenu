package com.example.george.digitalmenu.utils;

public class Utils {
    public static Double roundDouble(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String truncText(String text, int length) {
        if (text.length() < length) {
            return text;
        }
        return text.substring(0, length-1) + "...";
    }
}
