package com.example.matata;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;


/**
 * The Notification class provides utility methods for managing and sending notifications.
 * It includes support for creating notification channels (required for Android 8.0 and above),
 * subscribing to and unsubscribing from topics, and sending notifications to users.
 */
public class Notifications {

    /**
     * Tag used for logging.
     */
    private static final String TAG = "NotificationHelper";

    /**
     * Unique ID for the notification channel.
     */
    private static final String CHANNEL_ID = "waitlist_notifications";

    /**
     * Name of the notification channel.
     */
    private static final String CHANNEL_NAME = "Waitlist Notifications";

    /**
     * Description of the notification channel.
     */
    private static final String CHANNEL_DESCRIPTION = "Notifications for event waitlists";

    /**
     * Firebase Functions instance for sending notifications.
     */
    private static final FirebaseFunctions firebaseFunctions = FirebaseFunctions.getInstance();

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
     */
    public void subscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Subscribed to topic: " + topic);
                    } else {
                        Log.e(TAG, "Failed to subscribe to topic: " + topic, task.getException());
                   }
                });
    }

    /**
     * Unsubscribes the user from a specific topic.
     *
     * @param topic   The topic to unsubscribe from (e.g., event waitlist ID).
     */
    public void unsubscribeFromTopic(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Unsubscribed from topic: " + topic);
                    } else {
                        Log.e(TAG, "Failed to unsubscribe from topic: " + topic, task.getException());
                    }
                });
    }

    /**
     * Sends a notification to all users subscribed to a specific topic.
     *
     * @param topic   The topic to send the notification to (e.g., waitlist ID).
     * @param title   The title of the notification.
     * @param message The body of the notification.
     * @param context Application context.
     */
    public void sendNotification(Context context, String topic, String title, String message) {
        Map<String, Object> data = new HashMap<>();
        data.put("topic", topic);
        data.put("title", title);
        data.put("message", message);

        firebaseFunctions.getHttpsCallable("sendNotification")
                .call(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Notification sent successfully!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Notification sent successfully to topic: " + topic);
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseFunctionsException) {
                            FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                            FirebaseFunctionsException.Code code = ffe.getCode();
                            Object details = ffe.getDetails();
                            Log.e(TAG, "Firebase Functions Error: " + code + ", Details: " + details);
                        }
                        Toast.makeText(context, "Failed to send notification.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to send notification to topic: " + topic, e);
                    }
                });
    }

}