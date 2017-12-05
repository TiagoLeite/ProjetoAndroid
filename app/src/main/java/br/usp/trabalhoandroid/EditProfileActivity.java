package br.usp.trabalhoandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Victor on 04/12/2017.
 */

public class EditProfileActivity extends AppCompatActivity {

    EditText etName, etBirthDateDay, etBirthDateMonth, etBirthDateYear, etEmail, etPassword, etConfirmPassword;
    RadioButton rbSexM, rbSexF;
    Button btSave;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        final SharedPreferences.Editor editor = getSharedPreferences(Constants.LOGIN_PREFS, MODE_PRIVATE).edit();
        context = this;
        etName = (EditText) findViewById(R.id.etName);
        etBirthDateDay = (EditText) findViewById(R.id.etBirthDateDay);
        etBirthDateMonth = (EditText) findViewById(R.id.etBirthDateMonth);
        etBirthDateYear = (EditText) findViewById(R.id.etBirthDateYear);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPasswordRegister);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        rbSexF = (RadioButton) findViewById(R.id.rbSexF);
        rbSexM = (RadioButton) findViewById(R.id.rbSexM);
        btSave = (Button) findViewById(R.id.btnOKRegister);

        String birthDate = Constants.BIRTH;
        etName.setText(Constants.NAME);
        etBirthDateDay.setText(birthDate.substring(0, 2));
        etBirthDateMonth.setText(birthDate.substring(3, 5));
        etBirthDateYear.setText(birthDate.substring(6, 10));
        etEmail.setText(Constants.EMAIL);
        etPassword.setText(Constants.PASSWORD);
        etConfirmPassword.setText(Constants.PASSWORD);

        if(Constants.GENDER != null) {
            if (Constants.GENDER.equals("female")) {
                rbSexF.toggle();
            } else rbSexM.toggle();
        }

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInput()) {
                    final String name = etName.getText().toString();
                    final String birthDate = etBirthDateDay.getText().toString() + "/" + etBirthDateMonth.getText().toString() + "/" + etBirthDateYear.getText().toString();
                    final String email = etEmail.getText().toString();
                    final String password = etPassword.getText().toString();
                    final String confirmPassword = etConfirmPassword.getText().toString();
                    final String gender;
                    if (rbSexM.isChecked())
                        gender = "M";
                    else
                        gender = "F";
                    Constants.NAME = name;
                    Constants.EMAIL = email;
                    Constants.PASSWORD = password;
                    Constants.BIRTH = birthDate;
                    Constants.GENDER = gender;

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPDATE_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("onResponse", "Entrou");
                                    if (response.contains("Update successfully!")) {

                                        editor.putString("email", Constants.EMAIL);
                                        editor.putString("name", Constants.NAME);
                                        editor.putString("password", Constants.PASSWORD);
                                        editor.putString("birth", Constants.BIRTH);
                                        editor.putString("gender", Constants.GENDER);
                                        editor.apply();

                                        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                                        EditProfileActivity.this.startActivity(intent);
                                        Toast.makeText(context, "Update successfully!",
                                                Toast.LENGTH_LONG).show();

                                    } else if (response.contains("There was a error during update. Please, try again.")) {
                                        Toast.makeText(context, "Error during update. Please, try again later.",
                                                Toast.LENGTH_LONG).show();
                                        Log.d("Error", response);
                                        if (response.contains("5"))
                                            Toast.makeText(context, "555", Toast.LENGTH_LONG).show();
                                    } else if (response.contains("ID saved")) {
                                        Toast.makeText(context, "ID SAVDE", Toast.LENGTH_LONG).show();

                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                                    Log.d("Error", error.toString());
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
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
                    RequestQueue queue = Volley.newRequestQueue(EditProfileActivity.this);
                    queue.add(stringRequest);
                }else Toast.makeText(context, getResources().getString(R.string.check), Toast.LENGTH_LONG).show();
            }
        });

    }

    private boolean checkInput(){

        if(!checkField(etName)) return false;
        if(!checkField(etBirthDateDay)) return false;
        if(!checkField(etBirthDateMonth)) return false;
        if(!checkField(etBirthDateYear)) return false;
        if(!checkField(etEmail)) return false;
        if(!checkField(etPassword)) return false;
        if(!checkField(etConfirmPassword)) return false;
        if(!checkRadioButtons()) return false;

        if(!checkPassword()) {
            etConfirmPassword.setError(getString(R.string.wrongPasswords));
            return false;
        }

        return true;
    }

    private boolean checkRadioButtons(){
        if(!rbSexF.isChecked() && !rbSexM.isChecked()) return false;

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
