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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.george.digitalmenu.main.MainActivity.ORDER;

public class MenuPresenter implements MenuContract.Presenter {

    public static final List<Order> PREVIOUS_ORDERS = new ArrayList<>();
    private MenuContract.View view;
    private RestaurantDatabase db;
    private Map<Integer, OrderedDish> orderedDishMap = new HashMap<>();

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
        PREVIOUS_ORDERS.clear();
        ORDER = new Order();
    }

    @Override
    public void createNewOrder() {
        PREVIOUS_ORDERS.add(ORDER);
        ORDER = new Order();
    }

    @Override
    public void listenForOrderedDishUpdate(String id, Consumer<Order> callback) {
        db.listenForSentOrder(id, callback);
    }

    @Override
    public void sendOrder(Order order) {
        db.saveOrder(order, this::onSentComplete);
    }

    @Override
    public void addDish(OrderedDish dish) {
        ORDER.add(dish);
        orderedDishMap.put(dish.getId(), dish);
    }

    @Override
    public OrderedDish updateOrderedDish(OrderedDish updatedOrderedDish) {
        OrderedDish localOrderedDish = orderedDishMap.get(updatedOrderedDish.getId());
        localOrderedDish.setServed(updatedOrderedDish.isServed());
        return localOrderedDish;
    }

    @Override
    public void deleteOrderedDish(OrderedDish dish) {
        ORDER.delete(dish);
        orderedDishMap.remove(dish.getId());
    }

    @Override
    public void askForBill() {
        for (Order order: PREVIOUS_ORDERS) {
            order.setAskingForBill(true);
        }
        db.updateOrderedDishes(PREVIOUS_ORDERS, this::onAskForBillComplete);
        PREVIOUS_ORDERS.clear();
        ORDER =  new Order();
    }

    private void onAskForBillComplete(List<Order> orders) {
        for (Order order: orders) {
            for (OrderedDish orderedDish: order.getDishes()) {
                orderedDishMap.remove(orderedDish.getId());
            }
            db.removeListener(order.getId());
        }
    }

    private void onSentComplete(Order order) {
        for (OrderedDish orderedDish: order.getDishes()) {
            orderedDish.setIsSent(true);
        }
        view.update(order);
        createNewOrder();
        listenForOrderedDishUpdate(order.getId(), this::onServe);
    }

    private void onServe(Order order)  {
        view.update(order);
    }

    private void fetchData(String restaurantName) {
        db.getRestaurant(restaurantName, this::onFetchDataComplete);
    }

    private void onFetchDataComplete(Restaurant r) {
        view.displayMenu(r);
    }
}
