package com.example.team18project;
import com.example.team18project.model.Comment;
import com.example.team18project.model.Player;
import com.example.team18project.model.QRCode;

import java.util.ArrayList;

public class PlayerTest {
    // QRCode(String value, ArrayList<String> photoIds, ArrayList<Comment> comments, double longitude, double latitude)
    private Player mockPlayer() {
        ArrayList<QRCode> codes = new ArrayList<QRCode>();
        codes.add(new QRCode("1", new ArrayList<String>(), new ArrayList<Comment>(), 10, -4.5));
        codes.add(new QRCode("2", new ArrayList<String>(), new ArrayList<Comment>(), 20.1, -34.5));
        codes.add(new QRCode("3", new ArrayList<String>(), new ArrayList<Comment>(), -1.74, 44));
        Player player = new Player(new ArrayList<QRCode>(),
                                    "1234",
                                    "bobby",
                                    "bobby@mail.com",
                                    "987-654-3210",
                                    false);

        return player;
    }

}
