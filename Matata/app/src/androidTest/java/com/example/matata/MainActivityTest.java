/**
 * MainActivityTest class provides instrumentation tests for the MainActivity, validating UI interactions,
 * user profile initialization, and RecyclerView data population. It also tests intent navigation from
 * MainActivity to other activities, verifying correct component transitions using Espresso Intents.
 *
 * Purpose:
 * - Ensure correct initialization of user profile data.
 * - Test UI interactions and navigation within MainActivity.
 *
 * Outstanding Issues:
 * - Test for Firestore error handling when loading events is commented out due to mocking limitations.
 * - Ensure compatibility with various device configurations, especially for permissions and intent handling.
 */

package com.example.matata;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.provider.Settings;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Runs instrumentation tests for the MainActivity using AndroidJUnit4.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    /**
     * Rule that provides an ActivityScenario for MainActivity, enabling test control over its lifecycle.
     */
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Rule granting necessary permissions for reading external storage, required for profile image handling.
     */
    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.READ_EXTERNAL_STORAGE);

    /**
     * Firestore instance for accessing and verifying user profile data.
     */
    private FirebaseFirestore db;

    /**
     * Initializes resources before each test, including Firestore and Espresso Intents.
     */
    @Before
    public void setup() {
        Intents.init();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Releases resources after each test, such as Espresso Intents, to ensure proper cleanup.
     */
    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Tests the initialization of user profile data from Firestore, verifying that all expected fields are populated.
     */
    @Test
    public void testUserProfileInitialization() {
        String userId = Settings.Secure.getString(
                InstrumentationRegistry.getInstrumentation().getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        db.collection("USER_PROFILES").document(userId).get().addOnCompleteListener(task -> {
            assertTrue(task.isSuccessful());
            if (task.isSuccessful() && task.getResult() != null) {
                assertNotNull(task.getResult().get("username"));
                assertNotNull(task.getResult().get("phone"));
                assertNotNull(task.getResult().get("email"));
                assertNotNull(task.getResult().get("notifications"));
                assertNotNull(task.getResult().get("profileUri"));
            }
        });
    }

    /**
     * Tests clicking the profile icon, verifying that it navigates to ProfileActivity.
     * Expected outcome: Intent to ProfileActivity should be launched.
     */
    @Test
    public void testProfileIconClick() {
        ActivityScenario<MainActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> activity.findViewById(R.id.profile_picture).performClick());
        intended(hasComponent(ProfileActivity.class.getName()));
    }

    /**
     * Tests clicking the "add event" icon, verifying that it navigates to AddEvent.
     * Expected outcome: Intent to AddEvent activity should be launched.
     */
    @Test
    public void testNewEventIconClick() {
        ActivityScenario<MainActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> activity.findViewById(R.id.add_event).performClick());
        intended(hasComponent(AddEvent.class.getName()));
    }

    /**
     * Tests clicking the QR scanner icon, verifying that it navigates to the QR_camera activity.
     * Expected outcome: Intent to QR_camera activity should be launched.
     */
    @Test
    public void testQRScannerClick() {
        ActivityScenario<MainActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> activity.findViewById(R.id.qr_scanner).performClick());
        intended(hasComponent(QR_camera.class.getName()));
    }

    /**
     * Tests clicking the event history icon, verifying that it navigates to EventHistory.
     * Expected outcome: Intent to EventHistory activity should be launched.
     */
    @Test
    public void testEventHistoryClick() {
        ActivityScenario<MainActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> activity.findViewById(R.id.event_history).performClick());
        intended(hasComponent(EventHistory.class.getName()));
    }

    /**
     * Tests the initialization of the RecyclerView, verifying that it has a layout manager set up.
     * Expected outcome: RecyclerView and its LayoutManager should be initialized.
     */
    @Test
    public void testRecyclerViewInitialization() {
        ActivityScenario<MainActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            assertNotNull(activity.findViewById(R.id.recycler_view_events));
            RecyclerView recyclerView = activity.findViewById(R.id.recycler_view_events);
            assertNotNull(recyclerView.getLayoutManager());
        });
    }

    /**
     * Tests that events are loaded into the RecyclerView, ensuring that the adapter has populated items.
     * Expected outcome: Adapter item count should be greater than zero.
     */
    @Test
    public void testEventsLoadedIntoRecyclerView() {
        ActivityScenario<MainActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.recycler_view_events);
            RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            assertNotNull(adapter);
            assertTrue(adapter.getItemCount() > 0);  // Ensure events are loaded
        });
    }

//    /**
//     * Test for Firestore error handling when loading events. This is currently commented out as
//     * it requires a mock setup for Firestore which may not be compatible with all configurations.
//     */
//    @Test
//    public void testAddEventsInitFirebaseErrorHandling() {
//        // Mock Firestore to simulate an error response
//        when(mockDb.collection("EVENT_PROFILES").get()).thenReturn(Mockito.mock(Task.class));
//
//        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
//        scenario.onActivity(activity -> {
//            activity.db = mockDb;  // Inject the mocked Firestore instance
//            activity.addEventsInit();
//
//            // Check for error handling response here (e.g., UI response or log check)
//        });
//    }
}
