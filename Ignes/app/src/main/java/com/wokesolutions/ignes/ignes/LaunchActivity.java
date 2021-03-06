package com.wokesolutions.ignes.ignes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

public class LaunchActivity extends AppCompatActivity {

    private LinearLayout layout;
    private SharedPreferences sharedPref;
    private ProgressBar mProgressBar;
    private Drawable progressDrawable;

    private View mTouchText;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        sharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);

        layout = (LinearLayout) findViewById(R.id.launcher_touch);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        progressDrawable = mProgressBar.getIndeterminateDrawable().mutate();
        progressDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        mProgressBar.setProgressDrawable(progressDrawable);

        mTouchText = findViewById(R.id.touch_to_start_text);
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                validateToken();
                showProgress(true);
                layout.setOnClickListener(null);
            }
        });
    }

     private void validateToken() {

        // Gets the token from the shared preferences.
         String token = sharedPref.getString("token", "");
         authRequest(token);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mTouchText.setVisibility(show ? View.GONE : View.VISIBLE);
            mTouchText.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mTouchText.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mTouchText.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void authRequest(String token) {
            RequestsVolley.authRequest(token, this, LaunchActivity.this);
    }
}