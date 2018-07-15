package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private static final int L_EVERYWHERE = 1;

    private Button mChangePasswordButton, mLogoutAllButton, mChangeRadiusButton, mAddLocalityButton;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mMenu;
    private Context mContext;
    private String mRole, mToken;
    private SharedPreferences sharedPref;
    private LinearLayout mLoggoutButton, mFeedButton, mMapButton, mProfileButton, mSettingsButton,
            mContactsButton, mLeaderboardButton;
    private List<Address> mAddresses;
    private Geocoder mGeocoder;
    private Switch mEmailNotificationsSwitch;
    private Boolean mWantsEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);
        mRole = sharedPref.getString("userRole", "");
        mToken = sharedPref.getString("token", "");
        mWantsEmail = sharedPref.getBoolean("sendemail", true);


        if (mRole.equals("USER"))
            setContentView(R.layout.activity_settings);
        else if (mRole.equals("WORKER")) {
            setTheme(R.style.WorkerTheme);
            setContentView(R.layout.worker_settings);
        }

        mContext = this;
        mAddresses = null;
        mGeocoder = new Geocoder(this, Locale.getDefault());


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_settings);
        setSupportActionBar(myToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_settings);

        mMenu = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChangePasswordButton = findViewById(R.id.changepassword_button);
        mLogoutAllButton = findViewById(R.id.logout_all_button);
        mAddLocalityButton = findViewById(R.id.add_localities_button);


        setChangePassword();


        if (mRole.equals("USER")) {
            setAddLocality();
            setChangeRadius();
            mEmailNotificationsSwitch = findViewById(R.id.notifications_switch);
            mEmailNotificationsSwitch.setChecked(mWantsEmail);
        }

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

    private void setAddLocality() {

        mAddLocalityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setTitle("Adicionar localidade a seguir");
                mBuilder.setIcon(R.drawable.addlocalityicon);

                LayoutInflater inflater = SettingsActivity.this.getLayoutInflater();
                final View mView = inflater.inflate(R.layout.add_localities, null);
                mBuilder.setView(mView);
                final AlertDialog alert = mBuilder.create();
                alert.show();

                final EditText new_address = mView.findViewById(R.id.new_address);
                new_address.setError(null);
                Button addLocButton = mView.findViewById(R.id.submit_new_address);

                addLocButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attemptNewAddress(new_address, alert);
                    }
                });

            }
        });
    }

    private void attemptNewAddress(EditText new_address, AlertDialog alertDialog) {

        View focusView = null;
        boolean cancel = false;

        String address = new_address.getText().toString();

        if (!address.equals("")) {
            try {
                mAddresses = mGeocoder.getFromLocationName(address, 1);

                if (mAddresses.size() > 0) {
                    Toast.makeText(mContext, "Successfully added!", Toast.LENGTH_LONG).show();

                } else {
                    new_address.setError("Can't find location, please try a more detailed one");
                    focusView = new_address;
                    cancel = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new_address.setError(getString(R.string.error_field_required));
            focusView = new_address;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            focusView = new_address;
            RequestsVolley.userFollowLocalityRequest(address, mToken, mContext);
            alertDialog.dismiss();
        }

    }

    private void setChangePassword() {

        mChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setTitle(R.string.change_password);
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

    }

    private void setChangeRadius() {

        mChangeRadiusButton = findViewById(R.id.set_radius_button);

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

                Button changeRadiusButton = mView.findViewById(R.id.submit_radius);

                changeRadiusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean isCheck = false;

                        CheckBox radius_10km = mView.findViewById(R.id.radius_10km);
                        CheckBox radius_5km = mView.findViewById(R.id.radius_5km);
                        CheckBox radius_500m = mView.findViewById(R.id.radius_500m);
                        CheckBox radius_50m = mView.findViewById(R.id.radius_50m);

                        String newRad = "";

                        if (radius_10km.isChecked()) {
                            newRad = "10";
                            isCheck = true;
                        } else if (radius_5km.isChecked()) {
                            newRad = "5";
                            isCheck = true;
                        } else if (radius_500m.isChecked()) {
                            newRad = "0.5";
                            isCheck = true;
                        } else if (radius_50m.isChecked()) {
                            newRad = "0.05";
                            isCheck = true;
                        }


                        if (isCheck) {
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("userRadius", newRad);
                            editor.apply();
                            alert.dismiss();
                            Toast.makeText(mContext, "Your radius has been modified!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "Nothing selected!", Toast.LENGTH_LONG).show();

                        }
                    }
                });

            }
        });

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
                finish();
            }
        });

        mSettingsButton = (LinearLayout) findViewById(R.id.botao_settings);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
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

        mContactsButton = (LinearLayout) findViewById(R.id.botao_contacts);
        mContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SettingsActivity.this, ContactsActivity.class));
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
                finish();
            }
        });

        mLeaderboardButton = (LinearLayout) findViewById(R.id.menu_button_leaderboard);
        mLeaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, LeaderboardActivity.class));
                finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mRole.equals("USER"))
            if (mWantsEmail != mEmailNotificationsSwitch.isChecked()) {
                System.out.println("SEND REQUEST");
                RequestsVolley.changeSendEmailRequest(mToken, mContext);
                sharedPref.edit().putBoolean("sendemail", mEmailNotificationsSwitch.isChecked()).apply();
            }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMenu.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }


}
