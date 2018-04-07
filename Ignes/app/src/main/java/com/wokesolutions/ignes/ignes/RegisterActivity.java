package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;

import org.json.JSONObject;

import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    /**
     * Keep track of the registration task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mRegTask = null;

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

        mSignUp_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
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

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        //return true;
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        //return true;
        return password.length() > 4;
    }

    /**
     * Attempts to register the account specified by the register form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual register attempt is made.
     */
    private void attemptRegister() {
        if (mRegTask != null) {
            return;
        }

        // Reset errors.
        mEmail.setError(null);
        mPassword.setError(null);

        // Store values at the time of the register attempt.
        String username = mUsername.getText().toString();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String confirmation = mPasswordConfirm.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user register attempt.
           // showProgress(true);
            mRegTask = new UserRegisterTask(username, email, password, confirmation);
            mRegTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, String> {

        private final String mUsername;
        private final String mEmail;
        private final String mPasswordString;
        private final String mConfirmation;


        UserRegisterTask(String username, String email, String password, String confirmation) {
            mUsername = username;
            mEmail = email;
            mPasswordString = password;
            mConfirmation = confirmation;
        }

        /**
         * Cancel background network operation if we do not have network connectivity.
         */
        @Override
        protected void onPreExecute() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                // If no connectivity, cancel task and update Callback with null data.
                cancel(true);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                JSONObject credentials = new JSONObject();
                credentials.put("user_username", mUsername);
                credentials.put("user_email", mEmail);
                credentials.put("user_password", mPasswordString);
                credentials.put("user_confirmation", mConfirmation);

                System.out.println("Credentials JSON to send:" + credentials);


                URL url = new URL("https://hardy-scarab-200218.appspot.com/api/register/user");

                String s = RequestsREST.doPOST(url, credentials);
                System.out.println("Strrrrriiiing - " + s);
                return s;
            } catch (Exception e) {
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(final String result) {
            mRegTask = null;
            //showProgress(false);

            if (result != null) {
                //showSucessfulRegister(true);
                System.out.println("REGISTADO COM SUCESSO " + result);
            } else {
                mPassword.setError(getString(R.string.error_incorrect_password));
                mPassword.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mRegTask = null;
            //showProgress(false);
            //showSucessfulRegister(false);
        }
    }


}