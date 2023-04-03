package com.example.team18project.controller;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.team18project.model.Player;
import com.example.team18project.model.QRCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * A class whose purpose and function revolve around storing and updating the information on firebase necessary for quickly retrieve leaderboards
 */
public class LeaderBoardInformationController {
    // an instance of the firestore that will be used by the class
    private FirebaseFirestore db;

    /**
     * A constructor the initializes the db.
     */
    public LeaderBoardInformationController() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Update all players in the firabase player collection in case any follow the old legacy format
     * NOTE: only to be called by admins for fixing firebase and not to be run the app normally
     */
    /*
    public void updateLegacyPlayer() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Players")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                String username = documentSnapshot.getString("username");

                                //user account was found
                                String email = documentSnapshot.getString("email");
                                String phoneNumber = documentSnapshot.getString("phoneNumber");
                                boolean isHidden = documentSnapshot.getBoolean("isHidden");
                                ArrayList<DocumentReference> codeRefs = (ArrayList<DocumentReference>) documentSnapshot.get("codes");
                                ArrayList<QRCode> codes = new ArrayList<QRCode>();

                                //convert QR code DocumentReferences to QRCode objects
                                for (int i = 0; i < codeRefs.size(); i++) {
                                    DocumentReference doc = codeRefs.get(i);
                                    codes.add(new QRCode(codeRefs.get(i)));
                                }
                                // Generate a new user than start then start the home frame and navigation bar
                                Player player = new Player(codes,documentSnapshot.getId(),username,email,phoneNumber,isHidden);
                                updatePlayer(player);
                            }
                        } else {
                            Log.d("SearchError:", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    */

    /**
     * Update a passed players in the firabase player collection
     * @param player
     */
    public void updatePlayer(Player player) {
        CollectionReference playersRef = db.collection("Players");
        // -- add QR count
        Map<String, Object> QRCount = new HashMap<>();
        try {
            QRCount.put("QRCount", player.totalAmountOfQRCodes());
        } catch (Exception e) {
            QRCount.put("QRCount", 0);
        }
        playersRef.document(player.getUid()).set(QRCount, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Player QR count updated successfully"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating player QR count", e));

        // -- add highscore
        Map<String, Object> HighScore = new HashMap<>();
        try {
            HighScore.put("highscore", player.totalQRScore());
        } catch (Exception e) {
            HighScore.put("highscore", 0);
        }

        playersRef.document(player.getUid()).set(HighScore, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Player high score updated successfully"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating player high score", e));

        // -- best QR
        Map<String, Object> BestQR = new HashMap<>();
        try {
            BestQR.put("BestQRScore", player.getHighestQRCode().getScore());
        } catch (Exception e) {
            BestQR.put("BestQRScore", 0);
        }

        playersRef.document(player.getUid()).set(BestQR, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Player best QR score updated successfully"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating player best QR score", e));
    }

    /**
     * Update a passed QR in the firabase player collection
     * @param QR
     */
    public void updateQR(QRCode QR) {
        CollectionReference qrRef = db.collection("QRCodes");
        Map<String, Object> Score = new HashMap<>();
        try {
            Score.put("Score", QR.getScore());
        } catch (Exception e) {
            Score.put("Score", 0);
        }

        qrRef.document(QR.getQid()).set(Score, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "QR score updated successfully"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating QR score", e));
    }

    /**
     * Find a QR codes rank
     * @param QR
     * @return the rank of a QR code
     */
    public int FindQRRank(QRCode QR) {
        return 0;
    }
}
