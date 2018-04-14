package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;

import java.net.URL;

public class ReportFormActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mMenu;
    private Button mLoggout;
    private SharedPreferences sharedPref;
    private Button mReport;
    private Button mFilter;

    private SendLogoutTask mSendLogoutTask = null;
    private int mRequestCode = 1;

    private Button mTitleButton;
    private Button mAddressButton;
    private Button mDescriptionButton;
    private Button mImageButton;
    private Button mUploadButton;
    private Button mSubmitButton;

    private TextInputLayout mTitleForm;
    private TextInputLayout mAddressForm;
    private TextInputLayout mDescriptionForm;
    private LinearLayout mImageForm;

    private EditText mTitle;
    private EditText mAddress;
    private EditText mDescription;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mUploadButton = (Button) findViewById(R.id.report_upload_button);
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);

                if(intent.resolveActivity(getPackageManager())!=null)
                    startActivityForResult(intent, mRequestCode);
            }
        });

        mTitleButton = (Button) findViewById(R.id.report_title_button);
        mAddressButton = (Button) findViewById(R.id.report_address_button);
        mDescriptionButton = (Button) findViewById(R.id.report_description_button);
        mImageButton = (Button) findViewById(R.id.report_image_button);
        mUploadButton = (Button) findViewById(R.id.report_upload_button);
        mSubmitButton = (Button) findViewById(R.id.report_submit_button);

        mTitleForm = (TextInputLayout) findViewById(R.id.report_title_form);
        mAddressForm = (TextInputLayout) findViewById(R.id.report_address_form);
        mDescriptionForm = (TextInputLayout) findViewById(R.id.report_description_form);
        mImageForm = (LinearLayout) findViewById(R.id.report_image_form);

        mTitle = (EditText) findViewById(R.id.report_title);
        mAddress = (EditText) findViewById(R.id.report_address);
        mDescription = (EditText) findViewById(R.id.report_description);
        mImage = (ImageView) findViewById(R.id.report_image);

        showReportForm();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_map);

        mMenu = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ignesred);


        menuButtons();
        mReport = (Button) findViewById(R.id.reporticon);
        mFilter = (Button) findViewById(R.id.filtericon);

    }

    public void onActivityResult(int requestcode, int resultcode, Intent data){
        if(requestcode == mRequestCode)
            if(resultcode == RESULT_OK){
            Bundle bundle = new Bundle();
            bundle = data.getExtras();
            Bitmap bmp;
            bmp = (Bitmap) bundle.get("data");
            mImage.setImageBitmap(bmp);

            }


    }

    public void showReportForm() {
        mTitleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibility("Title");
            }
        });
        mAddressButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibility("Address");
            }
        });
        mDescriptionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibility("Description");
            }
        });
        mImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibility("Image");
            }
        });
    }

    public void changeVisibility(String option) {
        switch (option) {
            case "Title": {
                mTitleForm.setVisibility(View.VISIBLE);
                mAddressForm.setVisibility(View.GONE);
                mDescriptionForm.setVisibility(View.GONE);
                mImageForm.setVisibility(View.GONE);
            }
            break;
            case "Address": {
                mTitleForm.setVisibility(View.GONE);
                mAddressForm.setVisibility(View.VISIBLE);
                mDescriptionForm.setVisibility(View.GONE);
                mImageForm.setVisibility(View.GONE);
            }
            break;
            case "Description": {
                mTitleForm.setVisibility(View.GONE);
                mAddressForm.setVisibility(View.GONE);
                mDescriptionForm.setVisibility(View.VISIBLE);
                mImageForm.setVisibility(View.GONE);
            }
            break;
            case "Image": {
                mTitleForm.setVisibility(View.GONE);
                mAddressForm.setVisibility(View.GONE);
                mDescriptionForm.setVisibility(View.GONE);
                mImageForm.setVisibility(View.VISIBLE);
            }
            break;
        }
    }
    private void menuButtons() {

        sharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);
        mLoggout = (Button) findViewById(R.id.botao_logout);
        mLoggout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = sharedPref.getString("token", "");
                sharedPref.edit().remove("token").commit();
                sendLogoutTask(token);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMenu.onOptionsItemSelected(item))
            return true;
        if(item.getItemId() == R.id.reporticon)
           // startActivity(new Intent(ReportFormActivity.this, ReportFormActivity.class));
        if(item.getItemId() == R.id.filtericon)
            filterTask();
        return super.onOptionsItemSelected(item);
    }

    private void filterTask() {
        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /*private void reportTask() {
        mReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }*/

    /*--------------------------------------------------------------------------------*/
    public void sendLogoutTask(String token) {
        if (mSendLogoutTask != null) {
            return;
        }

        // Kick off a background task to perform the token authentication attempt.
        mSendLogoutTask = new SendLogoutTask(token);
        mSendLogoutTask.execute((Void) null);
    }
    public class SendLogoutTask extends AsyncTask<Void, Void, String> {

        private final String mToken;

        SendLogoutTask(String token) {
            mToken = token;
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

                URL url = new URL("https://hardy-scarab-200218.appspot.com/api/logout");

                String s = RequestsREST.doGET(url, mToken);
                //Assumes from this side that the response is ok
                return s;
            } catch (Exception e) {
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(final String result) {
            mSendLogoutTask = null;

            System.out.println("User Logged Out");
            startActivity(new Intent(ReportFormActivity.this, LoginActivity.class));
            finish();
        }

        @Override
        protected void onCancelled() {
            mSendLogoutTask = null;

        }
    }
}
