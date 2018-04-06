package com.wokesolutions.ignes.ignes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;

public class LaunchActivity extends AppCompatActivity {

    private LinearLayout layout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        layout = (LinearLayout) findViewById(R.id.launcher_touch);
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                finish();

            }
        });

    }
}
