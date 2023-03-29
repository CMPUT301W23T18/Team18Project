package com.example.team18project.controller;

import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.team18project.model.Comment;
import com.example.team18project.model.Player;
import com.example.team18project.model.QRCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class QRViewController {
    private Player player;
    private QRCode code;

    public QRViewController(Player player, QRCode code) {
        this.player = player;
        this.code = code;
    }

    public void postComment(String text) {
        String posterId = player.getUid();
        String posterUsername = player.getUsername();
        Comment comment = new Comment(null,posterId,posterUsername,text);
        FirebaseWriter.getInstance().addComment(comment,code.getQid());
        code.addComment(comment);
        //TODO only adds to this instance of the code right now
        //  since parcelables only pass copies, fix by either
        //  adding sync method to QRCode or refactoring code to
        //  have singleton session class to hold logged in player
        //  how it works right now:
        //  after adding a comment, if you click on the home button,
        //  then click on the QR code again and view the comments,
        //  the new one(s) won't show up, but if you do it again,
        //  it shows up. It seems that what fixes it is changing the
        //  screen twice
    }

    public void getOtherPlayers(QRCode code, ArrayList<String> otherPlayerList, ArrayAdapter<String> otherPlayerAdapter) {
        CollectionReference qrColl = FirebaseFirestore.getInstance().collection("QRCodes");
        FirebaseFirestore.getInstance().collection("Players")
                .whereEqualTo("isHidden", false)
                .whereArrayContains("codes", qrColl.document(code.getQid()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String newUser = document.get("username").toString();
                                otherPlayerList.add(newUser);
                            }
                            otherPlayerAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("SearchError:", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
