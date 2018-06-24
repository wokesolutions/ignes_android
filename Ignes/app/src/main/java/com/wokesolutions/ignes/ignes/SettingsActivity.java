package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private static final int L_EVERYWHERE = 1;

    private Button mChangePasswordButton, mLogoutAllButton, mChangeRadiusButton;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mMenu;
    private Context mContext;
    private String mRole, mToken;
    private SharedPreferences sharedPref;
    private LinearLayout mLoggoutButton, mFeedButton, mMapButton, mProfileButton, mSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);
        mRole = sharedPref.getString("userLevel", "");
        mToken = sharedPref.getString("token", "");

        if (mRole.equals("USER"))
            setContentView(R.layout.activity_settings);
        else if (mRole.equals("WORKER")) {
            setTheme(R.style.WorkerTheme);
            setContentView(R.layout.worker_settings);
        }

        mContext = this;

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_settings);
        setSupportActionBar(myToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_settings);

        mMenu = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChangePasswordButton = findViewById(R.id.changepassword_button);
        mLogoutAllButton = findViewById(R.id.logout_all_button);
        mChangeRadiusButton = findViewById(R.id.set_radius_button);

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

                TextView changepasswordAdvice = mView.findViewById(R.id.changepassword_advice);

                Button changePassButton = mView.findViewById(R.id.submit_pass_change);
                if (mRole.equals("WORKER")) {
                    changePassButton.setBackgroundResource(R.drawable.worker_button);
                    changepasswordAdvice.setTextColor(ContextCompat.getColor(mContext, R.color.colorIgnesWorker));
                }

                changePassButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        attemptPasswordChange(mView, alert);

                    }
                });
            }
        });

        mChangeRadiusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setTitle("Change Radius");
                mBuilder.setIcon(R.drawable.radius_icon);

                LayoutInflater inflater = SettingsActivity.this.getLayoutInflater();
                final View mView = inflater.inflate(R.layout.change_radius, null);
                mBuilder.setView(mView);
                final AlertDialog alert = mBuilder.create();

                alert.show();

                TextView changeRadiusAdvice = mView.findViewById(R.id.changeradius_advice);
                Button changeRadiusButton = mView.findViewById(R.id.submit_radius);

                if (mRole.equals("WORKER")) {
                    changeRadiusButton.setBackgroundResource(R.drawable.worker_button);
                    changeRadiusAdvice.setTextColor(ContextCompat.getColor(mContext, R.color.colorIgnesWorker));
                }

                changeRadiusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        attemptRadiusChange(mView, alert);

                    }
                });

            }
        });

        mLogoutAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestsVolley.logoutRequest(mToken, mContext, SettingsActivity.this, L_EVERYWHERE);
            }
        });


        // logoutRequest(token, mContext, LogoutActivity.this,null, L_ONCE);

        general_menuButtons();

        if (mRole.equals("WORKER")) {
            getSupportActionBar().setIcon(R.drawable.ignesworkergreen);

        } else if (mRole.equals("USER")) {
            getSupportActionBar().setIcon(R.drawable.ignesred);
            user_menuButtons();
        }
    }

    /*----- About Menu Bar -----*/
    private void general_menuButtons() {
        mLoggoutButton = (LinearLayout) findViewById(R.id.botao_logout);
        mLoggoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestsVolley.logoutRequest(mToken, mContext, SettingsActivity.this, 0);
            }
        });

        mFeedButton = (LinearLayout) findViewById(R.id.botao_feed);
        mFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SettingsActivity.this, FeedActivity.class));
            }
        });

        mSettingsButton = (LinearLayout) findViewById(R.id.botao_settings);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            }
        });

        mMapButton = (LinearLayout) findViewById(R.id.menu_button_map);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void user_menuButtons() {

        mProfileButton = (LinearLayout) findViewById(R.id.botao_profile);
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
            }
        });
    }

    private void attemptPasswordChange(View view, AlertDialog alert) {

        View focusView = null;
        boolean cancel = false;

        final EditText oldPassword = view.findViewById(R.id.current_password);
        final EditText newPassword = view.findViewById(R.id.new_password);
        EditText confirmNewPassword = view.findViewById(R.id.confirm_new_password);

        // Reset errors.
        oldPassword.setError(null);
        newPassword.setError(null);
        confirmNewPassword.setError(null);

        String oldPass = oldPassword.getText().toString();
        String newPass = newPassword.getText().toString();
        String newPassConfirmation = confirmNewPassword.getText().toString();

        System.err.println("OLD: " + oldPass + " NEW: " + newPass);


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(oldPass)) {
            oldPassword.setError(getString(R.string.error_field_required));
            focusView = oldPassword;
            cancel = true;
        } else if (!isPasswordValid(newPass)) {
            newPassword.setError(getString(R.string.error_invalid_password));
            focusView = newPassword;
            cancel = true;
        } else if (!passwordEqualsConfirmation(newPass, newPassConfirmation)) {
            confirmNewPassword.setError("Must be equal to password");
            focusView = confirmNewPassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user register attempt.
            // showProgress(true);
            focusView = oldPassword;
            RequestsVolley.changePasswordRequest(oldPass, newPass, mContext, focusView, oldPassword, alert);
        }
    }

    private void attemptRadiusChange(View view, AlertDialog alert) {

        View focusView = null;
        boolean cancel = false;

        final EditText newRadius = view.findViewById(R.id.new_radius);

        // Reset errors.
        newRadius.setError(null);

        String newRad = newRadius.getText().toString();

        if (TextUtils.isEmpty(newRad)) {
            newRadius.setError(getString(R.string.error_field_required));
            focusView = newRadius;
            cancel = true;
        } else if (! (Double.parseDouble(newRad) <= 10 && Double.parseDouble(newRad) > 0)) {
            newRadius.setError("Only allowed radius between 1 and 10 km.");
            cancel = true;
            focusView = newRadius;
        }

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user register attempt.
            // showProgress(true);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("userRadius", newRad);
            editor.apply();
            alert.dismiss();
        }
    }


    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private boolean passwordEqualsConfirmation(String password, String confirmation) {
        return password.equals(confirmation);
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
