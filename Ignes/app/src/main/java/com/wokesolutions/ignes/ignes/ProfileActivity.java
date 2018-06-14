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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import static android.R.layout.simple_spinner_item;


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

    private LinearLayout mAboutLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);

        mUsername = sharedPref.getString("username","ERROR");

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

        mEditButton = findViewById(R.id.edit_button);

        mAboutLayout = findViewById(R.id.about_layout);

        context = this;
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

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 1900; i--) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapterI = new ArrayAdapter<String>(this, simple_spinner_item, years);
        final Spinner spinYear = mAboutLayout.findViewById(R.id.year_spinner);
        spinYear.setAdapter(adapterI);

        ArrayList<String> days = new ArrayList<String>();
        for (int i = 1; i <= 31; i++) {
            days.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapterII = new ArrayAdapter<String>(this, simple_spinner_item, days);
        final Spinner spinDay = mAboutLayout.findViewById(R.id.day_spinner);
        spinDay.setAdapter(adapterII);


        ArrayAdapter<CharSequence> adapterIII = ArrayAdapter.createFromResource(this,
                R.array.month_array, simple_spinner_item);
        final Spinner spinMonth = mAboutLayout.findViewById(R.id.month_spinner);
        spinMonth.setAdapter(adapterIII);

        final Button save_button = mAboutLayout.findViewById(R.id.save_button);

        final TextView gender = mAboutLayout.findViewById(R.id.gender);
        final TextView address = mAboutLayout.findViewById(R.id.address);
        final TextView name = mAboutLayout.findViewById(R.id.name);
        final TextView job = mAboutLayout.findViewById(R.id.job);
        final TextView contacts = mAboutLayout.findViewById(R.id.contacts);
        final TextView phonenumber = mAboutLayout.findViewById(R.id.phonenumber);

        final TextView day = mAboutLayout.findViewById(R.id.day);
        final TextView month = mAboutLayout.findViewById(R.id.month);
        final TextView year = mAboutLayout.findViewById(R.id.year);

        final TextView edit_day = mAboutLayout.findViewById(R.id.edit_day);
        final TextView edit_month = mAboutLayout.findViewById(R.id.edit_month);
        final TextView edit_year = mAboutLayout.findViewById(R.id.edit_year);

        final EditText edit_address = mAboutLayout.findViewById(R.id.edit_address);
        final EditText edit_name = mAboutLayout.findViewById(R.id.edit_name);
        final EditText edit_job = mAboutLayout.findViewById(R.id.edit_job);
        final EditText edit_contacts = mAboutLayout.findViewById(R.id.edit_contacts);
        final EditText edit_phonenumber = mAboutLayout.findViewById(R.id.edit_phonenumber);
        final EditText edit_gender_self = mAboutLayout.findViewById(R.id.edit_gender_self);

        final View edit_birthdate = mAboutLayout.findViewById(R.id.edit_birthdate_layout);
        final View birthdate = mAboutLayout.findViewById(R.id.birthdate_layout);

        final RadioGroup edit_gender = mAboutLayout.findViewById(R.id.edit_gender);
        final RadioButton checkBox_female = mAboutLayout.findViewById(R.id.checkbox_female);
        final RadioButton checkBox_male = mAboutLayout.findViewById(R.id.checkbox_male);
        final RadioButton checkBox_other = mAboutLayout.findViewById(R.id.checkbox_other);


        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address.setVisibility(View.GONE);
                name.setVisibility(View.GONE);
                job.setVisibility(View.GONE);
                contacts.setVisibility(View.GONE);
                phonenumber.setVisibility(View.GONE);
                birthdate.setVisibility(View.GONE);
                gender.setVisibility(View.GONE);


                spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        edit_month.setText(spinMonth.getItemAtPosition(position).toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                spinDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        edit_day.setText(spinDay.getItemAtPosition(position).toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        edit_year.setText(spinYear.getItemAtPosition(position).toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                edit_address.setVisibility(View.VISIBLE);
                edit_name.setVisibility(View.VISIBLE);
                edit_job.setVisibility(View.VISIBLE);
                edit_contacts.setVisibility(View.VISIBLE);
                edit_phonenumber.setVisibility(View.VISIBLE);
                edit_birthdate.setVisibility(View.VISIBLE);
                edit_gender.setVisibility(View.VISIBLE);

                save_button.setVisibility(View.VISIBLE);

                save_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String new_gender = "";

                        //falta mostrar o edittext para escreveer o genero que se quer

                        if (checkBox_female.isChecked())
                            new_gender = "Female";
                        else if (checkBox_male.isChecked())
                            new_gender = "Male";
                        else if (checkBox_other.isChecked()) {
                            edit_gender_self.setVisibility(View.VISIBLE);
                            new_gender = edit_gender_self.getText().toString();
                        }

                        String new_address = edit_address.getText().toString();
                        String new_name = edit_name.getText().toString();
                        String new_contacts = edit_contacts.getText().toString();
                        String new_job = edit_job.getText().toString();
                        String new_phonenumber = edit_phonenumber.getText().toString();

                        String new_day = edit_day.getText().toString();
                        String new_month = edit_month.getText().toString();
                        String new_year = edit_year.getText().toString();

                        day.setText(new_day);
                        month.setText(new_month);
                        year.setText(new_year);

                        String birth = day.getText().toString() +"/"+month.getText().toString()+"/"+year.getText().toString();

                        gender.setText(new_gender);
                        address.setText(new_address);
                        name.setText(new_name);
                        job.setText(new_job);
                        contacts.setText(new_contacts);
                        phonenumber.setText(new_phonenumber);

                        address.setVisibility(View.VISIBLE);
                        name.setVisibility(View.VISIBLE);
                        job.setVisibility(View.VISIBLE);
                        contacts.setVisibility(View.VISIBLE);
                        phonenumber.setVisibility(View.VISIBLE);
                        birthdate.setVisibility(View.VISIBLE);
                        gender.setVisibility(View.VISIBLE);

                        edit_address.setVisibility(View.GONE);
                        edit_name.setVisibility(View.GONE);
                        edit_job.setVisibility(View.GONE);
                        edit_contacts.setVisibility(View.GONE);
                        edit_phonenumber.setVisibility(View.GONE);
                        edit_birthdate.setVisibility(View.GONE);
                        edit_gender.setVisibility(View.GONE);
                        edit_gender_self.setVisibility(View.GONE);

                        save_button.setVisibility(View.GONE);

                        mEditProfileTask = new EditProfileTask(phonenumber.getText().toString(), name.getText().toString(), gender.getText().toString(), address.getText().toString(), "locality", "zip", birth, job.getText().toString(), "skills");
                        mEditProfileTask.execute((Void) null);
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

                String username = sharedPref.getString("username","ERROR");

                URL url = new URL("https://hardy-scarab-200218.appspot.com/api/profile/activate/"+username);

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


            } else {
                System.out.println("ERRO A CONFIRMAR CONTA: " +result);
            }
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
        private final String mBirth;
        private final String mJob;
        private final String mSkills;


        EditProfileTask(String phone, String name, String gender, String address, String locality, String zip, String birth, String job, String skills) {
            mToken = sharedPref.getString("token", "");
            mPhone = phone;
            mName = name;
            mGender = gender;
            mAddress = address;
            mLocality = locality;
            mZip = zip;
            mBirth = birth;
            mJob = job;
            mSkills =skills;
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
                json.put("useroptional_birth", mBirth);
                json.put("useroptional_job", mJob);
                json.put("useroptional_skills", mSkills);

                System.out.println("JSON ->>>> : " + json);

                URL url = new URL("https://hardy-scarab-200218.appspot.com/api/profile/update/"+mUsername);

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


            } else {
                System.out.println("ERRO A EDITAR CONTA: " +result);
            }
        }

        @Override
        protected void onCancelled() {
            mEditProfileTask = null;

        }
    }

}
