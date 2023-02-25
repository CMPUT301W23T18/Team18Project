package com.example.team18project;

public class Comment {
    private String cid;
    private Player poster;
    private String text;

    public Comment(Player poster, String text) {
        this.poster = poster;
        this.text = text;
    }

    //getters and setters

    public Player getPoster() {
        return poster;
    }

    public void setPoster(Player poster) {
        this.poster = poster;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
