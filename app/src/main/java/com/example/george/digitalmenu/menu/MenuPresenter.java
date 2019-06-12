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
import static com.example.george.digitalmenu.utils.Utils.roundDouble;

public class MenuPresenter implements MenuContract.Presenter {

    private  Order currentOrder = new Order();
    private List<Order> previousOrders = new ArrayList<>();
    private MenuContract.View view;
    private RestaurantDatabase db;
    private Table table = new Table();
    private Map<String, OrderedDish> orderedDishMap = new HashMap<>();
    private Restaurant restaurant = new Restaurant();
    private String userName;
    private Order sharedOrder = new Order();

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
        sharedOrder = new Order();
        currentOrder = new Order();
        currentOrder.setName(userName);
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
    public void sendOrder() {
        db.saveOrder(currentOrder, this::onSentComplete);
    }

    @Override
    public void addDish(OrderedDish dish) {
        currentOrder.add(dish);
        orderedDishMap.put(dish.getId(), dish);
        view.updateWithAddedDish(dish);
    }

    @Override
    public OrderedDish updateOrderedDish(OrderedDish updatedOrderedDish) {
        OrderedDish localOrderedDish = orderedDishMap.get(updatedOrderedDish.getId());
        localOrderedDish.setServed(updatedOrderedDish.isServed());
        return localOrderedDish;
    }

    @Override
    public void deleteOrderedDish(OrderedDish dish) {
        if (dish.isShared()) {
            db.removeSharedDishWithId(dish, table.getTableID(), this::deleteOrderedDish);
            return;
        }
        currentOrder.delete(dish);
        orderedDishMap.remove(dish.getId());
        view.updatePrice();
    }

    @Override
    public void askForBill() {
        for (Order order: previousOrders) {
            order.setAskingForBill(true);
        }
        db.updateOrderedDishes(previousOrders, this::onAskForBillComplete);
    }

    private void onAskForBillComplete(List<Order> orders) {
        for (Order order : orders) {
            for (OrderedDish orderedDish : order.getDishes()) {
                orderedDishMap.remove(orderedDish.getId());
            }
            db.removeListener(order.getId());
        }
        previousOrders.clear();
        sharedOrder.clean();
        currentOrder =  new Order();
    }

    public Double getTotalPrice() {
        Double sum = 0.0;
        for (Order order: previousOrders) {
                sum += order.getTotalPrice();
        }
        sum += sharedOrder.getTotalPrice();
        sum += currentOrder.getTotalPrice();
        return roundDouble(sum,2);
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



    @Override
    public boolean isAllServed() {
        if (previousOrders.isEmpty()) return false;
        if (currentOrder.isEmpty()) {
            for (OrderedDish dish: getOrderedDishes()) {
                if (!dish.isServed()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isCannotSent() {
        return currentOrder.isEmpty() && previousOrders.isEmpty();
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
        if (isAllServed()) {
            view.updateWithEverythingIsServed();
        }
    }

    private void fetchData(String restaurantName) {
        db.getRestaurant(restaurantName, this::onFetchDataComplete);
    }

    private void onFetchDataComplete(Restaurant r) {
        setRestaurant(r);
        saveUserToTable(userName, Order.tableNumber);
        table.setTableID(Order.tableNumber);
        setListenToTableForNewUsers();
        setListenToTableForSharedDish();
        view.displayMenu(r);
    }

    public void setUserName(String userName) {
        this.userName = userName;
        currentOrder.setName(userName);
    }


    @Override
    public void leaveRestaurant() {
        cleanOrder();
        db.removeUserFromTable(userName, table.getTableID());
        db.removeSharedOrderListener();
    }

    @Override
    public String getUserName() {
        return userName;
    }

    private void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }



    /* Sharing methods */

    @Override
    public List<String> getFriends() {
        List<String> friends = new ArrayList<>();
        for (String name: table.getUsers()) {
            if (!name.equals(userName)) {
                friends.add(name);
            }
        }
        return friends;
    }


    private void setListenToTableForSharedDish() {
        db.listenForTableSharedDish(table.getTableID(), this::onNewSharedDish);
    }

    private void onNewSharedDish(SharedDish sharedDish) {
        if (sharedDish.isShareTo(userName)) {
            OrderedDish orderedDish = sharedDish.getOrderedDish();
            orderedDish.setDish(restaurant.getDishWithName(orderedDish.getName()));
            orderedDish.setSharingNumber(sharedDish.getSharingNumber());
            orderedDish.setIsShared(true);
            if (!userName.equals(sharedDish.getManager())) {
                orderedDish.setIsManageable(false);
                addSharedDish(orderedDish);
            } else {
                orderedDish.setIsManageable(true);
                addDish(orderedDish);
            }
        }
    }

    private void addSharedDish(OrderedDish orderedDish) {
        sharedOrder.add(orderedDish);
        orderedDishMap.put(orderedDish.getId(), orderedDish);
        view.updateWithAddedDish(orderedDish);
    }

    @Override
    public void setTable(Table table) {
        this.table = table;
    }

    @Override
    public void listenForTableWithId(Integer tableNumber, Consumer<Table> callback) {
        db.listenForTableWithId(tableNumber, callback);
    }

    @Override
    public void saveUserToTable(String username, Integer tableNumber) {
        db.saveTable(username, tableNumber);
    }

    private void setListenToTableForNewUsers() {
        listenForTableWithId(Order.tableNumber, this::onNewTableUser);
    }

    private void onNewTableUser(Table table) {
        setTable(table);
    }

    @Override
    public void shareToFriends(OrderedDish orderedDish, Map<String, Boolean> nameMap) {
        SharedDish sharedDish = new SharedDish(orderedDish, getFriendsToShare(nameMap), userName);
        db.uploadSharedDish(table.getTableID(), sharedDish);
    }

    private List<String> getFriendsToShare(Map<String, Boolean> nameMap) {
        List<String> friendsToShare = new ArrayList<>();
        for(Map.Entry<String,Boolean> friends : nameMap.entrySet()) {
            if(friends.getValue()) {
                friendsToShare.add(friends.getKey());
            }
        }
        friendsToShare.add(userName);
        return friendsToShare;
    }

}
