package com.wokesolutions.ignes.ignes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class MarkerActivity extends AppCompatActivity{


    ImageView marker_image;
    TextView marker_title;
    TextView marker_description;
    TextView marker_address;
    TextView marker_username;
    TextView marker_date;
    TextView marker_gravity;
    TextView marker_status;

    MarkerClass mMarkerClass;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_complete);

        marker_image = findViewById(R.id.marker_image);
        marker_title = findViewById(R.id.marker_title);
        marker_description = findViewById(R.id.marker_description);
        marker_address = findViewById(R.id.marker_address);
        marker_date = findViewById(R.id.marker_date);
        marker_username = findViewById(R.id.marker_reporter_username);
        marker_gravity = findViewById(R.id.marker_gravity);
        marker_status = findViewById(R.id.marker_status);
        

        Intent intent = getIntent();
        marker_image.setImageBitmap((Bitmap) intent.getExtras().get("markerImg"));
        marker_title.setText(intent.getExtras().getString("markerTitle"));
        marker_description.setText(intent.getExtras().getString("markerDescription"));
        marker_address.setText(intent.getExtras().getString("markerAddress"));
        marker_date.setText(intent.getExtras().getString("markerDate"));
        marker_username.setText(intent.getExtras().getString("markerUsername"));
        marker_gravity.setText(intent.getExtras().getString("markerGravity"));
        marker_status.setText(intent.getExtras().getString("markerStatus"));

    }


}
