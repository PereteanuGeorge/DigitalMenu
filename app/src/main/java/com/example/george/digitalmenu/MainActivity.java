package com.example.george.digitalmenu;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.widget.Toast;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private QrCodeScanner scanner;
    public static final String INTENT_KEY = "bestmangal";
    private MainContract.Presenter presenter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        AppCenter.start(getApplication(), "8c0a4068-3689-46f7-b4bc-8eaaf16b1661",
                Analytics.class, Crashes.class);
        SurfaceView surfaceView = findViewById(R.id.camerapreview);
        scanner = new RestaurantQrCodeScanner(surfaceView, this);

        presenter = new MainPresenter(this, scanner);

        context = getApplicationContext();

        presenter.onViewCompleteCreate();
    }

    @Override
    public void switchToMenuActivity(String s) {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(INTENT_KEY, s);
        startActivity(intent);
    }

    @Override
    public void checkNetworkConnectivity() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            Toast toast = Toast.makeText(context, "Please connect to the internet", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void notifyScanFailure() {
        runOnUiThread(() -> {
            Toast.makeText(context, "Could not recognize the QrCode", Toast.LENGTH_SHORT).show();
        });
    }
}