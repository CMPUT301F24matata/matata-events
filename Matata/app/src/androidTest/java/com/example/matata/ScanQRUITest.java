package com.example.matata;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.provider.Settings;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class ScanQRUITest {

        @Rule
        public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

        @Before
        public void setUp(){
            Intents.init();
        }

        @After
        public void tearDown(){
            Intents.release();
        }

        @Test
        public void testQRbutton() throws InterruptedException {
            onView(withId(R.id.qr_scanner)).perform(click());
            Thread.sleep(5000);
            intended(hasComponent(QR_camera.class.getName()));

        }



}
