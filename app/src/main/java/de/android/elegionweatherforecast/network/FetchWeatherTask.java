package de.android.elegionweatherforecast.network;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

import de.android.elegionweatherforecast.BuildConfig;
import de.android.elegionweatherforecast.ui.activities.MainActivity;

public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    private String getReadableDateString(long time) {
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    private String formatHighLows(double high, double low) {
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);
        String highLowString = roundedHigh + "/" + roundedLow;
        return highLowString;
    }

    private String[] getWeatherDataFromJson(String forecastJsonString) throws JSONException {
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_MAX = "temp_max";
        final String OWM_MIN = "temp_min";
        final String OWM_TEMP = "temp";
        final String OWM_MAIN = "main";
        final String OWM_DESCRIPTION = "description";
        final String OWM_WIND = "wind";
        final String OWM_SPEED = "speed";
        final String OWM_DEG = "deg";
        final String OWM_NAME = "name";

        JSONObject forecastJson = new JSONObject(forecastJsonString);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
//        Log.d(LOG_TAG, "weatherArrayLength " + weatherArray.length());

        Time dayTime = new Time();
        dayTime.setToNow();
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        String[] resultString = new String[weatherArray.length()];
        for (int i = 0; i < weatherArray.length(); i++) {
            String day;
            String description;
            String highAndLow;
            String temp;
            double speed;
            double deg;
            String name;

            JSONObject cityForecast = weatherArray.getJSONObject(i);

            day = getReadableDateString(dayTime.setJulianDay(julianStartDay));

            JSONObject weatherObject = cityForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            JSONObject temperatureObject = cityForecast.getJSONObject(OWM_MAIN);
            temp = String.valueOf(Math.round(temperatureObject.getDouble(OWM_TEMP)));
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);
            highAndLow = formatHighLows(high, low);

            JSONObject windObject = cityForecast.getJSONObject(OWM_WIND);
            speed = windObject.getDouble(OWM_SPEED);
            deg = windObject.getDouble(OWM_DEG);

            name = cityForecast.getString(OWM_NAME);

            resultString[i] = name + " " +
                    day + " - " + description + " - " + temp + " " + speed + " " + deg;
        }
//        for (String s : resultString) {
//            Log.d(LOG_TAG, "Forecast entry " + s);
//        }
        return resultString;
    }

    @Override
    protected String[] doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection= null;
        BufferedReader reader = null;
        String forecastJsonString = null;

        String format = "json";
        String units = "metric";
        String lang = "de";

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

            // work URL
            URL url = new URL(builtUri.toString());

            // test URL
//            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&appid=a67b395621fb7f28f3896da2171e6a40");

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
//            Log.d(LOG_TAG, "Forecast string: " + forecastJsonString);

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

        try {
            return getWeatherDataFromJson(forecastJsonString);
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] result) {
        if (result != null) {
            MainActivity.sForecastAdapter.clear();
            for (String dayForecastString : result) {
                MainActivity.sForecastAdapter.add(dayForecastString);
            }
        }
    }
}