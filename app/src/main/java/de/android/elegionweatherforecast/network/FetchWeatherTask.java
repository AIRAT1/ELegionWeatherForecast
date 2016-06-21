package de.android.elegionweatherforecast.network;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.android.elegionweatherforecast.BuildConfig;

public class FetchWeatherTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    @Override
    protected Void doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection= null;
        BufferedReader reader = null;
        String forecastJsonString = null;

        String format = "json";
        String units = "metric";
        String lang = "ru";

        try {
            final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/group?";
            final String QUERY_PARAM = "id";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String LANG_PARAM = "lang";
            final String APP_ID_PARAM = "APPID";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNITS_PARAM, units)
                    .appendQueryParameter(LANG_PARAM, lang)
                    .appendQueryParameter(APP_ID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                    .build();

//            example query to find city id
//            http://api.openweathermap.org/data/2.5/weather?q=Berlin&APPID=a67b395621fb7f28f3896da2171e6a40

            URL url = new URL(builtUri.toString());
            Log.d(LOG_TAG, "Built URI " + builtUri.toString());

            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            forecastJsonString = buffer.toString();
            Log.d(LOG_TAG, forecastJsonString);

            splitJsonString(forecastJsonString);

        }catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
            return null;
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    private void splitJsonString(String forecastJsonString) {
        String[] strings = forecastJsonString.split("coord");
        for (int i = 1; i < strings.length; i++) {
            Log.d(LOG_TAG, strings[i]);
        }
    }
}