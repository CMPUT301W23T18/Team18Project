package com.example.team18project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Extends the ArrayAdapter<Comment> class so that it can provide views for Comments.
 * These views display the comment's text and the username of the poster instead of just
 * its toString() representation
 * @author Michael Schaefer-Pham
 * @see android.widget.ArrayAdapter
 * @see Comment
 */
public class CommentArrayAdapter extends ArrayAdapter<Comment> {
    public CommentArrayAdapter(Context context, ArrayList<Comment> comments) {
        super(context, 0, comments);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull
    ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_comment,
                    parent, false);
        } else {
            view = convertView;
        }
        Comment comment = getItem(position);
        TextView usernameText = view.findViewById(R.id.username);
        TextView contentText = view.findViewById(R.id.content);
        usernameText.setText(comment.getPosterUsername());
        contentText.setText(comment.getText());

        return view;
    }
}
