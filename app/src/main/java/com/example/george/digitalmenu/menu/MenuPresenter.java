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
import com.example.george.digitalmenu.utils.Table;
import com.example.george.digitalmenu.utils.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.george.digitalmenu.main.MainActivity.USERNAME;
import static com.example.george.digitalmenu.utils.Utils.roundDouble;

public class MenuPresenter implements MenuContract.Presenter {

    private  Order currentOrder = new Order();
    private List<Order> previousOrders = new ArrayList<>();
    private MenuContract.View view;
    private RestaurantDatabase db;
    private static Table TABLE = new Table();
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
        previousOrders.clear();
        currentOrder = new Order();
    }

    @Override
    public void createNewOrder() {
        previousOrders.add(currentOrder);
        currentOrder = new Order();
    }

    @Override
    public void listenForOrderedDishUpdate(String id, Consumer<Order> callback) {
        db.listenForSentOrder(id, callback);
    }

    @Override
    public void saveUserToTable(String username, Integer tableNumber) {
        db.saveTable(username, tableNumber);
    }

    @Override
    public void listenForTableWithId(Integer tableNumber, Consumer<Table> callback) {
        db.listenForTableWithId(tableNumber, callback);
    }

    @Override
    public void addNewTable(Table table) {
        TABLE = table;
    }

    @Override
    public void sendOrder() {
        db.saveOrder(currentOrder, this::onSentComplete);
    }

    @Override
    public void addDish(OrderedDish dish) {
        currentOrder.add(dish);
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
        currentOrder.delete(dish);
        orderedDishMap.remove(dish.getId());
    }

    @Override
    public Double getTotalPrice() {
        Double sum = 0.0;
        for (Order order: previousOrders) {
                sum += order.getTotalPrice();
        }
        sum += currentOrder.getTotalPrice();
        return roundDouble(sum,2);
    }

    @Override
    public void setOrderUserName(String text) {
        currentOrder.setName(text);
    }

    @Override
    public List<OrderedDish> getOrderedDishes() {
        List<OrderedDish> orderedDishes = new ArrayList<>();
        for (Order order: previousOrders) {
            orderedDishes.addAll(order.getDishes());
        }
        orderedDishes.addAll(currentOrder.getDishes());
        return orderedDishes;
    }


    //Refactor this
    @Override
    public Integer getConfirmState() {
        if (currentOrder.isEmpty()) {
            return 1;
        }
        return 0;
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
        saveUserToTable(USERNAME, Order.tableNumber);
        setListenToTable();
        view.displayMenu(r);
    }

    private void setListenToTable() {
        listenForTableWithId(Order.tableNumber, this::onNewTable);
    }

    private void onNewTable(Table table) {
        addNewTable(table);
    }
}
