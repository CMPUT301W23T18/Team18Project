package com.example.team18project;

import static android.text.Selection.setSelection;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.util.Log;

import androidx.test.espresso.ViewAction;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import junit.framework.AssertionFailedError;

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

    @Test
    public void testChangeUsername() throws InterruptedException {
        Intent intent = new Intent();
        intent.putExtra("isTesting", true);
        intent.putExtra("testAndroidID", "DummyAcc");
        rule.launchActivity(intent);
        //Test doesn't succeed without a sleep (player is null), I'm guessing it needs time to login
        Thread.sleep(2000);
        onView(withId(R.id.profile_icon)).perform(click());
        //TODO: fix line below
        onView(withId(R.id.UserName_editText)).perform(clearText(), typeText("useruser"));
        onView(withId(R.id.changeUsername)).perform(click());

        CollectionReference playersColl = FirebaseFirestore.getInstance().collection("Players");
        DocumentReference playerReference = playersColl.document("DummyAcc");
        playerReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String newUsername = documentSnapshot.getString("username");
                Log.d("testing", newUsername);
                assertTrue(newUsername.equals("useruser"));
                playerReference.update("username", "I");
            }
        });
    }

    @Test
    public void testChangePhoneNumber() throws InterruptedException {
        Intent intent = new Intent();
        intent.putExtra("isTesting", true);
        intent.putExtra("testAndroidID", "DummyAcc");
        rule.launchActivity(intent);
        //Test doesn't succeed without a sleep (player is null), I'm guessing it needs time to login
        Thread.sleep(2000);
        onView(withId(R.id.profile_icon)).perform(click());
        onView(withId(R.id.player_phone_number_editTextPhone)).perform(clearText(), typeText("666-666-6666"));
        onView(withId(R.id.submitPhoneButton)).perform(click());

        CollectionReference playersColl = FirebaseFirestore.getInstance().collection("Players");
        DocumentReference playerReference = playersColl.document("DummyAcc");
        playerReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String phoneNumber = documentSnapshot.getString("phoneNumber");
                assertTrue(phoneNumber.equals("666-666-6666"));
                playerReference.update("phoneNumber", "123-456-1234");
            }
        });
    }

    @Test
    public void testChangeEmail() throws InterruptedException {
        Intent intent = new Intent();
        intent.putExtra("isTesting", true);
        intent.putExtra("testAndroidID", "DummyAcc");
        rule.launchActivity(intent);
        //Test doesn't succeed without a sleep (player is null), I'm guessing it needs time to login
        Thread.sleep(2000);
        onView(withId(R.id.profile_icon)).perform(click());
        onView(withId(R.id.playerEmail_TextEmailAddress)).perform(clearText(), typeText("gary@hotmail.com"));
        onView(withId(R.id.submitEmailbutton)).perform(click());

        CollectionReference playersColl = FirebaseFirestore.getInstance().collection("Players");
        DocumentReference playerReference = playersColl.document("DummyAcc");
        playerReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String email = documentSnapshot.getString("email");
                assertTrue(email.equals("gary@hotmail.com"));
                playerReference.update("email", "test@test.com");
            }
        });
    }

//    @Test
//    public void testChangeHidden() throws InterruptedException {
//        Intent intent = new Intent();
//        intent.putExtra("isTesting", true);
//        intent.putExtra("testAndroidID", "DummyAcc");
//        rule.launchActivity(intent);
//        //Test doesn't succeed without a sleep (player is null), I'm guessing it needs time to login
//        Thread.sleep(2000);
////        onView(withId(R.id.profile_icon)).perform(click());
////        try {
////            onView(withId(R.id.hide_Account_switch)).check(matches(isChecked())).perform(click());
////        } catch (AssertionFailedError e) {
////        }
////
////        Thread.sleep(20000);
//
//        CollectionReference playersColl = FirebaseFirestore.getInstance().collection("Players");
//        DocumentReference playerReference = playersColl.document("DummyAcc");
//        playerReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                boolean isHidden = documentSnapshot.getBoolean("isHidden");
//                assertTrue(isHidden);
//            }
//        });
//    }
}
