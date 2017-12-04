package br.usp.trabalhoandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etBirthDateDay, etBirthDateMonth, etBirthDateYear, etEmail, etUsername, etPassword, etConfirmPassword;
    TextView t;
    RadioButton rbSexM, rbSexF;
    Button btnRegister;
    Context context;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final SharedPreferences.Editor editor = getSharedPreferences(Constants.LOGIN_PREFS, MODE_PRIVATE).edit();
        context = this;
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

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        //if(networkInfo != null )

        //TextListeners change the focus of the Date of Birth EditTexts as the user types the day, month and year
        etBirthDateDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etBirthDateDay.getText().toString().length() == 2) {
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
                if (etBirthDateMonth.getText().toString().length() == 2) {
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
                if (etBirthDateYear.getText().toString().length() == 4) {
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

                if (checkInput()) {
                    String name = etName.getText().toString();
                    final String birthDate = etBirthDateDay.getText().toString() + "/" + etBirthDateMonth.getText().toString() + "/" + etBirthDateYear.getText().toString();
                    final String email = etEmail.getText().toString();
                    final String username = etUsername.getText().toString();
                    final String password = etPassword.getText().toString();
                    final String confirmPassword = etConfirmPassword.getText().toString();
                    final String gender;
                    if (rbSexM.isChecked())
                        gender = "male";
                    else
                        gender = "female";
                    Constants.NAME = name;
                    Constants.EMAIL = email;
                    Constants.PASSWORD = password;
                    Constants.BIRTH = birthDate;
                    Constants.GENDER = gender;
                    Constants.USERNAME = username;

                    //name, email, gender, birth, password

                    // Register Request
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.REGISTER_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if(response.contains("Registration successfully!")){
                                        Toast.makeText(context, "Registration successfully. Welcome!", Toast.LENGTH_LONG).show();
                                        editor.putString("username", Constants.USERNAME);
                                        editor.putString("email", Constants.EMAIL);
                                        editor.putString("name", Constants.NAME);
                                        editor.putString("password", Constants.PASSWORD);
                                        editor.putString("birth", Constants.BIRTH);
                                        editor.putString("gender", Constants.GENDER);
                                        editor.apply();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);

                                        startActivity(intent);
                                    }
                                    else
                                        Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "There was a connection error. Please, try again.", Toast.LENGTH_LONG).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams(){
                            Map<String, String> params = new HashMap<String, String>();
                            params.put(Constants.KEY_NAME, Constants.NAME);
                            params.put(Constants.KEY_EMAIL, Constants.EMAIL);
                            params.put(Constants.KEY_BIRTH, Constants.BIRTH);
                            params.put(Constants.KEY_GENDER, Constants.GENDER);
                            params.put(Constants.KEY_PASSWORD, Constants.PASSWORD);
                            params.put(Constants.KEY_USERNAME, Constants.USERNAME);
                            return params;
                        }

                    };
                    MySingleton.getInstance(context).addToRequestQueue(stringRequest);

                } else {
                    Toast.makeText(context, "Please, check the fields.", Toast.LENGTH_LONG).show();
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
