package com.example.george.digitalmenu;

import android.util.Log;
import android.widget.Toast;

import java.util.logging.Handler;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private final QrCodeScanner scanner;

    public MainPresenter(MainContract.View view, QrCodeScanner scanner) {
        this.scanner = scanner;
        this.view = view;
    }

    @Override
    public void onViewCompleteCreate() {
        view.checkNetworkConnectivity();
        scanner.scan(s -> submitScanResult(s));
    }

    private void submitScanResult(String s) {

        if (s.equals("bestmangal")) {
            view.switchToMenuActivity(s);
        } else {
            Log.d("MainPresenter", "Not best mangal!");
            view.notifyScanFailure();
        }
    }
}
