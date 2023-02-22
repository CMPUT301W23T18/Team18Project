package com.example.team18project;

import java.util.ArrayList;

public class Player {
    private ArrayList<QRCode> codes;
    private String username;
    private String email;
    private String phoneNumber;
    private boolean isHidden;

    public Player(ArrayList<QRCode> codes, String username, String email, String phoneNumber, boolean isHidden) {
        this.codes = codes;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isHidden = isHidden;
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
}
