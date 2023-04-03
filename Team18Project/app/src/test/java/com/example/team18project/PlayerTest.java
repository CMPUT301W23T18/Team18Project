package com.example.team18project;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.team18project.controller.QRViewController;
import com.example.team18project.model.Comment;
import com.example.team18project.model.Player;
import com.example.team18project.model.QRCode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class PlayerTest {
    private Player player;

    @Before
    public void setUp() throws Exception {
        TestSettings.getInstance().setTesting(true);
        TestSettings.getInstance().setFirebaseEnabled(false);
        TestSettings.getInstance().setTestAndroidID("1234");
        player = getMockPlayer();
    }

    private Player getMockPlayer() {
        ArrayList<QRCode> codes = new ArrayList<QRCode>();
        //score = 4045
        QRCode code1 = new QRCode("6b71cd550a88f6712b60f257a91e862bcae6a1634665ea76d993245d92b0", new ArrayList<String>(), new ArrayList<Comment>(), 10, -4.5);
        //score = 4413
        QRCode code2 = new QRCode("90236548d872e262cf6bc671bf3e7424e7f8e5deb37ed59fee89dbf064114d", new ArrayList<String>(), new ArrayList<Comment>(), 20.1, -34.5);
        //score = 4190
        QRCode code3 = new QRCode("c38c3e8ce7216d78ea5c49c772d23217671197f3c6df487a85f91507f68f87", new ArrayList<String>(), new ArrayList<Comment>(), -1.74, 44);
        code1.setQid("TEST1");
        code2.setQid("TEST2");
        code3.setQid("TEST3");
        codes.add(code1);
        codes.add(code2);
        codes.add(code3);
        Player player = new Player(codes,
                                    "1234",
                                    "bobby",
                                    "bobby@mail.com",
                                    "987-654-3210",
                                    false);

        return player;
    }
    private QRCode getMockCode() {
        //score = 4533
        QRCode code = new QRCode("2eb3614a1560dfd81f521dc0a22148dd9bf5ea8570edcb3b89f6e82b9548b392",
                new ArrayList<String>(),
                new ArrayList<Comment>(),
                -122.084,
                37.421998333333335);
        code.setQid("TEST4");

        return code;
    }

    /**
     * Tests the add QRCode functionality
     */
    @Test
    public void testAdd() {
        QRCode code = getMockCode();
        assertTrue(player.getCodes().size() == 3);
        player.addQRCode(code);
        assertTrue(player.getCodes().size() == 4);
        assertTrue(code.equals(player.getCodes().get(3)));
    }

    /**
     * Tests the add QRCode functionality
     */
    @Test
    public void testDelete() {
        QRCode code = player.getCodes().get(0);
        assertTrue(player.getCodes().size() == 3);
        player.removeQRCode(code);
        assertTrue(player.getCodes().size() == 2);
        assertFalse(player.getCodes().contains(code));
    }

    /**
     * Tests the stats functionality
     */
    @Test
    public void testStats() {
        assertTrue(player.totalAmountOfQRCodes() == 3);
        assertTrue(player.getLowestQRCode().equals(player.getCodes().get(0)));
        assertTrue(player.getHighestQRCode().equals(player.getCodes().get(1)));
        assertTrue(player.totalQRScore() == 12648);
    }

    @After
    public void tearDown() {
        TestSettings.resetSettings();
    }
}
