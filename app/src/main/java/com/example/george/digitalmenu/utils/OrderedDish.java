package com.example.george.digitalmenu.utils;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

import static com.example.george.digitalmenu.utils.OrderStatus.ADDED;
import static com.example.george.digitalmenu.utils.OrderStatus.READY;
import static com.example.george.digitalmenu.utils.OrderStatus.SENT;
import static com.example.george.digitalmenu.utils.OrderStatus.SERVED;
import static com.example.george.digitalmenu.utils.OrderStatus.SHARED;
import static com.example.george.digitalmenu.utils.Utils.roundDouble;
import static java.lang.StrictMath.max;

@IgnoreExtraProperties
public class OrderedDish {
    private Dish dish = new Dish();

    private String name;
    private Integer number;
    private Map<String, Boolean> options = new HashMap<>();
    private Double price;
    private boolean isAdded = false;
    private boolean isServed = false;
    private boolean isSent = false;
    private String id;
    private Integer sharingNumber;
    private boolean isShared = false;
    private boolean isManageable = true;
    private OrderStatus status;
    private Map<String, Boolean> nameMap = new HashMap<>();

    public OrderedDish() {}

    public OrderedDish(String name, Integer number, Map<String, Boolean> options,
                       Double price, Boolean isServed, String id) {
        this.name = name;
        this.number = number;
        this.options = options;
        this.price = price;
        this.isServed = isServed;
        this.id = id;
    }

    // Adding order
    public OrderedDish(Dish dish) {
        this.dish = dish;
        this.name = dish.getName();
        this.number = 1;
        this.options = dish.getOptions();
        this.price = dish.getPrice();
        this.isAdded = false;
        this.id = IdGenerator.generate();
        this.sharingNumber = 1;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Map<String, Boolean> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Boolean> options) {
        this.options = options;
    }

    public Double getPrice() {
        if (dish != null) {
                this.price = this.number * dish.getPrice();
        }
        return roundDouble(price, 2);
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isServed() {
        return isServed;
    }

    public void setServed(boolean isServed) {
        this.isServed = isServed;
    }

    public void increment() {
        number++;
    }

    public void decrement() {
        number = max(1, --number);
    }

    public void put(String text, boolean checked) {
        options.put(text, checked);
    }


    @Exclude
    public void setIsAdded() {
        this.isAdded = true;
    }

    @Exclude
    public Boolean isAdded() {
        return isAdded;
    }

    @Exclude
    public byte[] getPicture() {
        return dish.getPicture();
    }

    @Exclude
    public String getDescription() {
        return dish.getDescription();
    }

    public String getCurrency() {
        return dish.getCurrency();
    }

    @Exclude
    public void setIsSent(Boolean isSent) {
        this.isSent = isSent;
    }

    @Exclude
    public Boolean isSent() {
        return this.isSent;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public OrderStatus getStatus() {
        if (isShared && !isManageable) {
            this.status = SHARED;
        } else if (isServed) {
            this.status = SERVED;
        } else if (isSent) {
            this.status = SENT;
        } else if (isAdded) {
            this.status = ADDED;
        } else  {
            this.status = READY;
        }
        return this.status;
    }

    public void setSharingNumber(Integer sharingNumber) {
        this.sharingNumber = sharingNumber;
    }

    @Exclude
    public Integer getSharingNumber() {
        return this.sharingNumber;
    }

    @Exclude
    public void setDish(Dish dish) {
        this.dish = dish;
    }

    @Exclude
    public void setIsShared(boolean isShared) {
        this.isShared = isShared;
    }

    @Exclude
    public boolean isShared() {
        return isShared;
    }

    @Exclude
    public void setIsManageable(boolean isManageable) {
        this.isManageable = isManageable;
    }

    @Exclude
    public boolean isManageable() {
        return this.isManageable;
    }

    @Exclude
    public boolean isOptionOperatable() {
        if (isServed) return false;
        if (isSent) return  false;
        if (isShared && !isManageable) return false;
        return true;
    }

    @Exclude
    public Map<String, Boolean> getNameMap() {
        return this.nameMap;
    }

    @Exclude
    public void setNameMap(Map<String, Boolean> nameMap) {
        this.nameMap = nameMap;
    }

    public static class IdGenerator {

        private static int count;

        public static String generate() {
            return String.valueOf(++count);
        }
    }
}
