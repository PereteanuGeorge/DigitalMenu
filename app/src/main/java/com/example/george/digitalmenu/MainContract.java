package com.example.george.digitalmenu;

public interface MainContract {
    interface Presenter {

        void onViewCompleteCreate();

    }

    interface View {

        void switchToMenuActivity(String s);

        void checkNetworkConnectivity();

        void notifyScanFailure();
    }
}
