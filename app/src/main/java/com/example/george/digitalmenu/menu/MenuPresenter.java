package com.example.george.digitalmenu.menu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.Consumer;

import com.example.george.digitalmenu.utils.Dish;
import com.example.george.digitalmenu.utils.Order;
import com.example.george.digitalmenu.utils.OrderStatus;
import com.example.george.digitalmenu.utils.OrderedDish;
import com.example.george.digitalmenu.utils.Restaurant;
import com.example.george.digitalmenu.utils.RestaurantDatabase;
import com.example.george.digitalmenu.utils.ServiceRegistry;
import com.example.george.digitalmenu.utils.SharedDish;
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
            return;
        }
        callback.accept(BitmapFactory.decodeByteArray(dish.getPicture(), 0, dish.getPicture().length));
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
        view.showLoadingScreen();

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
        view.updateWithDeletedDishWithId(dish.getId());
    }

    @Override
    public void askForBill() {
        for (Order order: previousOrders) {
            order.setAskingForBill(true);
        }
        db.updateOrderedDishes(previousOrders, this::onAskForBillComplete);
    }

    private void onAskForBillComplete(List<Order> orders) {
        detachListeners(orders);
        cleanOrder();
    }

    private void detachListeners(List<Order> orders) {
        for (Order order : orders) {
            for (OrderedDish orderedDish : order.getDishes()) {
                orderedDishMap.remove(orderedDish.getId());
            }
            db.removeListener(order.getId());
        }
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
        orderedDishes.addAll(sharedOrder.getDishes());
        return orderedDishes;
    }



    @Override
    public boolean isAllServed() {
        if (previousOrders.isEmpty()) return false;
        if (currentOrder.isEmpty()) {
            for (OrderedDish dish: getOrderedDishes()) {
                if (!(dish.getStatus().equals(OrderStatus.SHARED)) && !dish.isServed()) {
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

        if (Order.tableNumber < 1 || Order.tableNumber > r.getNumberOfTables()) {
            view.notifyInvalidTable();
            return;
        }

        setRestaurant(r);
        saveUserToTable(userName, Order.tableNumber);
        table.setTableID(Order.tableNumber);
        setListenToTableForNewUsers();
        setListenToTableForSharedDish();
        setListenToSharedOrders();
        view.displayMenu(r);
    }

    private void setListenToSharedOrders() {
        db.listenForRemovedSharedDishes(table.getTableID(), this::removeSharedDishWithId);
    }

    public void setUserName(String userName) {
        this.userName = userName;
        currentOrder.setName(userName);
    }


    @Override
    public void leaveRestaurant() {
        detachListeners(previousOrders);
        cleanOrder();
        db.removeUserFromTable(userName, table.getTableID());
        db.removeSharedOrderListener();
        db.removeRemovingShareOrderListener();
        db.removeNewUserListener();
        restaurant = new Restaurant();
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void onMenuDisplayed() {
        view.hideLoadingScreen();
    }

    @Override
    public void deleteOrderedDishWithCounter(OrderedDish orderedDish, int deleteCounter) {
        for (int i = 1; i <= deleteCounter; i++) {
            orderedDish.decrement();
        }
        if (orderedDish.getNumber() == 0) {
            deleteOrderedDish(orderedDish);
        } else {
            view.updateWithModifiedDish(orderedDish);
        }
    }

    @Override
    public void onInvalidTableDialogOkPressed() {
        view.restartMainActivity();
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
            orderedDish.setNameMap(getNameMap(sharedDish.getUsers()));
            if (!userName.equals(sharedDish.getManager())) {
                orderedDish.setIsManageable(false);
                addSharedDish(orderedDish);
            } else {
                orderedDish.setIsManageable(true);
                addDish(orderedDish);
            }
        }
    }

    private Map<String, Boolean> getNameMap(List<String> users) {
        Map<String, Boolean> nameMap = new HashMap<>();
        for (String user: getFriends()) {
            nameMap.put(user, false);
        }
        for (String user: users) {
            nameMap.put(user, true);
        }
        return nameMap;
    }

    private void addSharedDish(OrderedDish orderedDish) {
        sharedOrder.add(orderedDish);
        orderedDishMap.put(orderedDish.getId(), orderedDish);
        view.updateWithAddedDish(orderedDish);
    }

    private void removeSharedDishWithId(String id) {
        sharedOrder.deleteWithId(id);
        orderedDishMap.remove(id);
        view.updateWithDeletedDishWithId(id);
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
