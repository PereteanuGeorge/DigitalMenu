package com.example.george.digitalmenu.menu;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.constraint.ConstraintLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.utils.Order;
import com.example.george.digitalmenu.utils.OrderedDish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.george.digitalmenu.main.MainActivity.ORDER;
import static com.example.george.digitalmenu.menu.MenuActivity.DISH;
import static com.example.george.digitalmenu.menu.MenuPresenter.PREVIOUS_ORDERS;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderPageFragment extends Fragment implements BoardFragmentListener{

    View orderView;
    private LayoutInflater inflater;
    private List<FragmentListener> listeners = new ArrayList<>();
    private Map<Integer, ConstraintLayout> orderDishMap = new HashMap<>();
    private Map<Integer, String> textStatusMap = new HashMap<Integer, String>() {{
        put(2, " Served ");
        put(1, " Sent ");
        put(0, " Added ");
    }};


    private Map<Integer, Integer>  backgroundStatusMap = new HashMap<Integer, Integer>() {{
        put(2, R.drawable.servedroundbutton);
        put(1, R.drawable.sentroundbutton);
        put(0, R.drawable.addedroundbutton);
    }};

    public OrderPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        orderView = inflater.inflate(R.layout.fragment_order, container, false);
        displayOrders(inflater);
        setTotalPrice();
        setGoBackButton();
        setConfirmButton();
        setBackgroundClick();
        return orderView;

    }

    private void setBackgroundClick() {
        orderView.setOnClickListener(v -> {});
    }

    private void setTotalPrice() {
        TextView totalPrice = orderView.findViewById(R.id.total_price);
        double sum = 0;
        for (Order order: PREVIOUS_ORDERS) {
            sum += order.getTotalPrice();
        }
        sum += ORDER.getTotalPrice();
        totalPrice.setText(String.valueOf(sum));
    }

    private void setConfirmButton() {
        Button confirmButton = orderView.findViewById(R.id.confirm_button);
        if (ORDER.isEmpty()) {
            confirmButton.setText("Go payment");
        }
        confirmButton.setOnClickListener(v -> {
            if (ORDER.isEmpty()) {
                Toast.makeText(getActivity(), "Open Payment", Toast.LENGTH_LONG).show();
            } else {
                for (FragmentListener listener : listeners) {
                    listener.sendOrder(ORDER);
                }
                Toast.makeText(getActivity(), "Order Sent", Toast.LENGTH_LONG).show();
            }
        });

    }


    private void displayOrders(LayoutInflater inflater) {
        // init
        LinearLayout orderPanel = orderView.findViewById(R.id.order_panel);
        for (Order order: PREVIOUS_ORDERS) {
            for (OrderedDish dish : order.getOrderedDishes()) {
                displayOrder(inflater, dish, orderPanel);
            }
        }
        for (OrderedDish dish : ORDER.getOrderedDishes()) {
            displayOrder(inflater, dish, orderPanel);
        }
        setGobackInstruction(inflater, orderPanel);

    }

    private void setGobackInstruction(LayoutInflater inflater, LinearLayout orderPanel) {
        TextView instruction = (TextView) inflater.inflate(R.layout.grey_instruction, orderPanel, false);
        instruction.setText("- For more dishes please go back to menu -");
        instruction.setId(View.generateViewId());
        addItem(instruction, orderPanel);
    }



    private void updateOrderCard(OrderedDish updatedOrderedDish, ConstraintLayout orderCard) {
        TextView statusText = orderCard.findViewById(R.id.status);
        statusText.setText(textStatusMap.get(updatedOrderedDish.getStatus()));
        statusText.setBackgroundResource(backgroundStatusMap.get(updatedOrderedDish.getStatus()));
    }

    private ConstraintLayout displayOrder(LayoutInflater inflater, OrderedDish dish, LinearLayout orderPanel) {
        ConstraintLayout orderCard = (ConstraintLayout) inflater.inflate(R.layout.order_card, orderPanel, false);


        TextView nameText = orderCard.findViewById(R.id.name);
        nameText.setText(dish.getName());


        TextView priceText = orderCard.findViewById(R.id.price);
        priceText.setText(String.valueOf(dish.getCurrency()).concat(String.valueOf(dish.getPrice())));


        TextView numberText = orderCard.findViewById(R.id.quantity);
        numberText.setText(String.valueOf(dish.getNumber()).concat("X"));

        orderDishMap.put(dish.getId(), orderCard);

        updateOrderCard(dish, orderCard);

        orderCard.setId(View.generateViewId());
        addItem(orderCard, orderPanel);

        setOnClick(dish, orderCard);
        return orderCard;
    }


    private void setOnClick(OrderedDish dish, ConstraintLayout orderCard) {
        orderCard.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Open", Toast.LENGTH_LONG).show();
            DISH = dish;
            OrderBoardFragment fragment = new OrderBoardFragment();
            fragment.addListener(this);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.dish_info_fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }


    private void addItem(View view, LinearLayout orderPanel) {
        orderPanel.addView(view);
    }

    private void setGoBackButton() {
        View back = orderView.findViewById(R.id.back_button);
        back.setOnClickListener(v -> {
            for (FragmentListener listener: listeners) {
                listener.goBack();
            }
        });
    }

    public void addListener(FragmentListener listener) {
        listeners.add(listener);
    }

    public void updateOrderedDish(OrderedDish updatedOrderedDish) {
        ConstraintLayout view = orderDishMap.get(updatedOrderedDish.getId());
        updateOrderCard(updatedOrderedDish, view);
    }

    @Override
    public void addDish(OrderedDish dish) { }

    @Override
    public void deleteOrderedDish(OrderedDish dish) {
        for (FragmentListener listener: listeners) {
            listener.deleteOrderedDish(dish);
        }
        LinearLayout orderPanel = orderView.findViewById(R.id.order_panel);
        orderPanel.removeView(orderDishMap.get(dish.getId()));
    }
}
