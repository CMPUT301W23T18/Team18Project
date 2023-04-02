package com.example.team18project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.team18project.view.MainActivity;
import com.example.team18project.view.StatsFragment;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


/**
 * Tests the search functionality. Has a test for searching for an account the is show and one that is hidden
 */
public class SearchTesting {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class,false,false);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    // test to make sure search work and it isn't case sensitive/can find usernames with even a partial matches
    @Test
    public void testSearchShown() throws InterruptedException{
        Intent intent = new Intent();
        intent.putExtra("isTesting", true);
        intent.putExtra("testAndroidID", "tOTGhDf8tNjzO8W1UpSU");
        rule.launchActivity(intent);
        //Test doesn't succeed without a sleep (player is null), I'm guessing it needs time to login
        Thread.sleep(2000);
        onView(withId(R.id.search_icon)).perform(click());
        onView(withId(R.id.username_search)).perform(typeText("nox"));
        solo.clickOnButton("Search");
        assertTrue(solo.waitForText("TestNox", 1, 10000));
    }

    // test to make sure users who are set to hidden to not show up in the search
    @Test
    public void testSearchHidden() throws InterruptedException{
        Intent intent = new Intent();
        intent.putExtra("isTesting", true);
        intent.putExtra("testAndroidID", "tOTGhDf8tNjzO8W1UpSU");
        rule.launchActivity(intent);
        //Test doesn't succeed without a sleep (player is null), I'm guessing it needs time to login
        Thread.sleep(2000);
        onView(withId(R.id.search_icon)).perform(click());
        onView(withId(R.id.username_search)).perform(typeText("nox"));
        solo.clickOnButton("Search");
        assertFalse(solo.waitForText("testNoxHidden", 1, 10000));
    }
    @Test
    public void testSearchChangeView() throws InterruptedException{
        Intent intent = new Intent();
        intent.putExtra("isTesting", true);
        intent.putExtra("testAndroidID", "tOTGhDf8tNjzO8W1UpSU");
        rule.launchActivity(intent);
        //Test doesn't succeed without a sleep (player is null), I'm guessing it needs time to login
        Thread.sleep(2000);
        onView(withId(R.id.search_icon)).perform(click());
        onView(withId(R.id.username_search)).perform(typeText("nox"));
        solo.clickOnButton("Search");
        solo.clickInList(0);
        solo.assertCurrentActivity("right activity", MainActivity.class);

    }

    @After
    public void tearDown() {
    }
}
