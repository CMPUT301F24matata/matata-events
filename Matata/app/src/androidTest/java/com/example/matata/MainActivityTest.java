package com.example.matata;

import android.content.Intent;
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

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.READ_EXTERNAL_STORAGE);

    private FirebaseFirestore db;

    @Before
    public void setup() {
        Intents.init();
        db = FirebaseFirestore.getInstance();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

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

    @Test
    public void testProfileIconClick() {
        ActivityScenario<MainActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> activity.findViewById(R.id.profile_picture).performClick());
        intended(hasComponent(ProfileActivity.class.getName()));
    }

    @Test
    public void testNewEventIconClick() {
        ActivityScenario<MainActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> activity.findViewById(R.id.add_event).performClick());
        intended(hasComponent(AddEvent.class.getName()));
    }

    @Test
    public void testQRScannerClick() {
        ActivityScenario<MainActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> activity.findViewById(R.id.qr_scanner).performClick());
        intended(hasComponent(QR_camera.class.getName()));
    }

    @Test
    public void testEventHistoryClick() {
        ActivityScenario<MainActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> activity.findViewById(R.id.event_history).performClick());
        intended(hasComponent(EventHistory.class.getName()));
    }

    @Test
    public void testRecyclerViewInitialization() {
        ActivityScenario<MainActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            assertNotNull(activity.findViewById(R.id.recycler_view_events));
            RecyclerView recyclerView = activity.findViewById(R.id.recycler_view_events);
            assertNotNull(recyclerView.getLayoutManager());
        });
    }

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
