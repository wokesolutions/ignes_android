package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {
    private Context context;

    private ConfirmAccountTask mConfirmAccountTask = null;

    private EditProfileTask mEditProfileTask = null;

    private SharedPreferences sharedPref;

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mMenu;

    private LinearLayout mLoggoutButton;

    private LinearLayout mFeedButton;

    private LinearLayout mMapButton;

    private String mUsername;

    private Button mAboutButton;
    private Button mLessAboutButton;
    private Button mConfirmAccountButton;

    private Button mEditButton;
    private Button mSaveButton;

    private LinearLayout mAboutLayout;

    private LinearLayout mEditAboutLayout;

    private TextView mDay;
    private TextView mGender;
    private TextView mAddress;
    private TextView mName;
    private TextView mJob;
    private TextView mContacts;
    private TextView mPhonenumber;
    private TextView mMonth;
    private TextView mYear;
    private TextView mSkills;
    private TextView mLocality;
    private TextView mProfileName;

    private boolean backBool;
    private String isConfirmed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);

        mUsername = sharedPref.getString("username", "ERROR");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_profile);

        setSupportActionBar(myToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_profile);

        mMenu = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ignesred);

        menuButtons();

        mConfirmAccountButton = findViewById(R.id.confirm_account_button);
        mConfirmAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAccount();
            }
        });
        mAboutButton = findViewById(R.id.profile_about_button);
        mLessAboutButton = findViewById(R.id.profile_less_button);
        mAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAboutClick();
            }
        });


        mAboutLayout = findViewById(R.id.about_layout);
        mEditAboutLayout = findViewById(R.id.edit_about_layout);

        context = this;

        mEditButton = mAboutLayout.findViewById(R.id.edit_button);

        mGender = mAboutLayout.findViewById(R.id.gender);
        mAddress = mAboutLayout.findViewById(R.id.address);
        mName = mAboutLayout.findViewById(R.id.name);
        mJob = mAboutLayout.findViewById(R.id.job);
        mContacts = mAboutLayout.findViewById(R.id.contacts);
        mPhonenumber = mAboutLayout.findViewById(R.id.phonenumber);
        mDay = mAboutLayout.findViewById(R.id.day);
        mMonth = mAboutLayout.findViewById(R.id.month);
        mYear = mAboutLayout.findViewById(R.id.year);
        mSkills = mAboutLayout.findViewById(R.id.skills);
        mLocality = findViewById(R.id.locality);
        mProfileName = findViewById(R.id.profile_name);

        mProfileName.setHint(mUsername);

        initializeProfile();

        backBool = false;

        checkIfAccountConfirmed();


    }

    /*----- About Menu Bar -----*/
    private void menuButtons() {
        mLoggoutButton = (LinearLayout) findViewById(R.id.botao_logout);
        mLoggoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, LogoutActivity.class));
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
                startActivity(new Intent(ProfileActivity.this, FeedActivity.class));
                finish();
            }
        });

    }

    private void checkIfAccountConfirmed() {

        isConfirmed = sharedPref.getString("isConfirmed", "");

        if (isConfirmed.equals("true"))
            mConfirmAccountButton.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem item0 = menu.findItem(R.id.username);
        item0.setTitle(mUsername);

        MenuItem item1 = menu.findItem(R.id.refreshicon);
        item1.setVisible(false);
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

    private void initializeProfile() {

        mGender.setText(sharedPref.getString("user_gender", ""));
        mAddress.setText(sharedPref.getString("user_address", ""));
        mName.setText(sharedPref.getString("user_name", ""));
        mJob.setText(sharedPref.getString("user_job", ""));
        mContacts.setText(sharedPref.getString("user_contacts", ""));
        mPhonenumber.setText(sharedPref.getString("user_phone", ""));
        mDay.setText(sharedPref.getString("user_day", ""));
        mMonth.setText(sharedPref.getString("user_month", ""));
        mYear.setText(sharedPref.getString("user_year", ""));
        mSkills.setText(sharedPref.getString("user_skills", ""));
        mLocality.setText(sharedPref.getString("user_locality", ""));
        mProfileName.setText(sharedPref.getString("user_name", ""));
    }


    private void onAboutClick() {

        mEditButton.setVisibility(View.VISIBLE);
        mAboutLayout.setVisibility(View.VISIBLE);
        mLessAboutButton.setVisibility(View.VISIBLE);
        mAboutButton.setVisibility(View.GONE);

        mLessAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditButton.setVisibility(View.GONE);
                mAboutLayout.setVisibility(View.GONE);
                mLessAboutButton.setVisibility(View.GONE);
                mAboutButton.setVisibility(View.VISIBLE);
            }
        });

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setContentView(R.layout.profile_about_edit);

                backBool = true;

                mSaveButton = findViewById(R.id.save_button);

                final EditText edit_day = findViewById(R.id.edit_day);
                edit_day.setText(mDay.getText().toString());
                final EditText edit_month = findViewById(R.id.edit_month);
                edit_month.setText(mMonth.getText().toString());
                final EditText edit_year = findViewById(R.id.edit_year);
                edit_year.setText(mYear.getText().toString());
                final EditText edit_address = findViewById(R.id.edit_address);
                edit_address.setText(mAddress.getText().toString());
                final EditText edit_locality = findViewById(R.id.edit_locality);
                edit_locality.setText(mLocality.getText().toString());
                final EditText edit_skills = findViewById(R.id.edit_skills);
                edit_skills.setText(mSkills.getText().toString());
                final EditText edit_name = findViewById(R.id.edit_name);
                edit_name.setText(mName.getText().toString());
                final EditText edit_job = findViewById(R.id.edit_job);
                edit_job.setText(mJob.getText().toString());
                final EditText edit_contacts = findViewById(R.id.edit_contacts);
                edit_contacts.setText(mContacts.getText().toString());
                final EditText edit_phonenumber = findViewById(R.id.edit_phonenumber);
                edit_phonenumber.setText(mPhonenumber.getText().toString());
                final EditText edit_gender_self = findViewById(R.id.edit_gender_self);


                mSaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String new_gender = "";


                        RadioGroup edit_gender = findViewById(R.id.edit_gender);
                        RadioButton checkBox_female = findViewById(R.id.checkbox_female);
                        RadioButton checkBox_male = findViewById(R.id.checkbox_male);
                        RadioButton checkBox_other = findViewById(R.id.checkbox_other);

                        if (checkBox_female.isChecked())
                            new_gender = "Female";
                        else if (checkBox_male.isChecked())
                            new_gender = "Male";
                        else if (checkBox_other.isChecked()) {
                            edit_gender_self.setVisibility(View.VISIBLE);
                            new_gender = edit_gender_self.getText().toString();
                        }


                        String new_day = edit_day.getText().toString();
                        String new_month = edit_month.getText().toString();
                        String new_year = edit_year.getText().toString();
                        String new_address = edit_address.getText().toString();
                        String new_name = edit_name.getText().toString();
                        String new_contacts = edit_contacts.getText().toString();
                        String new_job = edit_job.getText().toString();
                        String new_phonenumber = edit_phonenumber.getText().toString();
                        String new_locality = edit_locality.getText().toString();
                        String new_skills = edit_skills.getText().toString();

                        boolean cancel = false;
                        View focusView = null;

                        if (!(new_day.equals("") && new_month.equals("") && new_year.equals(""))) {

                            Calendar calendar = Calendar.getInstance();

                            if (Integer.parseInt(new_year) >= 1900 && Integer.parseInt(new_year) <= calendar.get(Calendar.YEAR))
                                mYear.setText(new_year);
                            else {
                                edit_year.setError("Invalid Year!");
                                cancel = true;
                                focusView = edit_year;
                            }


                            if (Integer.parseInt(new_month) <= 12 && Integer.parseInt(new_month) >= 1)
                                mMonth.setText(new_month);
                            else {
                                edit_month.setError("Invalid Month!");
                                focusView = edit_month;
                                cancel = true;
                            }

                            if (Integer.parseInt(new_day) >= 1 && Integer.parseInt(new_day) <= 31)
                                mDay.setText(new_day);
                            else {
                                edit_day.setError("Invalid Day!");
                                cancel = true;
                                focusView = edit_day;
                            }
                        }

                        mGender.setText(new_gender);
                        mAddress.setText(new_address);
                        mName.setText(new_name);
                        mJob.setText(new_job);
                        mContacts.setText(new_contacts);
                        mPhonenumber.setText(new_phonenumber);
                        mLocality.setText(new_locality);
                        mSkills.setText(new_skills);

                        if (cancel) {
                            // There was an error; don't attempt register and focus the first
                            // form field with an error.
                            focusView.requestFocus();
                        } else {
                            mEditProfileTask = new EditProfileTask(mPhonenumber.getText().toString(),
                                    mName.getText().toString(), mGender.getText().toString(), mAddress.getText().toString(),
                                    mLocality.getText().toString(), "zip", mDay.getText().toString(), mMonth.getText().toString(),
                                    mYear.getText().toString(), mJob.getText().toString(), mSkills.getText().toString());
                            mEditProfileTask.execute((Void) null);
                        }
                    }
                });
            }
        });

    }

    private void confirmAccount() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Confirm Account");

        LayoutInflater inflater = ProfileActivity.this.getLayoutInflater();
        final View mView = inflater.inflate(R.layout.confirm_account, null);
        mBuilder.setView(mView);
        final AlertDialog alert = mBuilder.create();

        alert.show();

        final EditText mSearchText = (EditText) mView.findViewById(R.id.confirmation_code_text);

        Button mConfirm = (Button) mView.findViewById(R.id.confirm_alert_button);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String insertedCode = mSearchText.getText().toString();
                System.out.println("INSERTED CODE: " + insertedCode);
                mConfirmAccountTask = new ConfirmAccountTask(insertedCode);
                mConfirmAccountTask.execute((Void) null);
                alert.dismiss();
            }
        });

    }

    public void onBackPressed() {

        if (backBool)
            recreate();
        else
            finish();


    }

    public class ConfirmAccountTask extends AsyncTask<Void, Void, String> {

        private final String mToken;
        private final String mCode;

        ConfirmAccountTask(String code) {
            mToken = sharedPref.getString("token", "");
            mCode = code;
        }

        /**
         * Cancel background network operation if we do not have network connectivity.
         */
        @Override
        protected void onPreExecute() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                // If no connectivity, cancel task and update Callback with null data.
                cancel(true);
            }

        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                JSONObject codejson = new JSONObject();

                codejson.put("code", mCode);

                String username = sharedPref.getString("username", "ERROR");

                URL url = new URL("https://hardy-scarab-200218.appspot.com/api/profile/activate/" + username);

                HttpURLConnection s = RequestsREST.doPOST(url, codejson, mToken);
                System.out.println("RESPOSTA DO VALIDATE - " + s.getResponseCode());
                return s.getResponseMessage();
            } catch (Exception e) {
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(final String result) {
            mConfirmAccountTask = null;

            if (result.equals("OK")) {

                Toast.makeText(context, "Your account has been confirmed", Toast.LENGTH_LONG).show();
                sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("isConfirmed", "true");


            } else {
                System.out.println("ERRO A CONFIRMAR CONTA: " + result);
            }

            checkIfAccountConfirmed();
        }

        @Override
        protected void onCancelled() {
            mConfirmAccountTask = null;

        }
    }

    public class EditProfileTask extends AsyncTask<Void, Void, String> {


        private final String mToken;
        private final String mPhone;
        private final String mName;
        private final String mGender;
        private final String mAddress;
        private final String mLocality;
        private final String mZip;
        private final String mDay;
        private final String mMonth;
        private final String mYear;
        private final String mJob;
        private final String mSkills;


        EditProfileTask(String phone, String name, String gender, String address,
                        String locality, String zip, String day, String month, String year, String job, String skills) {
            mToken = sharedPref.getString("token", "");
            mPhone = phone;
            mName = name;
            mGender = gender;
            mAddress = address;
            mLocality = locality;
            mZip = zip;
            mDay = day;
            mMonth = month;
            mYear = year;
            mJob = job;
            mSkills = skills;
        }

        /**
         * Cancel background network operation if we do not have network connectivity.
         */
        @Override
        protected void onPreExecute() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                // If no connectivity, cancel task and update Callback with null data.
                cancel(true);
            }

        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                JSONObject json = new JSONObject();

                json.put("useroptional_phone", mPhone);
                json.put("useroptional_name", mName);
                json.put("useroptional_gender", mGender);
                json.put("useroptional_address", mAddress);
                json.put("useroptional_locality", mLocality);
                json.put("useroptional_zip", mZip);
                json.put("useroptional_birth", mDay + " " + mMonth + " " + mYear);
                json.put("useroptional_job", mJob);
                json.put("useroptional_skills", mSkills);

                System.out.println("JSON ->>>> : " + json);

                URL url = new URL("https://hardy-scarab-200218.appspot.com/api/profile/update/" + mUsername);

                HttpURLConnection s = RequestsREST.doPOST(url, json, mToken);

                System.out.println("RESPOSTA DO EDIT - " + s.getResponseCode());

                return s.getResponseMessage();

            } catch (Exception e) {
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(final String result) {
            mEditProfileTask = null;

            if (result.equals("OK")) {

                Toast.makeText(context, "Your account has been successfully edited", Toast.LENGTH_LONG).show();
                sharedPref = getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putString("user_phone", mPhone);
                editor.putString("user_name", mName);
                editor.putString("user_gender", mGender);
                editor.putString("user_address", mAddress);
                editor.putString("user_locality", mLocality);
                editor.putString("user_zip", mZip);
                editor.putString("user_day", mDay);
                editor.putString("user_month", mMonth);
                editor.putString("user_year", mYear);
                editor.putString("user_job", mJob);
                editor.putString("user_skills", mSkills);
                editor.apply();

                recreate();

            } else {
                System.out.println("ERRO A EDITAR CONTA: " + result);
            }
        }

        @Override
        protected void onCancelled() {
            mEditProfileTask = null;

        }
    }

}
