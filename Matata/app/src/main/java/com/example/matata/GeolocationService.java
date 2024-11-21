package com.example.matata;

import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * The `GeolocationService` class represents a background service for handling geolocation-related tasks.
 * This service currently provides a basic implementation and can be extended to include functionality
 * such as tracking user location, monitoring geofences, or performing background location updates.
 */
public class GeolocationService extends android.app.Service{
    /**
     * Called when a client binds to the service.
     * This implementation currently does not support binding, so it returns `null`.
     *
     * @param intent The `Intent` used to bind to the service.
     * @return `null` as binding is not supported in this implementation.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
