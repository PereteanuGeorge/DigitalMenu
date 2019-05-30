package com.example.george.digitalmenu;

import android.graphics.Bitmap;
import android.support.v4.util.Consumer;

public interface MenuContract {
    interface Presenter {

        void fetchDishImage(Dish d, Consumer<Bitmap> callback);

        void fetchTagImage(Tag t, Consumer<Bitmap> callback);

        void fetchThemeImage(Restaurant r, Consumer<Bitmap> callback);

        void onViewCompleteCreate();

        void onDishItemClick(Dish d);
    }

    interface View {

        void displayInfoFood(Dish dish);

        String getRestaurantName();

        void displayMenu(Restaurant r);

        void notifyModelInitFailure();
    }
}
