package com.example.matata;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertTrue;

import android.provider.Settings;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
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
            Espresso.onIdle();
            intended(hasComponent(QR_camera.class.getName()));

        }

        @Test
        public void testGoBack() throws InterruptedException {
            try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
                onView(withId(R.id.qr_scanner)).perform(click());
                Espresso.onIdle();
                onView(withId(R.id.go_back_qr_screen)).perform(click());
                Espresso.onIdle();
                scenario.onActivity(activity -> {
                    assertTrue(activity.isFinishing() || !activity.isDestroyed());
                });
            }
        }

}
