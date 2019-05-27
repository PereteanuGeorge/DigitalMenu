package com.example.george.digitalmenu;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Network {

    public void checkIfNetworkIsAvailable(MainActivity mainActivity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            Context context = mainActivity.getApplicationContext();
        Toast toast = Toast.makeText(context, "Please connect to the internet", Toast.LENGTH_SHORT);
        toast.show();
    }
}
}
