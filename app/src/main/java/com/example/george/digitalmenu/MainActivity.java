package com.example.george.digitalmenu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.IOException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    CameraSource cameraSource;
    TextView textView;
    BarcodeDetector detector;

    private static Context context;
    private RestaurantDatabase db;
    private String TAG = "MainActivity";


    private CardView previousCard;
    private ConstraintLayout rootLayout;
    private ConstraintSet constraintSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkIfNetworkIsAvailable();

        surfaceView = findViewById(R.id.camerapreview);
        textView = findViewById(R.id.textView);
        surfaceView.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);

        rootLayout = findViewById(R.id.rootLayout);
        constraintSet = new ConstraintSet();

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInAnonymously:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        db = new RestaurantFirestore();
                        db.getRestaurant("bestmangal", r -> addMenuItems(r));
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInAnonymously:failure", task.getException());
                        Toast.makeText(context, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        askForCameraPermissionAndScanQrCode();
    }

    private void askForCameraPermissionAndScanQrCode() {
        new RxPermissions(this).request(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) {
                        if (granted) {
                            scanQrCode();
                        } else {
                            askForCameraPermissionAndScanQrCode();
                        }
                    }
                });
    }

    private void scanQrCode() {

        surfaceView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);

        detector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(640, 480).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            textView.setText(qrCodes.valueAt(0).displayValue);
                        }
                    });

                }

            }
        });



    }

    private void addMenuItems(Restaurant r) {
        for (Dish d : r.getDishes()) {
            addMenuItem(d);
        }
    }

    private void addMenuItem(Dish d) {

        /* Create card view with fields. */
        LayoutInflater inflater = getLayoutInflater();
        CardView card = (CardView) inflater.inflate(R.layout.menu_item_card, rootLayout, false);

        TextView infoText = card.findViewById(R.id.foodInfo);
        infoText.setText(d.getDescription());

        TextView nameText = card.findViewById(R.id.foodName);
        nameText.setText(d.getName());

        TextView priceText = card.findViewById(R.id.foodPrice);
        priceText.setText(String.valueOf(d.getPrice()));

        ImageView foodImage = card.findViewById(R.id.foodPicture);
        db.downloadDishPicture(d, bm -> foodImage.setImageBitmap(bm));

        card.setId(View.generateViewId());

        /* Add to existing list of cards*/
        rootLayout.addView(card);

        if (previousCard != null) {
            constraintSet.clone(rootLayout);
            constraintSet.connect(card.getId(), ConstraintSet.TOP, previousCard.getId(), ConstraintSet.BOTTOM);
            constraintSet.applyTo(rootLayout);
        }

        previousCard = card;
    }

    private void checkIfNetworkIsAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            context = getApplicationContext();
            Toast toast = Toast.makeText(context, "Please connect to the internet", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
