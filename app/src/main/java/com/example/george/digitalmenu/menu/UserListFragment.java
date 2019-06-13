package com.example.george.digitalmenu.menu;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.utils.OrderStatus;

import java.util.HashMap;
import java.util.Map;

import static com.example.george.digitalmenu.utils.OrderStatus.READY;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends Fragment{

    private Map<String, Boolean> nameMap = new HashMap<>();
    private UserListFragmentListener listener;
    private OrderStatus status = READY;
    private View view;

    public UserListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_list, container, false);

        LinearLayout namePanel = view.findViewById(R.id.name_panel);

        for (String user: listener.getFriends()) {
            if (!nameMap.containsKey(user)) {
                nameMap.put(user, false);
            }
        }

        for (String user: nameMap.keySet()) {
            displayUsersFromCurrentTable(inflater, namePanel, user);
        }

        if (status == READY) {
            setConfirmButton();
        } else {
            Button confirm = view.findViewById(R.id.confirm_friends);
            confirm.setVisibility(View.GONE);
        }

        return view;
    }

    private void setConfirmButton() {
        Button confirm = view.findViewById(R.id.confirm_friends);
        confirm.setOnClickListener(v -> {
            listener.setSharing(nameMap);
            getActivity().getFragmentManager().popBackStack();
        });
    }

    private void displayUsersFromCurrentTable(LayoutInflater inflater, LinearLayout namePanel, String user) {
        CheckedTextView nameView = (CheckedTextView) inflater.inflate(R.layout.user_text, namePanel, false);
        nameView.setText(user);
        nameView.setChecked(nameMap.get(user));
        nameView.setOnClickListener(view1 -> {
            if (status == READY) {
                nameView.setChecked(!nameView.isChecked());
                nameMap.put(user, nameView.isChecked());
            }
        });
        nameView.setId(View.generateViewId());
        namePanel.addView(nameView);
    }

    public void setListener(UserListFragmentListener listener) {
        this.listener = listener;
    }

    public void setNameMap(Map<String, Boolean> nameMap) {
        this.nameMap = nameMap;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
