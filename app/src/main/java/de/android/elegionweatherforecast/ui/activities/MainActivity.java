package de.android.elegionweatherforecast.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import de.android.elegionweatherforecast.R;
import de.android.elegionweatherforecast.network.FetchWeatherTask;

public class MainActivity extends AppCompatActivity {
    public static ArrayAdapter<String> sForecastAdapter;
    public static String[] sValues;
    private ListView mListView;
    public static ArrayList<Integer> cityesId;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityesId = new ArrayList<>();
        initCityArray();


        sForecastAdapter = new ArrayAdapter<>(
                this,
                R.layout.list_item_forecast,
                R.id.list_item_forecast_text_view,
                new ArrayList<String>());
        mListView = (ListView)findViewById(R.id.list_view_forecast);
        mListView.setAdapter(sForecastAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("LOG", "onClick " + cityesId.get(position));
                String forecast;
                if (sValues.length > 0) {
                    forecast = sValues[position];
                }else {
                    forecast = sForecastAdapter.getItem(position);
                }
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, forecast);
                Log.d("LOG", "forecast = " + forecast);
                startActivity(intent);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Long click", Toast.LENGTH_SHORT).show();
                cityesId.remove(position);
                savePreferences();
                updateWeather();

                return true;
            }
        });
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
                                startActivity(AddCityActivity.newIntent(MainActivity.this));
                            }
                        }).show();
            }
        });
    }

    private void savePreferences() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        int arraysSize = MainActivity.cityesId.size();
        editor.putInt("arrays_size", arraysSize);
        for (int i = 0; i < arraysSize; i++) {
            editor.putInt("city " + i, MainActivity.cityesId.get(i));
        }
        editor.apply();
    }

    private void initCityArray() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        int arraysSize = sp.getInt("arrays_size", 0);
        if (arraysSize != 0) {
            for (int i = 0; i < arraysSize; i++) {
                cityesId.add(sp.getInt("city " + i, 0));
            }
        }else {
//            cityesId.add(2172797); Cair
//            cityesId.add(323777); Antalya
//            cityesId.add(524901); Moscow
            cityesId.add(2950159);
            cityesId.add(2867714);
            cityesId.add(2911298);
            cityesId.add(2886242);
            cityesId.add(2945024);
        }
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
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {
        Context context = getApplicationContext();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (isNetWorkAvailable(context)) {
            Toast.makeText(MainActivity.this, "Internet connection available", Toast.LENGTH_SHORT).show();
            FetchWeatherTask weatherTask = new FetchWeatherTask(MainActivity.this);
//        weatherTask.execute("2950159,2867714,2911298,2886242,2945024");
            Log.d("LOG",cityesIdToString());
            weatherTask.execute(cityesIdToString());
        }else {
            Toast.makeText(MainActivity.this, "Internet connection failed, load data from history", Toast.LENGTH_SHORT).show();
             // todo load values from memory
            int resultLength = sp.getInt("result_length", 0);
            Log.d("LOG", "MainActivity resultLength " + String.valueOf(resultLength));
            if (resultLength != 0) {
                sForecastAdapter.clear();
                sValues = new String[resultLength];
                for (int i = 0; i < resultLength; i++) {
                    sForecastAdapter.add(sp.getString("shortForecast " + i, null));
                    sValues[i] = sp.getString("longForecast " + i, null);
                }
            }else {
                Toast.makeText(MainActivity.this, "You need internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean isNetWorkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }else {
            return false;
        }
    }

    private String cityesIdToString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cityesId.size() - 1; i++) {
            builder.append(cityesId.get(i)).append(",");
        }
        builder.append(cityesId.get(cityesId.size() - 1));
        return builder.toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateWeather();
    }
}