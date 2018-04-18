package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;

import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;
import android.widget.SeekBar;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class ReportFormActivity extends AppCompatActivity {

    private int mRequestCode;

    private LinearLayout mLongForm;
    //private LinearLayout mShortForm;
    private LinearLayout mMediumForm;

    private Button mTitleButton;
    private Button mAddressButton;
    private Button mDescriptionButton;
    private Button mImageButton;
    private Button mUploadButton;
    private Button mCameraButton;
    private Button mSubmitButton;
    private SeekBar mGravitySlider;

    private TextInputLayout mTitleForm;
    private TextInputLayout mAddressForm;
    private TextInputLayout mDescriptionForm;
    //private LinearLayout mImageForm;
    private LinearLayout mReportLongImageForm;
    private LinearLayout mSliderForm;

    private EditText mTitle;
    private EditText mAddress;
    private EditText mDescription;
    private byte[] byteArray;
   // private ImageView mImage;

    private Bitmap mImage;

    private String mReportType;
    private Location mCurrentLocation;
    private int mGravity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Intent intent = getIntent();
        mReportType = intent.getExtras().getString("TYPE");
        mCurrentLocation = (Location) intent.getExtras().get("LOCATION");


        mLongForm = (LinearLayout) findViewById(R.id.report_long_form);
        //mShortForm = (LinearLayout) findViewById(R.id.report_short_form);
        mMediumForm = (LinearLayout) findViewById(R.id.report_medium_form);

        mCameraButton = (Button) findViewById(R.id.report_camera_button);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        mUploadButton = (Button) findViewById(R.id.report_upload_button);
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mImage.setVisibility(View.VISIBLE);

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
        //mImageForm = (LinearLayout) findViewById(R.id.report_image_form);
        mReportLongImageForm = (LinearLayout) findViewById(R.id.report_long_image_form);

        mTitle = (EditText) findViewById(R.id.report_title);
        mAddress = (EditText) findViewById(R.id.report_address);
        mDescription = (EditText) findViewById(R.id.report_description);

        mSliderForm = (LinearLayout) findViewById(R.id.report_slider_form);
        mGravitySlider = (SeekBar) findViewById(R.id.gravity_slider);

        mGravity = 0;
        mGravitySlider.setMax(4);
        mGravitySlider.incrementProgressBy(1);
        mGravitySlider.setProgress(0);
        mGravitySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mGravity = progress+1;
                System.out.println(mGravity);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //mImage = (ImageView) findViewById(R.id.report_image);

        showReportForm();

        mSubmitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportFormActivity.this, MapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Address", mAddress.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        changeFormVisibility(mReportType);

    }

    public void onActivityResult(int requestcode, int resultcode, Intent data) {

        switch (requestcode) {
            case 0:
                if (resultcode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    mImage = (Bitmap) bundle.get("data");

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArray = stream.toByteArray();
                    mImage.recycle();
                    //mImage.setImageBitmap(bmp);
                }
                break;
            case 1:
                if (resultcode == RESULT_OK) {

                    try {
                        Uri imageUri = data.getData();
                        InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        mImage = BitmapFactory.decodeStream(imageStream);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        mImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byteArray = stream.toByteArray();
                        mImage.recycle();
                        //mImage.setImageBitmap(bmp);
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
                //mImageForm.setVisibility(View.GONE);
            }
            break;
            case "Address": {
                mTitleForm.setVisibility(View.GONE);
                mAddressForm.setVisibility(View.VISIBLE);
                mDescriptionForm.setVisibility(View.GONE);
                //mImageForm.setVisibility(View.GONE);
            }
            break;
            case "Description": {
                mTitleForm.setVisibility(View.GONE);
                mAddressForm.setVisibility(View.GONE);
                mDescriptionForm.setVisibility(View.VISIBLE);
                //mImageForm.setVisibility(View.GONE);
            }
            break;
            case "Image": {
                mTitleForm.setVisibility(View.GONE);
                mAddressForm.setVisibility(View.GONE);
                mDescriptionForm.setVisibility(View.GONE);
                mReportLongImageForm.setVisibility(View.VISIBLE);
                //mImageForm.setVisibility(View.VISIBLE);
            }
            break;
        }
    }

    private void changeFormVisibility(String reportType) {
        switch (reportType) {
            case "fast": {
                //mShortForm.setVisibility(View.VISIBLE);
                mMediumForm.setVisibility(View.GONE);
                mLongForm.setVisibility(View.GONE);
                //mImageForm.setVisibility(View.VISIBLE);
                mSliderForm.setVisibility(View.GONE);
                openCamera();
            }
            break;
            case "medium": {
                //mShortForm.setVisibility(View.GONE);
                mMediumForm.setVisibility(View.VISIBLE);
                mLongForm.setVisibility(View.GONE);
                //mImageForm.setVisibility(View.VISIBLE);
                mSliderForm.setVisibility(View.VISIBLE);
                openCamera();
            }
            break;
            case "detailed": {
                //mImageForm.setVisibility(View.GONE);
                mSliderForm.setVisibility(View.VISIBLE);
            }
            break;
        }
    }

    private void openCamera() {
        //mImage.setVisibility(View.VISIBLE);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mRequestCode = 0;
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, mRequestCode);
    }


    /*--------------------------------------------------------------------------------*/
    public class ReportTask extends AsyncTask<Void, Void, String> {

        String mAddress;
        double mLat;
        double mLng;

        ReportTask(String address, byte[] img) {

            mAddress = address;

            Geocoder coder = new Geocoder(ReportFormActivity.this);
            try {
                List<Address> a = coder.getFromLocationName(mAddress, 1);
                mLat=a.get(0).getLatitude();
                mLng=a.get(0).getLongitude();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

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
        protected String doInBackground(Void... voids) {
            try {
                URL url = null;
                JSONObject report = new JSONObject();

                report.put("report_address", mAddress);
                report.put("report_lat", "morada");
                report.put("report_lng", "morada");

                url = new URL("https://hardy-scarab-200218.appspot.com/api/report/create");

                HttpURLConnection s = RequestsREST.doPOST(url, report, null);

                return s.getResponseMessage();

            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
