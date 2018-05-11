package com.wokesolutions.ignes.ignes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class MarkerActivity extends AppCompatActivity {

    ImageView marker_image;
    TextView marker_title;
    TextView marker_description;
    TextView marker_address;
    TextView marker_username;
    TextView marker_date;
    TextView marker_gravity;
    TextView marker_status;
    TextView marker_likes;
    TextView marker_dislikes;
    TextView marker_comments;

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
        marker_likes = findViewById(R.id.marker_likes_number);
        marker_dislikes = findViewById(R.id.marker_dislikes_number);
        marker_comments = findViewById(R.id.marker_comments_number);

        Intent intent = getIntent();

        byte[] img = intent.getExtras().getByteArray("markerImg");

        Bitmap img_bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);

        marker_image.setImageBitmap(img_bitmap);
        marker_title.setText(intent.getExtras().getString("markerTitle"));
        marker_description.setText(intent.getExtras().getString("markerDescription"));
        marker_address.setText(intent.getExtras().getString("markerAddress"));
        marker_date.setText(intent.getExtras().getString("markerDate"));
        marker_username.setText(intent.getExtras().getString("markerUsername"));
        marker_gravity.setText(intent.getExtras().getString("markerGravity"));
        marker_status.setText(intent.getExtras().getString("markerStatus"));
        marker_likes.setText(intent.getExtras().getString("markerLikes"));
        marker_dislikes.setText(intent.getExtras().getString("markerDislikes"));

    }
}
