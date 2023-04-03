package com.example.team18project.view;

import static com.google.android.gms.location.Granularity.GRANULARITY_FINE;

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
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import com.example.team18project.QrCodeCaptureActivity;
import com.example.team18project.R;
import com.example.team18project.model.Comment;
import com.example.team18project.model.QRCode;

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
    private double latitude = QRCode.NULL_LOCATION;
    private double longitude = QRCode.NULL_LOCATION;
    private Intent data = new Intent();
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            CurrentLocationRequest.Builder requestBuilder = new CurrentLocationRequest.Builder();
//            requestBuilder.setGranularity(GRANULARITY_FINE);
//            requestBuilder.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
//            CurrentLocationRequest request = requestBuilder.build();
//            Task<Location> currentLocationTask = fusedLocationClient.getCurrentLocation(request, null);
//
//            currentLocationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                    if (location != null) {
////                        latitude = location.getLatitude();
////                        longitude = location.getLongitude();
////                        Log.d("testing", "actual: " + location.getLatitude());
////                        Log.d("testing", "actual: " + location.getLongitude());
//                    }
//                }
//            });
//            while (!currentLocationTask.isComplete()) {
//                //wait until location is set
//            }

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(ScanQRCode.this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
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
                Bundle bundle = result.getData().getExtras();
                Bitmap scannedImage = (Bitmap) bundle.get("data");
                data.putExtra("image", scannedImage);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
    );

    ActivityResultLauncher<ScanOptions> barScanner = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String sha256 = null;
            QRCode code;

            try {
                sha256 = QRCode.getSHA256(result.getContents());
            } catch (NoSuchAlgorithmException e) {
                Toast.makeText(getApplicationContext(), "ERROR: could not hash value", Toast.LENGTH_LONG).show();
                finish();
            }

            if (keepLocation) {
                code = new QRCode(sha256, new ArrayList<String>(), new ArrayList<Comment>(), longitude, latitude);
            } else {
                code = new QRCode(sha256, new ArrayList<String>(), new ArrayList<Comment>(), QRCode.NULL_LOCATION, QRCode.NULL_LOCATION);
            }

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
                getLastLocation();
            } else {
                Toast.makeText(this, "Please enable location services", Toast.LENGTH_LONG).show();
            }
        }
    }

}