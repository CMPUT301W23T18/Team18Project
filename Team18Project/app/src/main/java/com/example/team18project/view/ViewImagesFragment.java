package com.example.team18project.view;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.team18project.R;
import com.example.team18project.controller.HomeController;
import com.example.team18project.controller.ViewImagesController;
import com.example.team18project.model.ImageArrayAdapter;
import com.example.team18project.model.Player;
import com.example.team18project.model.QRCode;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewImagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewImagesFragment extends Fragment {

    private static final String CODE = "code";
    private static final String PLAYER = "player";
    private QRCode code;
    private Player player;
    private ImageArrayAdapter imagesAdapter;
    private ViewImagesController controller;

    public ViewImagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *.
     * @return A new instance of fragment ViewImagesFragment.
     */
    public static ViewImagesFragment newInstance(Player player, QRCode code) {
        ViewImagesFragment fragment = new ViewImagesFragment();
        Bundle args = new Bundle();
        args.putParcelable(CODE, code);
        args.putParcelable(PLAYER, player);
        fragment.setArguments(args);
        fragment.controller = new ViewImagesController();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            code = getArguments().getParcelable(CODE);
            player = getArguments().getParcelable(PLAYER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_images, container, false);
        Button backButton = view.findViewById(R.id.back_button);
        ListView imagesList = view.findViewById(R.id.image_list);
        ArrayList<Bitmap> images = new ArrayList<Bitmap>();
        imagesAdapter = new ImageArrayAdapter(getContext(), images);
        controller.getImages(code, imagesAdapter);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).replaceFragment(QRViewFragment.newInstance(player,code));
            }
        });

        return view;
    }
}