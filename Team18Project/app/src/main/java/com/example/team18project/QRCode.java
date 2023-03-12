package com.example.team18project;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for modelling QR codes.
 */
public class QRCode implements Parcelable {
    private String qid;
    private String value;
    private ArrayList<String> photoIds; //TODO different data type might be better, look into it
    private ArrayList<Comment> comments; //TODO tree might be better if comments can be replied to
    private double longitude;
    private double latitude;

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

    public QRCode(String value, ArrayList<String> photoIds, ArrayList<Comment> comments, double longitude, double latitude) {
        this.value = value;
        this.photoIds = photoIds;
        this.comments = comments;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public QRCode(DocumentReference doc) {
        qid = doc.getId();
        Task task = doc.get();
        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                photoIds = null; //TODO figure out how photos will be stored
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
        while (!task.isComplete()) {
            //wait until document is read
        }
    }

    //Parcelable implementation

    protected QRCode(Parcel in) {
        qid = in.readString();
        value = in.readString();
        photoIds = in.createStringArrayList();
        comments = in.createTypedArrayList(Comment.CREATOR);
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    public static final Creator<QRCode> CREATOR = new Creator<QRCode>() {
        @Override
        public QRCode createFromParcel(Parcel in) {
            return new QRCode(in);
        }

        @Override
        public QRCode[] newArray(int size) {
            return new QRCode[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelableList(comments,flags);
        dest.writeStringList(photoIds);
        dest.writeString(qid);
        dest.writeString(value);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
    }

    //generate information based on hash value

    public String getVisual() { //TODO implement proper representation, maybe change return type
        return ":)";
    }

    /**
     * Convert the QR codes hash functions(value) into a unique 4 part name string
     * @return unique name string
     */
    public String getName() {
        char[] splitHash = value.toCharArray();
        // convert the hash into 4 key codes which is the sum of all char int values in that quarter
        int[] keyCodes = new int[4];
        for (int index = 0; index < 32; index++) {
            int key = index % 4;
            keyCodes[key] += (int) splitHash[index];
        }
        String[] name = new String[4];
        // create all string arrays the name can be pulled from
        // note* do to the way this function is implemented each array can be expanded easily and indefinably by simply adding a new string at the end of an array
        String[] title = {"Emperor", "Empress", "King", "Queen", "Duke", "Duchess", "Prince", "Princes"};
        String[] firstName = {"Nolan", "Maximus", "Leo", "Victor", "Katie", "Kiara", "Selena", "Amy"};
        String[] SurName = {"Smith", "White", "Price", "Harper", "Chambers", "Cohen", "Hoffman", "Freenet"};
        String[] origin = {"Sinniko", "Sandgate", "Blastmore", "Wastehold", "Grimwall", "Driuru", "Smogmore", "Hookfort"};
        // select a value from each string array
        name[0] = title[keyCodes[0] % title.length];
        name[1] = firstName[keyCodes[1] % firstName.length];
        name[2] = SurName[keyCodes[2] % SurName.length];
        name[3] = origin[keyCodes[3] % origin.length];
        // merge the names characteristics and return it
        return TextUtils.join(" ", name);
    }

    /**
     * Compute the score of a QR code as it's sum value of integer byte conversions
     * @return the score as an integer
     */
    public int getScore() {
        char[] splitHash = value.toCharArray();
        int score = 0;
        for (char hash: splitHash) {
            score += (int) hash;
        }
        return  score;
    }

    //getters and setters

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ArrayList<String> getPhotoIds() {
        return photoIds;
    }

    public void setPhotoIds(ArrayList<String> photoIds) {
        this.photoIds = photoIds;
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
