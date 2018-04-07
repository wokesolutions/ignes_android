package com.wokesolutions.ignes.ignes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;

public class RegisterActivity extends AppCompatActivity {

    private View mRegister_form;
    private View mSelectUser_form;
    private View mRegister_code_form;
    private View mRegister_username_form;
    private View mRegister_email_form;
    private View mRegister_password_form;

    private Button mUsername_button;
    private Button mEmail_button;
    private Button mPassword_button;
    private Button mSignUp_button;
    private Button mCode_button;

    private Button mCitizen_button;
    private Button mWorker_button;

    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mPasswordConfirm;
    private EditText mCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mSelectUser_form = (View) findViewById(R.id.register_select_user_form);
        mRegister_form = (View) findViewById(R.id.register_form);
        mRegister_username_form = (View) findViewById(R.id.register_username_form);
        mRegister_email_form = (View) findViewById(R.id.register_email_form);
        mRegister_password_form = (View) findViewById(R.id.register_password_form);
        mRegister_code_form = (View) findViewById(R.id.register_code_form);

        mCode_button = (Button) findViewById(R.id.register_code_button);
        mCitizen_button = (Button) findViewById(R.id.register_icon_citizen_button);
        mWorker_button = (Button) findViewById(R.id.register_icon_worker_button);
        mUsername_button = (Button) findViewById(R.id.register_username_button);
        mEmail_button = (Button) findViewById(R.id.register_email_button);
        mPassword_button = (Button) findViewById(R.id.register_password_button);
        mSignUp_button = (Button) findViewById(R.id.register_signup_button);

        mUsername = (EditText) findViewById(R.id.register_username);
        mEmail = (EditText) findViewById(R.id.register_email);
        mPassword = (EditText) findViewById(R.id.register_password);
        mPasswordConfirm = (EditText) findViewById(R.id.register_confirmation);
        mCode = (EditText) findViewById(R.id.register_code);

        mCitizen_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCode_button.setVisibility(View.GONE);
                initCitizen();
            }
        });

        mWorker_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCode_button.setVisibility(View.VISIBLE);
                initCitizen();
                mCode_button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeVisibility("Code");
                    }
                });
            }
        });
    }

    private void initCitizen (){

        mSelectUser_form.setVisibility(View.GONE);
        mRegister_form.setVisibility(View.VISIBLE);

        mUsername_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibility("Username");
            }
        });
        mEmail_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibility("Email");
            }
        });
        mPassword_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibility("Password");
            }
        });
    }

    private void changeVisibility(String option) {
        switch (option) {

            case "Username": {
                mRegister_username_form.setVisibility(View.VISIBLE );
                mRegister_email_form.setVisibility(View.GONE);
                mRegister_password_form.setVisibility(View.GONE);
                mRegister_code_form.setVisibility(View.GONE);
            }
            break;

            case "Email": {
                mRegister_email_form.setVisibility(View.VISIBLE);

                mRegister_username_form.setVisibility(View.GONE);
                mRegister_password_form.setVisibility(View.GONE);
                mRegister_code_form.setVisibility(View.GONE);
            }
            break;

            case "Password": {
                mRegister_password_form.setVisibility(View.VISIBLE);

                mRegister_email_form.setVisibility(View.GONE);
                mRegister_username_form.setVisibility(View.GONE);
                mRegister_code_form.setVisibility(View.GONE);
            }
            break;

            case "Code": {
                mRegister_code_form.setVisibility(View.VISIBLE);
                mRegister_username_form.setVisibility(View.GONE);
                mRegister_email_form.setVisibility(View.GONE);
                mRegister_password_form.setVisibility(View.GONE);
            }
            break;
        }

    }


}
