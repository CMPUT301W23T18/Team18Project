package com.example.team18project.controller;

import androidx.annotation.Nullable;

import com.example.team18project.model.QRCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapController {

    public void findCloseCodes(ArrayList<QRCode> qrCodes, Double currentLongitude, Double currentLatitude) {

        CollectionReference qrCodesColl = FirebaseFirestore.getInstance().collection("QRCodes");

        qrCodesColl.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Double codeLongitude = doc.getDouble("longitude");
                    Double codeLatitude = doc.getDouble("latitude");

                    if (codeLongitude != QRCode.NULL_LOCATION && codeLatitude != QRCode.NULL_LOCATION) {
                        // if distance is less than 50 meters append to qrCodes
                    }

                }
            }
        });

    }

}
