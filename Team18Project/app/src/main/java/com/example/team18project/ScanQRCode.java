package com.example.team18project;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

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
    private static final int LOCATION_PERMISSION_CODE = 1;
    private boolean takePhoto;
    private boolean keepLocation;
    private double latitude;
    private double longitude;
    private Intent data = new Intent();
    private LocationManager locationManager;

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

        Switch keepLocationSwitch = findViewById(R.id.keep_location_switch);
        keepLocation = keepLocationSwitch.isChecked();
        keepLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                keepLocation = isChecked;
            }
        });

        getLastLocation();

    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(ScanQRCode.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScanQRCode.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitude = lastLocation.getLatitude();
            longitude = lastLocation.getLongitude();
        }
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
                setResult(Activity.RESULT_OK, data);
                finish();
            }
    );

    ActivityResultLauncher<ScanOptions> barScanner = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String sha256 = null;
            String id = null;
            QRCode code = null;
            try {
                sha256 = QRCode.getSHA256(result.getContents());
            } catch (NoSuchAlgorithmException e) {
                Toast.makeText(getApplicationContext(), "ERROR: could not hash value", Toast.LENGTH_LONG).show();
                finish();
            }
            if (keepLocation) {
                code = new QRCode(sha256, new ArrayList<String>(), new ArrayList<Comment>(), latitude, longitude);
                id = QRCode.computeQid(latitude, longitude, sha256);
            } else {
                code = new QRCode(sha256, new ArrayList<String>(), new ArrayList<Comment>(), QRCode.NULL_LOCATION, QRCode.NULL_LOCATION);
                id = QRCode.computeQid(QRCode.NULL_LOCATION, QRCode.NULL_LOCATION, sha256);
            }

            code.setQid(id);
            data.putExtra("newCode", code);
            if (takePhoto) {
                Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePicture.launch(takePicIntent);
            } else {
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        }
    });

    /**
     * Handles the results of a permission request initiated by the system.
     * @param requestCode The request code used to represent a permission request.
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
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                @SuppressLint("MissingPermission") Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                latitude = lastLocation.getLatitude();
                longitude = lastLocation.getLongitude();
            } else {
                Toast.makeText(this, "Please enable location services", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

}