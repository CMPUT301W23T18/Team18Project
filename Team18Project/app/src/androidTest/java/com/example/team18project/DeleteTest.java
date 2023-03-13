package com.example.team18project;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class DeleteTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class,false,false);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    @Test
    public void testExistingAccount() throws InterruptedException {
        Intent intent = new Intent();
        intent.putExtra("isTesting", true);
        intent.putExtra("testAndroidID", "DummyAcc");
        rule.launchActivity(intent);
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        solo.waitForText("Empress Nolan Harper of Sandgate");

        Player player = rule.getActivity().getPlayer();
        QRCode code = player.getCodes().get(0);

        solo.clickOnText("Empress Nolan Harper of Sandgate");
        solo.clickOnText("Delete");
        assertTrue(player.getCodes().size() == 0);
        assertFalse(solo.waitForText("Score:",1,3000));
        player.addQRCode(code);
    }

    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
