package com.example.matata;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminReportActivityTest {

    @Rule
    public ActivityScenarioRule<AdminReportActivity> activityRule =
            new ActivityScenarioRule<>(AdminReportActivity.class);

    @Test
    public void testUIComponentsAreDisplayed() {
        ActivityScenario.launch(AdminReportActivity.class);

        // Verify main UI components
        onView(withId(R.id.btnBack)).check(matches(isDisplayed()));
        onView(withId(R.id.totalEvents)).check(matches(isDisplayed()));
        onView(withId(R.id.activeEvents)).check(matches(isDisplayed()));
        onView(withId(R.id.inactiveEvents)).check(matches(isDisplayed()));
        onView(withId(R.id.averageAttendance)).check(matches(isDisplayed()));
        onView(withId(R.id.popular_event)).check(matches(isDisplayed()));
        onView(withId(R.id.un_popular_event)).check(matches(isDisplayed()));
        onView(withId(R.id.totalUsers)).check(matches(isDisplayed()));
        onView(withId(R.id.activeUsers)).check(matches(isDisplayed()));
        onView(withId(R.id.frozenUsers)).check(matches(isDisplayed()));
        onView(withId(R.id.totalFacilities)).check(matches(isDisplayed()));
        onView(withId(R.id.activeFacilities)).check(matches(isDisplayed()));
        onView(withId(R.id.frozenFacilities)).check(matches(isDisplayed()));
        onView(withId(R.id.event_chart)).check(matches(isDisplayed()));
        onView(withId(R.id.user_chart)).check(matches(isDisplayed()));
        onView(withId(R.id.facility_chart)).check(matches(isDisplayed()));
    }

    @Test
    public void testEventStatisticsUpdate() {
        // Mocked data
        onView(withId(R.id.totalEvents)).check(matches(withText(containsString("5"))));
        onView(withId(R.id.activeEvents)).check(matches(withText(containsString("3"))));
        onView(withId(R.id.inactiveEvents)).check(matches(withText(containsString("2"))));
        onView(withId(R.id.averageAttendance)).check(matches(withText(containsString("%"))));
        onView(withId(R.id.popular_event)).check(matches(withText("Most Popular Event")));
        onView(withId(R.id.un_popular_event)).check(matches(withText("Least Popular Event")));
    }

    @Test
    public void testUserStatisticsUpdate() {
        onView(withId(R.id.totalUsers)).check(matches(withText(containsString("20"))));
        onView(withId(R.id.activeUsers)).check(matches(withText(containsString("15"))));
        onView(withId(R.id.frozenUsers)).check(matches(withText(containsString("5"))));
        onView(withId(R.id.entrantUsers)).check(matches(withText(containsString("10"))));
        onView(withId(R.id.organiserUsers)).check(matches(withText(containsString("5"))));
        onView(withId(R.id.adminUsers)).check(matches(withText(containsString("2"))));
    }

    @Test
    public void testFacilityStatisticsUpdate() {
        onView(withId(R.id.totalFacilities)).check(matches(withText(containsString("10"))));
        onView(withId(R.id.activeFacilities)).check(matches(withText(containsString("7"))));
        onView(withId(R.id.frozenFacilities)).check(matches(withText(containsString("3"))));
    }

    @Test
    public void testEventPieChartRendering() {
        Bitmap expectedBitmap = generateExpectedPieChartBitmap(3, 2);

        onView(withId(R.id.event_chart)).check((view, noViewFoundException) -> {
            ImageView imageView = (ImageView) view;
            Bitmap actualBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            assert compareBitmaps(expectedBitmap, actualBitmap);
        });
    }

    @Test
    public void testUserPieChartRendering() {
        Bitmap expectedBitmap = generateExpectedPieChartBitmap(15, 5);

        onView(withId(R.id.user_chart)).check((view, noViewFoundException) -> {
            ImageView imageView = (ImageView) view;
            Bitmap actualBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            assert compareBitmaps(expectedBitmap, actualBitmap);
        });
    }

    private Bitmap generateExpectedPieChartBitmap(int activeCount, int inactiveCount) {
        int total = activeCount + inactiveCount;
        if (total == 0) total = 1;

        float activePercentage = (float) activeCount / total * 360;
        float inactivePercentage = (float) inactiveCount / total * 360;

        int width = 500;
        int height = 500;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        canvas.drawColor(Color.TRANSPARENT);

        paint.setColor(Color.GREEN);
        canvas.drawArc(new RectF(50, 50, width - 50, height - 50), 0, activePercentage, true, paint);

        paint.setColor(Color.RED);
        canvas.drawArc(new RectF(50, 50, width - 50, height - 50), activePercentage, inactivePercentage, true, paint);

        return bitmap;
    }

    private boolean compareBitmaps(Bitmap expected, Bitmap actual) {
        if (expected.getWidth() != actual.getWidth() || expected.getHeight() != actual.getHeight()) {
            return false;
        }
        for (int x = 0; x < expected.getWidth(); x++) {
            for (int y = 0; y < expected.getHeight(); y++) {
                if (expected.getPixel(x, y) != actual.getPixel(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }
}
