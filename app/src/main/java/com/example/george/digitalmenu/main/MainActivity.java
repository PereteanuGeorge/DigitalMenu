package com.example.george.digitalmenu.main;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import com.example.george.digitalmenu.R;
import com.example.george.digitalmenu.menu.MenuActivity;
import com.example.george.digitalmenu.utils.Order;
import com.example.george.digitalmenu.utils.QrCodeScanner;
import com.example.george.digitalmenu.utils.RestaurantQrCodeScanner;


public class MainActivity extends AppCompatActivity implements MainContract.View{


    public static final String INTENT_KEY = "bestmangal";
    public static final String USERNAME_INTENT_KEY = "MainActivityUsername";

    private QrCodeScanner scanner;
    private MainContract.Presenter presenter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

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
        String username = getIntent().getStringExtra(MainActivity.USERNAME_INTENT_KEY);
        intent.putExtra(MenuActivity.USERNAME_INTENT_KEY, username);
        Log.d("MainActivity", "Table number in Main is " + Order.tableNumber);
        intent.putExtra(INTENT_KEY, s);
        startActivity(intent);
        finish();
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
        runOnUiThread(() -> Toast.makeText(context, "Could not recognize the QrCode", Toast.LENGTH_SHORT).show());
    }
}