package com.example.george.digitalmenu.menu;

import android.graphics.Bitmap;
import android.support.v4.util.Consumer;

import com.example.george.digitalmenu.utils.Dish;
import com.example.george.digitalmenu.utils.OrderedDish;
import com.example.george.digitalmenu.utils.Restaurant;
import com.example.george.digitalmenu.utils.Tag;

import java.util.List;

public interface MenuContract {
    interface Presenter {

        void registerView(View view);

        void fetchDishImage(Dish d, Consumer<Bitmap> callback);

        void fetchTagImage(Tag t, Consumer<Bitmap> callback);

        void fetchThemeImage(Restaurant r, Consumer<Bitmap> callback);

        void onViewCompleteCreate();

        void onDishItemClick(Dish d);

        void cleanOrder();

        void sendOrder();

        void createNewOrder();

        void listenForOrderedDishUpdate(String id, Consumer<List<OrderedDish>> callback);
    }

    interface View {

        void displayInfoFood(Dish dish);

        String getRestaurantName();

        void displayMenu(Restaurant r);

        void notifyModelInitFailure();
    }
}
