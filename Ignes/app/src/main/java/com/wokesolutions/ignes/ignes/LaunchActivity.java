package com.wokesolutions.ignes.ignes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;

import java.net.URL;

public class LaunchActivity extends AppCompatActivity {

    private LinearLayout layout;
    private TokenAuthTask mTokenAuthTask = null;
    private SharedPreferences sharedPref;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);


        sharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);

        layout = (LinearLayout) findViewById(R.id.launcher_touch);
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isTokenValid();
                //startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                //finish();

            }
        });

    }

    public void isTokenValid() {
        if (mTokenAuthTask != null) {
            return;
        }
        // Gets the token from the shared preferences.
        String token = sharedPref.getString("token", "");



        // Kick off a background task to perform the token authentication attempt.
        mTokenAuthTask = new TokenAuthTask(token);
        mTokenAuthTask.execute((Void) null);
    }

    public class TokenAuthTask extends AsyncTask<Void, Void, String> {

        private final String mToken;

        TokenAuthTask(String token) {
            mToken = token;
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

                URL url = new URL("https://hardy-scarab-200218.appspot.com/api/verifytoken");

                String s = RequestsREST.doGET(url, mToken);
                System.out.println("Strrrrriiiing - " + s);
                return s;
            } catch (Exception e) {
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(final String result) {
            mTokenAuthTask = null;

            if (result.equals("OK")) {

                System.out.println("TOKEN É VALIDO");
                startActivity(new Intent(LaunchActivity.this, MapActivity.class));


            } else {
                System.out.println("TOKEN INVÁLIDO");
                startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
            }
        }

        @Override
        protected void onCancelled() {
            mTokenAuthTask = null;

        }
    }
}
