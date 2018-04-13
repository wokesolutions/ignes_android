package com.wokesolutions.ignes.ignes;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class SideBarActivity extends AppCompatActivity {

    private Button mLoggout;
    private SharedPreferences sharedPref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sidebarnavigation);
        sharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);
        mLoggout=(Button) findViewById(R.id.botao_logout);
        mLoggout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.edit().remove("token").commit();
                startActivity(new Intent(SideBarActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}

