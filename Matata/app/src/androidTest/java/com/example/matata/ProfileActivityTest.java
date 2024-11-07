//package com.example.matata;
//
//import android.content.Context;
//import android.content.Intent;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Switch;
//import android.view.View;
//
//import androidx.test.core.app.ApplicationProvider;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
//import androidx.test.core.app.ActivityScenario;
//
//import com.example.matata.AdminView;
//import com.example.matata.ProfileActivity;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static org.junit.Assert.*;
//
//@RunWith(AndroidJUnit4.class)
//public class ProfileActivityTest {
//
//    @Rule
//    public ActivityScenarioRule<ProfileActivity> activityRule = new ActivityScenarioRule<>(ProfileActivity.class);
//
//    private EditText nameEditText;
//    private EditText phoneEditText;
//    private EditText emailEditText;
//    private Switch isOrganizer;
//    private ImageView profileIcon;
//
//    @Before
//    public void setUp() {
//        activityRule.getScenario().onActivity(activity -> {
//            nameEditText = activity.findViewById(R.id.nameEditText);
//            phoneEditText = activity.findViewById(R.id.phoneEditText);
//            emailEditText = activity.findViewById(R.id.emailEditText);
//            isOrganizer = activity.findViewById(R.id.switch_organizer);
//            profileIcon = activity.findViewById(R.id.profileIcon);
//        });
//    }
//
//    @Test
//    public void testProfileInputValidation() {
//        activityRule.getScenario().onActivity(activity -> {
//            nameEditText.setText("John Doe");
//            phoneEditText.setText("1234567890");
//            emailEditText.setText("john.doe@example.com");
//        });
//
//        assertEquals("John Doe", nameEditText.getText().toString());
//        assertEquals("1234567890", phoneEditText.getText().toString());
//        assertEquals("john.doe@example.com", emailEditText.getText().toString());
//    }
//
//    @Test
//    public void testOrganizerSwitchVisibility() {
//        activityRule.getScenario().onActivity(activity -> isOrganizer.setChecked(true));
//        activityRule.getScenario().onActivity(activity -> {
//            assertEquals(View.VISIBLE, activity.findViewById(R.id.organizerFields).getVisibility());
//        });
//
//        activityRule.getScenario().onActivity(activity -> isOrganizer.setChecked(false));
//        activityRule.getScenario().onActivity(activity -> {
//            assertEquals(View.GONE, activity.findViewById(R.id.organizerFields).getVisibility());
//        });
//    }
//
//    @Test
//    public void testProfilePictureLoading() {
//        Context context = ApplicationProvider.getApplicationContext();
//        String testUri = "android.resource://" + context.getPackageName() + "/" + R.drawable.ic_upload;
//
//        activityRule.getScenario().onActivity(activity -> activity.loadProfilePicture(testUri));
//
//        activityRule.getScenario().onActivity(activity -> {
//            assertNotNull(profileIcon.getDrawable());
//        });
//    }
//
//    @Test
//    public void testAdminViewSwitch() {
//        activityRule.getScenario().onActivity(activity -> {
//            activity.adminView.setChecked(true);
//            Intent expectedIntent = new Intent(activity, AdminView.class);
//            Intent actualIntent = activity.getNextStartedActivity();
//            assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
//        });
//    }
//
//    @Test
//    public void testSaveProfileData() {
//        activityRule.getScenario().onActivity(activity -> {
//            nameEditText.setText("Jane Smith");
//            phoneEditText.setText("9876543210");
//            emailEditText.setText("jane.smith@example.com");
//            activity.saveProfileData("Jane Smith", "9876543210", "jane.smith@example.com", true, "testUri");
//        });
//
//        assertEquals("Jane Smith", nameEditText.getText().toString());
//        assertEquals("9876543210", phoneEditText.getText().toString());
//        assertEquals("jane.smith@example.com", emailEditText.getText().toString());
//    }
//
//    @Test
//    public void testFacilityDataSaveLoad() {
//        activityRule.getScenario().onActivity(activity -> {
//            activity.facilityName.setText("Main Hall");
//            activity.facilityAddress.setText("123 Main St");
//            activity.facilityCapacity.setText("200");
//            activity.facilityContact.setText("hall.contact@example.com");
//            activity.facilityOwner.setText("John Owner");
//            activity.saveFacilityData("Main Hall", "123 Main St", "200", "123456789", "hall.contact@example.com", "John Owner");
//        });
//
//        assertEquals("Main Hall", activity.facilityName.getText().toString());
//        assertEquals("123 Main St", activity.facilityAddress.getText().toString());
//        assertEquals("200", activity.facilityCapacity.getText().toString());
//    }
//
//}
