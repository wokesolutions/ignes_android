package com.wokesolutions.ignes.ignes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
    TextView marker_gravity_title;
    Button marker_button_likes;
    Button marker_button_dislikes;

    EditText marker_comment;

    ProgressBar mProgressBar;

    private int mLikes;
    private int mDislikes;

    //falta receber se a pessoa tem like ou dislike no marker
    private boolean mTouchLike;
    private boolean mTouchDislike;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_complete);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_marker);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ignesred);

        mProgressBar = findViewById(R.id.marker_progress_likes);

        marker_button_likes = findViewById(R.id.likes_button);
        marker_button_dislikes = findViewById(R.id.dislikes_button);

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
        marker_gravity_title = findViewById(R.id.marker_gravity_title);

        marker_comment = findViewById(R.id.marker_comment);
        marker_comment.setFocusable(false);
        marker_comment.setFocusableInTouchMode(true);


        Intent intent = getIntent();

        byte[] img = intent.getExtras().getByteArray("markerImg");

        Bitmap img_bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);

        marker_image.setImageBitmap(img_bitmap);

        String title = intent.getExtras().getString("markerTitle");
        if (!title.equals(""))
            marker_title.setText(title);
        else
            marker_title.setVisibility(View.GONE);

        String description = intent.getExtras().getString("markerDescription");
        if (!description.equals(""))
            marker_description.setText(description);
        else
            marker_description.setVisibility(View.GONE);

        marker_address.setText(intent.getExtras().getString("markerAddress"));
        marker_date.setText(intent.getExtras().getString("markerDate"));
        marker_username.setText(intent.getExtras().getString("markerUsername"));

        String gravity = intent.getExtras().getString("markerGravity");
        if (!gravity.equals("0"))
            marker_gravity.setText(gravity);
        else {
            marker_gravity.setVisibility(View.GONE);
            marker_gravity_title.setVisibility(View.GONE);
        }


        marker_status.setText(intent.getExtras().getString("markerStatus"));
        marker_likes.setText(intent.getExtras().getString("markerLikes"));
        marker_dislikes.setText(intent.getExtras().getString("markerDislikes"));

        mLikes = Integer.parseInt(intent.getExtras().getString("markerLikes"));
        mDislikes = Integer.parseInt(intent.getExtras().getString("markerDislikes"));


        int totalProgress = mLikes + mDislikes;
        if (totalProgress == 0)
            mProgressBar.setMax(1);
        else
            mProgressBar.setMax(totalProgress);

        mProgressBar.setProgress(mLikes);

        mTouchLike = false;
        mTouchDislike = false;

        if (mTouchLike)
            marker_button_likes.setBackgroundResource(R.drawable.upicongrey);
        else
            marker_button_likes.setBackgroundResource(R.drawable.upicon);

        if (mTouchDislike)
            marker_button_dislikes.setBackgroundResource(R.drawable.downicongrey);
        else
            marker_button_dislikes.setBackgroundResource(R.drawable.downicon);

        marker_button_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTouchLike = !mTouchLike;

                if (mTouchLike) {
                    mLikes++;
                    marker_button_likes.setBackgroundResource(R.drawable.upicongrey);

                } else {
                    mLikes--;
                    marker_button_likes.setBackgroundResource(R.drawable.upicon);
                }
                mProgressBar.setMax(mLikes + mDislikes);
                mProgressBar.setProgress(mLikes);
                marker_likes.setText(""+mLikes);

            }
        });

        marker_button_dislikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTouchDislike = !mTouchDislike;

                if (mTouchDislike) {
                    mDislikes++;
                    marker_button_dislikes.setBackgroundResource(R.drawable.downicongrey);

                } else {
                    mDislikes--;
                    marker_button_dislikes.setBackgroundResource(R.drawable.downicon);
                }

                mProgressBar.setMax(mLikes + mDislikes);
                mProgressBar.setProgress(mLikes);
                marker_dislikes.setText(""+mDislikes);

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
