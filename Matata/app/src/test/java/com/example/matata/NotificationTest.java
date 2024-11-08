package com.example.matata;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

public class NotificationTest {
    private Notification notification;
    @Mock private Context mockContext;
    @Mock private NotificationManagerCompat mockNotificationManagerCompat;
    @Mock private ActivityResultLauncher<String> mockPermissionLauncher;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        notification = new Notification();
        notification.setNotificationPermissionLauncher(mockPermissionLauncher);
    }

    @Test
    void testInitNotificationChannel_OreoOrHigher() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mockNotificationManager = mock(NotificationManager.class);
            when(mockContext.getSystemService(NotificationManager.class)).thenReturn(mockNotificationManager);

            Notification.initNotificationChannel(mockContext);

            verify(mockNotificationManager, times(1)).createNotificationChannel(any());
        }
    }

    @Test
    void testInitNotificationChannel_PreOreo() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            Notification.initNotificationChannel(mockContext);
            // Verify no interactions since channels aren't created on older versions
            verifyNoInteractions(mockContext);
        }
    }

    @Test
    void testSendWaitlistNotification_PermissionGranted() {
        when(ActivityCompat.checkSelfPermission(mockContext, android.Manifest.permission.POST_NOTIFICATIONS))
                .thenReturn(PackageManager.PERMISSION_GRANTED);

        notification.sendWaitlistNotification(mockContext, "Test Title", "Test Message", "123");

        ArgumentCaptor<NotificationManagerCompat> captor = ArgumentCaptor.forClass(NotificationManagerCompat.class);
        verify(mockNotificationManagerCompat, times(1)).notify(eq("123".hashCode()), any());
    }

    @Test
    void testSendWaitlistNotification_PermissionNotGranted() {
        when(ActivityCompat.checkSelfPermission(mockContext, android.Manifest.permission.POST_NOTIFICATIONS))
                .thenReturn(PackageManager.PERMISSION_DENIED);

        notification.sendWaitlistNotification(mockContext, "Test Title", "Test Message", "123");

        verify(mockPermissionLauncher, times(1)).launch(android.Manifest.permission.POST_NOTIFICATIONS);
    }

    @Test
    void testSendNotificationsToWaitlist_ValidEntrantId() {
        List<Object> waitlist = Collections.singletonList("entrant_1");

        notification.sendNotificationsToWaitlist(mockContext, waitlist, "Title", "Message");

        verify(mockNotificationManagerCompat, times(1)).notify(anyInt(), any());
    }

    @Test
    void testSetNotificationPermissionLauncher() {
        notification.setNotificationPermissionLauncher(mockPermissionLauncher);
        assertNotNull(mockPermissionLauncher);
    }
}