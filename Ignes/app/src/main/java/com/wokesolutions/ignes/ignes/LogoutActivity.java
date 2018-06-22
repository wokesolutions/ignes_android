package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LogoutActivity extends AppCompatActivity {

    private static final int L_ONCE = 0;

    private SharedPreferences sharedPref;
    private String token;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_logout);
        mContext = this;

        sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);

        token = sharedPref.getString("token", "");

        RequestsVolley.logoutRequest(token, mContext, LogoutActivity.this,null, L_ONCE);
    }
}
