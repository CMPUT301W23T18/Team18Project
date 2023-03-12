package com.example.team18project;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

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

    public void addQRCode(QRCode qrCode) {
        this.codes.add(qrCode);
    }

    public void removeQRCode(QRCode qrCode) {
        assert (this.codes.contains(qrCode)); // throw an exception if the qrCode is not in codes
        this.codes.remove(qrCode);
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
}
