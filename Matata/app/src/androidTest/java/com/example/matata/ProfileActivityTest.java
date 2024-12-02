//package com.example.matata;
//
//import static androidx.test.espresso.Espresso.onData;
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
//import static androidx.test.espresso.action.ViewActions.scrollTo;
//import static androidx.test.espresso.action.ViewActions.typeText;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//
//import static org.hamcrest.Matchers.containsString;
//
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//
//import static org.hamcrest.Matchers.not;
//
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//@RunWith(AndroidJUnit4.class)
//public class ProfileActivityTest {
//
//    @Rule
//    public ActivityScenarioRule<TestProfileActivity> activityRule =
//            new ActivityScenarioRule<>(TestProfileActivity.class);
//
//    @Test
//    public void testNameField() {
//        // Type a name into the name field
//        onView(withId(R.id.nameEditText))
//                .perform(typeText("Test User"), closeSoftKeyboard());
//
//        // Verify the typed name
//        onView(withId(R.id.nameEditText))
//                .check(matches(withText("Test User")));
//
//        onView(withId(R.id.clearAllButton))
//                .perform(scrollTo(), click());
//    }
//
//    @Test
//    public void testEmailField() {
//        // Type an email into the email field
//        onView(withId(R.id.emailEditText))
//                .perform(typeText("testuser@example.com"), closeSoftKeyboard());
//
//        // Verify the typed email
//        onView(withId(R.id.emailEditText))
//                .check(matches(withText("testuser@example.com")));
//
//        onView(withId(R.id.clearAllButton))
//                .perform(scrollTo(), click());
//    }
//
//    @Test
//    public void testPhoneField() {
//        // Type a phone number into the phone field
//        onView(withId(R.id.phoneEditText))
//                .perform(typeText("1234567890"), closeSoftKeyboard());
//
//        // Verify the typed phone number
//        onView(withId(R.id.phoneEditText))
//                .check(matches(withText("1234567890")));
//
//        onView(withId(R.id.clearAllButton))
//                .perform(scrollTo(), click());
//    }
//
//    @Test
//    public void testDatePicker() {
//        // Open the date picker
//        onView(withId(R.id.dobEditText)).perform(click());
//
//        // Set the date
//        onView(withText("OK")).perform(click());
//
//        // Verify the selected date
//        onView(withId(R.id.dobEditText))
//                .check(matches(not(withText(""))));
//
//        onView(withId(R.id.clearAllButton))
//                .perform(scrollTo(), click());
//    }
//
//    @Test
//    public void testGenderDropdown() {
//        // Open the dropdown
//        onView(withId(R.id.genderSpinner)).perform(click());
//
//        // Select "Male" from the dropdown
//        onData(org.hamcrest.Matchers.allOf(
//                org.hamcrest.Matchers.is(org.hamcrest.Matchers.instanceOf(String.class)),
//                org.hamcrest.Matchers.is("Male")
//        )).perform(click());
//
//        // Verify the selected item
//        onView(withId(R.id.genderSpinner))
//                .check(matches(withSpinnerText(containsString("Male"))));
//
//        onView(withId(R.id.clearAllButton))
//                .perform(scrollTo(), click());
//    }
//
//    @Test
//    public void testSaveAllFields() {
//        // Fill in all fields
//        onView(withId(R.id.nameEditText)).perform(typeText("Test User"), closeSoftKeyboard());
//        onView(withId(R.id.emailEditText)).perform(typeText("testuser@example.com"), closeSoftKeyboard());
//        onView(withId(R.id.phoneEditText)).perform(typeText("1234567890"), closeSoftKeyboard());
//        onView(withId(R.id.dobEditText)).perform(click());
//        onView(withText("OK")).perform(click());
//        onView(withId(R.id.genderSpinner)).perform(click());
//        onData(org.hamcrest.Matchers.allOf(
//                org.hamcrest.Matchers.is(org.hamcrest.Matchers.instanceOf(String.class)),
//                org.hamcrest.Matchers.is("Male")
//        )).perform(click());
//
//        // Save profile
//        onView(withId(R.id.saveButton)).perform(scrollTo(), click());
//
//        onView(withId(R.id.nameEditText)).check(matches(withText("Test User")));
//        onView(withId(R.id.emailEditText)).check(matches(withText("testuser@example.com")));
//        onView(withId(R.id.phoneEditText)).check(matches(withText("1234567890")));
//        onView(withId(R.id.dobEditText)).check(matches(not(withText("")))); // Ensure it's not empty
//        onView(withId(R.id.genderSpinner)).check(matches(withSpinnerText(containsString("Male"))));
//
//        onView(withId(R.id.clearAllButton))
//                .perform(scrollTo(), click());
//    }
//
//    @Test
//    public void testClearAllButton() {
//        // Fill in all fields
//        onView(withId(R.id.nameEditText)).perform(typeText("Test User"), closeSoftKeyboard());
//        onView(withId(R.id.emailEditText)).perform(typeText("testuser@example.com"), closeSoftKeyboard());
//        onView(withId(R.id.phoneEditText)).perform(typeText("1234567890"), closeSoftKeyboard());
//        onView(withId(R.id.dobEditText)).perform(click());
//        onView(withText("OK")).perform(click());
//        onView(withId(R.id.genderSpinner)).perform(click());
//        onData(org.hamcrest.Matchers.allOf(
//                org.hamcrest.Matchers.is(org.hamcrest.Matchers.instanceOf(String.class)),
//                org.hamcrest.Matchers.is("Male")
//        )).perform(click());
//
//        // Click the "Clear All" button
//        onView(withId(R.id.clearAllButton))
//                .perform(scrollTo(), click());
//
//        // Ensure all fields are cleared
//        onView(withId(R.id.nameEditText)).check(matches(withText("")));          // Name field should be empty
//        onView(withId(R.id.emailEditText)).check(matches(withText("")));        // Email field should be empty
//        onView(withId(R.id.phoneEditText)).check(matches(withText("")));        // Phone field should be empty
//        onView(withId(R.id.dobEditText)).check(matches(withText("")));          // DOB field should be empty
//        onView(withId(R.id.genderSpinner)).check(matches(withSpinnerText("Select gender"))); // Spinner should reset to default
//        onView(withId(R.id.switch_notification)).check(matches(isNotChecked())); // Notification switch should be off
//    }
//}
