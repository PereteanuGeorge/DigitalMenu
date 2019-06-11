package com.example.george.digitalmenu.menu;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.utils.RestaurantFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends Fragment {

    ListView listView;
    SearchView searchView;
    ArrayAdapter<String> adapter;


    public UserListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        listView = (ListView) view.findViewById(R.id.users_list_view);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1,RestaurantFirestore.users);
        listView.setAdapter(adapter);
        return view;
    }
}
