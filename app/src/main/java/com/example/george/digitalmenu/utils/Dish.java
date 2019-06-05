package com.example.george.digitalmenu.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.george.digitalmenu.utils.RestaurantFirestore.MAX_DOWNLOAD_SIZE_BYTES;
import static com.example.george.digitalmenu.utils.Utils.roundDouble;

@IgnoreExtraProperties
public class Dish implements Parcelable, DisplayableDish {

    private String name;
    private String pic_url;
    private String description;
    private double price;
    private List<String> categories = new ArrayList<>();
    private List<String> tags = new ArrayList<>(); // ? do not to if it can be converted to this type
    private String currency;
    private List<String> options = new ArrayList<>();

    private byte[] picture = new byte[MAX_DOWNLOAD_SIZE_BYTES];
    private boolean downloaded = false;

    public static final Parcelable.Creator<Dish> CREATOR = new Parcelable.Creator<Dish>() {

        @Override
        public Dish createFromParcel(Parcel source) {
            return new Dish(source);
        }

        @Override
        public Dish[] newArray(int size) {
            return new Dish[size];
        }

    };

    public Dish() {}

    public Dish(Parcel parcel) {
        this.name = parcel.readString();
        this.pic_url = parcel.readString();
        this.description = parcel.readString();
        this.price = parcel.readDouble();
        parcel.readStringList(this.categories);
        parcel.readStringList(tags);
        this.currency = parcel.readString();
        parcel.readStringList(this.options);
        picture = parcel.createByteArray();
        this.downloaded = parcel.readByte() != 0;
    }

    public Dish(String name, String pic_url, String description, Double price,
                List<String> categories, List<String> tags, List<String> options) {
        this.name = name;
        this.pic_url = pic_url;
        this.description = description;
        this.price = roundDouble(price, 2);
        this.categories = categories;
        this.tags = tags;
        this.options = options;
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
        dest.writeStringList(options);
        dest.writeByteArray(picture);
        dest.writeByte((byte) (downloaded ? 1 : 0));
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Double price) {
        this.price = roundDouble(price, 2);
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }


    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    @Override
    public List<String> getTags() {
        return tags;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPic_url() {
        return pic_url;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public double getPrice() {
        return roundDouble(price, 2);
    }

    @Override
    public List<String> getCategories() {
        return categories;
    }

    @Override
    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    @Override
    public byte[] getPicture() {
        return picture;
    }

    @Override
    public String getCurrency() {
        return "$";
    }

    @Exclude @Override
    public List<Tag> getEnumTags() {
        List<Tag> etags = new ArrayList<>();
        for (String t : tags) {
            etags.add(Tag.valueOf(t));
        }
        return etags;
    }

    @Override
    public Integer getNumber() {
        return 1;
    }

    @Override
    public boolean isOrdered() {
        return false;
    }

    @Override
    public Map<String, Boolean> getOptions() {
        Map<String, Boolean> optionsMap = new HashMap<>();
        for (String s: options) {
            optionsMap.put(s, false);
        }
        return optionsMap;
    }

    @Override
    public void put(String text, boolean checked) {
        return;
    }

    @Override
    public void increment() {}

    @Override
    public void decrement() {}

    @Override
    public void setIsOrdered(boolean b) { }

    @Override
    public int describeContents() {
        return 0;
    }
}
