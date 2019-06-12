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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.utils.OrderedDish;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderBoardFragment extends Fragment implements UserListFragmentListener{


    private LayoutInflater inflater;
    private OrderedDish orderedDish;
    private View orderView;
    private BoardFragmentListener listener;
    public static String DISH_TO_SHARE;
    public static List<Integer> friendsAtEachTime = new ArrayList<>();

    public OrderBoardFragment() {
        // Required empty public constructor
    }

    public static OrderBoardFragment newInstance() {
        OrderBoardFragment fragment = new OrderBoardFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        this.orderView = inflater.inflate(R.layout.fragment_order_board, container, false);

        setInformation();
        setGoBack();

        return this.orderView;
    }

    private void setInformation() {

        ImageView picture = orderView.findViewById(R.id.picture);
        picture.setImageBitmap(BitmapFactory.decodeByteArray(orderedDish.getPicture(),0, orderedDish.getPicture().length));

        TextView infoText = orderView.findViewById(R.id.description);
        infoText.setText(orderedDish.getDescription());


        TextView nameText = orderView.findViewById(R.id.name);
        DISH_TO_SHARE = orderedDish.getName();
        nameText.setText(orderedDish.getName());

        TextView priceText = orderView.findViewById(R.id.price);
        priceText.setText(String.valueOf(orderedDish.getPrice()));

        TextView currencyText = orderView.findViewById(R.id.currency);
        currencyText.setText(String.valueOf(orderedDish.getCurrency()));

        TextView descriptionText = orderView.findViewById(R.id.description);
        descriptionText.setText(orderedDish.getDescription());

        setOptions(orderedDish.getOptions(), orderView.findViewById(R.id.options_panel));

        Button increment = orderView.findViewById(R.id.increment);
        Button decrement = orderView.findViewById(R.id.decrement);

        TextView count = orderView.findViewById(R.id.counter);

        ImageButton shareButton = orderView.findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUserList();
            }
        });


        if (orderedDish.isSent()) {
            setSentButton();
            increment.setVisibility(View.GONE);
            decrement.setVisibility(View.GONE);
            count.setText(String.valueOf(orderedDish.getNumber()) + "X");
        } else {
            if (orderedDish.isOrdered()) {
                setDeleteButton();
            } else {
                setAddButton();
            }
            setNumberOfPortions(increment, decrement, count);
        }
    }

    private void openUserList() {
        UserListFragment fragment = new UserListFragment();
        fragment.setListener(this);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.list_of_users_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setSentButton() {
        Button button = orderView.findViewById(R.id.operation_button);
        button.setText("Sent");
        button.setBackgroundColor(Color.parseColor("#FF8C00"));
        button.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Sent dishes cannot be managed", Toast.LENGTH_LONG);
        });
    }


    private void setNumberOfPortions(Button increment, Button decrement, TextView count) {
        count.setText(String.valueOf(orderedDish.getNumber()));
        increment.setOnClickListener(view -> {
            orderedDish.increment();
            count.setText(String.valueOf(orderedDish.getNumber()));
        });
        decrement.setOnClickListener(view -> {
            orderedDish.decrement();
            count.setText(String.valueOf(orderedDish.getNumber()));
        });
    }


    //TODO; refactor this
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
            if (!orderedDish.isSent()) {
                optionText.setChecked(!optionText.isChecked());
                Toast.makeText(getActivity(), optionText.getText().toString(), Toast.LENGTH_LONG).show();
                orderedDish.put(optionText.getText().toString(), optionText.isChecked());
            } else {
                Toast.makeText(getActivity(), "Sent dishes cannot be managed", Toast.LENGTH_LONG).show();
            }
        });
        options_panel.setId(View.generateViewId());
        options_panel.addView(optionText);
    }

    private void setDeleteButton() {
        Button button = orderView.findViewById(R.id.operation_button);
        button.setText("DELETE");
        button.setBackgroundColor(Color.parseColor("#F44336"));
        button.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
            listener.deleteOrderedDish(orderedDish);
            getActivity().getFragmentManager().popBackStack();
        });
    }

    private void setAddButton() {
        Button button = orderView.findViewById(R.id.operation_button);
        button.setText("ADD");
        button.setBackgroundColor(Color.parseColor("#4CAF50"));
        button.setOnClickListener(v -> {
            listener.addDish(orderedDish);
            Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
            getActivity().getFragmentManager().popBackStack();
        });
    }


    private void setGoBack() {
        View back = orderView.findViewById(R.id.dish_info_back);
        back.setOnClickListener(v -> {
            getActivity().getFragmentManager().popBackStack();
            listener.updateOrderedDish(orderedDish);
        });

        View card = orderView.findViewById(R.id.dish_board);
        card.setOnClickListener(v -> {});
    }

    public void setListener(BoardFragmentListener listener) {
        this.listener = listener;
    }

    public void setOrderedDish(OrderedDish orderedDish) {
        this.orderedDish = orderedDish;
    }


    @Override
    public void setSharing(Map<String, Boolean> nameMap) {
        setShareButton(nameMap);
    }

    @Override
    public List<String> getFriends() {
        return listener.getFriends();
    }

    private void setShareButton(Map<String, Boolean> nameMap) {
        Button button = orderView.findViewById(R.id.operation_button);
        button.setText("Share");
        button.setBackgroundColor(Color.parseColor("#4CAF50"));
        button.setOnClickListener(v -> {
            listener.shareToFriends(orderedDish, nameMap);
            Toast.makeText(getActivity(), "Shared", Toast.LENGTH_SHORT).show();
            getActivity().getFragmentManager().popBackStack();
        });
    }
}