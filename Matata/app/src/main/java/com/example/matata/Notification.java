package com.example.matata;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

/**
 * Notification class manages the setup and sending of notifications within the application.
 * This class is responsible for initializing the notification channel and sending notifications
 * to entrants when they are selected from a waitlist.
 *
 * Outstanding issues: Notification permissions must be granted by the user, and the app handles
 * this with a permission launcher. The `sendNotificationsToWaitlist` method checks for both
 * String IDs and DocumentReferences, assuming both formats may be used for entrant identification.
 */
public class Notification {

    /**
     * Constant string representing the channel ID for waitlist notifications.
     */
    private static final String CHANNEL_ID = "waitlist_notification_channel";

    /**
     * Launcher for requesting notification permissions from the user.
     */
    private ActivityResultLauncher<String> notificationPermissionLauncher;


    /**
     * Initializes the notification channel for waitlist notifications if the device's Android version
     * supports notification channels (Android O and above).
     *
     * @param context the application context needed to create the notification channel
     */
    public static void initNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Waitlist Notifications";
            String description = "Notifications for entrants selected from the waitlist";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d("NotificationInitTest", "Notification channel created");
        }
    }

    /**
     * Sends a notification to a specific entrant when they are selected from a waitlist.
     *
     * @param context the application context for accessing system services
     * @param title the title of the notification
     * @param message the content text of the notification
     * @param entrantId the ID of the entrant to notify, used to create unique notifications per entrant
     */
    public void sendWaitlistNotification(Context context, String title, String message, String entrantId) {
        Log.d("NotificationSendingTest", "sendWaitlistNotification");

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("entrantId", entrantId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Check permission before sending notifications
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            return;
        }
        notificationManager.notify(entrantId.hashCode(), builder.build());
    }

    /**
     * Sends notifications to a list of entrants on the waitlist, using their unique IDs or DocumentReferences.
     *
     * @param context the application context for accessing system services
     * @param waitlistIds a list of entrant IDs or DocumentReferences for entrants to be notified
     * @param title the title of the notification
     * @param message the content text of the notification
     */
    public void sendNotificationsToWaitlist(Context context, List<Object> waitlistIds, String title, String message) {
        Log.d("NotificationTest", "Sending notifications to waitlist...");
        try {
            for (Object entrantId : waitlistIds) {
                if (entrantId instanceof String) {
                    sendWaitlistNotification(context, title, message, (String) entrantId);
                    Log.d("NotificationTest", "Notification sent to: " + entrantId);
                } else if (entrantId instanceof DocumentReference) {
                    DocumentReference ref = (DocumentReference) entrantId;
                    String entrantIdString = ref.getId(); // Get the ID of the document
                    sendWaitlistNotification(context, title, message, entrantIdString);
                    Log.d("NotificationTest", "Notification sent to DocumentReference: " + entrantIdString);
                }
            }
        } catch (Exception e) {
            Log.e("NotificationTest", "Error while sending notification", e);
        }
    }

    /**
     * Sets the ActivityResultLauncher to request notification permissions.
     *
     * @param launcher the launcher to request notification permissions from the user
     */
    public void setNotificationPermissionLauncher(ActivityResultLauncher<String> launcher) {
        this.notificationPermissionLauncher = launcher;
    }
}
