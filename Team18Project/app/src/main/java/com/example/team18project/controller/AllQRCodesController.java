package com.example.team18project.controller;

import androidx.annotation.Nullable;

import com.example.team18project.model.Comment;
import com.example.team18project.model.Player;
import com.example.team18project.model.QRCode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AllQRCodesController {
    private Player player;

    public AllQRCodesController() {
    }

    /**
     * Updates a given ArrayList to contain all QR codes in a given QuerySnapshot
     * @param qrList the ArrayList to update
     * @param queryDocumentSnapshots
     */
    public void updateQRCodes(ArrayList<QRCode> qrList, QuerySnapshot queryDocumentSnapshots) {
        qrList.clear();
        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
            //Log.d(TAG, String.valueOf(doc.getData().get("Province Name")));
            Double longitude = doc.getDouble("longitude");
            Double latitude = doc.getDouble("latitude");
            String value = doc.getString("value");

            ArrayList<DocumentReference> commentRefs = (ArrayList<DocumentReference>) doc.get("comments");
            ArrayList<Comment> comments = new ArrayList<Comment>();
            //convert QR code DocumentReferences to QRCode objects
            for (int i = 0; i < commentRefs.size(); i++) {
                comments.add(new Comment(commentRefs.get(i)));
            }
            //TODO you can't cast document references to objects like this, if we need
            //  to use photos for anything in this fragment (which we will) it'll break
            //  fix this when the problem comes up (you can base it off the comments
            //  one above, but there might be extra work if DocumentReference needs
            //  to be converted to String)
            ArrayList<String> photoIds = (ArrayList<String>) doc.get("photo");

            QRCode readCode = new QRCode(value, photoIds, comments, longitude, latitude);
            readCode.setQid(doc.getId());
            qrList.add(readCode);
        }
    }
}
