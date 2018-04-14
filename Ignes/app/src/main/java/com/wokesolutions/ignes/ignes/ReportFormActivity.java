package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

public class ReportFormActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mMenu;
    private Button mLoggout;
    private Button mReport;
    private Button mFilter;

    private int mRequestCode;

    private Button mTitleButton;
    private Button mAddressButton;
    private Button mDescriptionButton;
    private Button mImageButton;
    private Button mUploadButton;
    private Button mCameraButton;
    private Button mSubmitButton;

    private TextInputLayout mTitleForm;
    private TextInputLayout mAddressForm;
    private TextInputLayout mDescriptionForm;
    private LinearLayout mImageForm;

    private EditText mTitle;
    private EditText mAddress;
    private EditText mDescription;
    private ImageView mImage;
    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mView = (View) findViewById(R.id.report_view);
        mCameraButton = (Button) findViewById(R.id.report_camera_button);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImage.setVisibility(View.VISIBLE);
                mView.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mRequestCode = 0;
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(intent, mRequestCode);
            }
        });
        mUploadButton = (Button) findViewById(R.id.report_upload_button);
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImage.setVisibility(View.VISIBLE);
                mView.setVisibility(View.VISIBLE);
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                mRequestCode = 1;
                if (pickPhoto.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(pickPhoto, mRequestCode);
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

    public void onActivityResult(int requestcode, int resultcode, Intent data) {

        switch (requestcode) {
            case 0:
                if (resultcode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Bitmap bmp;
                    bmp = (Bitmap) bundle.get("data");
                    mImage.setImageBitmap(bmp);
                }
                break;
            case 1:
                if (resultcode == RESULT_OK) {

                    try {
                        Uri imageUri = data.getData();
                        InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                        mImage.setImageBitmap(bmp);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                break;
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

        mLoggout = (Button) findViewById(R.id.botao_logout);
        mLoggout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportFormActivity.this, LogoutActivity.class));
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
        if (item.getItemId() == R.id.reporticon)
            // startActivity(new Intent(ReportFormActivity.this, ReportFormActivity.class));
            if (item.getItemId() == R.id.filtericon)
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

    /*--------------------------------------------------------------------------------*/
}
