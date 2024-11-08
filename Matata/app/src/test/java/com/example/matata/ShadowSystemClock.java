package com.example.matata;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import android.os.SystemClock;

@Implements(SystemClock.class)
public class ShadowSystemClock {
    private static long currentTimeMillis = 0;

    @Implementation
    public static long uptimeMillis() {
        return currentTimeMillis;
    }

    public static void setUptimeMillis(long millis) {
        currentTimeMillis = millis;
    }
}

