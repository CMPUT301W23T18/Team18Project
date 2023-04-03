package com.example.team18project;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.team18project.view.MainActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class QRViewTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class,false,false);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    // test to make sure search work and it isn't case sensitive/can find usernames with even a partial matches
    @Test
    public void testComment() throws InterruptedException{
        TestSettings.getInstance().setTesting(true);
        TestSettings.getInstance().setFirebaseEnabled(true);
        TestSettings.getInstance().setTestAndroidID("QRViewTester");
        rule.launchActivity(null);
        //Test doesn't succeed without a sleep (player is null), I'm guessing it needs time to login
        Thread.sleep(2000);
        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.view_button)).perform(click());

        //check other players who scanned this code
        assertTrue(solo.searchText("Static"));
        assertTrue(solo.searchText("QRViewTestMan"));

        //check comments
        assertTrue(solo.searchText("Nice Code"));

        //post comment
        onView(withId(R.id.edit_text_comment)).perform(typeText("How Quaint"));
        onView(withId(R.id.post_comment_button)).perform(click());
        Espresso.closeSoftKeyboard();

        //check and delete new comment
        onView(withId(R.id.comment_list)).perform(ViewActions.swipeUp());
        assertTrue(solo.searchText("How Quaint"));
        solo.clickOnText("How Quaint");
        onView(withId(R.id.post_comment_button)).perform(click());
        onView(withId(R.id.delete_button)).perform(click());
        assertFalse(solo.searchText("How Quaint"));
    }

    @Test
    public void testProfileComment() throws InterruptedException{
        TestSettings.getInstance().setTesting(true);
        TestSettings.getInstance().setFirebaseEnabled(true);
        TestSettings.getInstance().setTestAndroidID("QRViewTester");
        rule.launchActivity(null);
        //Test doesn't succeed without a sleep (player is null), I'm guessing it needs time to login
        Thread.sleep(2000);
        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.view_button)).perform(click());

        //check other players who scanned this code
        assertTrue(solo.searchText("Static"));
        assertTrue(solo.searchText("QRViewTestMan"));

        //check comments
        assertTrue(solo.searchText("Nice Code"));

        //post comment
        onView(withId(R.id.edit_text_comment)).perform(typeText("How Quaint"));
        onView(withId(R.id.post_comment_button)).perform(click());
        Espresso.closeSoftKeyboard();

        //check and delete new comment
        onView(withId(R.id.comment_list)).perform(ViewActions.swipeUp());
        assertTrue(solo.searchText("How Quaint"));
        solo.clickOnText("How Quaint");
        onView(withId(R.id.post_comment_button)).perform(click());
        onView(withId(R.id.view_button)).perform(click());
        solo.assertCurrentActivity("right activity", MainActivity.class);
    }


    @After
    public void tearDown() {
        TestSettings.resetSettings();
    }
}
