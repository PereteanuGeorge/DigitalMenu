package com.example.george.digitalmenu.menu;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.utils.OrderStatus;
import com.example.george.digitalmenu.utils.OrderedDish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.george.digitalmenu.utils.OrderStatus.ADDED;
import static com.example.george.digitalmenu.utils.OrderStatus.SENT;
import static com.example.george.digitalmenu.utils.OrderStatus.SERVED;
import static com.example.george.digitalmenu.utils.OrderStatus.SHARED;
import static com.example.george.digitalmenu.utils.Utils.roundDouble;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderPageFragment extends Fragment implements BoardFragmentListener{

    View orderPageView;
    private FragmentListener listener;
    private List<OrderedDish> orderedDishes = new ArrayList<>();

    private Map<String, ConstraintLayout> orderDishMap = new HashMap<>();

    private Map<OrderStatus, String> textStatusMap = new HashMap<OrderStatus, String>() {{
        put(SHARED, " Shared ");
        put(SERVED, " Served ");
        put(SENT, " Sent ");
        put(ADDED, " Added ");
    }};


    private Map<OrderStatus, Integer>  backgroundStatusMap = new HashMap<OrderStatus, Integer>() {{
        put(SHARED, R.drawable.sharedroundbutton);
        put(SERVED, R.drawable.servedroundbutton);
        put(SENT, R.drawable.sentroundbutton);
        put(ADDED, R.drawable.addedroundbutton);
    }};
    private LayoutInflater inflater;

    public OrderPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        orderPageView = inflater.inflate(R.layout.fragment_order, container, false);
        this.inflater = inflater;
        displayOrders(inflater);
        setTotalPrice();
        setGoBackButton();
        setConfirmButton();
        setBackgroundClick();
        return orderPageView;

    }

    private void setBackgroundClick() {
        orderPageView.setOnClickListener(v -> {});
    }

    public void setTotalPrice() {
        TextView totalPrice = orderPageView.findViewById(R.id.total_price);
        totalPrice.setText(String.valueOf(listener.getTotalPrice()));
    }

    private void setConfirmButton() {
        if (listener.isAllServed()) {
            setAskForBillButton();
        } else if (listener.isCannotSent()) {
            setCannotSentButton();
        } else {
            setSentButton();
        }

    }

    private void setSentButton() {
        Button confirmButton = orderPageView.findViewById(R.id.confirm_button);
        confirmButton.setText(" send order ");
        confirmButton.setOnClickListener(v -> {
            listener.sendOrder();
            Toast.makeText(getActivity(), confirmButton.getText(), Toast.LENGTH_LONG).show();
        });
    }

    private void setCannotSentButton() {
        Button confirmButton = orderPageView.findViewById(R.id.confirm_button);
        confirmButton.setText(" send order ");
        confirmButton.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Cannot send empty orders", Toast.LENGTH_LONG).show();
        });
    }

    private void setAskForBillButton() {
        Button confirmButton = orderPageView.findViewById(R.id.confirm_button);
        confirmButton.setText(" Ask Bill ");
        confirmButton.setOnClickListener(v -> {
            Toast.makeText(getActivity(), confirmButton.getText(), Toast.LENGTH_LONG).show();
            listener.askForBill();
            getActivity().getFragmentManager().popBackStack();
        });
    }


    private void displayOrders(LayoutInflater inflater) {
        // init
        LinearLayout orderPanel = orderPageView.findViewById(R.id.order_panel);
        setGobackInstruction(inflater, orderPanel);
        for (OrderedDish orderedDish: orderedDishes) {
            displayOrder(inflater, orderedDish, orderPanel);
        }
    }

    private void setGobackInstruction(LayoutInflater inflater, LinearLayout orderPanel) {
        TextView instruction = (TextView) inflater.inflate(R.layout.grey_instruction, orderPanel, false);
        instruction.setText("- For more dishes please go back to menu -");
        instruction.setId(View.generateViewId());
        addItem(instruction, orderPanel);
    }


    private ConstraintLayout displayOrder(LayoutInflater inflater, OrderedDish dish, LinearLayout orderPanel) {
        ConstraintLayout orderCard = (ConstraintLayout) inflater.inflate(R.layout.order_card, orderPanel, false);
        orderDishMap.put(dish.getId(), orderCard);
        showOnOrderCard(dish, orderCard);

        orderCard.setId(View.generateViewId());

        orderPanel.addView(orderCard, orderPanel.getChildCount()-1);

        setOnClick(dish, orderCard);
        return orderCard;
    }

    private void showOnOrderCard(OrderedDish dish, ConstraintLayout orderCard) {
        TextView nameText = orderCard.findViewById(R.id.name);
        nameText.setText(dish.getName());


        TextView priceText = orderCard.findViewById(R.id.price);
        Double priceDivided = roundDouble(dish.getPrice() / dish.getSharingNumber(), 2);
        priceText.setText(String.valueOf(dish.getCurrency()).concat(String.valueOf(priceDivided)));


        TextView numberText = orderCard.findViewById(R.id.quantity);
        String quantityDivided = String.valueOf(roundDouble(dish.getNumber().doubleValue() / dish.getSharingNumber(), 2)).concat("X");

        if (dish.isShared()) {
            quantityDivided = quantityDivided.concat(",S");
        }

        numberText.setText(quantityDivided);

        TextView statusText = orderCard.findViewById(R.id.status);
        statusText.setText(textStatusMap.get(dish.getStatus()));
        statusText.setBackgroundResource(backgroundStatusMap.get(dish.getStatus()));
    }


    private void setOnClick(OrderedDish dish, ConstraintLayout orderCard) {
        orderCard.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Open", Toast.LENGTH_LONG).show();
            OrderBoardFragment fragment = new OrderBoardFragment();
            fragment.setListener(this);
            fragment.setOrderedDish(dish);
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
        View back = orderPageView.findViewById(R.id.back_button);
        back.setOnClickListener(v -> listener.goBack());
    }

    public void setListener(FragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public void updateOrderedDish(OrderedDish updatedOrderedDish) {
        ConstraintLayout view = orderDishMap.get(updatedOrderedDish.getId());
        showOnOrderCard(updatedOrderedDish, view);
        setTotalPrice();
        setConfirmButton();
    }

    @Override
    public void shareToFriends(OrderedDish orderedDish, Map<String, Boolean> nameMap) {

    }

    @Override
    public List<String> getFriends() {
        return listener.getFriends();
    }

    @Override
    public void deleteOrderedDishWithCounter(OrderedDish orderedDish, int deleteCounter) {
        listener.deleteOrderedDishWithCounter(orderedDish, deleteCounter);
    }

    @Override
    public void addDish(OrderedDish dish) { }

    @Override
    public void deleteOrderedDish(OrderedDish dish) {
        listener.deleteOrderedDish(dish);
        LinearLayout orderPanel = orderPageView.findViewById(R.id.order_panel);
        orderPanel.removeView(orderDishMap.get(dish.getId()));
        setConfirmButton();
    }

    public void setOrderedDishes(List<OrderedDish> orderedDishes) {
        this.orderedDishes = orderedDishes;
    }

    public void setGetBillButton() {
        Button confirmButton = orderPageView.findViewById(R.id.confirm_button);
        confirmButton.setText("get bill");
    }

    public void updateWithAddedDish(OrderedDish dish) {
        LinearLayout orderPanel = orderPageView.findViewById(R.id.order_panel);
        displayOrder(inflater, dish, orderPanel);

        /*refactor this, add item to second last part*/
    }

    public void updateWithDeletedDishWithId(String id) {
        LinearLayout orderPanel = orderPageView.findViewById(R.id.order_panel);
        orderPanel.removeView(orderDishMap.get(id));
    }

    public void updateWithModifiedDish(OrderedDish orderedDish) {
        showOnOrderCard(orderedDish, orderDishMap.get(orderedDish.getId()));
    }
}
