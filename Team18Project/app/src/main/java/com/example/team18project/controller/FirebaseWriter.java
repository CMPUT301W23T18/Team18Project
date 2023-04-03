package com.example.team18project.controller;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.team18project.TestSettings;
import com.example.team18project.model.Comment;
import com.example.team18project.model.Player;
import com.example.team18project.model.QRCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private FirebaseStorage storage;

    /**
     * Private empty constructor
     */
    private FirebaseWriter() {
        if (TestSettings.getInstance().isFirebaseEnabled()) {
            db = FirebaseFirestore.getInstance();
            storage = FirebaseStorage.getInstance();
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
                    // -- make a custom username
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                    String newUniqueUsername = currentDateTime.format(formatter);
                    // -- load into the DataBase
                    Map<String, Object> data = new HashMap<>();
                    data.put("codes",new ArrayList<>());
                    data.put("email","");
                    data.put("isHidden",true);
                    data.put("phoneNumber","");
                    data.put("username",newUniqueUsername);
                    data.put("highscore",0);
                    data.put("QRCount",0);
                    data.put("BestQRScore",0);
                    playerReference.set(data);
                    Log.d("TEST","WRITTEN");
                }
            }
        });
        Log.d("TEST","END ADD");
    }

    /**
     * Adds a qr code to Firebase and sets its qid field to the ID of the
     * new document. If the code's qid field already has a value,
     * then nothing happens. Also appends a reference to an image if the image
     * path is provided.
     * @param code
     */
    public void addQRCode(QRCode code, String imagePath) {
        if (!TestSettings.getInstance().isFirebaseEnabled()) {
            return;
        }

        if (code.getQid() != null) {
            return;
        }

        String id = QRCode.computeQid(code.getLatitude(), code.getLongitude(), code.getValue());
        CollectionReference codesColl = db.collection("QRCodes");
        DocumentReference codesRef = codesColl.document(id);
        code.setQid(id);
        Log.d("testing", "hash: " + id);
        Task readTask = codesRef.get();

        readTask.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String hash = documentSnapshot.getString("value");

                //QRCode not found, safe to write
                if (hash == null) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("comments",new ArrayList<DocumentReference>());
                    data.put("latitude",code.getLatitude());
                    data.put("longitude",code.getLongitude());
                    data.put("photo", new ArrayList<String>());
                    data.put("value", code.getValue());
                    data.put("Score", code.getScore());
                    codesRef.set(data);
                }

                // add a reference to an image of the surroundings
                if (imagePath != null) {
                    codesRef.update("photo", FieldValue.arrayUnion(imagePath));
                }
            }
        });

    }

    /**
     * Compresses and uploads a new image to firebase. Returns the path to the image as
     * a string.
     * @param image the image to be uploaded
     * @return
     */
    public String addImage(Bitmap image) {
        if (!TestSettings.getInstance().isFirebaseEnabled()) {
            return "";
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        String path = "images/img" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storage.getReference().child(path);
        UploadTask uploadTask = imageRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "ERROR: upload failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "upload succeeded :)");
            }
        });

        return path;

    }

    /**
     * Adds a comment to Firebase and sets its cid field to the ID of the
     * new document. If the comment's cid field already has a value,
     * then nothing happens.
     * @param comment
     * @param qid
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
