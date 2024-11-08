/**
 * ShadowSystemClock class provides a shadow implementation of Android's SystemClock for use in testing environments.
 * This class allows manual control over the system uptime to simulate various time-based scenarios.
 *
 * Purpose:
 * - Mock the system uptime in unit tests using Robolectric to control and verify time-dependent logic.
 *
 * Outstanding Issues:
 * - Ensure compatibility with other time-based methods in SystemClock if additional mocking is required.
 */

package com.example.matata;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import android.os.SystemClock;

/**
 * Shadow implementation of the SystemClock class.
 * Used to override system time in tests by providing a mock uptime value.
 */
@Implements(SystemClock.class)
public class ShadowSystemClock {

    /**
     * Stores the current mock time in milliseconds, representing the system uptime.
     */
    private static long currentTimeMillis = 0;

    /**
     * Provides the current mock system uptime in milliseconds.
     *
     * @return the current mock uptime in milliseconds as set by setUptimeMillis.
     */
    @Implementation
    public static long uptimeMillis() {
        return currentTimeMillis;
    }

    /**
     * Sets the mock uptime in milliseconds.
     * This allows control over the return value of uptimeMillis to simulate passage of time.
     *
     * @param millis the mock uptime in milliseconds to be set.
     */
    public static void setUptimeMillis(long millis) {
        currentTimeMillis = millis;
    }
}
