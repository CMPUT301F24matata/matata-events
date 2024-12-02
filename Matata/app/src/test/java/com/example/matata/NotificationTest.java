package com.example.matata;

import static org.junit.jupiter.api.Assertions.assertEquals;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.junit.runner.RunWith;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {30}) // Specify the SDK version
public class NotificationTest {

    private Context context;

    @BeforeEach
    void setUp() {
        context = org.robolectric.RuntimeEnvironment.getApplication();
    }

    @Test
    void testNotificationBuildAndExtract() {
        // Arrange
        String expectedTitle = "Test Title";
        String expectedMessage = "This is a test message";
        String expectedChannelId = "test_channel";

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Act
        Notification notification = new NotificationCompat.Builder(context, expectedChannelId)
                .setContentTitle(expectedTitle)
                .setContentText(expectedMessage)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        // Extract data from the notification
        CharSequence actualTitle = notification.extras.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence actualMessage = notification.extras.getCharSequence(Notification.EXTRA_TEXT);
        String actualChannelId = notification.getChannelId();

        // Assert
        assertEquals(expectedTitle, actualTitle);
        assertEquals(expectedMessage, actualMessage);
        assertEquals(expectedChannelId, actualChannelId);
    }
}