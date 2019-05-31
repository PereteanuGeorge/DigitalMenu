package com.example.george.digitalmenu.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import static com.example.george.digitalmenu.utils.RestaurantFirestore.MAX_DOWNLOAD_SIZE_BYTES;

public class Dish implements Parcelable {

    private String name;
    private String pic_url;
    private String description;
    private double price;
    private List<String> categories = new ArrayList<>();
    private List<String> tags = new ArrayList<>(); // ? do not to if it can be converted to this type
    private String currency;
    private byte[] picture = new byte[MAX_DOWNLOAD_SIZE_BYTES];

    public static final Parcelable.Creator<Dish> CREATOR =  new Parcelable.Creator<Dish>() {

        @Override
        public Dish createFromParcel(Parcel source) {
            return new Dish(source);
        }

        @Override
        public Dish[] newArray(int size) {
            return new Dish[size];
        }
    };
    /*
    private List<String> sides; // ? create side class
    private String time_start; // ? use local variable
    private String time_end; // ? same as above
    private List<DayOfWeek> days;// ? is it usable
    */



    public Dish() {}

    public Dish(Parcel parcel) {
        this.name = parcel.readString();
        this.pic_url = parcel.readString();
        this.description = parcel.readString();
        this.price = parcel.readDouble();
        parcel.readStringList(this.categories);
        parcel.readStringList(tags);
        this.currency = parcel.readString();
        picture = parcel.createByteArray();
    }

    public Dish(String name, String pic_url, String description, Double price, List<String> categories, List<String> tags) {
        this.name = name;
        this.pic_url = pic_url;
        this.description = description;
        this.price = price;
        this.categories = categories;
        this.tags = tags;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getName() {
        return name;
    }

    public String getPic_url() {
        return pic_url;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public byte[] getPicture() {
        return picture;
    }

    public String getCurrency() {
        return "$";
    }

    public List<Tag> getEnumTags() {
        List<Tag> etags = new ArrayList<>();
        for (String t: tags) {
            etags.add(Tag.valueOf(t));
        }
        return etags;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(pic_url);
        dest.writeString(description);
        dest.writeDouble(price);

        dest.writeStringList(categories);
        dest.writeStringList(tags);
        dest.writeString(currency);
        dest.writeByteArray(picture);
    }
}
