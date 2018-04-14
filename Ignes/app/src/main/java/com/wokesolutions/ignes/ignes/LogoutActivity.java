package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.net.URL;

public class LogoutActivity extends AppCompatActivity {

    private SendLogoutTask mSendLogoutTask = null;
    private SharedPreferences sharedPref;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
        token = sharedPref.getString("token", "");

    }

    @Override
    protected void onStart() {
        super.onStart();
        sendLogoutTask(token);
        finish();
    }

    public void sendLogoutTask(String token) {
        if (mSendLogoutTask != null) {
            return;
        }

        // Kick off a background task to perform the token authentication attempt.
        mSendLogoutTask = new SendLogoutTask(token);
        mSendLogoutTask.execute((Void) null);
    }
    public class SendLogoutTask extends AsyncTask<Void, Void, String> {

        private final String mToken;

        SendLogoutTask(String token) {
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

                URL url = new URL("https://hardy-scarab-200218.appspot.com/api/logout");

                String s = RequestsREST.doGET(url, mToken);
                //Assumes from this side that the response is ok
                return s;
            } catch (Exception e) {
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(final String result) {
            mSendLogoutTask = null;
            sharedPref.edit().remove("token").commit();
            System.out.println("User Logged Out");
            startActivity(new Intent(LogoutActivity.this, LoginActivity.class));
            finish();
        }

        @Override
        protected void onCancelled() {
            mSendLogoutTask = null;

        }
    }
}
