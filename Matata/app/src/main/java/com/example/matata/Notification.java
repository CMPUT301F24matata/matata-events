package com.example.matata;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.matata.EventDetailActivity;

import java.util.List;

public class Notification {

    private static final String CHANNEL_ID = "waitlist_notification_channel";
    private ActivityResultLauncher<String> notificationPermissionLauncher;

    public static void initNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Waitlist Notifications";
            String description = "Notifications for entrants selected from the waitlist";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
        }
    }

    public void sendWaitlistNotification(Context context, String title, String message, String entrantId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("entrantId", entrantId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            return;
        }
        notificationManager.notify(entrantId.hashCode(), builder.build());
    }

    public void sendNotificationsToWaitlist(Context context, List<String> waitlistIds, String title, String message) {
        for (String entrantId : waitlistIds) {
            sendWaitlistNotification(context, title, message, entrantId);
        }
    }

    public void setNotificationPermissionLauncher(ActivityResultLauncher<String> launcher) {
        this.notificationPermissionLauncher = launcher;
    }
}
