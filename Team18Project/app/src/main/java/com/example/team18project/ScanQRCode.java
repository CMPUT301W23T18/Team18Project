package com.example.team18project;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class used for scanning new QRCodes.
 *
 * Citations:
 *
 * (Used this library to implement the QRCode scanner)
 * ZXing Android Embedded
 * https://github.com/journeyapps/zxing-android-embedded
 * Authors: JourneyApps (https://journeyapps.com),
 *      https://github.com/journeyapps/zxing-android-embedded/graphs/contributors
 * Apache License 2.0
 */
public class ScanQRCode extends AppCompatActivity {
    private boolean takePhoto;
    private double latitude;
    private double longitude;
    private Intent data = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        Button scanButton = findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });

        Switch takePhotoSwitch = findViewById(R.id.take_photo_switch);
        takePhoto = takePhotoSwitch.isChecked();
        takePhotoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                takePhoto = isChecked;
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 5, locationListener);
        //TODO: figure out why it doesn't work on Dom's computer.
        //Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //latitude = lastLocation.getLatitude();
        //longitude = lastLocation.getLongitude();

    }

    /**
     * Creates a new capture activity and initiates the scan.
     */
    public void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scanning Codes");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(QrCodeCaptureActivity.class);
        barScanner.launch(options);
    }

    ActivityResultLauncher<Intent> takePicture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                finish();
            }
    );

    ActivityResultLauncher<ScanOptions> barScanner = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String sha256 = null;
            try {
                sha256 = QRCode.getSHA256(result.getContents());
            } catch (NoSuchAlgorithmException e) {
                Toast.makeText(getApplicationContext(), "ERROR: could not hash value", Toast.LENGTH_LONG).show();
                finish();
            }

            double roundedLat = (double) Math.round(latitude * 10000) / 10000;
            double roundedLong = (double) Math.round(longitude * 10000) / 10000;
            QRCode code = new QRCode(sha256, new ArrayList<String>(), new ArrayList<Comment>(), latitude, longitude);
            String id = sha256 + "_" + Double.toString(roundedLat) + "_" + Double.toString(roundedLong);
            code.setQid(id);
            data.putExtra("newCode", code);
            setResult(Activity.RESULT_OK, data);
            if (takePhoto) {
                Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePicture.launch(takePicIntent);
            } else {
                finish();
            }
        }
    });

    /**
     * Handles the results of a permission request initiated by the system.
     * @param requestCode The request code passed in {@link #requestPermissions(
     * android.app.Activity, String[], int)}
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_LONG).show();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get the user's location
            } else {
                finish();
            }
        }
    }

}