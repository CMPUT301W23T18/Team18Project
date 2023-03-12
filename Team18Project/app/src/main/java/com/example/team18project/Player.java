package com.example.team18project;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class for modelling players. Stores scanned QR codes and account information.
 */
public class Player implements Parcelable {
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

    //QR Code methods

    public void addQRCode(QRCode qrCode) {
        this.codes.add(qrCode);
    }

    public void removeQRCode(QRCode qrCode) {
        assert (this.codes.contains(qrCode)); // throw an exception if the qrCode is not in codes
        this.codes.remove(qrCode);
    }

    //Parcelable implementation

    /**
     * Constructs a Player from a given Parcel
     * @param in The parcel to construct the player from
     */
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
}
