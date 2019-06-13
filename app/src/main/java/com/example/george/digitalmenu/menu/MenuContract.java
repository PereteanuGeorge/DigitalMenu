package com.example.george.digitalmenu.menu;

import android.graphics.Bitmap;
import android.support.v4.util.Consumer;

import com.example.george.digitalmenu.utils.Dish;
import com.example.george.digitalmenu.utils.Order;
import com.example.george.digitalmenu.utils.OrderedDish;
import com.example.george.digitalmenu.utils.Restaurant;
import com.example.george.digitalmenu.utils.Table;
import com.example.george.digitalmenu.utils.Tag;

import java.util.List;
import java.util.Map;

public interface MenuContract {
    interface Presenter {

        void registerView(View view);

        void fetchDishImage(Dish d, Consumer<Bitmap> callback);

        void fetchTagImage(Tag t, Consumer<Bitmap> callback);

        void fetchThemeImage(Restaurant r, Consumer<Bitmap> callback);

        void onViewCompleteCreate();

        void onDishItemClick(Dish d);

        void cleanOrder();

        void createNewOrder();

        void saveUserToTable(String username, Integer tableNumber);

        void listenForTableWithId(Integer tableNumber,  Consumer<Table> callback);

        void setTable(Table table);
        void listenForOrderedDishUpdate(String id, Consumer<Order> callback);
        

        void addDish(OrderedDish dish);

        OrderedDish updateOrderedDish(OrderedDish updatedOrderedDish);

        void deleteOrderedDish(OrderedDish dish);

        void askForBill();
        
        Double getTotalPrice();

        List<OrderedDish> getOrderedDishes();

        void sendOrder();

        boolean isAllServed();

        boolean isCannotSent();

        void shareToFriends(OrderedDish orderedDish, Map<String, Boolean> nameMap);

        void setUserName(String stringExtra);

        List<String> getFriends();

        void leaveRestaurant();

        String getUserName();

        void onMenuDisplayed();
    }

    interface View {

        void displayInfoFood(Dish dish);

        String getRestaurantName();

        void displayMenu(Restaurant r);

        void notifyModelInitFailure();

        void update(Order order);

        void updateWithEverythingIsServed();

        void updateWithAddedDish(OrderedDish dish);

        void showLoadingScreen();

        void hideLoadingScreen();
        void updatePrice();

    }
}
