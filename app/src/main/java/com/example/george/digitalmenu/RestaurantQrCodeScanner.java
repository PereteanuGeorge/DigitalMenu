package com.example.george.digitalmenu;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
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

    public RestaurantQrCodeScanner(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;

        surfaceView.setVisibility(View.GONE);
    }

    @Override
    public void askForCameraPermissionAndScanQrCode(AppCompatActivity mainActivity) {

        new RxPermissions(mainActivity).request(Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) {
                        scanQrCode(mainActivity);
                    } else {
                        askForCameraPermissionAndScanQrCode(mainActivity);
                    }
                });
    }

    private void scanQrCode(AppCompatActivity mainActivity) {

        surfaceView.setVisibility(View.VISIBLE);

        BarcodeDetector detector = new BarcodeDetector.Builder(mainActivity)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        this.cameraSource = new CameraSource.Builder(mainActivity, detector)
                .setRequestedPreviewSize(640, 480).build();

        this.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(mainActivity.getApplicationContext(),
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
                    Vibrator vibrator = (Vibrator) mainActivity.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                    value = qrCodes.valueAt(0).displayValue;
                    ((MainActivity) mainActivity).createSecondActivity(value);
                }

            }
        });
    }

}
