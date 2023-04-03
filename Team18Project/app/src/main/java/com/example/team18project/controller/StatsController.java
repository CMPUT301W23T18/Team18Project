package com.example.team18project.controller;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.team18project.view.StatsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class StatsController {

    /**
     * An empty constructor
     */
    public StatsController() {

    }

    /**
     * get the ranking of a player based on qid
     * @param qid the qid of player
     * @param callback the callback object
     */
    public void getRank(String qid, final StatsFragment.UserRankCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("QRCodes").orderBy("Score", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int count = 1;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String newQid = document.getId().toString();
                                if (newQid.equals(qid)) {
                                    callback.onUserRankGenerated(count);
                                }
                                count++;
                            }
                        } else {
                            Log.d("SearchError:", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
