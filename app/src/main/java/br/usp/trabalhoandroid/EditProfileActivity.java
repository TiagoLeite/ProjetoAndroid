package br.usp.trabalhoandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;

/**
 * Created by Victor on 04/12/2017.
 */

public class EditProfileActivity extends AppCompatActivity {

    EditText etName, etBirthDateDay, etBirthDateMonth, etBirthDateYear, etEmail, etPassword, etConfirmPassword;
    RadioButton rbSexM, rbSexF;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName = (EditText) findViewById(R.id.etName);
        etBirthDateDay = (EditText) findViewById(R.id.etBirthDateDay);
        etBirthDateMonth = (EditText) findViewById(R.id.etBirthDateMonth);
        etBirthDateYear = (EditText) findViewById(R.id.etBirthDateYear);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPasswordRegister);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        rbSexF = (RadioButton) findViewById(R.id.rbSexF);
        rbSexM = (RadioButton) findViewById(R.id.rbSexM);

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

    }
}
