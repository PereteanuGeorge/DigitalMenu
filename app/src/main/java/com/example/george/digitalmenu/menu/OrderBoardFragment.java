package com.example.george.digitalmenu.menu;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.utils.DisplayableDish;
import com.example.george.digitalmenu.utils.Order;
import com.example.george.digitalmenu.utils.OrderedDish;

import java.util.Map;

import static com.example.george.digitalmenu.main.MainActivity.ORDER;
import static com.example.george.digitalmenu.menu.MenuActivity.DISH;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderBoardFragment extends Fragment {


    private LayoutInflater inflater;
    private DisplayableDish dish;
    int counter = 1;

    public OrderBoardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.dish = DISH;
        this.inflater = inflater;
        View order = inflater.inflate(R.layout.fragment_order_board, container, false);

        setInformation(dish, order);
        setGoBack(order);

        return order;
    }

    private void setInformation(DisplayableDish dish, View order) {

        ImageView picture = order.findViewById(R.id.picture);
        picture.setImageBitmap(BitmapFactory.decodeByteArray(dish.getPicture(),0, dish.getPicture().length));

        TextView infoText = order.findViewById(R.id.description);
        infoText.setText(dish.getDescription());

        TextView nameText = order.findViewById(R.id.name);
        nameText.setText(dish.getName());

        TextView priceText = order.findViewById(R.id.price);
        priceText.setText(String.valueOf(Order.roundDouble(dish.getPrice(),2)));

        TextView currencyText = order.findViewById(R.id.currency);
        currencyText.setText(String.valueOf(dish.getCurrency()));

        TextView descriptionText = order.findViewById(R.id.description);
        descriptionText.setText(dish.getDescription());

        setOptions(dish.getOptions(), order.findViewById(R.id.options_panel));


        if (dish.isOrdered()) {
            setDeleteButton(order, (OrderedDish) dish);
        } else {
            dish = new OrderedDish(dish, 1);
            setAddButton(order, (OrderedDish) dish);
        }

        setCounter(order, dish);
    }

    private void setCounter(View order, DisplayableDish dish) {
        Button increment = order.findViewById(R.id.increment);
        Button decrement = order.findViewById(R.id.decrement);

        TextView count = order.findViewById(R.id.counter);
        count.setText(String.valueOf(dish.getPortion()));

        increment.setOnClickListener(view -> {
            dish.increment();
            count.setText(String.valueOf(dish.getPortion()));
        });
        decrement.setOnClickListener(view -> {
            dish.decrement();
            count.setText(String.valueOf(dish.getPortion()));
        });
    }

    private void setOptions(Map<String, Boolean> options, LinearLayout options_panel) {
        for (String option: options.keySet()) {
            setOption(option, options.get(option), options_panel);
        }
    }

    private void setOption(String option, Boolean isChecked, LinearLayout options_panel) {
        CheckedTextView optionText = (CheckedTextView) inflater.inflate(R.layout.option_text, options_panel, false);
        optionText.setText(option);
        optionText.setChecked(isChecked);
        optionText.setOnClickListener(v -> {
            optionText.setChecked(!optionText.isChecked());
            Toast.makeText(getActivity(), optionText.getText().toString(), Toast.LENGTH_LONG).show();
            dish.put(optionText.getText().toString(), optionText.isChecked());
        });

        options_panel.setId(View.generateViewId());
        options_panel.addView(optionText);
    }

    private void setDeleteButton(View view, OrderedDish dish) {
        Button button = view.findViewById(R.id.operation_button);
        button.setText("DELETE");
        button.setBackgroundColor(Color.parseColor("#F44336"));
        button.setOnClickListener(v -> {
            ORDER.delete(dish);
            Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
            reFreshOrderPage();
        });
    }

    private void reFreshOrderPage() {
        getActivity().getFragmentManager().popBackStack();
        if (dish.isOrdered()) {
            getActivity().getFragmentManager().popBackStack();
            OrderPageFragment fragment = new OrderPageFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.order_fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void setAddButton(View view, OrderedDish dish) {
        Button button = view.findViewById(R.id.operation_button);
        button.setText("ADD");
        button.setBackgroundColor(Color.parseColor("#4CAF50"));
        button.setOnClickListener(v -> {
            ORDER.add(dish,counter);
            dish.setIsOrdered(true);
            Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
            getActivity().getFragmentManager().popBackStack();
        });
    }


    private void setGoBack(View dish_info) {
        View back = dish_info.findViewById(R.id.dish_info_back);
        back.setOnClickListener(v -> reFreshOrderPage());

        View card = dish_info.findViewById(R.id.dish_board);
        card.setOnClickListener(v -> {});
    }
}
