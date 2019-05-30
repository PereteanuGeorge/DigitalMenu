package com.example.george.digitalmenu;

import android.graphics.Bitmap;
import android.support.v4.util.Consumer;

public class MenuPresenter implements MenuContract.Presenter {

    private MenuContract.View view;
    private RestaurantDatabase db;

    public MenuPresenter(MenuContract.View view, RestaurantDatabase db) {
        this.view = view;
        this.db = db;
    }

    @Override
    public void fetchFoodImage(Dish d, Consumer<Bitmap> callback) {
        db.downloadDishPicture(d, callback);
    }

    @Override
    public void fetchTagImage(Tag t, Consumer<Bitmap> callback) {
        db.downloadTagPicture(t, callback);
    }

    @Override
    public void fetchThemeImage(Restaurant r, Consumer<Bitmap> callback) {
        db.downloadThemePicture(r, callback);
    }

    @Override
    public void onViewCompleteCreate() {
        String restaurantName = view.getRestaurantName();

        Runnable success = () -> fetchData(restaurantName);

        Runnable failure = () -> view.notifyModelInitFailure();

        db.init(success, failure);
    }

    @Override
    public void onDishItemClick(Dish d) {
        view.displayInfoFood(d);
    }

    private void fetchData(String restaurantName) {
        db.getRestaurant(restaurantName, this::onFetchDataComplete);
    }

    private void onFetchDataComplete(Restaurant r) {
        view.displayMenu(r);
    }
}
