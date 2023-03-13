package com.example.team18project;

import static android.content.ContentValues.TAG;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class for modelling players. Stores scanned QR codes and account information.
 */
public class Player implements Parcelable, Serializable {
    private ArrayList<QRCode> codes;
    private String uid;
    private String username;
    private String email;
    private String phoneNumber;
    private boolean isHidden;

    /**
     * Constructs a Player with the specified information
     * @param codes The QR codes of the player
     * @param uid The player's Firestore document ID
     * @param username The username of the player
     * @param email The email of the player
     * @param phoneNumber The phone number of the player
     * @param isHidden true if the player's account information is hidden, false otherwise
     */
    public Player(ArrayList<QRCode> codes, String uid, String username, String email, String phoneNumber, boolean isHidden) {
        this.codes = codes;
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isHidden = isHidden;
    }

    /**
     * Constructs a Player with the specified ID. Intended for making new accounts
     * @param uid The player's Firestore document ID
     */
    public Player(String uid) {
        this.uid = uid;
        this.codes = new ArrayList<QRCode>();
        this.username = "";
        this.email = "";
        this.phoneNumber = "";
        this.isHidden = true;
    }

    /**
     * Add an instance of a qr code to the the players firebase associated document
     * @param qrCode
     */
    public void addQRCode(QRCode qrCode) {
        this.codes.add(qrCode);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference QRCodesRef = db.collection("QRCodes");
        CollectionReference PlayersRef = db.collection("Players");
        DocumentReference player = PlayersRef.document(this.getUid());
        DocumentReference code = QRCodesRef.document(qrCode.getQid());
        // Append the new QRCode document reference to the player's "codes" array
        player.update("codes", FieldValue.arrayUnion(code))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully added!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    /**
     * Remove an instance of a QR code from the players associated document
     * @param qrCode
     */
    public void removeQRCode(QRCode qrCode) {
        if (this.codes.contains(qrCode)) {
            this.codes.remove(qrCode);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            //CollectionReference QRCodesRef = db.collection("QRCodes");
            CollectionReference PlayersRef = db.collection("Players");
            DocumentReference player = PlayersRef.document(this.getUid());
            player.update("codes", FieldValue.arrayRemove(qrCode.getQid()))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });
        }
    }

    //Parcelable implementation

    /**
     * Constructs a Player from a given Parcel
     * @param in The parcel to construct the player from
     */
    protected Player(Parcel in) {
        Log.d("parse", "out");
        codes = in.createTypedArrayList(QRCode.CREATOR);
        uid = in.readString();
        username = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        isHidden = in.readByte() != 0;
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        Log.d("parse", "in");
        dest.writeParcelableList(codes,flags);
        dest.writeString(uid);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeByte((byte) (isHidden ? 1 : 0));
    }

    //getters and setters

    /**
     * Gets the QR codes of the player
     * @return The player's QR codes
     */
    public ArrayList<QRCode> getCodes() {
        return codes;
    }

    /**
     * Sets the QR codes of the player
     * @param codes The QR codes of the player
     */
    public void setCodes(ArrayList<QRCode> codes) {
        this.codes = codes;
    }

    /**
     * Gets the username of the player
     * @return The player's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the player
     * @param username The username of the player
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the email of the player
     * @return The player's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the player
     * @param email The email of the player
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the phone number of the player
     * @return The player's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the player
     * @param phoneNumber The phone number of the player
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Checks if the player's account information is hidden to other users
     * @return true if the player is hidden, false otherwise
     */
    public boolean isHidden() {
        return isHidden;
    }

    /**
     * Sets whether the player's account information is hidden to other users or not
     * @param hidden true if the player is hidden, false otherwise
     */
    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    /**
     * Gets the Firestore document ID of the player
     * @return The player's Firestore document ID
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the Firestore document ID of the player
     * @param uid The player's Firestore document ID
     */
    public void setUid(String uid) {
        this.uid = uid;
    }



    /**
     * retrieve the sum score of all the QR codes owned by the player
     * @return final sum
     */
    public int totalQRScore() {
        int score = 0;
        for (QRCode code : codes) {
            score += code.getScore();
        }
        return score;
    }

    /**
     *      returns the highest scored QRcode
     *      @return a QR code
     */
    public QRCode getHighestQRCode(){
        int score = Integer.MIN_VALUE;
        QRCode highestQR = null;
        for(QRCode code : codes){
            if(code.getScore() > score){
                score = code.getScore();
                highestQR = code;
            }
        }
        return highestQR;
    }

    /**
     *  returns the lowest scored QRcode
     *  @return a QR code
     */
    public QRCode getLowestQRCode() {
        int score = Integer.MAX_VALUE;
        QRCode lowestQR = null;
        for(QRCode code : codes){
            if(code.getScore() < score){
                score = code.getScore();
                lowestQR = code;
            }
        }
        return lowestQR;
    }

    /**
     * Calculate number of QR codes scanned by this player
     * @return an integer sum
     */
    public int totalAmountOfQRCodes() {
        int sum = 0;
        for(QRCode code : codes){
            sum ++;
        }
        return sum;
    }
}
