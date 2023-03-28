package com.example.team18project;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    public interface UserListCallback {
        void onUserListGenerated(ArrayList<Pair<String, String>> userList);
    }
    private FirebaseFirestore db;
    private Player clickedPlayer;

    CollectionReference playerColRef;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        db = FirebaseFirestore.getInstance();
        playerColRef = db.collection("Players");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        EditText usernameSearchEditText = view.findViewById(R.id.username_search);
        Button searchButton = view.findViewById(R.id.search_button);
        ListView userListView = view.findViewById(R.id.user_list);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = usernameSearchEditText.getText().toString();
                generateUserList(new UserListCallback() {
                    @Override
                    public void onUserListGenerated(ArrayList<Pair<String, String>> userList) {
                        ArrayList<String> idFilteredUserList = new ArrayList<>();
                        ArrayList<String> usernameFilteredUserList = new ArrayList<>();

                        for (Pair<String, String> item : userList) {
                            if (item.second.toLowerCase().contains(searchText.toLowerCase())) {
                                idFilteredUserList.add(item.first);
                                usernameFilteredUserList.add(item.second);
                            }
                        }
                        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, usernameFilteredUserList);
                        //ArrayAdapter ids =
                        // try custom addapter  then pass
                        userListView.setAdapter(adapter);
                        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String username = adapter.getItem(position).toString();
                                Toast.makeText(getContext(), username, Toast.LENGTH_SHORT).show();

                                //FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                //transaction.replace(R.id.f);
                                // qureys fire base then returns a doucment that contaoins  all the player info to be passed to the stats Fragment
                                playerColRef.whereEqualTo("username", username)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        String email = document.getString("email");
                                                        String phoneNumber = document.getString("phoneNumber");
                                                        boolean isHidden = document.getBoolean("isHidden");
                                                        ArrayList<DocumentReference> codeRefs = (ArrayList<DocumentReference>) document.get("codes");
                                                        ArrayList<QRCode> codes = new ArrayList<QRCode>();

                                                        for (int i = 0; i < codeRefs.size(); i++) {
                                                            codes.add(new QRCode(codeRefs.get(i)));
                                                            Log.d("codes",codes.get(i).toString());
                                                        }

                                                        clickedPlayer = new Player(codes,document.getId(),username,email,phoneNumber,isHidden);
                                                        MainActivity activity = (MainActivity) getActivity();
                                                        activity.replaceFragment(new StatsFragment().newInstance(clickedPlayer));
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });
                                //MainActivity activity = (MainActivity) getActivity();
                                //activity.replaceFragment(new StatsFragment().newInstance(clickedPlayer));
                            }
                        });

                    }
                });
            }
        });

        return view;
    }

    public void generateUserList(final UserListCallback callback) {
        db = FirebaseFirestore.getInstance();
        db.collection("Players").whereEqualTo("isHidden", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Pair<String, String>> userList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Pair<String, String> newUser = new Pair<>(document.getId(), document.get("username").toString());
                                userList.add(newUser);
                            }
                            callback.onUserListGenerated(userList);
                        } else {
                            Log.d("SearchError:", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}