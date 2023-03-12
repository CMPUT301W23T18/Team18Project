package com.example.team18project;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class Comment implements Parcelable {
    private String cid;
    private String posterId;
    private String text;

    public Comment(String cid, String posterId, String text) {
        this.cid = cid;
        this.posterId = posterId;
        this.text = text;
    }

    public Comment(DocumentReference doc) {
        Task task = doc.get();
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                cid = documentSnapshot.getId();
                posterId = documentSnapshot.getString("posterId");
                text = documentSnapshot.getString("text");
            }
        });
        while (!task.isComplete()) {
            //wait until document is read
        }
    }

    //Parcelable implementation

    protected Comment(Parcel in) {
        cid = in.readString();
        posterId = in.readString();
        text = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(cid);
        dest.writeString(posterId);
        dest.writeString(text);
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
