package com.example.matata;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RunWith(AndroidJUnit4.class)
public class AddEventTest {

    @Rule
    public ActivityScenarioRule<AddEvent> activityScenarioRule = new ActivityScenarioRule<>(AddEvent.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testBackButtonClick_withEmptyFields_finishesActivity() {
        ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class);
        onView(withId(R.id.btnBackCreateEvent)).perform(click());
        scenario.onActivity(activity -> assertTrue(activity.isFinishing()));
    }

    @Test
    public void testBackButtonClick_withNonEmptyFields_showsConfirmationDialog() {
        ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class);
        onView(withId(R.id.eventTitle)).perform(typeText("Sample Event"));
        onView(withId(R.id.btnBackCreateEvent)).perform(click());

        scenario.onActivity(activity -> {
            assertNotNull(activity.getSupportFragmentManager().findFragmentByTag("BackPressFragment"));
        });
    }

    @Test
    public void testDateGroupClick_opensDatePickerDialog() {
        ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class);
        onView(withId(R.id.dateGroup)).perform(click());
        onView(isRoot()).inRoot(isDialog()).check(matches(isDisplayed()));
    }

    @Test
    public void testTimeGroupClick_opensTimePickerDialog() {
        ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class);
        onView(withId(R.id.timeGroup)).perform(click());
        onView(isRoot()).inRoot(isDialog()).check(matches(isDisplayed()));
    }

    @Test
    public void testPosterPicClick_opensImageSelector() {
        ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class);
        onView(withId(R.id.posterPicUpload)).perform(click());
        Intent expectedIntent = new Intent(Intent.ACTION_PICK);
        expectedIntent.setType("image/*");
        intended(hasAction(Intent.ACTION_PICK));
    }

    @Test
    public void testSaveEventInfo_withValidData() throws InterruptedException {
        ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class);

        onView(withId(R.id.editTextDate)).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.dateField)).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.number_of_people_event)).perform(replaceText("100"));

        onView(withId(R.id.desc_box)).perform(typeText("Testing Description"));

        onView(withId(R.id.eventTitle)).perform(typeText("Testing Event"));

        closeSoftKeyboard();

        onView(withId(R.id.posterPicUpload)).perform(click());
//      over here we manually select the image as there seems to be a lot of errors in the testing while trying to
//      apply the image automatically hence we wait.
        onView(withId(R.id.genQR)).perform(click());

        Thread.sleep(2000);

        Intents.intended(IntentMatchers.hasComponent(ViewEvent.class.getName()));

        // Database verification
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add a delay to give Firestore enough time to save the data
        Thread.sleep(3000);

        // Assuming EVENT_ID is generated and saved in Firestore, retrieve and verify the data
        String expectedEventTitle = "Testing Event";
        String expectedDescription = "Testing Description";
        String expectedCapacity = "100";

        db.collection("EVENT_PROFILES")
                .whereEqualTo("Title", expectedEventTitle)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        throw new AssertionError("No matching document found in Firestore for the given Title");
                    }

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        // Validate the document fields
                        assertEquals(expectedEventTitle, document.getString("Title"));
                        assertEquals(expectedDescription, document.getString("Description"));
                        assertEquals(expectedCapacity, String.valueOf(document.getLong("Capacity")));
                    }
                })
                .addOnFailureListener(e -> {
                    throw new AssertionError("Failed to retrieve document: " + e.getMessage());
                });
    }

    @Test
    public void testImageSelectionAndDisplay() {
        Uri imageUri = Uri.parse("content://media/external/images/media/1");

        Intent resultData = new Intent();
        resultData.setData(imageUri);
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_PICK))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData));

        try (ActivityScenario<AddEvent> scenario = activityScenarioRule.getScenario()) {
            Espresso.onView(withId(R.id.posterPicUpload)).perform(ViewActions.click());
            Espresso.onView(withId(R.id.posterPicUpload)).check(matches(isDisplayed()));
        }
    }

}
