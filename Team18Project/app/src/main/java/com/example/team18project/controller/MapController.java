package com.example.team18project.controller;

import android.location.Location;

import androidx.annotation.Nullable;

import com.example.team18project.model.Comment;
import com.example.team18project.model.QRCode;
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

    public void findCloseCodes(ArrayList<QRCode> qrCodes, double currentLongitude, double currentLatitude) {

        CollectionReference qrCodesColl = FirebaseFirestore.getInstance().collection("QRCodes");

        qrCodesColl.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    double codeLongitude =  doc.getDouble("longitude");
                    double codeLatitude = doc.getDouble("latitude");

                    if (codeLongitude != QRCode.NULL_LOCATION && codeLatitude != QRCode.NULL_LOCATION) {
                        float[] distance = new float[1];
                        Location.distanceBetween(currentLatitude, currentLongitude, codeLatitude, codeLongitude, distance);
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

                }
            }
        });

    }

}
