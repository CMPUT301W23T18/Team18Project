package com.example.team18project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ProfileTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class,false,false);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    // test the two stats related to the lowest QR code, the score and the name
    @Test
    public void testUsernameUpdate() throws InterruptedException{
        Intent intent = new Intent();
        intent.putExtra("isTesting", true);
        String id = "lt6n69ixMi7FT6Y2apeo";
        intent.putExtra("testAndroidID", id);
        rule.launchActivity(intent);
        //Test doesn't succeed without a sleep (player is null), I'm guessing it needs time to login
        Thread.sleep(2000);
        // open the profile tab
        onView(withId(R.id.profile_icon)).perform(click());
        // check base profile is correct
        assertTrue(solo.waitForText("TestNoxProfile", 1, 10000));
        // update the profile and see if it updated firebase
        // - update profile
        onView(withId(R.id.UserName_editText)).perform(clearText());
        onView(withId(R.id.UserName_editText)).perform(typeText("TestNoxProfile_update"));
        solo.clickOnButton(R.id.SubmitchangeUsername);
        // - set up firebase reference to the specific user
        CollectionReference playersColl = FirebaseFirestore.getInstance().collection("Players");
        DocumentReference playerReference = playersColl.document(id);
        Task readTask = playerReference.get();
        // - read the information from the account
        readTask.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                assertTrue(documentSnapshot.getString("username") == "TestNoxProfile_update");
            }
        });
        // see if the update sync with firebase

        // restore original values
        onView(withId(R.id.username_search)).perform(typeText("TestNoxProfile"));
        solo.clickOnButton(R.id.SubmitchangeUsername);
    }
}
