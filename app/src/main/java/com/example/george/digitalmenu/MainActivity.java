package com.example.george.digitalmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.SurfaceView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private QrCodeScanner scanner;
    public static final String INTENT_KEY = "bestmangal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Network().checkIfNetworkIsAvailable(this);

        SurfaceView surfaceView = findViewById(R.id.camerapreview);

        scanner = new RestaurantQrCodeScanner(surfaceView);
        scanner.askForCameraPermissionAndScanQrCode(this);

    }

    public void createSecondActivity(String value) {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra(INTENT_KEY, value);
        startActivity(intent);

    }
}
