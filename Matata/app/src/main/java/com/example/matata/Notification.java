package com.example.matata;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

public class Notification {

    private static final String TAG = "NotificationHelper";
    private static final String CHANNEL_ID = "waitlist_notifications";
    private static final String CHANNEL_NAME = "Waitlist Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for event waitlists";

    /**
     * Initializes the notification channel for the app (required for Android 8.0+).
     *
     * @param context Application context.
     */
    public static void initializeNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Subscribes the user to a specific topic.
     *
     * @param topic   The topic to subscribe to (e.g., event waitlist ID).
     * @param context Application context for displaying messages.
     */
    public static void subscribeToTopic(String topic, Context context) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Subscribed to topic: " + topic, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Subscribed to topic: " + topic);
                    } else {
                        Toast.makeText(context, "Failed to subscribe to topic.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to subscribe to topic: " + topic, task.getException());
                    }
                });
    }

    /**
     * Unsubscribes the user from a specific topic.
     *
     * @param topic   The topic to unsubscribe from (e.g., event waitlist ID).
     * @param context Application context for displaying messages.
     */
    public static void unsubscribeFromTopic(String topic, Context context) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Unsubscribed from topic: " + topic, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Unsubscribed from topic: " + topic);
                    } else {
                        Toast.makeText(context, "Failed to unsubscribe from topic.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to unsubscribe from topic: " + topic, task.getException());
                    }
                });
    }

    /**
     * Sends a notification to all users subscribed to a specific topic.
     *
     * * @param context Application context.
     * @param topic   The topic to send the notification to (e.g., waitlist ID).
     * @param title   The title of the notification.
     * @param message The body of the notification.
     */
    public static void sendTopicNotification(Context context, String topic, String title, String message) {
    
    }
}
