package com.example.george.digitalmenu.menu;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.utils.OrderedDish;

import static com.example.george.digitalmenu.main.MainActivity.ORDER;
import static com.example.george.digitalmenu.menu.MenuActivity.DISH;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderPageFragment extends Fragment {

    View order;
    View previousView;

    public OrderPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        order = inflater.inflate(R.layout.fragment_order, container, false);
        displayOrders(inflater);
        setTotalPrice();
        setGoBackButton();
        setConfirmButton();
        return order;

    }

    private void setTotalPrice() {
        TextView totalPrice = order.findViewById(R.id.total_price);
        totalPrice.setText(String.valueOf(ORDER.getTotalPrice()));
    }

    private void setConfirmButton() {
        order.findViewById(R.id.confirm_button).setOnClickListener(v -> Toast.makeText(getActivity(), "Confirmed", Toast.LENGTH_LONG).show());

    }

    private void displayOrders(LayoutInflater inflater) {
        // init
        ConstraintLayout orderPanel = order.findViewById(R.id.order_panel);
        for (OrderedDish dish: ORDER.getOrderedDishes()) {
            displayOrder(inflater, dish,orderPanel);
        }
    }

    private void displayOrder(LayoutInflater inflater, OrderedDish dish, ConstraintLayout orderPanel) {
        ConstraintLayout orderCard = (ConstraintLayout) inflater.inflate(R.layout.order_card, orderPanel, false);

        TextView nameText = orderCard.findViewById(R.id.name);
        nameText.setText(dish.getName());

        TextView priceText = orderCard.findViewById(R.id.price);
        priceText.setText(String.valueOf(dish.getPrice()));

        TextView currencyText = orderCard.findViewById(R.id.currency);
        currencyText.setText(String.valueOf(dish.getCurrency()));

        TextView numberText = orderCard.findViewById(R.id.portion_number);
        numberText.setText(String.valueOf(dish.getNumber()));

        ImageView picture = orderCard.findViewById(R.id.picture);
        picture.setImageBitmap(BitmapFactory.decodeByteArray(dish.getPicture(),0, dish.getPicture().length));

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

    private void addItem(ConstraintLayout orderCard, ConstraintLayout orderPanel) {
        orderPanel.addView(orderCard);

        //TODO: show items nicely
        if (previousView != null) {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(orderPanel);
            constraintSet.connect(orderCard.getId(), ConstraintSet.TOP,
                    orderPanel.getId(), ConstraintSet.BOTTOM, 4);
            constraintSet.applyTo(orderPanel);
        }
        previousView = orderCard;
    }

    private void setGoBackButton() {
        View back = order.findViewById(R.id.back_button);
        back.setOnClickListener(v -> getActivity().getFragmentManager().popBackStack());
    }

}
