package com.example.george.digitalmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;

import android.support.v4.util.Consumer;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private QrCodeScanner scanner;
    private Network network;
    public static final String INTENT_KEY = "bestmangal";
    MainContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        SurfaceView surfaceView = findViewById(R.id.camerapreview);
        scanner = new RestaurantQrCodeScanner(surfaceView);

        network = new Network();

        presenter = new MainPresenter(network, scanner);
        presenter.registerView(this);

        presenter.onViewCompleteCreate();

    }

    @Override
    public void switchToMenuActivity(String s) {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra(INTENT_KEY, s);
        startActivity(intent);
    }

    @Override
    public void checkNetworkConnectivity() {
        network.checkIfNetworkIsAvailable(this);
    }

    @Override
    public void turnScannerOn() {
        scanner.askForCameraPermissionAndScanQrCode(this, new Consumer<String>() {
            @Override
            public void accept(String s) {
                presenter.submitScanResult(s);
            }
        });
    }
}