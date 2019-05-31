package com.example.george.digitalmenu.menu;


import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.utils.Dish;
import com.example.george.digitalmenu.utils.OrderedDish;

import java.util.ArrayList;
import java.util.List;

import static com.example.george.digitalmenu.menu.MenuActivity.DISH_KEY;


/**
 * A simple {@link Fragment} subclass.
 */
public class DishInfoFragment extends Fragment {

    private static final String TAG = "DishInfoFragment";
    private List<DishFragmentListener> listeners = new ArrayList<>();

    public DishInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Dish dish = getActivity().getIntent().getParcelableExtra(DISH_KEY);
        View dish_info = inflater.inflate(R.layout.dish_info_1, container, false);

        setInformation(dish, dish_info);
        setGoBack(dish_info);


        return dish_info;
    }

    private void setInformation(Dish dish, View dish_info) {
        ImageView picture = dish_info.findViewById(R.id.dish_picture);
        picture.setImageBitmap(BitmapFactory.decodeByteArray(dish.getPicture(),0, dish.getPicture().length));

        TextView infoText = dish_info.findViewById(R.id.description);
        infoText.setText(dish.getDescription());

        TextView nameText = dish_info.findViewById(R.id.name);
        nameText.setText(dish.getName());

        TextView priceText = dish_info.findViewById(R.id.price);
        priceText.setText(String.valueOf(dish.getPrice()));

        TextView currencyText = dish_info.findViewById(R.id.currency);
        currencyText.setText(String.valueOf(dish.getCurrency()));

        setAddButton(dish_info, new OrderedDish(dish, 1));
    }

    private void setAddButton(View view, OrderedDish dish) {
        Button button = view.findViewById(R.id.add_dish_button);
        button.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "hello", Toast.LENGTH_SHORT).show();
            notifyListeners(dish);
        });

        
    }

    private void notifyListeners(OrderedDish dish) {
        for (DishFragmentListener listener: listeners) {
            listener.addDishToOrder(dish);
        }
    }

    private void setGoBack(View dish_info) {
        View back = dish_info.findViewById(R.id.dish_info_back);
        back.setOnClickListener(v -> getActivity().getFragmentManager().popBackStack());
    }

    public void addListener(DishFragmentListener listener) {
        listeners.add(listener);
    }
}
