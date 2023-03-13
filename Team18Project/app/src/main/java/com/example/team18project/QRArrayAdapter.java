package com.example.team18project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Extends the ArrayAdapter<QRCode> class so that it can provide views for QR Codes.
 * These views display the QR codes name, score, and visual representation instead of just
 * its toString() representation
 * @author Michael Schaefer-Pham
 * @see android.widget.ArrayAdapter
 * @see QRCode
 */
public class QRArrayAdapter extends ArrayAdapter<QRCode> {
    public QRArrayAdapter(Context context, ArrayList<QRCode> cities) {
        super(context, 0, cities);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull
            ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.qr_code,
                    parent, false);
        } else {
            view = convertView;
        }
        QRCode code = getItem(position);
        TextView qrName = view.findViewById(R.id.qr_name);
        TextView qrScore = view.findViewById(R.id.qr_score);
        TextView qrVisual = view.findViewById(R.id.qr_visual);
        qrName.setText(code.getName());
        qrScore.setText("Score: " + Integer.toString(code.getScore()));
        qrVisual.setText(code.getVisual());
        return view;
    }
}