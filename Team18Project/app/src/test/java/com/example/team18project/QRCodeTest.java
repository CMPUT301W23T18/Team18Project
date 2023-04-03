package com.example.team18project;

import static org.junit.Assert.assertTrue;

import com.example.team18project.model.Comment;
import com.example.team18project.model.QRCode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class QRCodeTest {
    private QRCode code;

    @Before
    public void setUp() throws Exception {
        TestSettings.getInstance().setTesting(true);
        TestSettings.getInstance().setFirebaseEnabled(false);
        TestSettings.getInstance().setTestAndroidID("1234");
        code = getMockCode();
    }

    private QRCode getMockCode() throws NoSuchAlgorithmException {
        //score = 4533
        QRCode code = new QRCode(QRCode.getSHA256("abc"),
                new ArrayList<String>(),
                new ArrayList<Comment>(),
                -122.084,
                37.421998333333335);
        code.setQid("TEST4");

        return code;
    }

    /**
     * Tests the SHA256 generation
     */
    @Test
    public void testSHA256() {
        assertTrue(code.getValue().equals("ba7816bf8f1cfea414140de5dae2223b0361a396177a9cb410ff61f2015ad"));
    }

    /**
     * Tests the score generation
     */
    @Test
    public void testScore() {
        assertTrue(code.getScore() == 4294);
    }

    @After
    public void tearDown() {
        TestSettings.resetSettings();
    }
}
