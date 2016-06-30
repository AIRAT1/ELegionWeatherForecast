package de.android.elegionweatherforecast.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashSet;
import java.util.Set;

import de.android.elegionweatherforecast.R;

public class AddCityActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText editText;
    private TextInputLayout textInputLayout;
    private Button btnOk;
    private Set<String> set;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, AddCityActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);
        init();
    }

    private void init() {
        textInputLayout = (TextInputLayout)findViewById(R.id.input_layout);
        editText = (EditText)findViewById(R.id.new_city);
        editText.addTextChangedListener(new MyTextWatcher(editText));
        btnOk = (Button)findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        submitForm();
    }

    private void submitForm() {
        if (!validateValue()) {
            return;
        }
        MainActivity.cityesId.add(Integer.valueOf(editText.getText().toString()));
        saveSet();
//        MainActivity.sForecastAdapter.notifyDataSetChanged();
//        for (int i = 0; i < MainActivity.cityesId.size(); i++) {
//            Log.d("LOG", String.valueOf(MainActivity.cityesId.get(i)));
//        }
        finish();
    }

    private Set convertArrayToSet() {
        set = new HashSet<>();
        for (int i = 0; i < MainActivity.cityesId.size(); i++) {
            set.add(String.valueOf(MainActivity.cityesId.get(i)));
            Log.d("LOG", "set " + String.valueOf(MainActivity.cityesId.get(i)));
        }
        return set;
    }

    private void saveSet() {
        sp = getPreferences(MODE_PRIVATE);
        editor = sp.edit();
        editor.putStringSet("key", convertArrayToSet());
        editor.commit();
    }

    private boolean validateValue() {
        if (editText.getText().toString().trim().isEmpty()) {
            textInputLayout.setError(getString(R.string.enter_values));
            requestFocus(editText);
            return false;
        }else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private class MyTextWatcher implements TextWatcher {
        private View view;

        public MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()) {
                case R.id.new_city:
                    validateValue();
                    break;
                default:
                    break;
            }
        }
    }
}
