package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class ReportFormActivity extends AppCompatActivity {

    private String BAD_REQUEST = "java.io.IOException: HTTP error code: 400";
    private final int REQUEST_IMAGE_CAPTURE = 0;

    private ReportTask mReportTask = null;

    private Context context;

    private int mRequestCode;
    private int mGravity;

    private byte[] byteArray;

    private String mCurrentPhotoPath;
    private Bitmap mImage;
    private Uri mImageURI;
    private File mImgFile;

    private String mReportType;

    private Location mCurrentLocation;

    private Geocoder mCoder;

    private Button mUploadButton;
    private Button mCameraButton;
    private Button mSubmitButton;

    private CheckBox mCheckBox;

    private SeekBar mGravitySlider;

    private LinearLayout mMediumForm;
    private LinearLayout mSliderForm;
    private LinearLayout mLongForm;
    private LinearLayout mSelectImage;

    private EditText mTitle;
    private EditText mMediumTitle;
    private EditText mAddress;
    private EditText mDescription;

    String address;
    String district;
    String locality;
    double lat;
    double lng;


    private ImageView mImageView;

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
        mSelectImage = (LinearLayout) findViewById(R.id.report_long_image_form);

        mCheckBox = (CheckBox) findViewById(R.id.report_checkbox);

        mCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox) v).isChecked()) {
                    mAddress.setText(address);
                } else
                    mAddress.setText("");
            }
        });

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

        mUploadButton = (Button) findViewById(R.id.report_upload_button);
        mSubmitButton = (Button) findViewById(R.id.report_submit_button);
        mSliderForm = (LinearLayout) findViewById(R.id.report_slider_form);

        mTitle = (EditText) findViewById(R.id.report_title);
        mMediumTitle = (EditText) findViewById(R.id.report_medium_title);
        mAddress = (EditText) findViewById(R.id.report_address);
        mDescription = (EditText) findViewById(R.id.report_description);

        mImageView = (ImageView) findViewById(R.id.report_image);


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
        changeFormVisibility(mReportType);

        mSubmitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptReport();
            }
        });

        if(mCurrentLocation!=null) {
            try {
                List<Address> addresses = mCoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
                address = addresses.get(0).getAddressLine(0);
                district = addresses.get(0).getAdminArea();
                locality = addresses.get(0).getLocality();

            } catch (IOException e) {
                e.printStackTrace();
            }
            lat = mCurrentLocation.getLatitude();
            lng = mCurrentLocation.getLongitude();
        }
        else
            mCheckBox.setVisibility(View.INVISIBLE);

    }

    private File createImageFile() throws IOException {
        //Unique filename to prevent name collision
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imgFileName = "IGNES_" + timeStamp + "_";
        File storageDir = getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
        File image = File.createTempFile(imgFileName,".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void addPicToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(mCurrentPhotoPath);
        mImageURI = Uri.fromFile(file);
        mediaScanIntent.setData(mImageURI);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    addPicToGallery();
                    /*Bundle bundle = data.getExtras();
                    mImage = (Bitmap) bundle.get("data");
                    mImageView.setVisibility(View.VISIBLE);

                    RoundedBitmapDrawable roundedBitmap = RoundedBitmapDrawableFactory.create(getResources(), mImage);
                    roundedBitmap.setCircular(true);

                    mImageView.setImageDrawable(roundedBitmap);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byteArray = stream.toByteArray();*/
                    //nao consigooooooo
                    // mImage = (Bitmap) MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(), ContentUris.parseId(mImageURI),MediaStore.Images.Thumbnails.MINI_KIND, (BitmapFactory.Options) null);
                    final int THUMBSIZE = 128;
                    mImage = ThumbnailUtils.extractThumbnail(
                            BitmapFactory.decodeFile(mCurrentPhotoPath),
                            300,
                            300);
                    mImageView.setVisibility(View.VISIBLE);

                    RoundedBitmapDrawable roundedBitmap = RoundedBitmapDrawableFactory.create(getResources(), mImage);
                    roundedBitmap.setCircular(true);

                    mImageView.setImageDrawable(roundedBitmap);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byteArray = stream.toByteArray();

                    //mImage.recycle();
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {

                    try {
                        Uri imageUri = data.getData();
                        InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        mImage = BitmapFactory.decodeStream(imageStream);
                        mImageView.setVisibility(View.VISIBLE);

                        RoundedBitmapDrawable roundedBitmap = RoundedBitmapDrawableFactory.create(getResources(), mImage);
                        roundedBitmap.setCircular(true);

                        mImageView.setImageDrawable(roundedBitmap);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        System.out.println("QUE BOLEEAN DEEEEEU?!: " + mImage.compress(Bitmap.CompressFormat.JPEG, 100, stream));
                        byteArray = stream.toByteArray();
                        //mImage.recycle();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void openCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File pictureFile = null;
            try {
                pictureFile = createImageFile();
            } catch (IOException e) {
                System.out.println("ERROR CREATING FILE");
                e.printStackTrace();
            }
            if(pictureFile != null) {
                Uri pictureURI = FileProvider.getUriForFile(context, "com.wokesolutions.ignes.ignes", pictureFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureURI);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void changeFormVisibility(String reportType) {
        switch (reportType) {

            case "fast": {
                mMediumForm.setVisibility(View.GONE);
                mLongForm.setVisibility(View.GONE);
                mSliderForm.setVisibility(View.GONE);
                mSelectImage.setVisibility(View.GONE);
                openCamera();
            }
            break;

            case "medium": {
                mMediumForm.setVisibility(View.VISIBLE);
                mLongForm.setVisibility(View.GONE);
                mSliderForm.setVisibility(View.VISIBLE);
            }
            break;

            case "detailed": {
                mSliderForm.setVisibility(View.VISIBLE);
            }
            break;
        }
    }

    private void attemptReport() {
        byte[] image = byteArray;
        String description = "";
        String title = "";
        String locality = this.locality;
        String district = this.district;
        String address = this.address;
        double lat = this.lat;
        double lng = this.lng;
        int gravity = mGravity;

        if (mReportType.equals("detailed")) {
            if(!mCheckBox.isChecked()) {
                address = mAddress.getText().toString();
                try {
                    System.out.println("MORADA DENTRO DO LONG: " + address);
                    List<Address> addresses = mCoder.getFromLocationName(address, 1);
                    lat = addresses.get(0).getLatitude();
                    lng = addresses.get(0).getLongitude();
                    district = addresses.get(0).getAdminArea();
                    locality = addresses.get(0).getLocality();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            title = mTitle.getText().toString();
            description = mDescription.getText().toString();

        } else if (mReportType.equals("medium")) {

            title = mMediumTitle.getText().toString();

        } else if (mReportType.equals("fast")) {

        }

        mReportTask = new ReportTask(image, description, title, district, address, locality, gravity, lat, lng);
        mReportTask.execute((Void) null);
    }

    public class ReportTask extends AsyncTask<Void, Void, String> {

        byte[] mImage;
        double mLat;
        double mLng;
        String base64;
        String mDescription;
        int mGravity;
        String mTitle;
        String mDistrict;
        String mAddress;
        String mLocality;

        SharedPreferences prefs = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        ReportTask(byte[] img, String description, String title, String district, String address, String locality, int gravity, double lat, double lng) {
            mImage = img;
            base64 = Base64.encodeToString(mImage, Base64.DEFAULT);
            mLat = lat;
            mLng = lng;
            mDescription = description;
            mTitle = title;
            mDistrict = district;
            mAddress = address;
            mLocality = locality;
            mGravity = gravity;
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

                    report.put("report_lat", mLat);
                    report.put("report_lng", mLng);
                    report.put("report_img", base64);
                    report.put("report_thumbnail", "alo");
                    report.put("report_address", mAddress);
                    report.put("report_city", mDistrict);
                    report.put("report_locality", mLocality);

                } else if (mReportType.equals("medium")) {

                    report.put("report_lat", mLat);
                    report.put("report_lng", mLng);
                    report.put("report_img", base64);
                    report.put("report_thumbnail", "alo");
                    report.put("report_title", mTitle);
                    report.put("report_gravity", mGravity);
                    report.put("report_address", mAddress);
                    report.put("report_city", mDistrict);
                    report.put("report_locality", mLocality);

                } else if (mReportType.equals("detailed")) {

                    report.put("report_lat", mLat);
                    report.put("report_lng", mLng);
                    report.put("report_img", base64);
                    report.put("report_thumbnail", "alo");
                    report.put("report_title", mTitle);
                    report.put("report_gravity", mGravity);
                    report.put("report_description", mDescription);
                    report.put("report_address", mAddress);
                    report.put("report_city", mDistrict);
                    report.put("report_locality", mLocality);
                }

                System.out.println("REPORT JSON: " + report);
                System.out.println("ADDRESS DO DETAILED: "+ mAddress +"Localidade e cidade "+ mLocality +" " + mDistrict);

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
            System.out.println("RESPOSTA DO REPORT " + result);
            if (result.equals("OK")) {
                Toast.makeText(context, "Report successfully registered", Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_OK, new Intent());
                finish();
            } else if (result.equals(BAD_REQUEST)) {
                Toast.makeText(context, "Report bad requested", Toast.LENGTH_LONG).show();
            }
        }
    }
}