package br.usp.trabalhoandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class LoginActivity extends AppCompatActivity{

    EditText etUsername, etPassword;
    Button btnRegister, btnLogin;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final SharedPreferences prefs = getApplicationContext().getSharedPreferences(Constants.LOGIN_PREFS, MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        context = this;

        if(prefs.getString("username", null) != null){
            Log.d("username", prefs.getString("username", null));
            Constants.USERNAME = prefs.getString("username", null);
            Constants.PASSWORD = prefs.getString("password", null);
            Constants.NAME = prefs.getString("name", null);
            Constants.EMAIL = prefs.getString("email", null);
            Constants.BIRTH = prefs.getString("birth", null);
            Constants.GENDER = prefs.getString("gender", null);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(intent);
        }

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnRegister.setPaintFlags(btnRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                Constants.USERNAME = username;
                Constants.PASSWORD = password;
                if(username.length() == 0)
                    Toast.makeText(context, getResources().getString(R.string.valid_user), Toast.LENGTH_LONG).show();
                else{
                    if(password.length() == 0)
                        Toast.makeText(context, getResources().getString(R.string.valid_pass), Toast.LENGTH_LONG).show();
                    else{
                        // Login Request
                        // RequestQueue queue = Volley.newRequestQueue(context);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_URL,
                                new Response.Listener<String>()
                                {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonResponse = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                                            boolean success = jsonResponse.getBoolean("success");
                                            Log.d("onResponse", "Entrou");
                                            if (success) {
                                                Constants.NAME = jsonResponse.getString("name");
                                                Constants.EMAIL = jsonResponse.getString("email");
                                                Constants.GENDER = jsonResponse.getString("gender");
                                                Constants.BIRTH = jsonResponse.getString("birth");
                                                Constants.USERNAME = jsonResponse.getString("username");
                                                Constants.PASSWORD = jsonResponse.getString("password");

                                                editor.putString("username", Constants.USERNAME);
                                                editor.putString("email", Constants.EMAIL);
                                                editor.putString("name", Constants.NAME);
                                                editor.putString("password", Constants.PASSWORD);
                                                editor.putString("birth", Constants.BIRTH);
                                                editor.putString("gender", Constants.GENDER);
                                                editor.apply();

                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                LoginActivity.this.startActivity(intent);

                                            } else {
                                                if(!jsonResponse.getBoolean("usernameExists"))
                                                    Toast.makeText(context, getResources().getString(R.string.wrong_pass), Toast.LENGTH_LONG).show();
                                                else
                                                    Toast.makeText(context, getResources().getString(R.string.wrong) +
                                                            getResources().getString(R.string.try_again), Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                            builder.setMessage(e.getMessage())
                                                    .setNegativeButton("Ok", null)
                                                    .create()
                                                    .show();
                                        }

                                    }
                                },
                                new Response.ErrorListener()
                                {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                                        Log.d("Error", error.toString());
                                    }
                                }
                        ) {
                            @Override
                            protected Map<String, String> getParams()
                            {
                                Map<String, String>  params = new HashMap<String, String>();
                                params.put(Constants.KEY_PASSWORD, Constants.PASSWORD);
                                params.put(Constants.KEY_USERNAME, Constants.USERNAME);

                                return params;
                            }
                        };
                        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                        queue.add(stringRequest);
                    }
                }

            }
        });

        etUsername.setText(getIntent().getStringExtra("username"));
        etPassword.setText(getIntent().getStringExtra("password"));
    }

    @Override
    public void onBackPressed(){

    }
}
