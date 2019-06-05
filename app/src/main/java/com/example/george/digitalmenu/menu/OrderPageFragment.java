package com.example.george.digitalmenu.menu;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.utils.Order;
import com.example.george.digitalmenu.utils.OrderedDish;

import java.util.ArrayList;
import java.util.List;

import static com.example.george.digitalmenu.main.MainActivity.ORDER;
import static com.example.george.digitalmenu.menu.MenuActivity.DISH;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderPageFragment extends Fragment {

    View order;
    private LayoutInflater inflater;
    private List<FragmentListener> listeners = new ArrayList<>();

    public OrderPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        order = inflater.inflate(R.layout.fragment_order, container, false);
        displayOrders(inflater);
        setTotalPrice();
        setGoBackButton();
        setConfirmButton();
        setBackgroundClick();
        return order;

    }

    private void setBackgroundClick() {
        order.setOnClickListener(v -> {});
    }

    private void setTotalPrice() {
        TextView totalPrice = order.findViewById(R.id.total_price);
        totalPrice.setText(String.valueOf(ORDER.getTotalPrice()));
    }

    private void setConfirmButton() {
        order.findViewById(R.id.confirm_button).setOnClickListener(v -> {
            for (FragmentListener listener: listeners) {
                listener.sendOrder();
            }
            Toast.makeText(getActivity(), "Confirmed", Toast.LENGTH_LONG).show();
        });

    }

    private void displayOrders(LayoutInflater inflater) {
        // init
        LinearLayout orderPanel = order.findViewById(R.id.order_panel);
        for (OrderedDish dish: ORDER.getOrderedDishes()) {
            displayOrder(inflater, dish,orderPanel);
        }
    }

    private void displayOrder(LayoutInflater inflater, OrderedDish dish, LinearLayout orderPanel) {
        ConstraintLayout orderCard = (ConstraintLayout) inflater.inflate(R.layout.order_card, orderPanel, false);

//        TextView descriptionText = orderCard.findViewById(R.id.description);
//        descriptionText.setText(dish.getDescription());

        TextView nameText = orderCard.findViewById(R.id.name);
        nameText.setText(dish.getName());


        TextView priceText = orderCard.findViewById(R.id.price);
        priceText.setText(String.valueOf(dish.getCurrency()).concat(String.valueOf(dish.getPrice())));


        TextView numberText = orderCard.findViewById(R.id.quantity);
        numberText.setText(String.valueOf(dish.getNumber()).concat("X"));

        orderCard.setId(View.generateViewId());
        addItem(orderCard, orderPanel);

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


    private void addItem(ConstraintLayout orderCard, LinearLayout orderPanel) {
        orderPanel.addView(orderCard);
    }

    private void setGoBackButton() {
        View back = order.findViewById(R.id.back_button);
        back.setOnClickListener(v -> getActivity().getFragmentManager().popBackStack());
    }

    public void addListener(FragmentListener listener) {
        listeners.add(listener);
    }
}
