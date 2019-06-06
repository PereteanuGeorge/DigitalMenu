package com.example.george.digitalmenu.restaurant;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.utils.Order;
import com.example.george.digitalmenu.utils.OrderedDish;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class TableOrdersFragment extends Fragment {

    private LinearLayout tableOrdersContainer;
    private List<TableOrdersFragmentListener> listeners = new ArrayList<>();
    private Button serveButton;
    private Deque<Order> undisplayedOrders = new ArrayDeque<>();

    private static final String TABLE_NUMBER_KEY = "TableNumber";

    private boolean initialised = false;

    public TableOrdersFragment() {
        // Required empty public constructor
    }

    public static TableOrdersFragment newInstance(Integer tableNumber) {
        TableOrdersFragment fragment = new TableOrdersFragment();
        Bundle args = new Bundle();
        args.putInt(TABLE_NUMBER_KEY, tableNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_table_orders, container, false);
        tableOrdersContainer = rootView.findViewById(R.id.table_orders_container);
        serveButton = rootView.findViewById(R.id.serve_button);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        synchronized (undisplayedOrders) {
            initialised = true;
            while (!undisplayedOrders.isEmpty()) {
                addOrder(undisplayedOrders.getFirst());
            }

        }

    }

    public void registerListener(TableOrdersFragmentListener listener) {
        this.listeners.add(listener);
    }

    private void addOrderedDish(OrderedDish orderedDish) {

        LayoutInflater inflater = getActivity().getLayoutInflater();

        ConstraintLayout orderEntry = (ConstraintLayout) inflater.inflate(R.layout.table_order_entry, tableOrdersContainer, false);

        orderEntry.setOnClickListener(v -> onOrderEntrySelected(v, orderedDish));

        TextView quantityText = orderEntry.findViewById(R.id.order_dish_quantity);
        quantityText.setText(String.valueOf(orderedDish.getNumber()));

        TextView nameText = orderEntry.findViewById(R.id.order_dish_name);
        nameText.setText(orderedDish.getName());

        TextView priceText = orderEntry.findViewById(R.id.order_dish_price);
        priceText.setText(String.valueOf(orderedDish.getPrice()));

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

    private void onOrderEntrySelected(View selectedEntry, OrderedDish orderedDish) {

        serveButton.setEnabled(true);
        serveButton.setPressed(false);
        serveButton.setOnClickListener(v -> {
            notifyServedOrder(orderedDish);

            ((ViewGroup) selectedEntry.getParent()).removeView(selectedEntry);
        });

    }

    private void notifyServedOrder(OrderedDish orderedDish) {
        for (TableOrdersFragmentListener listener: listeners) {
            listener.onOrderServed(orderedDish);
        }
    }


    private void notifyFragmentReady() {
        for (TableOrdersFragmentListener listener : listeners) {
            listener.onFragmentReady();
        }
    }

    public void addOrder(Order order) {

        if (!initialised) {
            synchronized (undisplayedOrders) {
                undisplayedOrders.add(order);
            }
            return;
        }

        for (OrderedDish dish : order.getOrderedDishes()) {
            addOrderedDish(dish);
        }

    }
}