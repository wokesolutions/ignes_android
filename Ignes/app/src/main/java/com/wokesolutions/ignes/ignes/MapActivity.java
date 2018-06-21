package com.wokesolutions.ignes.ignes;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int REPORT_ACTIVITY = 1;
    public static final int GPS_ACTIVITY = 2;
    public static Map<String, MarkerClass> mReportMap;
    public static Map<String, TaskClass> mWorkerTaskMap;
    public static Location mCurrentLocation;
    public static LatLng mLatLng;
    public Geocoder mCoder;
    public boolean isReady;
    public List<Address> addresses;
    public RequestQueue queue;
    public static String mUsername;
    public String teste;
    public String mRole;
    private static GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private ClusterManager<MarkerClass> mClusterManager;
    private ClusterManager<TaskClass> mWorkerClusterManager;
    private LocationManager mManager;
    private boolean mGps;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mMenu;
    private LinearLayout mLoggoutButton;
    private LinearLayout mProfileButton;
    private LinearLayout mFeedButton;
    private LinearLayout mSettingsButton;
    private LinearLayout mWorkRoomButton;
    private LinearLayout mTasksButton;
    private static Context mContext;
    private SharedPreferences sharedPref;
    private String mToken;
    private String mCurrentLocality;
    private List<String> orderedIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);
        mToken = sharedPref.getString("token", "");
        mUsername = sharedPref.getString("username", "ERROR");
        mRole = sharedPref.getString("userLevel", "");
        System.out.println("MROOOOOLE-> " + mRole);

        if (mRole.equals("USER"))
            setContentView(R.layout.activity_map);
        else if (mRole.equals("WORKER")) {
            setTheme(R.style.WorkerTheme);
            setContentView(R.layout.worker_map);
        }

        mContext = this;
        teste = "";
        queue = Volley.newRequestQueue(this);
        isReady = false;
        orderedIds = new LinkedList<>();

        String languageToLoad = "pt_PT";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);

        mCoder = new Geocoder(this, Locale.getDefault());
        mCurrentLocation = null;
        mLatLng = null;
        mReportMap = new HashMap<>();
        mWorkerTaskMap = new HashMap<>();
        //  mReportList = new LinkedList<>();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(myToolbar);

        /*----- About Menu Bar -----*/
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_map);

        mMenu = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (mRole.equals("USER")) {
            getSupportActionBar().setIcon(R.drawable.ignesred);
            user_menuButtons();
        } else if (mRole.equals("WORKER")) {
            getSupportActionBar().setIcon(R.drawable.ignesworkergreen);
            worker_menuButtons();
        }

        /*----- About Google Maps -----*/
        checkLocationPermission();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mGps = mManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        addresses = null;

    }

    /*----- About Google Maps -----*/

    private void setUpCluster(LatLng latLng) {
        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        if (mRole.equals("USER")) {
            // Add cluster items (markers) to the cluster manager.
            System.out.println("TAMANHO DA LISTA " + mReportMap.size());

            mClusterManager = new ClusterManager<MarkerClass>(mContext, mMap);

            // Point the map's listeners at the listeners implemented by the cluster
            // manager.
            mMap.setOnCameraIdleListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);

            // Add ten cluster items in close proximity, for purposes of this example.
            Iterator it = mReportMap.keySet().iterator();

            while (it.hasNext()) {
                String key = (String) it.next();
                System.out.println("ITEM " + key);
                mClusterManager.cluster();
                mClusterManager.addItem(mReportMap.get(key));
                mClusterManager.setRenderer(new OwnIconRendered(mContext, mMap, mClusterManager));
            }
        } else if (mRole.equals("WORKER")) {
            // Add cluster items (markers) to the cluster manager.
            System.out.println("TAMANHO DA LISTA " + mWorkerTaskMap.size());

            mWorkerClusterManager = new ClusterManager<TaskClass>(mContext, mMap);

            // Point the map's listeners at the listeners implemented by the cluster
            // manager.
            mMap.setOnCameraIdleListener(mWorkerClusterManager);
            mMap.setOnMarkerClickListener(mWorkerClusterManager);

            // Add ten cluster items in close proximity, for purposes of this example.
            Iterator it = mWorkerTaskMap.keySet().iterator();

            while (it.hasNext()) {
                String key = (String) it.next();
                System.out.println("ITEM " + key);
                mWorkerClusterManager.cluster();
                mWorkerClusterManager.addItem(mWorkerTaskMap.get(key));
                mWorkerClusterManager.setRenderer(new OwnIconRenderedWorker(mContext, mMap, mWorkerClusterManager));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("ON RESUMEE");

        /*if (mCurrentLocation != null) {
            setMarkers(readFromFile(mContext), mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();

        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public void setMarkers(JSONArray markers, double lat, double lng, String locality) {
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
                        description, gravity, title, likes, dislikes, locality, reportID);

                if (!mReportMap.containsKey(reportID)) {
                    mReportMap.put(reportID, report);
                    orderedIds.add(reportID);
                }

                /*if(!mReportList.contains(report))
                    mReportList.add(report);*/
            }

            //  mReportMap = temp;

            setUpCluster(new LatLng(lat, lng));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setWorkerMarkers(JSONArray markers, double lat, double lng, String locality) {
        try {


            System.out.println("ENTREI DENTRO DO SETWORKERMARKERS!!!    " + markers);
            //Map<String, MarkerClass> temp = new HashMap<>();

            JSONArray jsonarray = markers;

            for (int i = 0; i < jsonarray.length(); i++) {

                JSONObject jsonobject = jsonarray.getJSONObject(i);

                String reportID = jsonobject.getString("Report");

                double latitude = Double.parseDouble(jsonobject.getString("report_lat"));

                double longitude = Double.parseDouble(jsonobject.getString("report_lng"));

                String likes = "0";
                if (jsonobject.has("reportvotes_up"))
                    likes = jsonobject.getString("reportvotes_up");

                String dislikes = "0";
                if (jsonobject.has("reportvotes_down"))
                    dislikes = jsonobject.getString("reportvotes_down");

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

                String indications = "";
                if (jsonobject.has("report_indications"))
                    title = jsonobject.getString("report_title");

                String contacts = "";
                if (jsonobject.has("report_contacts"))
                    title = jsonobject.getString("report_title");

                TaskClass report = new TaskClass(latitude, longitude, status, address, date, name,
                        description, gravity, title, likes, dislikes, locality, reportID, indications, contacts);

                if (!mWorkerTaskMap.containsKey(reportID)) {
                    mWorkerTaskMap.put(reportID, report);
                    orderedIds.add(reportID);
                }

                /*if(!mReportList.contains(report))
                    mReportList.add(report);*/
            }

            //  mReportMap = temp;

            setUpCluster(new LatLng(lat, lng));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                                GPS_ACTIVITY);
                        mGps = true;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        mGps = false;
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("ignes_markers",
                    Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            System.out.println("File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("ignes_markers");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.toString());
        } catch (IOException e) {
            System.out.println("Can not read file: " + e.toString());
        }

        return ret;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Ignes Location Permission")
                        .setMessage("Location permissions are needed to access all aplication features.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap);
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            mMap.setMyLocationEnabled(true);

            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        mCurrentLocation = location;
                        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                        mLatLng = loc;
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));

                        try {
                            addresses = mCoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
                            mCurrentLocality = addresses.get(0).getLocality();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mapRequest(loc.latitude, loc.longitude, 10000, mToken, "");
                    }
                }
            });

            if (!mGps) {
                buildAlertMessageNoGps();
                mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        recreate();
                        return true;
                    }
                });

            } else {
                mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        LatLng loc = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                        try {
                            addresses = mCoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
                            mCurrentLocality = addresses.get(0).getLocality();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                });
            }
        }
    }

    /*----- About Menu Bar -----*/
    private void user_menuButtons() {

        mLoggoutButton = (LinearLayout) findViewById(R.id.botao_logout);
        mLoggoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this, LogoutActivity.class));
                finish();
            }
        });

        mProfileButton = (LinearLayout) findViewById(R.id.botao_profile);
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this, ProfileActivity.class));
            }
        });

        mFeedButton = (LinearLayout) findViewById(R.id.botao_feed);
        mFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MapActivity.this, FeedActivity.class));
            }
        });

        mSettingsButton = (LinearLayout) findViewById(R.id.botao_settings);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MapActivity.this, SettingsActivity.class));
            }
        });
    }

    /*----- About Menu Bar -----*/
    private void worker_menuButtons() {

        mLoggoutButton = (LinearLayout) findViewById(R.id.botao_logout);
        mLoggoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this, LogoutActivity.class));
                finish();
            }
        });

        mWorkRoomButton = (LinearLayout) findViewById(R.id.botao_workroom);
        mWorkRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this, ProfileActivity.class));
            }
        });

        mTasksButton = (LinearLayout) findViewById(R.id.botao_feed);
        mTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MapActivity.this, FeedActivity.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (mRole.equals("USER")) {
            inflater.inflate(R.menu.menu, menu);
        } else if (mRole.equals("WORKER")) {
            inflater.inflate(R.menu.worker_menu, menu);
        }

        MenuItem item1 = menu.findItem(R.id.username);
        item1.setVisible(false);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMenu.onOptionsItemSelected(item))
            return true;
        if (isReady) {

            if (mRole.equals("USER")) {

                if (item.getItemId() == R.id.reporticon) {
                    onReport();
                    return true;
                }

                if (item.getItemId() == R.id.searchicon)
                    filterTask();

            }
            if (item.getItemId() == R.id.refreshicon)
                recreate();
        } else
            Toast.makeText(mContext, "Try again later", Toast.LENGTH_LONG).show();

        return super.onOptionsItemSelected(item);
    }

    public void onReport() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Report");
        mBuilder.setIcon(R.drawable.ocorrenciared);

        LayoutInflater inflater = MapActivity.this.getLayoutInflater();
        final View mView = inflater.inflate(R.layout.report_choice, null);
        mBuilder.setView(mView);
        final AlertDialog alert = mBuilder.create();

        alert.show();

        if (mCurrentLocation != null) {
            Button mFast = (Button) mView.findViewById(R.id.report_fast_button);
            mFast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MapActivity.this, ReportFormActivity.class);
                    i.putExtra("TYPE", "fast");
                    i.putExtra("LOCATION", mCurrentLocation);
                    alert.dismiss();
                    startActivity(i);

                }
            });

            Button mMedium = (Button) mView.findViewById(R.id.report_medium_button);
            mMedium.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MapActivity.this, ReportFormActivity.class);
                    i.putExtra("TYPE", "medium");
                    i.putExtra("LOCATION", mCurrentLocation);
                    alert.dismiss();
                    startActivityForResult(i, REPORT_ACTIVITY);
                }
            });
        } else {
            Toast.makeText(mContext, "You should enable your gps to do report something", Toast.LENGTH_LONG).show();
        }
        //----LONG-----
        Button mLong = (Button) mView.findViewById(R.id.report_long_button);
        mLong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapActivity.this, ReportFormActivity.class);
                i.putExtra("TYPE", "detailed");

                if (mCurrentLocation != null)
                    i.putExtra("LOCATION", mCurrentLocation);

                alert.dismiss();
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REPORT_ACTIVITY:

                if (resultCode == RESULT_OK)
                    recreate();
                break;
            case GPS_ACTIVITY:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                LayoutInflater inflater = MapActivity.this.getLayoutInflater();
                final View mView = inflater.inflate(R.layout.map_refresh, null);
                builder.setView(mView);

                builder.setCancelable(true);

                final AlertDialog dlg = builder.create();

                dlg.show();

                final Timer t = new Timer();
                t.schedule(new TimerTask() {
                    public void run() {
                        dlg.dismiss(); // when the task active then close the dialog
                        t.cancel();// also just top the timer thread, otherwise, you may receive a crash report
                    }
                }, 4000); // after 3 second (or 3000 miliseconds), the task will be active.
                dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        recreate();
                    }
                });
                break;
        }
    }

    public static void getDirections(LatLng origin, LatLng dest){

        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    private void filterTask() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Search");
        mBuilder.setIcon(R.drawable.lupared);

        LayoutInflater inflater = MapActivity.this.getLayoutInflater();
        final View mView = inflater.inflate(R.layout.map_filter, null);
        mBuilder.setView(mView);
        final AlertDialog alert = mBuilder.create();

        alert.show();

        final EditText mSearchText = (EditText) mView.findViewById(R.id.search_location_text);

        Button mSearch = (Button) mView.findViewById(R.id.search_filters_button);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = mSearchText.getText().toString();
                System.out.println(address);
                alert.dismiss();

                try {
                    addresses = mCoder.getFromLocationName(address, 1);
                    if (addresses.size() > 0) {
                        double lat = addresses.get(0).getLatitude();
                        double lng = addresses.get(0).getLongitude();
                        mCurrentLocality = addresses.get(0).getLocality();
                        mapRequest(lat, lng, 10000, mToken, "");
                    } else
                        Toast.makeText(mContext, "Can't find location, please try a more detailed one", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void mapRequest(double lat, double lng, int radius, String token, String cursor) {

        RequestsVolley.mapRequest(lat, lng, radius, token, cursor, mContext, this);

        //votesRequest(mUsername, "");
    }

    public void votesRequest(String username, String cursor) {
        RequestsVolley.votesRequest(username, cursor, mContext, this);
    }

    public void setUserVotes(JSONArray jsonArray) {

        try {
            JSONArray jsonarray = jsonArray;

            for (int i = 0; i < jsonarray.length(); i++) {

                JSONObject jsonobject = jsonarray.getJSONObject(i);

                if (jsonobject.has("uservote_report")) {

                    String idReport = jsonobject.getString("uservote_report");
                    String vote = jsonobject.getString("uservote_type");

                    if (mReportMap.containsKey(idReport)) {
                        MarkerClass markerClass = mReportMap.get(idReport);
                        markerClass.setmVote(vote);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class OwnIconRendered extends DefaultClusterRenderer<MarkerClass> {

        public OwnIconRendered(Context context, GoogleMap map,
                               ClusterManager<MarkerClass> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerClass item, MarkerOptions markerOptions) {
            markerOptions.title("Marker " + item.getPosition());
            markerOptions.snippet(item.getSnippet());

            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }

    class OwnIconRenderedWorker extends DefaultClusterRenderer<TaskClass> {

        public OwnIconRenderedWorker(Context context, GoogleMap map,
                                     ClusterManager<TaskClass> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(TaskClass item, MarkerOptions markerOptions) {
            markerOptions.title("Marker " + item.getPosition());
            markerOptions.snippet(item.getSnippet());

            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }

    private static String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.e("Exception downloading", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private static class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private static class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

           /* if (result.size() < 1) {
                Toast.makeText(mContext, "No Points", Toast.LENGTH_SHORT).show();
                return;
            }*/

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(7);
                lineOptions.color(Color.RED);

            }

            //tvDistanceDuration.setText("Distance:" + distance + ", Duration:" + duration);

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }
}