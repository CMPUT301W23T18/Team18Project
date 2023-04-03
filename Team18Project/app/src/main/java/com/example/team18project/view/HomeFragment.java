package com.example.team18project.view;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.team18project.controller.HomeController;
import com.example.team18project.controller.LeaderBoardInformationController;
import com.example.team18project.controller.ProfileController;
import com.example.team18project.model.QRArrayAdapter;
import com.example.team18project.R;
import com.example.team18project.model.Player;
import com.example.team18project.model.QRCode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A fragment used to represent the home screen, where QR codes are displayed and added
 */
public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "player";
    private HomeController controller;
    private Player player;
    private QRArrayAdapter qrAdapter;
    private ListView qrList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param player The logged in player
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(Player player) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1,player);
        fragment.setArguments(args);
        fragment.controller = new HomeController(player);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            player = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, null);
        FloatingActionButton scanCodeButton = view.findViewById(R.id.add_qr_button);
        scanCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScanQRCode.class);
                scanCode.launch(intent);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //make ListView
        ArrayList<QRCode> qrData = player.getCodes();
        qrList = (ListView) getView().findViewById(R.id.qr_list);
        qrAdapter = new QRArrayAdapter(getContext(), qrData);
        qrList.setAdapter(qrAdapter);

        qrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QRCode clicked = (QRCode) qrList.getItemAtPosition(position);
                new QRMenuFragment(player, clicked, new QRMenuFragment.OnDeleteListener() {
                    @Override
                    public void onDelete() {
                        //I'm sorry for this horrible mess, but it's the only way I could get deleting
                        //to update properly
                        HomeFragment.this.player.removeQRCode(clicked);
                        HomeFragment.this.qrAdapter = new QRArrayAdapter(getContext(), HomeFragment.this.player.getCodes());
                        HomeFragment.this.qrList.setAdapter(HomeFragment.this.qrAdapter);
                        LeaderBoardInformationController updater = new LeaderBoardInformationController();
                        updater.updatePlayer(player);
                    }
                }).show(getParentFragmentManager(),"Menu");
            }
        });
    }

    ActivityResultLauncher<Intent> scanCode = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    QRCode scannedCode = (QRCode) result.getData().getParcelableExtra("newCode");
                    Bitmap image = (Bitmap) result.getData().getParcelableExtra("image");
                    controller.addScannedQRCode(scannedCode, image);
                    player.addQRCode(scannedCode);
                    LeaderBoardInformationController updater = new LeaderBoardInformationController();
                    updater.updatePlayer(player);
                    boolean isNew = true;
                    for (int i = 0; i < qrAdapter.getCount(); i++) {
                        if (qrAdapter.getItem(i).getQid().equals(scannedCode.getQid())) {
                            isNew = false;
                        }
                    }
                    if (isNew) qrAdapter.add(scannedCode);
                }
            }
    );

}