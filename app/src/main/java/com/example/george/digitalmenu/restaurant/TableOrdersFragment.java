package com.example.george.digitalmenu.restaurant;


import android.graphics.drawable.Drawable;
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
    private Deque<OrderedDish> undisplayedOrderedDishes = new ArrayDeque<>();
    private List<UnservedOrderedDish> unservedOrderedDishes = new ArrayList<>();
    private List<UnservedOrderedDish> dishesToServe = new ArrayList<>();

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
        serveButton.setOnClickListener(v -> onServeButtonClicked());
        return rootView;
    }

    public static TableOrdersFragment newInstance(Integer tableNumber) {
        TableOrdersFragment fragment = new TableOrdersFragment();
        Bundle args = new Bundle();
        args.putInt(TABLE_NUMBER_KEY, tableNumber);
        fragment.setArguments(args);
        return fragment;
    }


    private void onServeButtonClicked() {

        for (UnservedOrderedDish dish : dishesToServe) {
            dish.serve();
        }

        notifyServedOrders(dishesToServe);
        dishesToServe.clear();

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

    private void displayOrderedDish(OrderedDish orderedDish) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        ConstraintLayout orderEntry = (ConstraintLayout) inflater.inflate(R.layout.table_order_entry, tableOrdersContainer, false);

        /* Register new UnservedOrderedDish which is associated with the view */
        UnservedOrderedDish unservedDish = new UnservedOrderedDish(orderEntry, orderedDish);
        unservedOrderedDishes.add(unservedDish);

        orderEntry.setOnClickListener(v -> onUnservedOrderEntrySelected(v, unservedDish));

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

    public void addOrderedDish(OrderedDish orderedDish) {

        if (!initialised) {
            synchronized (undisplayedOrderedDishes) {
                undisplayedOrderedDishes.add(orderedDish);
            }
            return;
        }

        displayOrderedDish(orderedDish);
    }

    private void onUnservedOrderEntrySelected(View selectedEntry,
                                              UnservedOrderedDish unservedOrderedDish) {

        unservedOrderedDish.select();

        if (dishesToServe.isEmpty()) {
            serveButton.setEnabled(false);
            serveButton.setPressed(true);
        } else {
            serveButton.setEnabled(true);
            serveButton.setPressed(false);
        }

    }

    private void notifyServedOrders(List<UnservedOrderedDish> servedDishes) {

        /* Unwrapping UnservedOrderedDish classes */
        List<OrderedDish> orderedDishes = new ArrayList<>(servedDishes.size());
        for (UnservedOrderedDish dish : servedDishes) {
            orderedDishes.add(dish.getDish());
        }

        for (TableOrdersFragmentListener listener: listeners) {
            listener.onOrderDishesServed(tableNumber, orderedDishes);
            if (unservedOrderedDishes.isEmpty()) {
                listener.onAllOrdersServed(tableNumber);
            }
        }
    }

    public int getTableNumber() {
        return tableNumber;
    }

    /* Inner class representation of a Ordered Dish Entry in fragment. */
    private class UnservedOrderedDish {

        private final View rootView;
        private final Drawable originalBackground;
        private final Drawable buttonPressedBackground;
        OrderedDish dish;
        boolean selectedToServe = false;

        public UnservedOrderedDish(View v, OrderedDish dish) {
            this.rootView = v;
            this.originalBackground = v.getBackground();
            this.dish = dish;
            this.buttonPressedBackground = getResources().getDrawable(R.drawable.dish_card_border_pressed);
        }

        public OrderedDish getDish() {
            return dish;
        }

        public void select() {
            selectedToServe = !selectedToServe;

            if (selectedToServe) {
                rootView.setBackground(buttonPressedBackground);
                dishesToServe.add(this);
            } else {
                rootView.setBackground(originalBackground);
                dishesToServe.remove(this);
            }
        }

        public boolean isSelected() {
            return selectedToServe;
        }

        public void serve() {
            unservedOrderedDishes.remove(this);
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }
}