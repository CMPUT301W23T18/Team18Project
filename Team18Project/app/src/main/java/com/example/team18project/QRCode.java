package com.example.team18project;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class QRCode {
    private String name;
    private Bitmap photoBitmap; //TODO might need to replace with file if bitmaps are too big
    private ArrayList<Comment> comments; //TODO tree might be better if comments can be replied to
    private int score;
    private double longitude;
    private double latitude;

    public QRCode(String name, Bitmap photoBitmap, ArrayList<Comment> comments, int score, double longitude, double latitude) {
        this.name = name;
        this.photoBitmap = photoBitmap;
        this.comments = comments;
        this.score = score;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    //getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getPhotoBitmap() {
        return photoBitmap;
    }

    public void setPhotoBitmap(Bitmap photoBitmap) {
        this.photoBitmap = photoBitmap;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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
}
