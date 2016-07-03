package de.android.elegionweatherforecast.data;

import android.text.format.Time;

public class WeatherContract {
    public static long normalizeDate(long startDate) {
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }
}
