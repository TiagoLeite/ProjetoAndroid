package br.usp.trabalhoandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by Victor on 28/11/2017.
 */

public class RegisterActivity extends AppCompatActivity {

    EditText etBirthDateDay, etBirthDateMonth, etBirthDateYear, etName, etEmail;
    TextView t;
    RadioButton rbSexM, rbSexF;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etBirthDateDay = (EditText) findViewById(R.id.etBirthDateDay);
        etBirthDateMonth = (EditText) findViewById(R.id.etBirthDateMonth);
        etBirthDateYear = (EditText) findViewById(R.id.etBirthDateYear);
        t = (TextView) findViewById(R.id.titler);
        rbSexF = (RadioButton) findViewById(R.id.rbSexF);
        rbSexM = (RadioButton) findViewById(R.id.rbSexM);


        //TextListeners change the focus of the Date of Birth EditTexts as the user types the day, month and year
        etBirthDateDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etBirthDateDay.getText().toString().length() == 2) {
                    etBirthDateMonth.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etBirthDateMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etBirthDateMonth.getText().toString().length() == 2) {
                    etBirthDateYear.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etBirthDateYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etBirthDateYear.getText().toString().length() == 4) {
                    etEmail.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });





    }
}
