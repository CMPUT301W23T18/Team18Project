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


import java.util.ArrayList;

public class Player implements Parcelable {
    private ArrayList<QRCode> codes;
    private String uid;
    private String username;
    private String email;
    private String phoneNumber;
    private boolean isHidden;

    public Player(ArrayList<QRCode> codes, String uid, String username, String email, String phoneNumber, boolean isHidden) {
        this.codes = codes;
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isHidden = isHidden;
    }

    public Player(String uid) {
        this.uid = uid;
        this.codes = new ArrayList<QRCode>();
        this.username = "";
        this.email = "";
        this.phoneNumber = "";
        this.isHidden = true;
    }

    //QR Code methods
    public void addQRCode(QRCode qrCode) {
        this.codes.add(qrCode);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference QRCodesRef = db.collection("QRCodes");
        CollectionReference PlayersRef = db.collection("Players");
        DocumentReference player = PlayersRef.document(this.getUid());
                // Append the new QRCode document reference to the player's "codes" array
        player.update("codes", FieldValue.arrayUnion(qrCode.getQid()))
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

    protected Player(Parcel in) {
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
        dest.writeParcelableList(codes,flags);
        dest.writeString(uid);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeByte((byte) (isHidden ? 1 : 0));
    }

    //getters and setters

    public ArrayList<QRCode> getCodes() {
        return codes;
    }

    public void setCodes(ArrayList<QRCode> codes) {
        this.codes = codes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public String getUid() {
        return uid;
    }

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
     *      returns name of the highest scored QRcode
     *      @return a QR code
     */
    public QRCode getHighestQRCode(){
        QRCode code = null;
        int score = 0;
        for(int i = 0; i < codes.size(); i++){
            QRCode QRobject = codes.get(i);
            if(QRobject.getScore() > score){
                code = QRobject;
                score = QRobject.getScore();
            }
        }
        return code;
    }

    /**
     *  returns name of the lowest scored QRcode
     *  @return a QR code
     */
    public QRCode getLowestQRCode() {
        QRCode code = null;
        int score = 0;
        for (int i = 0; i < codes.size(); i++) {
            QRCode QRobject = codes.get(i);
            if (QRobject.getScore() < score) {
                code = QRobject;
                score = QRobject.getScore();
            }
        }
        return code;
    }
}
