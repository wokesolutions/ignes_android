package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class SettingsActivity extends AppCompatActivity {

    private Button mChangePasswordButton;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mMenu;
    private Context mContext;

    private String mRole;

    private SharedPreferences sharedPref;

    private LinearLayout mLoggoutButton;
    private LinearLayout mWorkRoomButton;
    private LinearLayout mFeedButton;
    private LinearLayout mMapButton;
    private LinearLayout mProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mContext = this;

        sharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);
        mRole = sharedPref.getString("userLevel", "");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_settings);
        setSupportActionBar(myToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_settings);

        mMenu = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChangePasswordButton = findViewById(R.id.changepassword_button);

        mChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setTitle("Change Password");
                mBuilder.setIcon(R.drawable.keyicon);

                LayoutInflater inflater = SettingsActivity.this.getLayoutInflater();
                final View mView = inflater.inflate(R.layout.change_password, null);
                mBuilder.setView(mView);
                final AlertDialog alert = mBuilder.create();

                alert.show();

                final EditText oldPassword = mView.findViewById(R.id.current_password);
                final EditText newPassword = mView.findViewById(R.id.new_password);
                EditText confirmNewPassword = mView.findViewById(R.id.confirm_new_password);

                Button changePassButton = mView.findViewById(R.id.submit_pass_change);
                changePassButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String oldPass = oldPassword.getText().toString();
                        String newPass = newPassword.getText().toString();

                        System.err.println("OLD: " + oldPass + " NEW: " + newPass);

                        RequestsVolley.changePasswordRequest(oldPass, newPass, mContext);
                    }
                });
            }
        });


        if (mRole.equals("WORKER")) {
            getSupportActionBar().setIcon(R.drawable.ignesworkergreen);
            worker_menuButtons();

        } else if (mRole.equals("USER")) {
            getSupportActionBar().setIcon(R.drawable.ignesred);
            user_menuButtons();

        }

    }

    private void user_menuButtons() {

        mLoggoutButton = (LinearLayout) findViewById(R.id.botao_logout);
        mLoggoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, LogoutActivity.class));
                finish();
            }
        });

        mProfileButton = (LinearLayout) findViewById(R.id.botao_profile);
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
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
                startActivity(new Intent(SettingsActivity.this, FeedActivity.class));
                finish();
            }
        });
    }

    /*----- About Menu Bar -----*/
    private void worker_menuButtons() {

        mLoggoutButton = (LinearLayout) findViewById(R.id.botao_logout);
        mLoggoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, LogoutActivity.class));
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
                startActivity(new Intent(SettingsActivity.this, FeedActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (mRole.equals("USER")) {
            inflater.inflate(R.menu.menu, menu);

            MenuItem item1 = menu.findItem(R.id.searchicon);
            item1.setVisible(false);
            MenuItem item2 = menu.findItem(R.id.reporticon);
            item2.setVisible(false);

        } else if (mRole.equals("WORKER")) {
            inflater.inflate(R.menu.worker_menu, menu);
        }

        MenuItem item3 = menu.findItem(R.id.refreshicon);
        item3.setVisible(false);
        MenuItem item4 = menu.findItem(R.id.username);
        item4.setVisible(false);

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
