package com.example.team18project.view;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.team18project.R;
import com.example.team18project.model.Comment;
import com.example.team18project.model.Player;
import com.example.team18project.model.QRArrayAdapter;
import com.example.team18project.model.QRCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Fragment for menu that pops up when a comment is clicked
 */
public class CommentMenuFragment extends DialogFragment {
    interface OnDeleteListener {
        public void onDelete();
    }

    private Comment comment;
    private boolean isPoster;

    private Player clickedPlayer;


    CollectionReference playerColRef;

    private OnDeleteListener listener;

    public CommentMenuFragment(Comment comment, boolean isPoster, OnDeleteListener listener) {
        this.comment = comment;
        this.isPoster = isPoster;
        this.listener = listener;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment_menu, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View content = LayoutInflater.from(getContext()).inflate(R.layout.fragment_comment_menu, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = builder.setView(content).create();

        TextView viewButton = content.findViewById(R.id.view_button);
        TextView deleteButton = content.findViewById(R.id.delete_button);

        if (!isPoster) {
            deleteButton.setEnabled(false);
        }
        Log.d("HERE",Boolean.toString(getActivity() == null));

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO view poster profile

                Task readTask = FirebaseFirestore.getInstance().collection("Players").document(comment.getPosterId()).get();
                readTask.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String username = documentSnapshot.getString("username");

                                //user account was found
                                String email = documentSnapshot.getString("email");
                                String phoneNumber = documentSnapshot.getString("phoneNumber");
                                boolean isHidden = documentSnapshot.getBoolean("isHidden");
                                ArrayList<DocumentReference> codeRefs = (ArrayList<DocumentReference>) documentSnapshot.get("codes");
                                ArrayList<QRCode> codes = new ArrayList<QRCode>();

                                //convert QR code DocumentReferences to QRCode objects
                                for (int i = 0; i < codeRefs.size(); i++) {
                                    codes.add(new QRCode(codeRefs.get(i)));
                                }
                                clickedPlayer = new Player(codes,documentSnapshot.getId(),username,email,phoneNumber,isHidden);
                                ((MainActivity) getActivity()).replaceFragment(StatsFragment.newInstance(clickedPlayer));
                                dialog.cancel();
                            }
                        });

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDelete();
                dialog.cancel();

            }
        });

        return dialog;
    }
}