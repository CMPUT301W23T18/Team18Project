package com.example.team18project;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QRCode {
    private String qid;
    private String value;
    private File photo; //TODO different data type might be better, look into it
    private ArrayList<Comment> comments; //TODO tree might be better if comments can be replied to
    private double longitude;
    private double latitude;

    public QRCode(String value, File photo, ArrayList<Comment> comments, double longitude, double latitude) {
        this.value = value;
        this.photo = photo;
        this.comments = comments;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public QRCode(DocumentReference doc) {
        qid = doc.getId();
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                qid = doc.getId();
                photo = null; //TODO figure out how photos will be stored
                value = documentSnapshot.getString("value");
                longitude = documentSnapshot.getDouble("longitude");
                latitude = documentSnapshot.getDouble("latitude");
                ArrayList<DocumentReference> commentRefs = (ArrayList<DocumentReference>) documentSnapshot.get("comments");
                comments = new ArrayList<Comment>();

                //fill comments ArrayList
                for (int i = 0; i < commentRefs.size(); i++) {
                    comments.add(new Comment(commentRefs.get(i)));
                }
            }
        });
    }

    //TODO temporary method, copy paste this code to whereever you need it
    public static String getSHA256(String s) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(s.getBytes());
        String hashString = "";
        for (int i = 0; i < hashBytes.length; i++) {
            hashString += Integer.toHexString(((int)hashBytes[i]) & 0xFF);
        }

        return hashString;
    }

    public String getVisual() { //TODO implement proper representation, maybe change return type
        return ":)";
    }

    public String getName() { //TODO generate name properly
        return "QRmon";
    }

    public int getScore() { //TODO generate score properly
        return 1863;
    }

    //getters and setters

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }
}
