package com.wokesolutions.ignes.ignes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;

public class RegisterActivity extends AppCompatActivity {

    public int CONFLICT_ERROR = 409;
    public EditText mUsername;
    public View mProgressView;
    private Context context;
    private View mRegister_username_form, mRegister_email_form, mRegister_password_form, mRegisterView;
    private Button mUsername_button, mEmail_button, mPassword_button, mSignUp_button;
    private EditText mEmail, mPassword, mPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegister_username_form = (View) findViewById(R.id.register_username_form);
        mRegister_email_form = (View) findViewById(R.id.register_email_form);
        mRegister_password_form = (View) findViewById(R.id.register_password_form);
        mProgressView = findViewById(R.id.register_progress);
        mRegisterView = findViewById(R.id.register_form);


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

        mSignUp_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        context = this;

    }

    public void changeVisibility(String option) {
        switch (option) {

            case "Username": {
                mRegister_username_form.setVisibility(View.VISIBLE);

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

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private boolean passwordEqualsConfirmation(String password, String confirmation) {
        return password.equals(confirmation);
    }

    private void attemptRegister() {
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
        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        } else if (!passwordEqualsConfirmation(password, confirmation)) {
            mPasswordConfirm.setError("Must be equal to password");
            focusView = mPasswordConfirm;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
            changeVisibility("Email");
        } else if (!isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
            changeVisibility("Email");
        }
        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsername.setError(getString(R.string.error_field_required));
            focusView = mUsername;
            cancel = true;
            changeVisibility("Username");
        }

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user register attempt.
            showProgress(true);
            registerRequest(username, password, email);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void registerRequest(String username, String password, String email) {
        RequestsVolley.registerRequest(username, password, email, context, this);
    }
}