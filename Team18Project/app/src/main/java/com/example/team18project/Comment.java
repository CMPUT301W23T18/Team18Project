package com.example.team18project;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
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
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                cid = documentSnapshot.getId();
                posterId = documentSnapshot.getString("posterId");
                text = documentSnapshot.getString("text");
            }
        });
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

    /**
     * Gets the Firestore document ID of the player who posted the comment
     * @return The comment poster's id
     */
    public String getPosterId() {
        return posterId;
    }

    /**
     * Sets the Firestore document ID of the comment's poster
     * @param posterId The comment's poster's Firestore document ID
     */
    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    /**
     * Gets the text of the comment
     * @return The comment's text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text of the comment
     * @param text The comment's text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the Firestore document ID of the comment
     * @return The comment's Firestore document ID
     */
    public String getCid() {
        return cid;
    }

    /**
     * Sets the Firestore document ID of the comment
     * @param cid The comment's Firestore document ID
     */
    public void setCid(String cid) {
        this.cid = cid;
    }
}
