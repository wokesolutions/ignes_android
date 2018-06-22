package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    public Uri mImageURI;
    public RequestQueue queue;
    private Context context;
    private Bitmap mThumbnail;
    private ImageView mImageView;
    private byte[] byteArray;
    private SharedPreferences sharedPref;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mMenu;
    private LinearLayout mLoggoutButton;
    private LinearLayout mFeedButton;
    private LinearLayout mSettingsButton;
    private LinearLayout mMapButton;
    private LinearLayout mConfirmLayout;
    private String mUsername;
    private String mToken;
    private String mUserLevel;
    private int mRequestCode;
    private Button mAboutButton;
    private Button mLessAboutButton;
    private Button mConfirmAccountButton;
    private Button mEditButton;
    private Button mSaveButton;
    private LinearLayout mAboutLayout;
    private LinearLayout mEditAboutLayout;
    private TextView mDay;
    private TextView mPoints;
    private TextView mReportNum;
    private TextView mGender;
    private TextView mAddress;
    private TextView mName;
    private TextView mJob;
    private TextView mPhonenumber;
    private TextView mMonth;
    private TextView mYear;
    private TextView mSkills;
    private TextView mLocality;
    private TextView mProfileName;
    private TextView mProfileLevel;
    private TextView mUsernameEditText;
    private boolean backBool;
    private String isConfirmed;
    public RecyclerView recyclerView;
    private Map<String, MarkerClass> markerMap;
    public MarkerAdapter markerAdapter;
    private String storedAvatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);

        mImageView = findViewById(R.id.profile_avatar);

        storedAvatar = sharedPref.getString("Avatar", "404");
        if (!storedAvatar.equals("404")) {
            byte[] img = Base64.decode(storedAvatar, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);

            RoundedBitmapDrawable roundedBitmap = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            roundedBitmap.setCircular(true);
            mImageView.setImageDrawable(roundedBitmap);
        }


        queue = Volley.newRequestQueue(this);

        mUsername = sharedPref.getString("username", "ERROR");
        mToken = sharedPref.getString("token", "");
        mUserLevel = sharedPref.getString("userLevel", "NO LEVEL");

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
        mConfirmLayout = findViewById(R.id.confirm_account_layout);

        context = this;

        mEditButton = mAboutLayout.findViewById(R.id.edit_button);

        mPoints = findViewById(R.id.user_points);
        mReportNum = findViewById(R.id.user_reports);
        mGender = mAboutLayout.findViewById(R.id.gender);
        mAddress = mAboutLayout.findViewById(R.id.address);
        mName = mAboutLayout.findViewById(R.id.name);
        mJob = mAboutLayout.findViewById(R.id.job);
        mPhonenumber = mAboutLayout.findViewById(R.id.phonenumber);
        mDay = mAboutLayout.findViewById(R.id.day);
        mMonth = mAboutLayout.findViewById(R.id.month);
        mYear = mAboutLayout.findViewById(R.id.year);
        mSkills = mAboutLayout.findViewById(R.id.skills);
        mUsernameEditText = mAboutLayout.findViewById(R.id.username);
        mLocality = findViewById(R.id.locality);
        mProfileName = findViewById(R.id.profile_name);
        mProfileLevel = findViewById(R.id.profile_userLevel);

        mProfileName.setHint(mUsername);
        mProfileLevel.setText(mUserLevel);
        mUsernameEditText.setText(mUsername);

        initializeProfile();

        backBool = false;

        checkIfAccountConfirmed();


        recyclerView = (RecyclerView) findViewById(R.id.profile_recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        markerMap = new HashMap<>();

        userReportsRequest(mUsername, mToken, "", context, ProfileActivity.this);

    }

    public void teste(Map<String,MarkerClass> ola) {
        markerMap = ola;
    }

    public void setUserMap(JSONArray markers) {
        try {

            System.out.println("ENTREI DENTRO DO SETMARKERS!!!    " + markers);
            //Map<String, MarkerClass> temp = new HashMap<>();

            JSONArray jsonarray = markers;

            for (int i = 0; i < jsonarray.length(); i++) {

                JSONObject jsonobject = jsonarray.getJSONObject(i);

                String reportID = jsonobject.getString("Report");

                double latitude = Double.parseDouble(jsonobject.getString("report_lat"));

                double longitude = Double.parseDouble(jsonobject.getString("report_lng"));

                String likes = jsonobject.getString("reportvotes_up");

                String dislikes = jsonobject.getString("reportvotes_down");

                String status = jsonobject.getString("report_status");

                String address = jsonobject.getString("report_address");

                String date = jsonobject.getString("report_creationtimeformatted");

                String name = jsonobject.getString("report_username");

                String gravity = "0";
                if (jsonobject.has("report_gravity"))
                    gravity = jsonobject.getString("report_gravity");

                String description = "";
                if (jsonobject.has("report_description"))
                    description = jsonobject.getString("report_description");

                String title = "";
                if (jsonobject.has("report_title"))
                    title = jsonobject.getString("report_title");

                MarkerClass report = new MarkerClass(latitude, longitude, status, address, date, name,
                        description, gravity, title, likes, dislikes, "teste", reportID);

                if (!markerMap.containsKey(reportID)) {
                    markerMap.put(reportID, report);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

        mSettingsButton = (LinearLayout) findViewById(R.id.botao_settings);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
            }
        });
    }

    public void checkIfAccountConfirmed() {

        isConfirmed = sharedPref.getString("isConfirmed", "");

        if (isConfirmed.equals("true"))
            mConfirmLayout.setVisibility(View.GONE);
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

        mPoints.setText(sharedPref.getString("user_points", "Point Error"));
        mReportNum.setText(sharedPref.getString("user_reportNum", "Report Error"));
        mLocality.setText(sharedPref.getString("user_locality", ""));

        mGender.setText(sharedPref.getString("user_gender", ""));
        mAddress.setText(sharedPref.getString("user_address", ""));
        mName.setText(sharedPref.getString("user_name", ""));
        mJob.setText(sharedPref.getString("user_job", ""));
        mPhonenumber.setText(sharedPref.getString("user_phone", ""));

        mDay.setText(sharedPref.getString("user_day", ""));
        mMonth.setText(sharedPref.getString("user_month", ""));
        mYear.setText(sharedPref.getString("user_year", ""));
        mSkills.setText(sharedPref.getString("user_skills", ""));
        mProfileName.setText(sharedPref.getString("user_name", mUsername));
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
                mImageView = findViewById(R.id.avatar_picture);
                mUsernameEditText = findViewById(R.id.username);

                mUsernameEditText.setText(mUsername);

                if (!storedAvatar.equals("404")) {
                    byte[] img = Base64.decode(storedAvatar, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);

                    RoundedBitmapDrawable roundedBitmap = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                    roundedBitmap.setCircular(true);
                    mImageView.setImageDrawable(roundedBitmap);
                }




                final LinearLayout edit_avatar = findViewById(R.id.edit_avatar);
                edit_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.err.println("Cliquei no avatar!");

                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        mRequestCode = 1;
                        if (pickPhoto.resolveActivity(getPackageManager()) != null)
                            startActivityForResult(pickPhoto, mRequestCode);
                    }
                });

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
                final EditText edit_phonenumber = findViewById(R.id.edit_phonenumber);
                edit_phonenumber.setText(mPhonenumber.getText().toString());
                final EditText edit_gender_self = findViewById(R.id.edit_gender_self);

                //RadioGroup edit_gender = findViewById(R.id.edit_gender);
                final RadioButton checkBox_female = findViewById(R.id.checkbox_female);
                final RadioButton checkBox_male = findViewById(R.id.checkbox_male);
                final RadioButton checkBox_other = findViewById(R.id.checkbox_other);

                String gender = sharedPref.getString("user_gender", "");

                switch (gender) {
                    case "Female":
                        checkBox_female.toggle();
                        break;
                    case "Male":
                        checkBox_male.toggle();
                        break;
                    case "Prefer to self describe":
                        checkBox_other.toggle();
                }

                mSaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String new_gender = "";

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
                        mPhonenumber.setText(new_phonenumber);
                        mLocality.setText(new_locality);
                        mSkills.setText(new_skills);

                        if (cancel) {
                            // There was an error; don't attempt register and focus the first
                            // form field with an error.
                            focusView.requestFocus();
                        } else {
                            RequestsVolley.editProfileRequest(mPhonenumber.getText().toString(),
                                    mName.getText().toString(), mGender.getText().toString(), mAddress.getText().toString(),
                                    mLocality.getText().toString(), "zip", mDay.getText().toString(), mMonth.getText().toString(),
                                    mYear.getText().toString(), mJob.getText().toString(), mSkills.getText().toString()
                                    ,mUsername, context, ProfileActivity.this );

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
                confirmRequest(insertedCode);
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

    private void confirmRequest(String insertedCode) {
        RequestsVolley.confirmRequest(insertedCode, context, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        final int THUMBSIZE = 256;
        final int QUALITY = 85;

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {

                    mImageURI = data.getData();

                    //mThumbnail = getScaledBitmap(getRealPathFromURI(mImageURI), THUMBSIZE);
                    //mThumbnail = scaleImage(THUMBSIZE, getRealPathFromURI(mImageURI));
                    mThumbnail = ThumbnailUtils.extractThumbnail(
                            BitmapFactory.decodeFile(getRealPathFromURI(mImageURI)),
                            THUMBSIZE,
                            THUMBSIZE);

                    System.out.println("BYTE COUNT THUMB: " + mThumbnail.getByteCount());

                    try {
                        ExifInterface exif = new ExifInterface(getRealPathFromURI(mImageURI));
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        System.out.println("EXIF: " + orientation);
                        Matrix matrix = new Matrix();
                        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                            matrix.postRotate(90);
                        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                            matrix.postRotate(180);
                        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                            matrix.postRotate(270);
                        }
                        mThumbnail = Bitmap.createBitmap(mThumbnail, 0, 0, mThumbnail.getWidth(), mThumbnail.getHeight(), matrix, true);

                        System.out.println("BYTE COUNT 2 THUMB: " + mThumbnail.getByteCount());
                    } catch (Exception e) {
                        System.out.println("NO ORIENTATION FOUND");
                        e.printStackTrace();
                    }

                    RoundedBitmapDrawable roundedBitmap = RoundedBitmapDrawableFactory.create(getResources(), mThumbnail);
                    roundedBitmap.setCircular(true);
                    mImageView.setImageDrawable(roundedBitmap);

                    //mImageView.setImageBitmap(mThumbnail);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mThumbnail.compress(Bitmap.CompressFormat.JPEG, QUALITY, stream);
                    byteArray = stream.toByteArray();

                    String saveAvatarPicture = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putString("Avatar", saveAvatarPicture);

                    editor.apply();

                }
                break;
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public void userReportsRequest(String username, String token, String cursor, final Context context, final ProfileActivity activity) {
        RequestsVolley.userReportsRequest(username, token, cursor, context, activity);
    }
}
