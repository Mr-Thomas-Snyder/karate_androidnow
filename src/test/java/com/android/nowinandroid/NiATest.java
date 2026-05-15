package com.android.nowinandroid;

import com.intuit.karate.junit5.Karate;

/**
 * NiATest - Main Test Runner for Now in Android (Karate Pure ADB Edition)
 * 
 * This runner executes all .feature files found in the classpath.
 * It is configured to run tests sequentially to prevent ADB command collisions
 * and ensure clean state isolation via AdbDriver.resetApp().
 */
class NiATest {
    
    @Karate.Test
    Karate testAll() {
        return Karate.run().relativeTo(getClass()).outputHtmlReport(true).parallel(1);
    }
}
