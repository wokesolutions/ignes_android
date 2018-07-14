package com.wokesolutions.ignes.ignes;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class ReportFormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 77;

    public static final String LIXO = "Limpeza de lixo geral";
    public static final String PESADOS = "Transportes pesados";
    public static final String PERIGOSOS = "Transportes perigosos";
    public static final String PESSOAS = "Transporte de pessoas";
    public static final String TRANSPORTE = "Transportes gerais";
    public static final String MADEIRAS = "Madeiras";
    public static final String CARCACAS = "Carcaças";
    public static final String BIOLOGICO = "Outros resíduos biológicos";
    public static final String JARDINAGEM = "Jardinagem";
    public static final String MATAS = "Limpeza de matas/florestas";

    private final int REQUEST_IMAGE_CAPTURE = 0;
    public byte[] imgByteArray;
    public Uri mImageURI;
    public String mReportType;
    public boolean mIsPrivate;
    String address, district, locality, category;
    double lat, lng;
    private Context context;
    private int mRequestCode, mGravity, orientation;
    private byte[] byteArray;
    private String mCurrentPhotoPath;
    private Bitmap mImage, mThumbnail;
    private File mImgFile;
    private Location mCurrentLocation;
    private Geocoder mCoder;
    private Button mUploadButton, mCameraButton, mSubmitButton;
    private CheckBox mCheckBox, mCheckBox_Private;
    private SeekBar mGravitySlider;
    private LinearLayout mMediumForm, mSliderForm, mLongForm, mReportForm, mUploadPicture, mAddressLayout;
    private EditText mTitle, mMediumTitle, mAddress, mDescription;
    private ImageView mImageView;
    private ArrayList<LatLng> mPoints;
    private Spinner mReportTypeSpinner;

    public static Bitmap getScaledBitmap(String path, int newSize) {
        File image = new File(path);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inInputShareable = true;
        options.inPurgeable = true;

        BitmapFactory.decodeFile(image.getPath(), options);
        if ((options.outWidth == -1) || (options.outHeight == -1))
            return null;

        int originalSize = (options.outHeight > options.outWidth) ? options.outHeight
                : options.outWidth;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / newSize;

        Bitmap scaledBitmap = BitmapFactory.decodeFile(image.getPath(), opts);

        return scaledBitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        context = this;

        checkStoragePermission();

        Intent intent = getIntent();
        mReportType = intent.getExtras().getString("TYPE");
        mCurrentLocation = (Location) intent.getExtras().get("LOCATION");
        mPoints = (ArrayList<LatLng>) intent.getExtras().get("AREA");
        mCoder = new Geocoder(this);

        mReportForm = (LinearLayout) findViewById(R.id.report_form);
        mLongForm = (LinearLayout) findViewById(R.id.report_long_form);
        mMediumForm = (LinearLayout) findViewById(R.id.report_medium_form);
        mUploadPicture = (LinearLayout) findViewById(R.id.report_upload);
        mAddressLayout = findViewById(R.id.address_layout);

        mCheckBox = (CheckBox) findViewById(R.id.report_checkbox);
        mCheckBox_Private = (CheckBox) findViewById(R.id.report_checkbox_private);

        mReportTypeSpinner = findViewById(R.id.report_category_spinner);
        mReportTypeSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.report_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mReportTypeSpinner.setAdapter(adapter);

        mCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    mAddress.setText(address);
                } else
                    mAddress.setText("");
            }
        });

        mCheckBox_Private.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    mIsPrivate = true;
                } else
                    mIsPrivate = false;
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
                mGravity = progress;
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

        if (mCurrentLocation != null) {
            try {
                List<Address> addresses = mCoder.getFromLocation(mCurrentLocation.getLatitude(),
                        mCurrentLocation.getLongitude(), 1);
                address = addresses.get(0).getAddressLine(0);
                district = addresses.get(0).getAdminArea();
                locality = addresses.get(0).getLocality();

            } catch (IOException e) {
                e.printStackTrace();
            }
            lat = mCurrentLocation.getLatitude();
            lng = mCurrentLocation.getLongitude();
        } else
            mCheckBox.setVisibility(View.INVISIBLE);

    }

    private File createImageFile() throws IOException {
        //Unique filename to prevent name collision
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imgFileName = "IGNES_" + timeStamp + "_";
        //File storageDir = getExternalStoragePublicDirectory();
        File storageDir = new File(getExternalStoragePublicDirectory(DIRECTORY_PICTURES) + File.separator + "/Ignes/");
        storageDir.mkdir();


        File image = File.createTempFile(imgFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(this)
                        .setTitle("Ignes Storage Permission")
                        .setMessage("Storage permission is needed to save and upload pictures.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ReportFormActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_STORAGE);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    private void addPicToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(mCurrentPhotoPath);
        mImageURI = Uri.fromFile(file);
        mediaScanIntent.setData(mImageURI);
        this.sendBroadcast(mediaScanIntent);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null,
                null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private Bitmap scaleImage(int destWidth, String path) {
        Bitmap image = BitmapFactory.decodeFile(path);

        int originWidth = image.getWidth();
        int originHeight = image.getHeight();

        if (originWidth > destWidth) {
            int destHeight = originHeight / (originWidth / destWidth);

            Bitmap scaled = Bitmap.createScaledBitmap(image, destWidth, destHeight, false);

            /*ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            scaled.compress(Bitmap.CompressFormat.JPEG, 70, outStream);*/

            return scaled;
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        final int THUMBSIZE = 256;
        final int QUALITY = 85;

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    addPicToGallery();

                    //mThumbnail = scaleImage(THUMBSIZE, mCurrentPhotoPath);
                    mThumbnail = getScaledBitmap(mCurrentPhotoPath, THUMBSIZE);

                    mImageView.setVisibility(View.VISIBLE);

                    try {
                        ExifInterface exif = new ExifInterface(mCurrentPhotoPath);
                        orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        Matrix matrix = new Matrix();
                        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                            matrix.postRotate(90);
                            orientation = 90;
                        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                            matrix.postRotate(180);
                            orientation = 180;
                        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                            matrix.postRotate(270);
                            orientation = 270;
                        } else
                            orientation = 0;
                        mThumbnail = Bitmap.createBitmap(mThumbnail, 0, 0, mThumbnail.getWidth(),
                                mThumbnail.getHeight(), matrix, true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //RoundedBitmapDrawable roundedBitmap = RoundedBitmapDrawableFactory.create(getResources(), mThumbnail);
                    //roundedBitmap.setCircular(true);

                    mImageView.setImageBitmap(mThumbnail);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mThumbnail.compress(Bitmap.CompressFormat.JPEG, QUALITY, stream);
                    byteArray = stream.toByteArray();


                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {

                    mImageURI = data.getData();

                    mThumbnail = getScaledBitmap(getRealPathFromURI(mImageURI), THUMBSIZE);
                    //mThumbnail = scaleImage(THUMBSIZE, getRealPathFromURI(mImageURI));
                       /* mThumbnail = ThumbnailUtils.extractThumbnail(
                                BitmapFactory.decodeFile(getRealPathFromURI(mImageURI)),
                                THUMBSIZE,
                                THUMBSIZE);*/
                    mImageView.setVisibility(View.VISIBLE);

                    try {
                        ExifInterface exif = new ExifInterface(getRealPathFromURI(mImageURI));
                        orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        Matrix matrix = new Matrix();
                        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                            matrix.postRotate(90);
                            orientation = 90;
                        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                            matrix.postRotate(180);
                            orientation = 180;
                        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                            matrix.postRotate(270);
                            orientation = 270;
                        } else
                            orientation = 0;
                        mThumbnail = Bitmap.createBitmap(mThumbnail, 0, 0, mThumbnail.getWidth(),
                                mThumbnail.getHeight(), matrix, true);
                    } catch (Exception e) {
                        System.out.println("NO ORIENTATION FOUND");
                        e.printStackTrace();
                    }

                    //RoundedBitmapDrawable roundedBitmap = RoundedBitmapDrawableFactory.create(getResources(), mThumbnail);
                    //roundedBitmap.setCircular(true);


                    mImageView.setImageBitmap(mThumbnail);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mThumbnail.compress(Bitmap.CompressFormat.JPEG, QUALITY, stream);
                    byteArray = stream.toByteArray();
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
            if (pictureFile != null) {
                Uri pictureURI = FileProvider.getUriForFile(context, "com.wokesolutions.ignes.ignes",
                        pictureFile);
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
                mUploadPicture.setVisibility(View.GONE);
                openCamera();
            }
            break;

            case "medium": {
                mMediumForm.setVisibility(View.VISIBLE);
                mLongForm.setVisibility(View.GONE);
                mSliderForm.setVisibility(View.VISIBLE);
                if (mPoints != null)
                    mAddressLayout.setVisibility(View.GONE);
            }
            break;

            case "detailed": {
                mSliderForm.setVisibility(View.VISIBLE);
                if (mPoints != null)
                    mAddressLayout.setVisibility(View.GONE);
            }
            break;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        category = (String) parent.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    private void attemptReport() {
        mTitle.setError(null);
        mMediumTitle.setError(null);
        mAddress.setError(null);

        byte[] thumbnail = byteArray;
        String description = "";
        String title = "";
        String locality = this.locality;
        String category = this.category;
        String district = this.district;
        String address = this.address;
        double lat = this.lat;
        double lng = this.lng;
        int gravity = mGravity + 1;
        JSONArray jsonArray = null;

        boolean cancel = false;
        View focusView = null;


        if (mPoints != null) {
            jsonArray = new JSONArray();
            try {
                for (LatLng latLng : mPoints) {

                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("lat", latLng.latitude);
                    jsonObject.put("lng", latLng.longitude);

                    jsonArray.put(jsonObject);
                }

                List<Address> addresses = mCoder.getFromLocation(mPoints.get(0).latitude, mPoints.get(0).longitude, 1);
                address = addresses.get(0).getAddressLine(0);
                district = addresses.get(0).getAdminArea();
                locality = addresses.get(0).getLocality();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mReportType.equals("detailed")) {

            if (mCurrentLocation != null) {
                if (!mCheckBox.isChecked()) {
                    address = mAddress.getText().toString();

                    if (TextUtils.isEmpty(address)) {
                        mAddress.setError(getString(R.string.error_field_required));
                        focusView = mAddress;
                        cancel = true;
                    } else {
                        try {
                            List<Address> addresses = mCoder.getFromLocationName(address, 1);
                            lat = addresses.get(0).getLatitude();
                            lng = addresses.get(0).getLongitude();
                            district = addresses.get(0).getAdminArea();
                            locality = addresses.get(0).getLocality();
                        } catch (IOException e) {
                            e.printStackTrace();
                            mAddress.setError("Morada inválida");
                            focusView = mAddress;
                            cancel = true;
                        }
                    }
                }
            }
            title = mTitle.getText().toString();
            description = mDescription.getText().toString();

            if (TextUtils.isEmpty(title)) {
                mTitle.setError(getString(R.string.error_field_required));
                focusView = mTitle;
                cancel = true;
            }
            else if (!isTitleValid(title)) {
                mTitle.setError("Máximo de 20 caracteres (Tem "+title.length()+")");
                focusView = mTitle;
                cancel = true;
            }

        } else if (mReportType.equals("medium")) {

            title = mMediumTitle.getText().toString();

            if (TextUtils.isEmpty(title)) {
                mMediumTitle.setError(getString(R.string.error_field_required));
                focusView = mMediumTitle;
                cancel = true;
            }
            else if (!isTitleValid(title)) {
                mMediumTitle.setError("Máximo de 20 caracteres (Tem "+title.length()+")");
                focusView = mMediumTitle;
                cancel = true;
            }

        }

        LatLng pointAddress = new LatLng(lat, lng);

        switch (category) {
            case LIXO:
                category = "LIXO";
                break;
            case PESADOS:
                category = "PESADOS";
                break;
            case PERIGOSOS:
                category = "PERIGOSOS";
                break;
            case PESSOAS:
                category = "PESSOAS";
                break;
            case TRANSPORTE:
                category = "TRANSPORTE";
                break;
            case MADEIRAS:
                category = "MADEIRAS";
                break;
            case CARCACAS:
                category = "CARCACAS";
                break;
            case BIOLOGICO:
                category = "BIOLOGICO";
                break;
            case JARDINAGEM:
                category = "JARDINAGEM";
                break;
            case MATAS:
                category = "MATAS";
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            finish();
            reportRequest(description, title, district, address, locality, category, gravity, pointAddress, jsonArray);
        }


    }

    private boolean isTitleValid(String title) {
        return title.length() <= 20;
    }

    private void reportRequest(String description, String title, String district, String address,
                               String locality, String category, int gravity, LatLng pointAddress, JSONArray jsonArray) {

        RequestsVolley.reportRequest(description, title, district, address,
                locality, category, gravity, pointAddress, jsonArray, orientation, context, this);

    }
}