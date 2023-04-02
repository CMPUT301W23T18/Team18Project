package com.example.team18project.controller;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.team18project.TestSettings;
import com.example.team18project.model.Comment;
import com.example.team18project.model.Player;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FirebaseWriter {
    public interface OnWrittenListener {
        public void onWritten(boolean isSuccessful);
    }

    private static FirebaseWriter instance = null;
    private FirebaseFirestore db;

    /**
     * Private empty constructor
     */
    private FirebaseWriter() {
        if (TestSettings.getInstance().isFirebaseEnabled()) {
            db = FirebaseFirestore.getInstance();
        }
    }

    /**
     * Gets an instance of FirebaseWriter
     * @return an instance of FirebaseWriter
     */
    public static FirebaseWriter getInstance() {
        if (instance == null) {
            instance = new FirebaseWriter();
        }
        return instance;
    }

    /**
     * Adds a new document to the Players collection in Firebase with a
     * given ID. If the ID is already taken, then nothing happens.
     * @param ID the ID of the new document
     */
    public void addPlayer(String ID) {
        if (!TestSettings.getInstance().isFirebaseEnabled()) {
            return;
        }

        CollectionReference playersColl = db.collection("Players");
        DocumentReference playerReference = playersColl.document(ID);
        Task readTask = playerReference.get();

        //try to read account from database
        readTask.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String username = documentSnapshot.getString("username");

                //account not found, safe to write
                if (username == null) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("codes",new ArrayList<>());
                    data.put("email","");
                    data.put("isHidden",true);
                    data.put("phoneNumber","");
                    data.put("username","");
                    playerReference.set(data);
                    Log.d("TEST","WRITTEN");
                }
            }
        });
        Log.d("TEST","END ADD");
    }

    /**
     * Adds a comment to Firebase and sets its cid field to the ID of the
     * new document. If the comment's cid field already has a value,
     * then nothing happens.
     * @param comment The comment to be added
     * @param qid The Firebase document ID of the QR code the comment is posted under
     */
    public void addComment(Comment comment, String qid) {
        if (!TestSettings.getInstance().isFirebaseEnabled()) {
            return;
        }

        if (comment.getCid() != null) {
            return;
        }
        CollectionReference commentColl = db.collection("Comments");
        DocumentReference commentDoc = commentColl.document();
        comment.setCid(commentDoc.getId());

        //upload new comment to Firebase
        Map<String, String> data = new HashMap<>();
        data.put("posterId",comment.getPosterId());
        data.put("posterUsername",comment.getPosterUsername());
        data.put("text",comment.getText());
        commentDoc.set(data);

        //update QR code in Firebase to contain new comment
        CollectionReference qrColl = db.collection("QRCodes");
        DocumentReference qrDoc = qrColl.document(qid);
        qrDoc.update("comments", FieldValue.arrayUnion(commentDoc));
    }

    /**
     * Removes a comment from Firebase
     * @param comment The comment to be deleted
     * @param qid The Firebase document ID of the QR code the comment is posted under
     */
    public void deleteComment(Comment comment, String qid) {
        if (!TestSettings.getInstance().isFirebaseEnabled()) {
            return;
        }

        CollectionReference commentColl = db.collection("Comments");
        DocumentReference commentDoc = commentColl.document(comment.getCid());

        //remove comment document from firebase
        commentDoc.delete();

        //update QR code in Firebase to contain new comment
        CollectionReference qrColl = db.collection("QRCodes");
        DocumentReference qrDoc = qrColl.document(qid);
        qrDoc.update("comments", FieldValue.arrayRemove(commentDoc));
    }

    /**
     * Changes a players username in Firebase to a given value, provided that the new username
     * isn't already taken
     * @param player The player whose username will be updated
     * @param newUsername The new username
     * @param listener A listener that is called when the username is changed (or fails to change)
     */
    public void updateUsername(Player player, String newUsername, OnWrittenListener listener) {
        if (!TestSettings.getInstance().isFirebaseEnabled()) {
            listener.onWritten(true);
            return;
        }

        CollectionReference usersRef = db.collection("Players");
        DocumentReference playerRef = usersRef.document(player.getUid());
        Query query = usersRef.whereIn("username", Arrays.asList(newUsername));

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot.isEmpty()) {
                        // There are no instances of the data
                        playerRef.update("username", newUsername);
                        listener.onWritten(true);
                    } else {
                        listener.onWritten(false);
                        // There is at least one instance of the data
                        //userNameText.setText(currentPlayer.getUsername());
                        // implement pop up here
                        //Toast.makeText(getContext(), "Username already in Use!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle any errors
                }
            }
        });
    }
}
