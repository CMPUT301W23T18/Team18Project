package com.example.team18project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QRViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QRViewFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "player";
    private static final String ARG_PARAM2 = "code";

    private Player player;
    private QRCode code;
    private CommentArrayAdapter commentAdapter;
    private ArrayAdapter<String> otherPlayerAdaptor;

    private ArrayList<String> otherPlayerList;

    public QRViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param code The QR code being viewed
     * @return A new instance of fragment QRViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QRViewFragment newInstance(Player player, QRCode code) {
        QRViewFragment fragment = new QRViewFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, player);
        args.putParcelable(ARG_PARAM2, code);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            player = getArguments().getParcelable(ARG_PARAM1);
            code = getArguments().getParcelable(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_view, container, false);
        ImageView visual = view.findViewById(R.id.qr_visual);
        TextView name = view.findViewById(R.id.qrcode_name);
        TextView score = view.findViewById(R.id.qrcode_score);
        TextView location = view.findViewById(R.id.qrcode_location);
        TextView numScans = view.findViewById(R.id.qrcode_num_scans);
        ListView commentList = view.findViewById(R.id.comment_list);
        ListView otherPlayerListView = view.findViewById(R.id.other_player_list);
        Button commentButton = view.findViewById(R.id.post_comment_button);
        EditText commentEditText = view.findViewById(R.id.edit_text_comment);

        visual.setImageBitmap(code.getVisual(getContext(),2));
        name.setText(code.getName());
        score.setText("Score: " + Integer.toString(code.getScore()));
        location.setText("Latitude: " + Double.toString(code.getLatitude()) + "\nLongitude: " + Double.toString(code.getLongitude()));
        numScans.setText("TODO");
        commentAdapter = new CommentArrayAdapter(getContext(),code.getComments());
        commentList.setAdapter(commentAdapter);
        getOtherPlayer(code);
        otherPlayerAdaptor = new ArrayAdapter<String>(getContext(), 0, (List<String>) otherPlayerList);
        otherPlayerListView.setAdapter(otherPlayerAdaptor);


        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionReference commentColl = FirebaseFirestore.getInstance().collection("Comments");
                DocumentReference commentDoc = commentColl.document();

                String cid = commentDoc.getId();
                String posterId = player.getUid();
                String posterUsername = player.getUsername();
                String text = commentEditText.getText().toString();

                //upload new comment to Firebase
                Map<String, String> data = new HashMap<>();
                data.put("posterId",posterId);
                data.put("posterUsername",posterUsername);
                data.put("text",text);
                commentDoc.set(data);

                Comment comment = new Comment(cid,posterId,posterUsername,text);
                code.addComment(comment);
                //TODO only adds to this instance of the code right now
                //  since parcelables only pass copies, fix by either
                //  adding sync method to QRCode or refactoring code to
                //  have singleton session class to hold logged in player

                //update QR code in Firebase to contain new comment
                CollectionReference qrColl = FirebaseFirestore.getInstance().collection("QRCodes");
                DocumentReference qrDoc = qrColl.document(code.getQid());
                qrDoc.update("comments", FieldValue.arrayUnion(commentDoc));

                //update views
                commentAdapter.notifyDataSetChanged();
                commentEditText.setText("");
            }
        });
        return view;
    }

    private void getOtherPlayer(QRCode code) {
        otherPlayerList = new ArrayList<String>();
        FirebaseFirestore.getInstance().collection("Players")
                .whereEqualTo("isHidden", false)
                .whereArrayContains("codes", code.getQid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String newUser = document.get("username").toString();
                                otherPlayerList.add(newUser);
                            }
                        } else {
                            Log.d("SearchError:", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}