package com.example.team18project.controller;

import android.location.Location;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.team18project.model.Comment;
import com.example.team18project.model.QRCode;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapController {

    public void writeCodesToMap(GoogleMap googleMap, LatLng currentLocation) {
        googleMap.clear();

        CollectionReference qrCodesColl = FirebaseFirestore.getInstance().collection("QRCodes");
        qrCodesColl.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    double codeLongitude =  doc.getDouble("longitude");
                    double codeLatitude = doc.getDouble("latitude");

                    if (codeLongitude != QRCode.NULL_LOCATION && codeLatitude != QRCode.NULL_LOCATION) {
                        float[] distance = new float[1];
                        Location.distanceBetween(currentLocation.latitude, currentLocation.longitude, codeLatitude, codeLongitude, distance);
                        if (distance[0] <= 50) {
                            LatLng location = new LatLng(codeLatitude, codeLongitude);
                            String title = "" + doc.getDouble("Score");
                            MarkerOptions options = new MarkerOptions().position(location).title(title);
                            googleMap.addMarker(options);
                        }
                    }

                }
            }
        });
    }

}
