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
 * A simple {@link Fragment} subclass.
 * Use the {@link QRMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class QRMenuFragment extends DialogFragment {
    interface DeleteListener {
        public void delete(QRCode code);
    }

    private static final String ARG_PARAM1 = "code";

    private QRArrayAdapter adapter;
    private QRCode code;
    private Player player;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param code The QR code that was clicked to open the menu
     * @return A new instance of fragment QRMenuFragment.
     */
    public static QRMenuFragment newInstance(QRCode code) {
        QRMenuFragment fragment = new QRMenuFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, code);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Required empty public constructor
     */
    public QRMenuFragment() {
        // Required empty public constructor
    }

    public QRMenuFragment(Player player, QRCode code, QRArrayAdapter adapter) {
        this.player = player;
        this.code = code;
        this.adapter = adapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //code = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr_menu, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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