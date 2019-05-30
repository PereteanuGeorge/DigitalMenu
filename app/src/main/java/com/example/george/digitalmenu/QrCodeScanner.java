package com.example.george.digitalmenu;

import android.support.v4.util.Consumer;
import android.support.v7.app.AppCompatActivity;

interface QrCodeScanner {
    void askForCameraPermissionAndScanQrCode(AppCompatActivity mainActivity,
                                             Consumer<String> callback);
}
