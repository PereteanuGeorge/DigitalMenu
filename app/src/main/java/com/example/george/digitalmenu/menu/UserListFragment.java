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
import com.example.george.digitalmenu.utils.RestaurantFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends Fragment{

    private Map<String, Boolean> nameMap = new HashMap<>();
    private UserListFragmentListener listener;

    public UserListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        LinearLayout namePanel = view.findViewById(R.id.name_panel);
        List<String> users = listener.getFriends();
        for (String user: users) {
            displayUsersFromCurrentTable(inflater, namePanel, user);
        }
        Button confirm = view.findViewById(R.id.confirm_friends);
        confirm.setOnClickListener(v -> {
            listener.setSharing(nameMap);
            //getFriendsToShare();
            getActivity().getFragmentManager().popBackStack();
        });
        return view;
    }

    private void displayUsersFromCurrentTable(LayoutInflater inflater, LinearLayout namePanel, String user) {
        CheckedTextView nameView = (CheckedTextView) inflater.inflate(R.layout.user_text, namePanel, false);
        nameView.setText(user);
        nameMap.put(user, false);
        nameView.setOnClickListener(view1 -> {
            nameView.setChecked(!nameView.isChecked());
            nameMap.put(user, nameView.isChecked());
        });
        nameView.setId(View.generateViewId());
        namePanel.addView(nameView);
    }

    public void setListener(UserListFragmentListener listener) {
        this.listener = listener;
    }
}
