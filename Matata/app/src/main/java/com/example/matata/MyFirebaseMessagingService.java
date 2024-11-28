package com.example.matata;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 *The MyFirebaseMessagingService class extends FirebaseMessagingService and is responsible for handling incoming Firebase Cloud Messaging (FCM) messages.
 *It overrides the onMessageReceived method to handle incoming FCM messages and show notifications based on the received data.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    /**
     * onMessageReceived is called when a new FCM message is received.
     * This provided method is overridden from the FirebaseMessagingService class.
     * @param remoteMessage Remote message that has been received through FCM.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("Notification Received", "From: " + remoteMessage.getFrom());

        // Check if notifications are enabled
        SharedPreferences preferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        boolean isNotificationsEnabled = preferences.getBoolean("notifications_enabled", true); // Default is true

        if (!isNotificationsEnabled) {
            Log.d("Notification", "Notifications are disabled. Skipping notification.");
            return;
        }

        // Extract message data
        String title = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getTitle() : "Default Title";
        String body = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "Default Body";

        // Show notification
        showNotification(title, body);
    }

    /**
     * Show a notification with the provided title and message.
     * @param title
     * @param message
     */
    private void showNotification(String title, String message) {
        String channelId = "default_channel";
        String channelName = "Default Channel";

        // Create Notification Channel (for Android 8.0+)
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an Intent to open MainActivity on notification click
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification_icon) // Replace with your app's icon
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Show the notification
        notificationManager.notify(0, builder.build());
        Log.d("Notification", "showNotification: ");
    }
}