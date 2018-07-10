package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class ContactsActivity extends AppCompatActivity {

    private LinearLayout mLoggoutButton, mFeedButton, mSettingsButton, mMapButton, mProfileButton;
    private Context mContext;
    private String mToken, mRole;
    private SharedPreferences sharedPref;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        sharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);
        mToken = sharedPref.getString("token", "");
        mRole = sharedPref.getString("userRole", "");

        if (mRole.equals("USER")) {
            setContentView(R.layout.activity_contacts);
        } else if (mRole.equals("WORKER")) {
            setTheme(R.style.WorkerTheme);
            setContentView(R.layout.worker_contacts);
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_contacts);
        setSupportActionBar(myToolbar);

        if (mRole.equals("USER")) {
            getSupportActionBar().setIcon(R.drawable.ignesred);
            user_menuButtons();
        } else if (mRole.equals("WORKER")) {
            getSupportActionBar().setIcon(R.drawable.ignesworkergreen);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_contacts);

        mMenu = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        general_menuButtons();
    }

    private void general_menuButtons() {
        mLoggoutButton = (LinearLayout) findViewById(R.id.botao_logout);
        mLoggoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestsVolley.logoutRequest(mToken, mContext, ContactsActivity.this, 0);
            }
        });

        mMapButton = (LinearLayout) findViewById(R.id.menu_button_map);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSettingsButton = (LinearLayout) findViewById(R.id.botao_settings);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ContactsActivity.this, SettingsActivity.class));
                finish();
            }
        });

        mFeedButton = (LinearLayout) findViewById(R.id.botao_feed);
        mFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ContactsActivity.this, FeedActivity.class));
            }
        });
    }

    private void user_menuButtons() {

        mProfileButton = (LinearLayout) findViewById(R.id.botao_profile);
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactsActivity.this, ProfileActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem item0 = menu.findItem(R.id.username);
        item0.setVisible(false);
        MenuItem item1 = menu.findItem(R.id.refreshicon);
        item1.setVisible(false);
        MenuItem item2 = menu.findItem(R.id.searchicon);
        item2.setVisible(false);
        MenuItem item3 = menu.findItem(R.id.reporticon);
        item3.setVisible(false);


        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMenu.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
}
