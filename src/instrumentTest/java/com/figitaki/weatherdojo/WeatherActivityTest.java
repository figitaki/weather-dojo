package com.figitaki.weatherdojo;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.figitaki.weatherdojo.WeatherActivityTest \
 * com.figitaki.weatherdojo.tests/android.test.InstrumentationTestRunner
 */
public class WeatherActivityTest extends ActivityInstrumentationTestCase2<WeatherActivity> {

    public WeatherActivityTest() {
        super("com.figitaki.weatherdojo", WeatherActivity.class);
    }

}
