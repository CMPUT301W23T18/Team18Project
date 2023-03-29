package com.example.team18project.controller;

import com.example.team18project.model.Comment;
import com.example.team18project.model.Player;
import com.example.team18project.model.QRCode;

public class QRViewController {
    private Player player;
    private QRCode code;

    public QRViewController(Player player, QRCode code) {
        this.player = player;
        this.code = code;
    }

    public void postComment(String text) {
        String posterId = player.getUid();
        String posterUsername = player.getUsername();
        Comment comment = new Comment(null,posterId,posterUsername,text);
        FirebaseWriter.getInstance().addComment(comment,code.getQid());
        code.addComment(comment);
        //TODO only adds to this instance of the code right now
        //  since parcelables only pass copies, fix by either
        //  adding sync method to QRCode or refactoring code to
        //  have singleton session class to hold logged in player
        //  how it works right now:
        //  after adding a comment, if you click on the home button,
        //  then click on the QR code again and view the comments,
        //  the new one(s) won't show up, but if you do it again,
        //  it shows up. It seems that what fixes it is changing the
        //  screen twice
    }
}
