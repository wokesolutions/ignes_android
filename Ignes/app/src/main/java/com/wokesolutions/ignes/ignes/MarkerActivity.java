package com.wokesolutions.ignes.ignes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mMenu;

    private LinearLayout mLoggoutButton;
    private LinearLayout mFeedButton;
    private LinearLayout mMapButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_complete);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_marker);
        setSupportActionBar(myToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_feed);

        mMenu = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ignesred);

        menuButtons();


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

        String title = intent.getExtras().getString("markerTitle");
        if(!title.equals(""))
        marker_title.setText(title);
        else
            marker_title.setVisibility(View.GONE);

        String description = intent.getExtras().getString("markerDescription");
        if(!description.equals(""))
        marker_description.setText(description);
        else
            marker_description.setVisibility(View.GONE);

        marker_address.setText(intent.getExtras().getString("markerAddress"));
        marker_date.setText(intent.getExtras().getString("markerDate"));
        marker_username.setText(intent.getExtras().getString("markerUsername"));
        marker_gravity.setText(intent.getExtras().getString("markerGravity"));
        marker_status.setText(intent.getExtras().getString("markerStatus"));
        marker_likes.setText(intent.getExtras().getString("markerLikes"));
        marker_dislikes.setText(intent.getExtras().getString("markerDislikes"));

    }
    /*----- About Menu Bar -----*/
    private void menuButtons() {
        mLoggoutButton = (LinearLayout) findViewById(R.id.botao_logout);
        mLoggoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MarkerActivity.this, LogoutActivity.class));
                finish();
            }
        });
        mMapButton = (LinearLayout) findViewById(R.id.menu_button_map);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mFeedButton = (LinearLayout) findViewById(R.id.botao_feed);
        mFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MarkerActivity.this, FeedActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.refreshicon);
        item.setVisible(false);
        MenuItem item2 = menu.findItem(R.id.searchicon);
        item2.setVisible(false);
        MenuItem item3 = menu.findItem(R.id.reporticon);
        item3.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMenu.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
}
