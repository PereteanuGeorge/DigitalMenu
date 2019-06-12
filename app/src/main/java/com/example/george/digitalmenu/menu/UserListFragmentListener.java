package com.example.george.digitalmenu.menu;

import java.util.List;
import java.util.Map;

interface UserListFragmentListener {

    void setSharing(Map<String, Boolean> nameMap);

    List<String> getFriends();
}
