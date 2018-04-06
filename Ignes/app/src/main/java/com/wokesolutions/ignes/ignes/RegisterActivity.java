package com.wokesolutions.ignes.ignes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;

public class RegisterActivity extends AppCompatActivity {

    private View mRegister_form;
    private View mRegister_username_form;
    private View mRegister_email_form;
    private View mRegister_password_form;

    private Button mUsername_button;
    private Button mEmail_button;
    private Button mPassword_button;
    private Button mSignUp_button;

    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegister_form = (View) findViewById(R.id.register_form);
        mRegister_username_form = (View) findViewById(R.id.register_username_form);
        mRegister_email_form = (View) findViewById(R.id.register_email_form);
        mRegister_password_form = (View) findViewById(R.id.register_password_form);

        mUsername_button = (Button) findViewById(R.id.register_username_button);
        mEmail_button = (Button) findViewById(R.id.register_email_button);
        mPassword_button = (Button) findViewById(R.id.register_password_button);
        mSignUp_button = (Button) findViewById(R.id.register_signup_button);

        mUsername = (EditText) findViewById(R.id.register_username);
        mEmail = (EditText) findViewById(R.id.register_email);
        mPassword = (EditText) findViewById(R.id.register_password);
        mPasswordConfirm = (EditText) findViewById(R.id.register_confirmation);

        mUsername_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibility(true, "Username");
            }
        });
        mEmail_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibility(true, "Email");
            }
        });
        mPassword_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibility(true, "Password");
            }
        });

    }

    private void changeVisibility(final boolean show, String option) {
        switch (option) {

            case "Username": {
                mRegister_username_form.setVisibility(View.VISIBLE );

                mRegister_email_form.setVisibility(View.GONE);
                mRegister_password_form.setVisibility(View.GONE);
            }
            break;

            case "Email": {
                mRegister_email_form.setVisibility(View.VISIBLE);

                mRegister_username_form.setVisibility(View.GONE);
                mRegister_password_form.setVisibility(View.GONE);
            }
            break;

            case "Password": {
                mRegister_password_form.setVisibility(View.VISIBLE);

                mRegister_email_form.setVisibility(View.GONE);
                mRegister_username_form.setVisibility(View.GONE);
            }
            break;
        }

    }


}
