package com.example.team18project;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Fragment for menu that pops up when a QR Code is clicked
 */
public class QRMenuFragment extends DialogFragment {
    private QRArrayAdapter adapter;
    private QRCode code;
    private Player player;

    public QRMenuFragment(Player player, QRCode code, QRArrayAdapter adapter) {
        this.player = player;
        this.code = code;
        this.adapter = adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr_menu, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View content = LayoutInflater.from(getContext()).inflate(R.layout.fragment_qr_menu, null);
        TextView viewButton = content.findViewById(R.id.view_button);
        TextView commentButton = content.findViewById(R.id.comment_button);
        TextView deleteButton = content.findViewById(R.id.delete_button);

        //TODO add listeners for buttons
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.removeQRCode(code);
                adapter.notifyDataSetChanged();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder.setView(content).create();
    }
}