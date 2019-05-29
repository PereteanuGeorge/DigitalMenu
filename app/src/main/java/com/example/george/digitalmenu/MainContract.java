package com.example.george.digitalmenu;

public interface MainContract {
    interface Presenter {

        void onViewCompleteCreate();

        void registerView(View view);

        void submitScanResult(String s);
    }

    interface View {

        void switchToMenuActivity(String s);

        void checkNetworkConnectivity();

        void turnScannerOn();
    }
}
