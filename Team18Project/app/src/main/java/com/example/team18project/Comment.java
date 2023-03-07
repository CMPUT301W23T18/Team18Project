package com.example.team18project;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class Comment {
    private String cid;
    private String posterId;
    private String text;

    public Comment(String cid, String posterId, String text) {
        this.cid = cid;
        this.posterId = posterId;
        this.text = text;
    }

    public Comment(DocumentReference doc) {
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                cid = documentSnapshot.getId();
                posterId = documentSnapshot.getString("posterId");
                text = documentSnapshot.getString("text");
            }
        });
    }

    //getters and setters


    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
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
