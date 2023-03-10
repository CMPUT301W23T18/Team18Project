package com.example.team18project;

import static android.content.ContentValues.TAG;

import android.os.Parcel;
import android.os.Parcelable;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;

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
     * @param isHidden true if the player's account is hidden, false otherwise
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

    //QR Code methods
    /**
     This method adds a QRCode object to the list of codes and Firestore database.
     @param qrCode the QRCode object to be added.
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
     This method removes a QRCode object from the list of codes and Firestore database.
     @param qrCode the QRCode object to be removed
     */
    public void removeQRCode(QRCode qrCode) {
        if (this.codes.contains(qrCode)) {
            this.codes.remove(qrCode);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference QRCodesRef = db.collection("QRCodes");
            CollectionReference PlayersRef = db.collection("Players");
            DocumentReference player = PlayersRef.document(this.getUid());

            DocumentReference code = QRCodesRef.document(qrCode.getQid());
            player.update("codes", FieldValue.arrayRemove(code))
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
        else {
            Log.w(TAG, "Player doesn't have this QRCode");
        }
    }

    // parsing implementation
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
        dest.writeTypedList(codes);
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
     * Checks if the player is hidden to other users
     * @return true if the player is hidden, false otherwise
     */
    public boolean isHidden() {
        return isHidden;
    }

    /**
     * Sets whether the player is hidden to other users or not
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

    /**
     * Update the player to be up to date with the firestore
     */
    public void sync() {
        // Query all relevant information to the player from firebase
        CollectionReference playersColl = FirebaseFirestore.getInstance().collection("Players");
        DocumentReference playerReference = playersColl.document(uid);
        Task readTask = playerReference.get();

        // Read the data from the Query
        readTask.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String updatedUsername = documentSnapshot.getString("username");

                String updatedEmail = documentSnapshot.getString("email");
                String updatedPhoneNumber = documentSnapshot.getString("phoneNumber");
                boolean updatedIsHidden = documentSnapshot.getBoolean("isHidden");
                ArrayList<DocumentReference> codeRefs = (ArrayList<DocumentReference>) documentSnapshot.get("codes");
                ArrayList<QRCode> updatedCodes = new ArrayList<QRCode>();

                //convert QR code DocumentReferences to QRCode objects
                for (int i = 0; i < codeRefs.size(); i++) {
                    updatedCodes.add(new QRCode(codeRefs.get(i)));
                }

                // Implement the new data into our player variables
                setCodes(updatedCodes);
                setEmail(updatedEmail);
                setUsername(updatedUsername);
                setPhoneNumber(updatedPhoneNumber);
                setHidden(updatedIsHidden);
            }
        });
    }
}
