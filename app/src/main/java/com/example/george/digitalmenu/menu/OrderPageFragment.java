package com.example.george.digitalmenu.menu;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import org.w3c.dom.Text;

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
public class OrderPageFragment extends Fragment {

    View orderView;
    private LayoutInflater inflater;
    private List<FragmentListener> listeners = new ArrayList<>();
    Map<Integer, Pair<OrderedDish, ConstraintLayout>> orderDishMapFromId = new HashMap<>();

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
                    listener.sendOrder(this::refreshOrderPage);
                    listener.createNewOrder();
                }
                Toast.makeText(getActivity(), "Order Sent", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void refreshOrderPage() {
        getActivity().getFragmentManager().popBackStack();
        OrderPageFragment fragment = new OrderPageFragment();
        fragment.addListener((FragmentListener) getActivity());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.order_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void displayOrders(LayoutInflater inflater) {
        // init
        LinearLayout orderPanel = orderView.findViewById(R.id.order_panel);
        for (Order order: PREVIOUS_ORDERS) {
            for (OrderedDish dish : order.getOrderedDishes()) {
                displayOrder(inflater, dish, orderPanel);
            }
            for (FragmentListener listener: listeners) {
                listener.listenForOrderedDishUpdate(order.getId(), this::onServe);
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

    private void onServe(List<OrderedDish> updatedOrderedDishes)  {
        for (OrderedDish updatedOrderedDish: updatedOrderedDishes) {
            Pair<OrderedDish, ConstraintLayout> pair = orderDishMapFromId.get(updatedOrderedDish.getId());
            OrderedDish localOrederDish = pair.first;
            if (localOrederDish.isServed() != updatedOrderedDish.isServed()) {
                localOrederDish.setIsServed(updatedOrderedDish.isServed());
                updateOrderCard(pair.second);
            }
        }
    }

    private void updateOrderCard(ConstraintLayout orderCard) {
        TextView nameText = orderCard.findViewById(R.id.name);
        Toast.makeText(getActivity(), nameText.getText() + " is served", Toast.LENGTH_LONG).show();
    }

    private ConstraintLayout displayOrder(LayoutInflater inflater, OrderedDish dish, LinearLayout orderPanel) {
        ConstraintLayout orderCard = (ConstraintLayout) inflater.inflate(R.layout.order_card, orderPanel, false);


        TextView nameText = orderCard.findViewById(R.id.name);
        nameText.setText(dish.getName());


        TextView priceText = orderCard.findViewById(R.id.price);
        priceText.setText(String.valueOf(dish.getCurrency()).concat(String.valueOf(dish.getPrice())));


        TextView numberText = orderCard.findViewById(R.id.quantity);
        numberText.setText(String.valueOf(dish.getNumber()).concat("X"));

        setStatus(dish, orderCard);


        orderDishMapFromId.put(dish.getId(), new Pair<>(dish, orderCard));

        orderCard.setId(View.generateViewId());
        addItem(orderCard, orderPanel);

        setOnClick(dish, orderCard);
        return orderCard;
    }

    private void setStatus(OrderedDish dish, ConstraintLayout orderCard) {

        TextView statusText = orderCard.findViewById(R.id.status);
        if (dish.isServed()) {
            statusText.setText(" Served ");
            statusText.setBackgroundResource(R.drawable.servedroundbutton);
        } else if (dish.isSent()) {
            statusText.setText(" Sent ");
            statusText.setBackgroundResource(R.drawable.sentroundbutton);
        }
    }

    private void setOnClick(OrderedDish dish, ConstraintLayout orderCard) {
        orderCard.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Open", Toast.LENGTH_LONG).show();
            DISH = dish;
            OrderBoardFragment fragment = new OrderBoardFragment();
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
        back.setOnClickListener(v -> getActivity().getFragmentManager().popBackStack());
    }

    public void addListener(FragmentListener listener) {
        listeners.add(listener);
    }
}
