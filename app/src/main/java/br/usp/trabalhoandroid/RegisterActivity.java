package br.usp.trabalhoandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Victor on 28/11/2017.
 */

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etBirthDateDay, etBirthDateMonth, etBirthDateYear, etEmail, etUsername, etPassword, etConfirmPassword;
    TextView t;
    RadioButton rbSexM, rbSexF;
    Button btnRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = (EditText) findViewById(R.id.etName);
        etBirthDateDay = (EditText) findViewById(R.id.etBirthDateDay);
        etBirthDateMonth = (EditText) findViewById(R.id.etBirthDateMonth);
        etBirthDateYear = (EditText) findViewById(R.id.etBirthDateYear);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etUsername = (EditText) findViewById(R.id.etUsernameRegister);
        etPassword = (EditText) findViewById(R.id.etPasswordRegister);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        rbSexF = (RadioButton) findViewById(R.id.rbSexF);
        rbSexM = (RadioButton) findViewById(R.id.rbSexM);
        btnRegister = (Button) findViewById(R.id.btnOKRegister);


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


        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(checkInput()) {
                    String name = etName.getText().toString();
                    String birthDate = etBirthDateDay.getText().toString() + "/" + etBirthDateMonth.getText().toString() + "/" + etBirthDateYear.getText().toString();
                    String email = etEmail.getText().toString();
                    String username = etUsername.getText().toString();
                    String password = etPassword.getText().toString();
                    String confirmPassword = etConfirmPassword.getText().toString();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);

                    startActivity(intent);
                }else{

                }
            }
        });


    }

    private boolean checkInput(){
        if(!checkField(etName)) return false;
        if(!checkField(etBirthDateDay)) return false;
        if(!checkField(etBirthDateMonth)) return false;
        if(!checkField(etBirthDateYear)) return false;
        if(!checkField(etEmail)) return false;
        if(!checkField(etUsername)) return false;
        if(!checkField(etPassword)) return false;
        if(!checkField(etConfirmPassword)) return false;

        if(!checkPassword()) {
            etConfirmPassword.setError(getString(R.string.wrongPasswords));
            return false;
        }

        return true;
    }

    private boolean checkField(EditText et){
        String s = et.getText().toString();

        if(TextUtils.isEmpty(s)){
            et.setError(getString(R.string.emptyField));
            return false;
        }

        return true;
    }

    private boolean checkPassword(){
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        boolean check = password.equals(confirmPassword);
        return check;
    }
}
