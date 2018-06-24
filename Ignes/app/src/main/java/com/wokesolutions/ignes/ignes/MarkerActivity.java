package com.wokesolutions.ignes.ignes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MarkerActivity extends AppCompatActivity {

    private ImageView marker_image, marker_status_image;
    private TextView marker_title, marker_description, marker_address, marker_username, marker_date,
            marker_gravity, marker_status, marker_likes, marker_dislikes, marker_comments, marker_gravity_title;
    private Button marker_button_likes, marker_button_dislikes;
    private EditText marker_comment;
    private ProgressBar mProgressBar;
    private MarkerClass mMarker;
    private int mLikes, mDislikes;
    private boolean mTouchLike, mTouchDislike;

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

        marker_status_image = findViewById(R.id.marker_lock_img);
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

        final String markerID = intent.getExtras().getString("MarkerClass");
        final boolean isProfile = intent.getExtras().getBoolean("IsProfile");

        if (isProfile)
            mMarker = ProfileActivity.markerMap.get(markerID);
        else
            mMarker = MapActivity.mReportMap.get(markerID);

        Log.e("MAPPPPAAA ", mMarker + "     " + MapActivity.mReportMap.get(markerID));
        marker_image.setImageBitmap(mMarker.getmImg_bitmap());

        String title = mMarker.getmTitle();
        if (!title.equals(""))
            marker_title.setText(title);
        else
            marker_title.setVisibility(View.GONE);

        String description = mMarker.getmDescription();
        if (!description.equals(""))
            marker_description.setText(description);
        else
            marker_description.setVisibility(View.GONE);

        marker_address.setText(mMarker.getmAddress());
        marker_date.setText(mMarker.getmDate());
        marker_username.setText(mMarker.getmCreator_username());

        String gravity = mMarker.getmGravity();
        if (!gravity.equals("0"))
            marker_gravity.setText(gravity);
        else {
            marker_gravity.setVisibility(View.GONE);
            marker_gravity_title.setVisibility(View.GONE);
        }

        String status = mMarker.getmStatus();
        marker_status.setText(status);

        if (status.equals("CLOSE"))
            marker_status_image.setImageResource(R.drawable.lockclose);
        if (status.equals("OPEN"))
            marker_status_image.setImageResource(R.drawable.lockopen);

        marker_likes.setText(mMarker.getmLikes());
        marker_dislikes.setText(mMarker.getmDislikes());

        mLikes = Integer.parseInt(mMarker.getmLikes());
        mDislikes = Integer.parseInt(mMarker.getmDislikes());

        int totalProgress = mLikes + mDislikes;
        if (totalProgress == 0)
            mProgressBar.setMax(1);
        else
            mProgressBar.setMax(totalProgress);

        mProgressBar.setProgress(mLikes);

        if (mMarker.getmVote().equals("up"))
            mTouchLike = true;
        if (mMarker.getmVote().equals("down"))
            mTouchDislike = true;
        Log.e("LIKES DISLIKES HERE-> ", mMarker.getmLikes() + "    " + mMarker.getmDislikes());
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

                if (mTouchLike) {
                    mLikes--;
                    mMarker.setmVote("neutro");
                    marker_button_likes.setBackgroundResource(R.drawable.upicon);

                } else {
                    mLikes++;
                    if (mTouchDislike) {
                        mDislikes--;
                        marker_button_dislikes.setBackgroundResource(R.drawable.downicon);
                        mTouchDislike = false;
                    }
                    mMarker.setmVote("up");
                    marker_button_likes.setBackgroundResource(R.drawable.upicongrey);
                }
                mProgressBar.setMax(mLikes + mDislikes);
                mProgressBar.setProgress(mLikes);
                marker_dislikes.setText("" + mDislikes);
                marker_likes.setText("" + mLikes);
                mMarker.setmDislikes(String.valueOf(mDislikes));
                mMarker.setmLikes(String.valueOf(mLikes));
                Log.e("LIKES DISLIKES HERE-> ", mMarker.getmLikes() + "    " + mMarker.getmDislikes());

                mTouchLike = !mTouchLike;

            }
        });

        marker_button_dislikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTouchDislike) {
                    mDislikes--;
                    mMarker.setmVote("neutro");
                    marker_button_dislikes.setBackgroundResource(R.drawable.downicon);

                } else {
                    mDislikes++;
                    if (mTouchLike) {
                        mLikes--;
                        marker_button_likes.setBackgroundResource(R.drawable.upicon);
                        mTouchLike = false;
                    }
                    mMarker.setmVote("down");
                    marker_button_dislikes.setBackgroundResource(R.drawable.downicongrey);
                }

                mProgressBar.setMax(mLikes + mDislikes);
                mProgressBar.setProgress(mLikes);
                marker_likes.setText("" + mLikes);
                marker_dislikes.setText("" + mDislikes);
                mMarker.setmLikes(String.valueOf(mLikes));
                mMarker.setmDislikes(String.valueOf(mDislikes));

                mTouchDislike = !mTouchDislike;
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
