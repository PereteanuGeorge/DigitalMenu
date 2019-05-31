package com.example.george.digitalmenu.main;

public interface MainContract {
    interface Presenter {

        void onViewCompleteCreate();
    }

    interface View {

        void switchToMenuActivity(String s);

        void checkNetworkConnectivity();

    }
}
