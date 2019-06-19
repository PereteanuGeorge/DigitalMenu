package com.example.george.digitalmenu.restaurant;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.utils.OrderedDish;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TableOrdersFragment extends Fragment {

    private LinearLayout tableOrdersContainer;
    private List<TableOrdersFragmentListener> listeners = new ArrayList<>();
    private Button serveButton;
    private Deque<OrderedDish> undisplayedOrderedDishes = new ArrayDeque<>();
    private Set<OrderedDishCard> dishesOnDisplay = new HashSet<>();
    private List<OrderedDishCard> unservedOrderedDishes = new ArrayList<>();
    private List<OrderedDishCard> dishesOnServingTray = new ArrayList<>();

    private int tableNumber;

    private static final String TABLE_NUMBER_KEY = "TableNumber";

    private boolean initialised = false;

    public TableOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tableNumber = getArguments().getInt(TABLE_NUMBER_KEY);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_table_orders, container, false);
        tableOrdersContainer = rootView.findViewById(R.id.table_orders_container);
        TextView tableNumberText = rootView.findViewById(R.id.table_number_text);
        tableNumberText.setText("Table " + tableNumber);
        serveButton = rootView.findViewById(R.id.serve_button);
        serveButton.setEnabled(false);
        return rootView;
    }

    public static TableOrdersFragment newInstance(Integer tableNumber) {
        TableOrdersFragment fragment = new TableOrdersFragment();
        Bundle args = new Bundle();
        args.putInt(TABLE_NUMBER_KEY, tableNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public void resetFragment() {
        undisplayedOrderedDishes.clear();
        dishesOnServingTray.clear();
        unservedOrderedDishes.clear();
        dishesOnDisplay.clear();

        setActionButtonToServe();
        serveButton.setEnabled(false);
    }

    private void onServeButtonClicked() {

        for (OrderedDishCard dish : dishesOnServingTray) {
            dish.serve();
        }

        notifyServedOrders(dishesOnServingTray);
        dishesOnServingTray.clear();

        if (unservedOrderedDishes.isEmpty()) {
            /* change serve button to pay */
            setActionButtonToClear();
        }
    }

    private void onClearTableButtonClick() {
        Log.d("TableOrdersFragment", "TIME TO PAYYYYY!");
        /* Display loading screen. */

        notifyClearTable();
    }

    private void notifyClearTable() {
        for (TableOrdersFragmentListener listener : listeners) {
            listener.onClearTable(tableNumber);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        synchronized (undisplayedOrderedDishes) {
            initialised = true;
            while (!undisplayedOrderedDishes.isEmpty()) {
                addOrderedDish(undisplayedOrderedDishes.pollFirst());
            }
        }
    }

    public void registerListener(TableOrdersFragmentListener listener) {
        this.listeners.add(listener);
    }

    private OrderedDishCard displayOrderedDish(OrderedDish orderedDish) {
        /* Register new OrderedDishCard which is associated with the view */
        OrderedDishCard unservedDish = new OrderedDishCard(orderedDish);

        /* Display overall information of unserved dish. */
        unservedDish.display(getActivity());

        /* If orderedDish has already been served, show as served. */
        if (orderedDish.isServed()) {
            unservedDish.showAsServed();
        } else {
            unservedOrderedDishes.add(unservedDish);
            setActionButtonToServe();
        }

        return unservedDish;
    }

    private void setActionButtonToServe() {
        serveButton.setEnabled(true);
        serveButton.setText("SERVE");
        serveButton.setOnClickListener(v -> onServeButtonClicked());
    }

    private void setActionButtonToClear() {
        serveButton.setEnabled(true);
        serveButton.setText("Clear");
        serveButton.setOnClickListener(v -> onClearTableButtonClick());
    }

    public void addOrderedDish(OrderedDish orderedDish) {

        if (!initialised) {
            synchronized (undisplayedOrderedDishes) {
                undisplayedOrderedDishes.add(orderedDish);
            }
            return;
        }

        dishesOnDisplay.add(displayOrderedDish(orderedDish));

        serveButton.setEnabled(true);

        if (!unservedOrderedDishes.isEmpty()) {
            setActionButtonToServe();
        } else {
            setActionButtonToClear();
        }
    }

    private void onUnservedOrderEntrySelected(View selectedEntry,
                                              OrderedDishCard unservedOrderedDish) {

        unservedOrderedDish.select();

        if (dishesOnServingTray.isEmpty()) {
            serveButton.setEnabled(false);
        } else {
            serveButton.setEnabled(true);
        }

    }

    private void notifyServedOrders(List<OrderedDishCard> servedDishes) {

        /* Unwrapping OrderedDishCard classes */
        List<OrderedDish> orderedDishes = new ArrayList<>(servedDishes.size());
        for (OrderedDishCard dish : servedDishes) {
            orderedDishes.add(dish.getOrderedDish());
        }

        for (TableOrdersFragmentListener listener: listeners) {
            listener.onOrderedDishesServed(tableNumber, orderedDishes);
//            if (unservedOrderedDishes.isEmpty()) {
//                listener.onAllDishesFromOrderServed(tableNumber);
//            }
        }
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void destroyDisplayedOrderedDishes() {
        for (OrderedDishCard card : dishesOnDisplay) {
            card.destroy();
        }
    }

    /* Inner class representation of a Ordered Dish Entry in fragment. */
    private class OrderedDishCard {

        private View rootView;
        private final Drawable originalBackground;
        private final Drawable buttonPressedBackground;
        private final Drawable servedBackground;
        private final OrderedDish orderedDish;
        boolean selectedToServe = false;

        public OrderedDishCard(OrderedDish dish) {
            this.originalBackground = getResources().getDrawable(R.drawable.dish_card_border);
            this.orderedDish = dish;
            this.servedBackground = getResources().getDrawable(R.drawable.dish_card_border_served);
            this.buttonPressedBackground = getResources().getDrawable(R.drawable.dish_card_border_pressed);
        }

        public OrderedDish getOrderedDish() {
            return orderedDish;
        }

        public void select() {
            selectedToServe = !selectedToServe;

            if (selectedToServe) {
                rootView.setBackground(buttonPressedBackground);
                dishesOnServingTray.add(this);
            } else {
                rootView.setBackground(originalBackground);
                dishesOnServingTray.remove(this);
            }
        }

        public boolean isSelected() {
            return selectedToServe;
        }

        public void serve() {
            unservedOrderedDishes.remove(this);

            showAsServed();
        }

        public void display(FragmentActivity activity) {
            LayoutInflater inflater = activity.getLayoutInflater();

            ConstraintLayout orderEntry = (ConstraintLayout) inflater.inflate(R.layout.table_order_entry, tableOrdersContainer, false);
            this.rootView = orderEntry;

            orderEntry.setOnClickListener(v -> onUnservedOrderEntrySelected(v, this));

            TextView quantityText = orderEntry.findViewById(R.id.order_dish_quantity);
            quantityText.setText(String.valueOf(orderedDish.getNumber()));

            TextView nameText = orderEntry.findViewById(R.id.order_dish_name);
            nameText.setText(orderedDish.getName());

//            TextView priceText = orderEntry.findViewById(R.id.order_dish_price);
//            priceText.setText(String.valueOf(orderedDish.getPrice()));

            LinearLayout orderDishOptionsList = orderEntry.findViewById(R.id.order_dish_options_list);

            for (Map.Entry<String, Boolean> e : orderedDish.getOptions().entrySet()) {
                if (e.getValue()) {
                    TextView entry = (TextView) inflater.inflate(R.layout.order_dish_option_entry, orderDishOptionsList, false);
                    entry.setText(e.getKey());

                    orderDishOptionsList.addView(entry);
                }
            }

            tableOrdersContainer.addView(orderEntry);
        }

        public void showAsServed() {
            rootView.setBackground(servedBackground);
            rootView.setClickable(false);
        }

        public void destroy() {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }
}