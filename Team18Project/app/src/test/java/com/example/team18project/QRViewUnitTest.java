package com.example.team18project;

import static org.junit.Assert.assertTrue;

import android.content.Intent;

import com.example.team18project.controller.QRViewController;
import com.example.team18project.model.Comment;
import com.example.team18project.model.Player;
import com.example.team18project.model.QRCode;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Unit testing for QRViewController functionality
 */
public class QRViewUnitTest {
    private Player player;
    private QRCode code;
    private QRViewController controller;

    private Player getMockPlayer() {
        ArrayList<QRCode> codes = new ArrayList<QRCode>();
        codes.add(new QRCode("90236548d872e262cf6bc671bf3e7424e7f8e5deb37ed59fee89dbf064114d",
                new ArrayList<String>(),
                new ArrayList<Comment>(),
                -122.084,
                37.421998333333335));
        Player player = new Player(codes,
                "COMMENTTESTPLAYER",
                "Commenter",
                "",
                "",
                true);

        return player;
    }

    @Before
    public void setUp() throws Exception {
        TestSettings.getInstance().setTesting(true);
        TestSettings.getInstance().setFirebaseEnabled(false);
        TestSettings.getInstance().setTestAndroidID("COMMENTTESTPLAYER");
        player = getMockPlayer();
        code = player.getCodes().get(0);
        controller = new QRViewController(player,code);
    }

    /**
     * Tests the add comment functionality
     */
    @Test
    public void testComment() {
        controller.postComment("This is a test");
        assertTrue(code.getComments().get(0).getText().equals("This is a test"));
        assertTrue(code.getComments().get(0).getPosterId().equals("COMMENTTESTPLAYER"));
        assertTrue(code.getComments().get(0).getPosterUsername().equals("Commenter"));
    }
}
