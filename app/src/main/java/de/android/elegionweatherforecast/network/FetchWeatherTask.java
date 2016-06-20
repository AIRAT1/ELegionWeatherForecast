package de.android.elegionweatherforecast.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.android.elegionweatherforecast.BuildConfig;

public class FetchWeatherTask extends AsyncTask<Void, Void, Void> {
    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection urlConnection= null;
        BufferedReader reader = null;
        String forecastJsonString = null;

        try {
            String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=1";
            String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
            URL url = new URL(baseUrl.concat(apiKey));

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
}