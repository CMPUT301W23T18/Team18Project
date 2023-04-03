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
}
