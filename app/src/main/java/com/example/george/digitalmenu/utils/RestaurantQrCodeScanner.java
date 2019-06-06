package com.example.george.digitalmenu.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Consumer;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.IOException;

public class RestaurantQrCodeScanner implements QrCodeScanner {

    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private String value;
    private AppCompatActivity activityToShowScanner;

    public RestaurantQrCodeScanner(SurfaceView surfaceView, AppCompatActivity activity) {
        this.surfaceView = surfaceView;
        surfaceView.setVisibility(View.GONE);
        activityToShowScanner = activity;
    }

    @Override
    public void scan(Consumer<String> callback) {
        askForCameraPermissionAndScanQrCode(callback);
    }

    private void askForCameraPermissionAndScanQrCode(Consumer<String> callback) {

        new RxPermissions(activityToShowScanner).request(Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) {
                        scanQrCode(callback);
                    } else {
                        askForCameraPermissionAndScanQrCode(callback);
                    }
                });
    }

    private void scanQrCode(Consumer<String> callback) {

        surfaceView.setVisibility(View.VISIBLE);

        BarcodeDetector detector = new BarcodeDetector.Builder(activityToShowScanner)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        this.cameraSource = new CameraSource.Builder(activityToShowScanner, detector)
                .setRequestedPreviewSize(640, 480).build();

        this.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(activityToShowScanner.getApplicationContext(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if (qrCodes.size() != 0) {
                    Vibrator vibrator = (Vibrator) activityToShowScanner.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                    value = qrCodes.valueAt(0).displayValue;
                    String restaurantName = splitInTableNrAndRestaurantName();
                    callback.accept(restaurantName);
                }

            }
        });
    }

    private String splitInTableNrAndRestaurantName() {
        int position = value.lastIndexOf('/');
        Order.tableNumber = Integer.valueOf(value.substring(position + 1));
        return value.substring(0,position);
    }

}
