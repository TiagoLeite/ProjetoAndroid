package br.usp.trabalhoandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;


public class RegisterFragment extends Fragment
{
    EditText etBirthDateDay, etBirthDateMonth, etBirthDateYear, etName, etEmail;
    TextView t;
    RadioButton rbSexM, rbSexF;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        etName = (EditText)view.findViewById(R.id.etName);
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etBirthDateDay = (EditText) view.findViewById(R.id.etBirthDateDay);
        etBirthDateMonth = (EditText) view.findViewById(R.id.etBirthDateMonth);
        etBirthDateYear = (EditText) view.findViewById(R.id.etBirthDateYear);
        t = (TextView) view.findViewById(R.id.titler);
        rbSexF = (RadioButton) view.findViewById(R.id.rbSexF);
        rbSexM = (RadioButton) view.findViewById(R.id.rbSexM);

        setupViews();

        return view;
    }

    private void setupViews()
    {
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
