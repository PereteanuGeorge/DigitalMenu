package com.example.george.digitalmenu.menu;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.utils.RestaurantFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends Fragment {

    ListView listView;
    ArrayAdapter<String> adapter;
    private Map<String, Boolean> nameMap = new HashMap<>();


    public UserListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        LinearLayout namePanel = view.findViewById(R.id.name_panel);
        List<String> users = RestaurantFirestore.users;
        for (String user: users) {
            CheckedTextView nameView = (CheckedTextView) inflater.inflate(R.layout.user_text, namePanel, false);
            nameView.setText(user);
            nameMap.put(user, false);
            nameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    nameView.setChecked(!nameView.isChecked());
                    nameMap.put(user, nameView.isChecked());
                }
            });
            nameView.setId(View.generateViewId());
            namePanel.addView(nameView);
        }
        Button confirm = view.findViewById(R.id.confirm_friends);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> friendsToShare = new ArrayList<>();
                for(Map.Entry<String,Boolean> friends : nameMap.entrySet()) {
                    if(friends.getValue()) {
                        friendsToShare.add(friends.getKey());
                    }
                }
                System.out.println("Friends to share are " + friendsToShare);
            }
        });
        return view;
    }
}
