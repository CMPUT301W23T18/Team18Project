package com.example.team18project.controller;

import android.location.Location;
import android.util.Log;

import com.example.team18project.model.Comment;
import com.example.team18project.model.QRArrayAdapter;
import com.example.team18project.model.QRCode;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class MapController {

    public void findCloseCodes(ArrayList<QRCode> qrCodes, double currentLongitude, double currentLatitude, QRArrayAdapter qrAdapter) {

        CollectionReference qrCodesColl = FirebaseFirestore.getInstance().collection("QRCodes");

        qrCodesColl.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    double codeLongitude =  doc.getDouble("longitude");
                    double codeLatitude = doc.getDouble("latitude");

                    if (codeLongitude != QRCode.NULL_LOCATION && codeLatitude != QRCode.NULL_LOCATION) {
                        float[] distance = new float[1];
                        distance[0] = GeoDist(currentLatitude, currentLongitude, codeLatitude, codeLongitude);
                        String coordinates = Arrays.toString(new double[]{currentLatitude, currentLongitude, codeLatitude, codeLongitude});
                        coordinates = coordinates.replace("[", "").replace("]", "").replace(", ", ";");
                        Log.d("Distance", coordinates);
                        Log.d("Distance", String.valueOf(distance[0]));
                        if (distance[0] <= 50) {
                            String value = doc.getString("value");
                            ArrayList<DocumentReference> commentRefs = (ArrayList<DocumentReference>) doc.get("comments");
                            ArrayList<Comment> comments = new ArrayList<Comment>();
                            for (int i = 0; i < commentRefs.size(); i++) {
                                comments.add(new Comment(commentRefs.get(i)));
                            }
                            ArrayList<String> photoIds = (ArrayList<String>) doc.get("photo");

                            QRCode readCode = new QRCode(value, photoIds, comments, codeLongitude, codeLatitude);
                            readCode.setQid(doc.getId());
                            qrCodes.add(readCode);
                        }
                    }
                // add call back here
                }
                qrAdapter.notifyDataSetChanged();
            }
        });
    }

    private float GeoDist(double currentLatitude, double currentLongitude, double codeLatitude, double codeLongitude) {
        double lat1 = Math.toRadians(currentLatitude);
        double lon1 = Math.toRadians(currentLongitude);
        double lat2 = Math.toRadians(codeLatitude);
        double lon2 = Math.toRadians(codeLongitude);

        double distanceInKm = Math.acos(Math.sin(lat1)*Math.sin(lat2) + Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1)) * 6371;
        float distanceInMeters = (float) (distanceInKm * 1000);

        return distanceInMeters;
    }


}

