package com.example.team18project.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.team18project.QrCodeCaptureActivity;
import com.example.team18project.R;
import com.example.team18project.model.Comment;
import com.example.team18project.model.Player;
import com.example.team18project.model.QRCode;
import com.example.team18project.view.HomeFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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

    private Button scanButton;
    private boolean takePhoto;
    private double latitude;
    private double longitude;
    private Player player;
    private FirebaseFirestore db;
    Intent data = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            player = (Player) getIntent().getSerializableExtra("player");
        }
//        Log.d("testing", "Data in add qr activity");
//        Log.d("testing", player.toString());
//        if (player.getUid() == null) Log.d("testing", "no uid");
//        if (player.getUsername() == null) Log.d("testing", "no username");
//        if (player.getUid() == null) Log.d("testing", "no uid");
//        if (player.getPhoneNumber() == null) Log.d("testing", "no number");
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
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(QrCodeCaptureActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Codes");
        integrator.initiateScan();
    }

    /**
     * Processes the results from the scan activity. We first use the SHA256 algorithm to compute
     * the hash for the scanned QRCode. The id of the QRCode is determined by its hash value,
     * alongside its latitude and longitude. If the scanned QRCode is new, it is added to the
     * database. Finally, the we add our newly created QRCode object into the result Intent object.
     *
     * If the user checks the checks the take_photo_switch, an additional ACTION_IMAGE_CAPTURE
     * activity is started after the user scans the QR code, but processing the results of this
     * activity have not yet been implemented.
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     */
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
                QRCode code = new QRCode(sha256, new ArrayList<String>(), new ArrayList<Comment>(), latitude, longitude);
                code.setQid(id);
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
                        player.addQRCode(code);

                    }
                });

                data.putExtra("newCode", code);
                setResult(HomeFragment.RESULT_OK, data);
                finish();
            } else {
                Toast.makeText(this, "no result", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Handles the results of a permission request initiated by the system.
     * @param requestCode The request code passed in {requestPermissions(
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