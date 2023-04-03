package com.example.team18project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.team18project.model.Player;
import com.example.team18project.view.MainActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


/**
 * Tests the login functionality. Has a test for a new account, and one for an existing account
 */
public class LoginTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class,false,false);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    @Test
    public void testExistingAccount() {
        Intent intent = new Intent();
        intent.putExtra("isTesting", true);
        intent.putExtra("testAndroidID", "LoginTest");
        rule.launchActivity(intent);
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        assertTrue(solo.waitForText("Score:",2,3000));
        onView(withId(R.id.profile_icon)).perform(click());
        onView(withId(R.id.UserName_editText)).check(matches(withText("Sunfish")));
    }

    @Test
    public void testNewAccount() throws InterruptedException {
        Intent intent = new Intent();
        intent.putExtra("isTesting", true);
        intent.putExtra("testAndroidID", "NEW");
        rule.launchActivity(intent);
        //Test doesn't succeed without a sleep (player is null), I'm guessing it needs time to login
        Thread.sleep(2000);

        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        assertFalse(solo.waitForText("Score",1,3000));

        CollectionReference playersColl = FirebaseFirestore.getInstance().collection("Players");
        DocumentReference playerReference = playersColl.document("NEW");
        playerReference.delete();
    }

    @After
    public void tearDown() {
        solo.finishOpenedActivities();
        TestSettings.resetSettings();
    }
}
