package com.example.team18project.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.team18project.model.QRArrayAdapter;
import com.example.team18project.R;
import com.example.team18project.model.Player;
import com.example.team18project.model.QRCode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A fragment used to represent the home screen, where QR codes are displayed and added
 */
public class HomeFragment extends Fragment {

    public static int request_Code = 1;
    public static int RESULT_OK = 0;
    private static final String ARG_PARAM1 = "player";

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
        FloatingActionButton scanCode = view.findViewById(R.id.add_qr_button);
        scanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScanQRCode.class);
                Log.d("testing", "values in home fragment");
                Log.d("testing", player.toString());
                Log.d("testing", player.getUid());
                Log.d("testing", player.getCodes().toString());
                Log.d("testing", player.getEmail());
                Log.d("testing", player.getUsername());
                if (player != null) {
                    intent.putExtra("player", (Serializable) player);
                    Log.d("testing", "data passed successfully");
                }

                startActivityForResult(intent, 1);
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
                    }
                }).show(getParentFragmentManager(),"Menu");
            }
        });
    }

    /**
     * Method used to process the results of the ScanQRCode activity. Adds a
     * newly scanned QRCode object to the qrAdapter if the player hasn't already scanned this
     * code.
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == request_Code) {
            if (resultCode == RESULT_OK) {
                QRCode newCode = (QRCode) data.getParcelableExtra("newCode");
                boolean isNew = true;
                for (int i = 0; i < qrAdapter.getCount(); i++) {
                    if (qrAdapter.getItem(i).getQid().equals(newCode.getQid())) {
                        isNew = false;
                    }
                }
                if (isNew) qrAdapter.add(newCode);
            }
        }
    }

}