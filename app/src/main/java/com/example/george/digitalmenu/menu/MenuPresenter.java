package com.example.george.digitalmenu.menu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.Consumer;

import com.example.george.digitalmenu.utils.Dish;
import com.example.george.digitalmenu.utils.Order;
import com.example.george.digitalmenu.utils.OrderedDish;
import com.example.george.digitalmenu.utils.Restaurant;
import com.example.george.digitalmenu.utils.RestaurantDatabase;
import com.example.george.digitalmenu.utils.ServiceRegistry;
import com.example.george.digitalmenu.utils.Tag;

import java.util.ArrayList;
import java.util.List;

import static com.example.george.digitalmenu.main.MainActivity.ORDER;

public class MenuPresenter implements MenuContract.Presenter {

    public static final List<Order> PREVIOUS_ORDERS = new ArrayList<>();
    private MenuContract.View view;
    private RestaurantDatabase db;

    public MenuPresenter() {
        this.db = ServiceRegistry.getInstance().getService(RestaurantDatabase.class);
    }

    @Override
    public void registerView(MenuContract.View view) {
        this.view = view;
    }


    @Override
    public void fetchDishImage(Dish dish, Consumer<Bitmap> callback) {
        if (!dish.isDownloaded()) {
            db.downloadDishPicture(dish, callback);
            dish.setDownloaded(true);
        }
        callback.accept(BitmapFactory.decodeByteArray(dish.getPicture(),0, dish.getPicture().length));
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

    @Override
    public void cleanOrder() {
        ORDER.clean();
    }

    @Override
    public void sendOrder(Runnable callback) {
        db.saveOrder(ORDER, callback);
    }

    @Override
    public void createNewOrder() {
        for (OrderedDish  orderedDish: ORDER.getDishes()) {
            orderedDish.setIsSent();
        }
        PREVIOUS_ORDERS.add(ORDER);
        ORDER = new Order();
    }

    @Override
    public void listenForOrderedDishUpdate(String id, Consumer<List<OrderedDish>> callback) {
        db.listenForSentOrder(id, callback);
    }

    private void fetchData(String restaurantName) {
        db.getRestaurant(restaurantName, this::onFetchDataComplete);
    }

    private void onFetchDataComplete(Restaurant r) {
        view.displayMenu(r);
    }
}
