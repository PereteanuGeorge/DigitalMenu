package com.example.george.digitalmenu;


import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.george.digitalmenu.MenuActivity.DISH_KEY;


/**
 * A simple {@link Fragment} subclass.
 */
public class DishInfoFragment extends Fragment {

    private static final String TAG = "DishInfoFragment";

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
        picture.setImageBitmap(BitmapFactory.decodeByteArray(dish.getPicture(), 0, dish.getPicture().length));

        TextView infoText = dish_info.findViewById(R.id.description);
        infoText.setText(dish.getDescription());

        TextView nameText = dish_info.findViewById(R.id.name);
        nameText.setText(dish.getName());

        TextView priceText = dish_info.findViewById(R.id.price);
        priceText.setText(String.valueOf(dish.getPrice()));

        TextView currencyText = dish_info.findViewById(R.id.currency);
        currencyText.setText(String.valueOf(dish.getCurrency()));
    }

    private void setGoBack(View dish_info) {
        View back = dish_info.findViewById(R.id.dish_info_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
    }

}
