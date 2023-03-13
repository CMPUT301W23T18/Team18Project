package com.example.team18project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


/**
 * Tests the search functionality. Has a test for searching for an account the is show and one that is hidden
 */
public class StatsTesting {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class,false,false);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    // test the stats fragment shows the correct amount of total QR scans
    @Test
    public void testTotalScore() throws InterruptedException{
        Intent intent = new Intent();
        intent.putExtra("isTesting", true);
        intent.putExtra("testAndroidID", "tOTGhDf8tNjzO8W1UpSU");
        rule.launchActivity(intent);
        //Test doesn't succeed without a sleep (player is null), I'm guessing it needs time to login
        Thread.sleep(2000);
        onView(withId(R.id.stats_icon)).perform(click());
        onView(withId(R.id.QRTotalCount)).check(matches(withText("3")));
    }

    // test the stats fragment show the correct sum of QR scores
    @Test
    public void testSumQRScore() throws InterruptedException{
        Intent intent = new Intent();
        intent.putExtra("isTesting", true);
        intent.putExtra("testAndroidID", "tOTGhDf8tNjzO8W1UpSU");
        rule.launchActivity(intent);
        //Test doesn't succeed without a sleep (player is null), I'm guessing it needs time to login
        Thread.sleep(2000);
        onView(withId(R.id.stats_icon)).perform(click());
        onView(withId(R.id.QRTotalScore)).check(matches(withText("12876")));
    }

    // test the two stats related to the highest QR code, the score and the name
    @Test
    public void testHighestQRStats() throws InterruptedException{
        Intent intent = new Intent();
        intent.putExtra("isTesting", true);
        intent.putExtra("testAndroidID", "tOTGhDf8tNjzO8W1UpSU");
        rule.launchActivity(intent);
        //Test doesn't succeed without a sleep (player is null), I'm guessing it needs time to login
        Thread.sleep(2000);
        onView(withId(R.id.stats_icon)).perform(click());
        onView(withId(R.id.HighScoreQRname)).check(matches(withText("Queen Maximus Freenet of Sinniko")));
        onView(withId(R.id.HighScoreQRscore)).check(matches(withText("Score: 4395")));
    }

    // test the two stats related to the lowest QR code, the score and the name
    @Test
    public void testLowestQRStats() throws InterruptedException{
        Intent intent = new Intent();
        intent.putExtra("isTesting", true);
        intent.putExtra("testAndroidID", "tOTGhDf8tNjzO8W1UpSU");
        rule.launchActivity(intent);
        //Test doesn't succeed without a sleep (player is null), I'm guessing it needs time to login
        Thread.sleep(2000);
        onView(withId(R.id.stats_icon)).perform(click());
        onView(withId(R.id.LowScoreQRname)).check(matches(withText("Queen Maximus Hoffman of Sandgate")));
        onView(withId(R.id.LowScoreQRscore)).check(matches(withText("Score: 4227")));
    }

    @After
    public void tearDown() {
    }
}
