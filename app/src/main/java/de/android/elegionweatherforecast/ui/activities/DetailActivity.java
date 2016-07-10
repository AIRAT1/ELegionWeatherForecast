package de.android.elegionweatherforecast.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import de.android.elegionweatherforecast.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String forecstString = intent.getStringExtra(Intent.EXTRA_TEXT);

            String[] forecasts = forecstString.split(" ");

            if (forecasts.length == 11) {
                ((TextView)findViewById(R.id.detail_city_name)).setText("City: " + forecasts[0]);
                ((TextView)findViewById(R.id.detail_temperature)).setText("Current temperature: " + forecasts[1]);
                ((TextView)findViewById(R.id.detail_date)).setText("Current date: " + forecasts[2] +
                        " " + forecasts[3] + " " + forecasts[4]);
                ((TextView)findViewById(R.id.detail_weather)).setText("Current weather: " +
                        forecasts[5] + " " + forecasts[6]);
                ((TextView)findViewById(R.id.detail_wind_speed)).setText("Wind speed: " + forecasts[7]);
                ((TextView)findViewById(R.id.detail_wind_degree)).setText("Wind direction: " + getWindDegreeDescription(forecasts[8]));
                ((TextView)findViewById(R.id.detail_pressure)).setText("Pressure: " + forecasts[9]);
                ((TextView)findViewById(R.id.detail_max_min_temperature)).setText("max/min day temperature: " + forecasts[10]);
            }
        }
    }

    private String getWindDegreeDescription(String forecast) {
        double degree = Double.parseDouble(forecast);
        if (degree >= 339 || degree < 23) return "North";
        if (degree >= 23 || degree < 68) return "North - East";
        if (degree >= 68 || degree < 113) return "East";
        if (degree >= 113 || degree < 158) return "South - East";
        if (degree >= 158 || degree < 203) return "South";
        if (degree >= 203 || degree < 248) return "South - West";
        if (degree >= 248 || degree < 293) return "West";
        if (degree >= 293 || degree < 339) return "North - West";
        return null;
    }
}
