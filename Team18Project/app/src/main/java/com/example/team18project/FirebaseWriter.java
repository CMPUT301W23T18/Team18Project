package com.example.team18project;

import android.util.Log;

import androidx.annotation.NonNull;

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
    interface OnWrittenListener {
        public void onWritten(boolean isSuccessful);
    }

    private static FirebaseWriter instance = null;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Private empty constructor
     */
    private FirebaseWriter() {
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
     * @param comment
     * @param qid
     */
    public void addComment(Comment comment, String qid) {
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


    public void updateUsername(Player player, String newUsername, OnWrittenListener listener) {
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
