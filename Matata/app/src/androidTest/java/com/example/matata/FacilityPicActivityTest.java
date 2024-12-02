package com.example.matata;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FacilityPicActivityTest {

    @Rule
    public ActivityScenarioRule<TestFacilityPicActivity> activityRule =
            new ActivityScenarioRule<>(TestFacilityPicActivity.class);

    @Test
    public void testInitialProfilePictureDisplay() {
        onView(withId(R.id.ivProfilePicture))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testUploadButtonWithoutSelection() {
        onView(withId(R.id.btnUploadPicture)).perform(click());
    }

    @Test
    public void testDeleteButton() {
        onView(withId(R.id.btnDeletePicture)).perform(click());
    }

}
