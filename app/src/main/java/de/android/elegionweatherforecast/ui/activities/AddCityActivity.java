package de.android.elegionweatherforecast.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import de.android.elegionweatherforecast.R;

public class AddCityActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText editText;
    private TextInputLayout textInputLayout;
    private Button btnOk;

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
        finish();
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
