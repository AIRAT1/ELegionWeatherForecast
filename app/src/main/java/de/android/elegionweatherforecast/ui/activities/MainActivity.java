package de.android.elegionweatherforecast.ui.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.android.elegionweatherforecast.R;
import de.android.elegionweatherforecast.network.FetchWeatherTask;

public class MainActivity extends AppCompatActivity {
    private ArrayAdapter<String> mForecastAdapter;
    private ListView mListView;
    public static String[] sData = {
            "Berlin", //2950159
            "München", //2867714
            "Hamburg", //2911298
            "Köln", //2886242
            "Braunschweig" //2945024
    };
    List<String> mForecast = new ArrayList<>(Arrays.asList(sData));


    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mForecastAdapter = new ArrayAdapter<>(
                this,
                R.layout.list_item_forecast,
                R.id.list_item_forecast_text_view,
                mForecast);
        mListView = (ListView)findViewById(R.id.list_view_forecast);
        mListView.setAdapter(mForecastAdapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.add_a_new_city, Snackbar.LENGTH_LONG)
                        .setAction(R.string.yes, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, "Add new city here", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_refresh) {
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("2950159,2867714,2911298,2886242,2945024");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}