package com.example.team18project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScanQRCode extends AppCompatActivity {

    private Button scanButton;
    private boolean takePhoto;
    private double latitude;
    private double longitude;
    private Player player;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            player = (Player) getIntent().getParcelableExtra("player");
        }
        Log.d("testing", "Data in add qr activity");
        Log.d("testing", player.toString());
        if (player.getUid() == null) Log.d("testing", "no uid");
        if (player.getUsername() == null) Log.d("testing", "no username");
        if (player.getUid() == null) Log.d("testing", "no uid");
        if (player.getPhoneNumber() == null) Log.d("testing", "no number");
        db = FirebaseFirestore.getInstance();

        scanButton = findViewById(R.id.scan_button);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locationListener);

    }

    public void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(QrCodeCaptureActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Codes");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                if (takePhoto) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        try {
                            startActivityForResult(takePictureIntent, 1);
                        } catch (ActivityNotFoundException e) {
                            Log.d("ERROR", "Activity not found");
                        }
                }

                String sha256 = null;
                try {
                    sha256 = QRCode.getSHA256(result.getContents());
                } catch (NoSuchAlgorithmException e) {
                    Toast.makeText(getApplicationContext(), "ERROR: could not hash value", Toast.LENGTH_LONG).show();
                    finish();
                }

                double roundedLat = (double) Math.round(latitude * 10000) / 10000;
                double roundedLong = (double) Math.round(longitude * 10000) / 10000;
                String id = sha256 + "_" + Double.toString(roundedLat) + "_" + Double.toString(roundedLong);
                Log.d("testing", id);
                CollectionReference qrcodesColl = db.collection("QRCodes");
                DocumentReference qrcodesReference = qrcodesColl.document(id);
                Task readTask = qrcodesReference.get();

                String finalSha25 = sha256;
                readTask.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String hash = documentSnapshot.getString("value");
                        if (hash == null) { // this is a newly scanned qr code
                            Map<String, Object> data = new HashMap<>();
                            data.put("comments",new ArrayList<DocumentReference>());
                            data.put("latitude",latitude);
                            data.put("longitude",longitude);
                            data.put("photo", null);
                            data.put("value", finalSha25);
                            qrcodesReference.set(data);
                        }

                        QRCode code = new QRCode(finalSha25, new ArrayList<String>(), new ArrayList<Comment>(), latitude, longitude);
                        code.setQid(id);
                        //player.addQRCode(code);

                    }
                });

                finish();
            } else {
                Toast.makeText(this, "no result", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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