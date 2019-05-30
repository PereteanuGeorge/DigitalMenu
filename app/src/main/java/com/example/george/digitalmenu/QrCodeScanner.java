package com.example.george.digitalmenu;

import android.support.v7.app.AppCompatActivity;

interface QrCodeScanner {

    void askForCameraPermissionAndScanQrCode(AppCompatActivity activity);
}
