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
import com.example.george.digitalmenu.utils.OrderStatus;
import com.example.george.digitalmenu.utils.OrderedDish;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.george.digitalmenu.utils.OrderStatus.ADDED;
import static com.example.george.digitalmenu.utils.OrderStatus.READY;
import static com.example.george.digitalmenu.utils.OrderStatus.SENT;
import static com.example.george.digitalmenu.utils.OrderStatus.SERVED;
import static com.example.george.digitalmenu.utils.OrderStatus.SHARED;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderBoardFragment extends Fragment implements UserListFragmentListener{


    private LayoutInflater inflater;
    private OrderedDish orderedDish;
    private View orderView;
    private BoardFragmentListener listener;
    private View increment;
    private View decrement;
    private View shareButton;
    private TextView count;
    private Map<OrderStatus, Runnable> buttonMap = new HashMap<OrderStatus, Runnable>() {{
        put(READY, () -> setAddButton());
        put(ADDED, () -> setDeleteButton());
        put(SENT, () -> setSentButton());
        put(SERVED, () -> setServedButton());
        put(SHARED, () -> setSharedButton());
    }};

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
        this.increment = orderView.findViewById(R.id.increment);
        this.decrement = orderView.findViewById(R.id.decrement);
        this.shareButton = orderView.findViewById(R.id.share_button);
        this.count = orderView.findViewById(R.id.counter);

        setInformation();
        setGoBack();

        return this.orderView;
    }

    private void setInformation() {

        ImageView picture = orderView.findViewById(R.id.picture);
        picture.setImageBitmap(BitmapFactory.decodeByteArray(orderedDish.getPicture(),0, orderedDish.getPicture().length));

        TextView descriptionText = orderView.findViewById(R.id.description);
        descriptionText.setText(orderedDish.getDescription());

        TextView nameText = orderView.findViewById(R.id.name);
        nameText.setText(orderedDish.getName());

        TextView currencyText = orderView.findViewById(R.id.currency);
        currencyText.setText(String.valueOf(orderedDish.getCurrency()));

        setOptions(orderedDish.getOptions(), orderView.findViewById(R.id.options_panel));

        setPrice();

        setCounters();

        buttonMap.get(orderedDish.getStatus()).run();

        setSharingListButton();
    }

    private void setServedButton() {
        increment.setVisibility(View.GONE);
        decrement.setVisibility(View.GONE);
        Button button = orderView.findViewById(R.id.operation_button);
        button.setText("served");
        button.setBackgroundColor(Color.parseColor("#4267b2"));
        button.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Served dishes cannot be managed", Toast.LENGTH_LONG).show();
        });
    }

    private void setSharedButton() {
        increment.setVisibility(View.GONE);
        decrement.setVisibility(View.GONE);
        Button button = orderView.findViewById(R.id.operation_button);
        button.setText("Shared");
        button.setBackgroundColor(Color.parseColor("#4d4d33"));
        button.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Shared dishes from others cannot be managed", Toast.LENGTH_LONG).show();
        });
    }

    private void setSentButton() {
        increment.setVisibility(View.GONE);
        decrement.setVisibility(View.GONE);
        Button button = orderView.findViewById(R.id.operation_button);
        button.setText("Sent");
        button.setBackgroundColor(Color.parseColor("#FF8C00"));
        button.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Sent dishes cannot be managed", Toast.LENGTH_LONG).show();
        });
    }

    public void setDeleteButton() {
        Button button = orderView.findViewById(R.id.operation_button);
        button.setText("DELETE");
        button.setBackgroundColor(Color.parseColor("#F44336"));
        button.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
            listener.deleteOrderedDish(orderedDish); // TODO: consider isShared dish
            getActivity().getFragmentManager().popBackStack();
        });
    }

    public void setAddButton() {
        Button button = orderView.findViewById(R.id.operation_button);
        button.setText("ADD");
        button.setBackgroundColor(Color.parseColor("#4CAF50"));
        button.setOnClickListener(v -> {
            listener.addDish(orderedDish);
            Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
            getActivity().getFragmentManager().popBackStack();
        });
    }


    private void setCounters() {
        count.setText(String.valueOf(orderedDish.getNumber()));
        increment.setOnClickListener(view -> {
            orderedDish.increment();
            count.setText(String.valueOf(orderedDish.getNumber()));
            setPrice();
        });
        decrement.setOnClickListener(view -> {
            orderedDish.decrement();
            count.setText(String.valueOf(orderedDish.getNumber()));
            setPrice();
        });
    }

    private void setPrice() {
        TextView priceText = orderView.findViewById(R.id.price);
        priceText.setText(String.valueOf(orderedDish.getPrice()));
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
            if (orderedDish.isOptionOperatable()) {
                optionText.setChecked(!optionText.isChecked());
                Toast.makeText(getActivity(), optionText.getText().toString(), Toast.LENGTH_LONG).show();
                orderedDish.put(optionText.getText().toString(), optionText.isChecked());
            }
        });
        options_panel.setId(View.generateViewId());
        options_panel.addView(optionText);
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
    public List<String> getFriends() {
        return listener.getFriends();
    }

    @Override
    public void setSharing(Map<String, Boolean> nameMap) {
        orderedDish.setNameMap(nameMap);
        setShareButton(nameMap);
    }

    private void setSharingListButton() {
        View shareButton = orderView.findViewById(R.id.share_button);
        shareButton.setOnClickListener(view -> openUserList());
    }

    private void openUserList() {
        UserListFragment fragment = new UserListFragment();
        fragment.setListener(this);
        fragment.setNameMap(orderedDish.getNameMap());
        fragment.setStatus(orderedDish.getStatus());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.list_of_users_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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