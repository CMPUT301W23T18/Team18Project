package com.example.team18project.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.team18project.R;

import java.util.ArrayList;

public class ImageArrayAdapter extends ArrayAdapter<Bitmap> {

    public ImageArrayAdapter(Context context, ArrayList<Bitmap> codes) {
        super(context, 0, codes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.image,
                    parent, false);
        } else {
            view = convertView;
        }

        Bitmap image = getItem(position);
        ImageView visual = view.findViewById(R.id.qr_visual);
        visual.setImageBitmap(image);

        return view;
    }

}
