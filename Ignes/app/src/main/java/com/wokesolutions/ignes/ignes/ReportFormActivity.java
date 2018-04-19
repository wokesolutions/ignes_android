package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ReportFormActivity extends AppCompatActivity {

    private String BAD_REQUEST = "java.io.IOException: HTTP error code: 400";

    private ReportTask mReportTask = null;

    private Context context;

    private int mRequestCode;
    private int mGravity;

    private byte[] byteArray;

    private Bitmap mImage;

    private String mReportType;

    private Location mCurrentLocation;

    private Geocoder mCoder;

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

    private LinearLayout mReportLongImageForm;
    private LinearLayout mMediumForm;
    private LinearLayout mSliderForm;
    private LinearLayout mLongForm;

    private EditText mTitle;
    private EditText mMediumTitle;
    private EditText mAddress;
    private EditText mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        context = this;

        Intent intent = getIntent();
        mReportType = intent.getExtras().getString("TYPE");
        mCurrentLocation = (Location) intent.getExtras().get("LOCATION");

        mCoder = new Geocoder(this);

        mLongForm = (LinearLayout) findViewById(R.id.report_long_form);
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
        mReportLongImageForm = (LinearLayout) findViewById(R.id.report_long_image_form);
        mSliderForm = (LinearLayout) findViewById(R.id.report_slider_form);

        mTitle = (EditText) findViewById(R.id.report_title);
        mMediumTitle = (EditText) findViewById(R.id.report_medium_title);
        mAddress = (EditText) findViewById(R.id.report_address);
        mDescription = (EditText) findViewById(R.id.report_description);


        mGravitySlider = (SeekBar) findViewById(R.id.gravity_slider);

        mGravity = 0;
        mGravitySlider.setMax(4);
        mGravitySlider.incrementProgressBy(1);
        mGravitySlider.setProgress(0);
        mGravitySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mGravity = progress + 1;
                System.out.println(mGravity);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        showReportForm();

        changeFormVisibility(mReportType);

        mSubmitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptReport();
            }
        });
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
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void openCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mRequestCode = 0;
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, mRequestCode);
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
            }
            break;

            case "Address": {
                mTitleForm.setVisibility(View.GONE);
                mAddressForm.setVisibility(View.VISIBLE);
                mDescriptionForm.setVisibility(View.GONE);
            }
            break;

            case "Description": {
                mTitleForm.setVisibility(View.GONE);
                mAddressForm.setVisibility(View.GONE);
                mDescriptionForm.setVisibility(View.VISIBLE);
            }
            break;

            case "Image": {
                mTitleForm.setVisibility(View.GONE);
                mAddressForm.setVisibility(View.GONE);
                mDescriptionForm.setVisibility(View.GONE);
                mReportLongImageForm.setVisibility(View.VISIBLE);
            }
            break;
        }
    }

    private void changeFormVisibility(String reportType) {
        switch (reportType) {

            case "fast": {
                mMediumForm.setVisibility(View.GONE);
                mLongForm.setVisibility(View.GONE);
                mSliderForm.setVisibility(View.GONE);
                openCamera();
            }
            break;

            case "medium": {
                mMediumForm.setVisibility(View.VISIBLE);
                mLongForm.setVisibility(View.GONE);
                mSliderForm.setVisibility(View.VISIBLE);
                openCamera();
            }
            break;

            case "detailed": {
                mSliderForm.setVisibility(View.VISIBLE);
            }
            break;
        }
    }

    private String processCurrentLocation() {
        String address = "";

        try {
            List<Address> add = mCoder.getFromLocation(mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude(), 1);

            address = add.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    private void attemptReport() {
        String address;
        byte[] image = byteArray;
        String description = mDescription.getText().toString();
        String title = "";
        int gravity = mGravity;

        if (mReportType.equals("long")) {
            address = mAddress.getText().toString();
            title = mTitle.getText().toString();
        } else if (mReportType.equals("medium")) {
            address = processCurrentLocation();
            title = mMediumTitle.getText().toString();
        } else
            address = processCurrentLocation();

        mReportTask = new ReportTask(address, image, description, title, gravity);
        mReportTask.execute((Void) null);

    }

    /*--------------------------------------------------------------------------------*/
    public class ReportTask extends AsyncTask<Void, Void, String> {

        byte[] mImage;
        String mAddress;
        double mLat;
        double mLng;
        //String base64;
        String mDescription;
        int mGravity;
        String mTitle;

        SharedPreferences prefs = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        ReportTask(String address, byte[] img, String description, String title, int gravity) {
            mImage = img;
            //base64 = Base64.encodeToString(mImage, Base64.DEFAULT);
            mAddress = address;
            mDescription = description;
            mTitle = title;
            mGravity = gravity;
            try {
                List<Address> addresses = mCoder.getFromLocationName(mAddress, 1);
                mLat = addresses.get(0).getLatitude();
                mLng = addresses.get(0).getLongitude();
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

                JSONObject report = new JSONObject();

                if (mReportType.equals("fast")) {

                    report.put("report_address", mAddress);
                    report.put("report_lat", mLat);
                    report.put("report_lng", mLng);
                    report.put("report_img", mImage);

                } else if (mReportType.equals("medium")) {

                    report.put("report_address", mAddress);
                    report.put("report_lat", mLat);
                    report.put("report_lng", mLng);
                    report.put("report_img", mImage);
                    report.put("report_title", mTitle);
                    report.put("report_gravity", mGravity);

                } else if (mReportType.equals("detailed")) {

                    report.put("report_address", mAddress);
                    report.put("report_lat", mLat);
                    report.put("report_lng", mLng);
                    report.put("report_img", mImage);
                    report.put("report_title", mTitle);
                    report.put("report_gravity", mGravity);
                    report.put("report_description", mDescription);
                }

                URL url = new URL("https://hardy-scarab-200218.appspot.com/api/report/create");

                HttpURLConnection s = RequestsREST.doPOST(url, report, token);

                return s.getResponseMessage();

            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            mReportTask = null;

            if (result.equals("200")) {
                System.out.println("RESPOSTA DO REPORT " + result);
                Toast.makeText(context, "Report successfully registered", Toast.LENGTH_LONG).show();
                finish();
            } else if(result.equals(BAD_REQUEST)){
                Toast.makeText(context, "Report bad requested", Toast.LENGTH_LONG).show();
            }
        }
    }
}