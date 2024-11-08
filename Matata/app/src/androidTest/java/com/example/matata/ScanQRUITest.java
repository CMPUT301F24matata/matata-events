package com.example.matata;

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
        public ActivityScenarioRule<AddEvent> activityRule = new ActivityScenarioRule<>(AddEvent.class);

        @Before
        public void setUp(){
            Intents.init();
        }

        @After
        public void tearDown(){
            Intents.release();
        }

        @Test
        public void testQRbutton(){


        }


}
