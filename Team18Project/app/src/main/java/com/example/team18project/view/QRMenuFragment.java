package com.example.team18project.view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.team18project.model.QRArrayAdapter;
import com.example.team18project.R;
import com.example.team18project.model.Player;
import com.example.team18project.model.QRCode;

/**
 * Fragment for menu that pops up when a QR Code is clicked
 */
public class QRMenuFragment extends DialogFragment {
    interface OnDeleteListener {
        public void onDelete();
    }

    private QRArrayAdapter adapter;
    private QRCode code;
    private Player player;
    private OnDeleteListener listener;

    public QRMenuFragment(Player player, QRCode code, OnDeleteListener listener) {
        this.player = player;
        this.code = code;
        this.listener = listener;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = builder.setView(content).create();

        TextView viewButton = content.findViewById(R.id.view_button);
        TextView deleteButton = content.findViewById(R.id.delete_button);


        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).replaceFragment(QRViewFragment.newInstance(player,code));
                dialog.cancel();
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