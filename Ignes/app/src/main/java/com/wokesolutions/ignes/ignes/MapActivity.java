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
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

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
import java.util.Arrays;
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
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0x80388E3C;
    private static final int COLOR_PURPLE_ARGB = 0x8081C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;
    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);

    public static Map<String, MarkerClass> mReportMap;
    public static Map<String, MarkerClass> mSearchMap;
    public static Map<String, TaskClass> mWorkerTaskMap;
    public static Map<String, String> votesMap;
    public static Map<String, MarkerClass> userMarkerMap;
    public static Location mCurrentLocation;
    public static LatLng mLatLng;
    public static String mUsername;
    public static LinearLayout mGoogleMapsButtonLayout;
    public static GoogleMap mMap;
    public static RequestQueue queue;
    public static String mRole;
    public static boolean isSearch;
    private static Context mContext;
    private static Button mGoogleMapsButton;
    private static Polyline mMapPollyLine;
    private static ArrayList<LatLng> vector;
    private static List<String> orderedIds;
    public Geocoder mCoder;
    public boolean isReady;
    public List<Address> addresses;
    public String mUserRadius;
    private ArrayList<Polygon> currentPolygonsArray;
    private Map<String, Polygon> mapPolygons;
    private Button mFinishTaskPathButton;
    private Button mFinishDrawButton, mFinishDrawAddress, mNextButton;
    private FusedLocationProviderClient mFusedLocationClient;
    private ClusterManager<MarkerClass> mClusterManager;
    private ClusterManager<TaskClass> mWorkerClusterManager;
    private LocationManager mManager;
    private boolean mGps;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mMenu;
    private LinearLayout mLoggoutButton, mProfileButton, mFeedButton, mSettingsButton;
    private LinearLayout mContactsButton;
    private SharedPreferences sharedPref;
    private String mToken, mCurrentLocality;
    private int counter;

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

    public static void getDirections(LatLng dest, TaskClass taskClass) {
        String url = getDirectionsUrl(mLatLng, dest);
        DownloadTask downloadTask = new DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);
        mToken = sharedPref.getString("token", "");
        mUsername = sharedPref.getString("username", "ERROR");
        mRole = sharedPref.getString("userRole", "");

        if (!sharedPref.getString("userRadius", "").isEmpty())
            mUserRadius = sharedPref.getString("userRadius", "");
        else
            mUserRadius = "5";

        if (mRole.equals("USER")) {
            setContentView(R.layout.activity_map);
            mFinishDrawAddress = findViewById(R.id.done_button);
        } else if (mRole.equals("WORKER")) {
            setTheme(R.style.WorkerTheme);
            setContentView(R.layout.worker_map);

            mGoogleMapsButtonLayout = findViewById(R.id.googlemapsbutton_layout);
            mFinishTaskPathButton = findViewById(R.id.finish_task_path);

            mFinishTaskPathButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGoogleMapsButtonLayout.setVisibility(View.GONE);
                    if(mMapPollyLine!=null)
                          mMapPollyLine.remove();

                    Toast.makeText(mContext, "Finish Path", Toast.LENGTH_LONG).show();
                }
            });

        }

        mContext = this;
        queue = Volley.newRequestQueue(this);
        isReady = false;
        isSearch = false;
        orderedIds = new LinkedList<>();
        mFinishDrawButton = findViewById(R.id.done_button);
        mNextButton = findViewById(R.id.next_button);
        currentPolygonsArray = new ArrayList<>();

        String languageToLoad = "pt_PT";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);

        mCoder = new Geocoder(this, Locale.getDefault());
        mCurrentLocation = null;
        mLatLng = null;
        mReportMap = new HashMap<>();
        mSearchMap = new HashMap<>();
        mWorkerTaskMap = new HashMap<>();
        votesMap = new HashMap<>();
        userMarkerMap = new HashMap<>();
        mapPolygons = new HashMap<>();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        /*----- About Menu Bar -----*/
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_map);
        mMenu = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        general_menuButtons();

        if (mRole.equals("USER")) {

            getSupportActionBar().setIcon(R.drawable.ignesred);
            user_menuButtons();

            addresses = null;

            vector = new ArrayList<>();
            counter = 0;

            mFinishDrawButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createPolygon(vector);
                    mNextButton.setVisibility(View.VISIBLE);
                }
            });

        } else if (mRole.equals("WORKER")) {

            getSupportActionBar().setIcon(R.drawable.ignesworkergreen);

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


    }

    private void setUpCluster(LatLng latLng, Map<String, MarkerClass> map) {
        mMap.clear();
        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        if (mRole.equals("USER")) {
            if (map == mReportMap)
                isSearch = false;
            else if (map == mSearchMap)
                isSearch = true;

            // Add cluster items (markers) to the cluster manager.
            mClusterManager = new ClusterManager<MarkerClass>(mContext, mMap);

            // Point the map's listeners at the listeners implemented by the cluster
            // manager.
            mMap.setOnCameraIdleListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);

            // Add ten cluster items in close proximity, for purposes of this example.
            Iterator it = map.keySet().iterator();

            while (it.hasNext()) {
                String key = (String) it.next();
                final MarkerClass item = map.get(key);
                mClusterManager.cluster();
                mClusterManager.addItem(item);
                mClusterManager.setRenderer(new OwnIconRendered(mContext, mMap, mClusterManager));

                mMap.setOnInfoWindowClickListener(mClusterManager);
                mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<MarkerClass>() {
                    @Override
                    public void onClusterItemInfoWindowClick(MarkerClass markerClass) {

                        if (markerClass.mIsArea()) {

                            try {
                                if (!markerClass.mIsClicked()) {
                                    markerClass.setmIsClicked(true);
                                    Polygon polygon = setAreaReport(new JSONArray(markerClass.getmPoints()));
                                    mapPolygons.put(markerClass.getmId(), polygon);
                                } else {
                                    markerClass.setmIsClicked(false);
                                    Polygon polygon = mapPolygons.get(markerClass.getmId());
                                    polygon.remove();
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }

                        } else {
                            Intent i = new Intent(MapActivity.this, MarkerActivity.class);
                            i.putExtra("MarkerClass", markerClass.getmId());
                            startActivity(i);
                        }
                    }
                });

            }
        } else if (mRole.equals("WORKER")) {
            // Add cluster items (markers) to the cluster manager.
            mWorkerClusterManager = new ClusterManager<TaskClass>(mContext, mMap);

            // Point the map's listeners at the listeners implemented by the cluster
            // manager.
            mMap.setOnCameraIdleListener(mWorkerClusterManager);
            mMap.setOnMarkerClickListener(mWorkerClusterManager);

            // Add ten cluster items in close proximity, for purposes of this example.
            Iterator it = mWorkerTaskMap.keySet().iterator();

            while (it.hasNext()) {
                String key = (String) it.next();
                final TaskClass item = mWorkerTaskMap.get(key);
                mWorkerClusterManager.cluster();
                mWorkerClusterManager.addItem(item);
                mWorkerClusterManager.setRenderer(new OwnIconRenderedWorker(mContext, mMap,
                        mWorkerClusterManager));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public void setMarkers(JSONArray markers, double lat, double lng, String locality, boolean search) {


        try {
            JSONArray jsonarray = markers;

            for (int i = 0; i < jsonarray.length(); i++) {

                JSONObject jsonobject = jsonarray.getJSONObject(i);

                String reportID = jsonobject.getString("report");

                String likes = jsonobject.getString("ups");

                String dislikes = jsonobject.getString("downs");

                String status = jsonobject.getString("status");

                String address = jsonobject.getString("address");

                String date = jsonobject.getString("creationtime");

                String name = jsonobject.getString("username");

                String category = jsonobject.getString("category");

                switch (category) {
                    case "LIXO":
                        category = LIXO;
                        break;
                    case "PESADOS":
                        category = PESADOS;
                        break;
                    case "PERIGOSOS":
                        category = PERIGOSOS;
                        break;
                    case "PESSOAS":
                        category = PESSOAS;
                        break;
                    case "TRANSPORTE":
                        category = TRANSPORTE;
                        break;
                    case "MADEIRAS":
                        category = MADEIRAS;
                        break;
                    case "CARCACAS":
                        category = CARCACAS;
                        break;
                    case "BIOLOGICO":
                        category = BIOLOGICO;
                        break;
                    case "JARDINAGEM":
                        category = JARDINAGEM;
                        break;
                    case "MATAS":
                        category = MATAS;
                }

                double latitude = Double.parseDouble(jsonobject.getString("lat"));

                double longitude = Double.parseDouble(jsonobject.getString("lng"));

                boolean isArea = false;
                boolean isClicked = false;

                String points = null;
                if (jsonobject.has("points")) {
                    isArea = true;
                    points = jsonobject.getString("points");
                }

                String gravity = "0";
                if (jsonobject.has("gravity"))
                    gravity = jsonobject.getString("gravity");

                String description = "";
                if (jsonobject.has("description"))
                    description = jsonobject.getString("description");

                String title = "";
                if (jsonobject.has("title"))
                    title = jsonobject.getString("title");

                boolean isPrivate = jsonobject.getBoolean("isprivate");

                MarkerClass report = new MarkerClass(latitude, longitude, status, address, date, name,
                        description, gravity, title, likes, dislikes, locality, isArea, isClicked,
                        points, category, reportID, isPrivate);

                //TODO - 404 e 403 - AVATAR DA PESSOA PARA POR NO FEED
                //RequestsVolley.userAvatarRequest(report.getmCreator_username(), report, null, mContext);


                if (!search) {
                    if (!mReportMap.containsKey(reportID)) {
                        mReportMap.put(reportID, report);
                        orderedIds.add(reportID);
                    }
                    if (!userMarkerMap.containsKey(reportID)) {
                        if (name.equals(mUsername)) {
                            userMarkerMap.put(reportID, report);
                        }
                    }
                    setUpCluster(new LatLng(lat, lng), mReportMap);
                } else {
                    if (!mSearchMap.containsKey(reportID)) {
                        mSearchMap.put(reportID, report);
                    }

                    setUpCluster(new LatLng(lat, lng), mSearchMap);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setWorkerMarkers(JSONArray markers, double lat, double lng, String locality) {
        try {

            JSONArray jsonarray = markers;

            for (int i = 0; i < jsonarray.length(); i++) {

                JSONObject jsonobject = jsonarray.getJSONObject(i);

                String reportID = jsonobject.getString("task");

                double latitude = Double.parseDouble(jsonobject.getString("lat"));

                double longitude = Double.parseDouble(jsonobject.getString("lng"));

                String likes = "0";
                if (jsonobject.has("reportvotes_up"))
                    likes = jsonobject.getString("reportvotes_up");

                String dislikes = "0";
                if (jsonobject.has("reportvotes_down"))
                    dislikes = jsonobject.getString("reportvotes_down");

                String status = jsonobject.getString("status");

                String address = jsonobject.getString("address");

                String date = jsonobject.getString("creationtime");

                String name = jsonobject.getString("username");

                String category = jsonobject.getString("category");

                String phonenumber = "";

                if (jsonobject.has("phone"))
                    phonenumber = jsonobject.getString("phone");

                String gravity = "0";
                if (jsonobject.has("gravity"))
                    gravity = jsonobject.getString("gravity");

                String description = "";
                if (jsonobject.has("description"))
                    description = jsonobject.getString("description");

                String title = "";
                if (jsonobject.has("title"))
                    title = jsonobject.getString("title");

                String indications = "";
                if (jsonobject.has("indications"))
                    indications = jsonobject.getString("indications");

                boolean isPrivate = jsonobject.getBoolean("isprivate");

                TaskClass report = new TaskClass(latitude, longitude, status, address, date, name,
                        description, gravity, title, likes, dislikes, locality, reportID, indications, category, phonenumber, isPrivate);

                if (!mWorkerTaskMap.containsKey(reportID)) {
                    mWorkerTaskMap.put(reportID, report);
                    orderedIds.add(reportID);
                }
            }

            setUpCluster(new LatLng(lat, lng), null);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Polygon setAreaReport(JSONArray points) {
        ArrayList<LatLng> pointsArray = new ArrayList<>();

        for (int i = 0; i < points.length(); i++) {

            try {

                JSONObject jsonobject = points.getJSONObject(i);

                double latitude = Double.parseDouble(jsonobject.getString("lat"));

                double longitude = Double.parseDouble(jsonobject.getString("lng"));

                LatLng latLng = new LatLng(latitude, longitude);

                pointsArray.add(latLng);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return createPolygon(pointsArray);
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
                        mapRequest(loc.latitude, loc.longitude, Double.parseDouble(mUserRadius), mToken, "");
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
                        setUpCluster(loc, mReportMap);
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
    private void general_menuButtons() {
        mLoggoutButton = (LinearLayout) findViewById(R.id.botao_logout);
        mLoggoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestsVolley.logoutRequest(mToken, mContext, MapActivity.this, 0);
            }
        });

        mFeedButton = (LinearLayout) findViewById(R.id.botao_feed);
        mFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MapActivity.this, FeedActivity.class));
            }
        });

        mContactsButton = (LinearLayout) findViewById(R.id.botao_contacts);
        mContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this, ContactsActivity.class));
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

    private void user_menuButtons() {

        mProfileButton = (LinearLayout) findViewById(R.id.botao_profile);
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this, ProfileActivity.class));
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
                    onReportStart();
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

    private void cleanMapPolygons() {

        for (Polygon polygon : currentPolygonsArray)
            polygon.remove();
    }

    public void onReportStart() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle(R.string.report_alert);
        mBuilder.setIcon(R.drawable.ocorrenciared);

        LayoutInflater inflater = MapActivity.this.getLayoutInflater();
        final View mView = inflater.inflate(R.layout.report_address_choices, null);
        mBuilder.setView(mView);
        final AlertDialog alert = mBuilder.create();

        alert.show();

        Button mAddressButton = mView.findViewById(R.id.report_address_button);
        Button mAreaButton = mView.findViewById(R.id.report_area_button);

        mAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                onReport(false);

            }
        });

        mAreaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                mFinishDrawAddress.setVisibility(View.VISIBLE);
                onSelectArea();
            }
        });
    }

    public void onReport(final boolean isArea) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle(R.string.report_alert);
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

                    if (isArea)
                        i.putExtra("AREA", vector);
                    else
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

                    if (isArea)
                        i.putExtra("AREA", vector);
                    else
                        i.putExtra("LOCATION", mCurrentLocation);
                    alert.dismiss();
                    startActivityForResult(i, REPORT_ACTIVITY);
                }
            });
        } else {
            Toast.makeText(mContext, "You should enable your gps to do report something",
                    Toast.LENGTH_LONG).show();
        }
        //----LONG-----
        Button mLong = (Button) mView.findViewById(R.id.report_long_button);
        mLong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapActivity.this, ReportFormActivity.class);
                i.putExtra("TYPE", "detailed");

                if (isArea)
                    i.putExtra("AREA", vector);
                else {
                    if (mCurrentLocation != null)
                        i.putExtra("LOCATION", mCurrentLocation);

                }

                alert.dismiss();
                startActivity(i);
            }
        });

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                cleanMapPolygons();
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

    private void filterTask() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle(R.string.search_alert);
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
                alert.dismiss();

                try {
                    addresses = mCoder.getFromLocationName(address, 1);
                    if (addresses.size() > 0) {
                        double lat = addresses.get(0).getLatitude();
                        double lng = addresses.get(0).getLongitude();
                        mCurrentLocality = addresses.get(0).getLocality();
                        locationReportsRequest(lat, lng, mCurrentLocality, mToken, "");
                    } else
                        Toast.makeText(mContext, "Can't find location, please try a more detailed one", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void mapRequest(double lat, double lng, double radius, String token, String cursor) {

        RequestsVolley.mapRequest(lat, lng, radius, token, cursor, mContext, this);
    }

    public Polygon createPolygon(ArrayList<LatLng> points) {

        if (!points.isEmpty()) {
            // Add polygons to indicate areas on the map.
            Polygon polygon = mMap.addPolygon(new PolygonOptions()
                    .clickable(false)
                    .addAll(points));

            // Store a data object with the polygon, used here to indicate an arbitrary type.
            polygon.setTag("alpha");
            // Style the polygon.
            stylePolygon(polygon);

            currentPolygonsArray.add(polygon);

            mFinishDrawAddress.setVisibility(View.GONE);

            mNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReport(true);
                    mNextButton.setVisibility(View.GONE);

                }
            });
            return polygon;
        }
        return null;
    }

   /* public void createPolyline (LatLng c1, LatLng c2) {
        PolylineOptions rectOptions = new PolylineOptions()
                .add(c1,c2)
                .width(5)
                .color(Color.RED);

       mMap.addPolyline(rectOptions);
    }*/

    /**
     * Styles the polygon, based on type.
     *
     * @param polygon The polygon object that needs styling.
     */
    private void stylePolygon(Polygon polygon) {
        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "alpha":
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_GREEN_ARGB;
                fillColor = COLOR_PURPLE_ARGB;
                break;
            case "beta":
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA;
                strokeColor = COLOR_ORANGE_ARGB;
                fillColor = COLOR_BLUE_ARGB;
                break;
        }

        polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);
    }

    public void locationReportsRequest(double lat, double lng, String location, String token, String cursor) {

        RequestsVolley.locationReportsRequest(lat, lng, location, token, cursor, mContext, MapActivity.this);
    }

    public void votesRequest(String username, String cursor) {
        RequestsVolley.votesRequest(username, cursor, mContext, MapActivity.this);
    }

    public void setUserVotes(JSONArray jsonArray) {

        try {
            JSONArray jsonarray = jsonArray;

            for (int i = 0; i < jsonarray.length(); i++) {

                JSONObject jsonobject = jsonarray.getJSONObject(i);

                if (jsonobject.has("report")) {

                    String idReport = jsonobject.getString("report");
                    String vote = jsonobject.getString("vote");

                    if (mReportMap.containsKey(idReport)) {
                        MarkerClass markerClass = mReportMap.get(idReport);
                        markerClass.setmVote(vote);
                    }
                    if (userMarkerMap.containsKey(idReport)) {
                        MarkerClass markerClass = userMarkerMap.get(idReport);
                        markerClass.setmVote(vote);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public BitmapDescriptor setMarkersColor(BitmapDescriptor markerDescriptor, String gravity) {

        if (gravity.equals("1"))
            markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        else if (gravity.equals("2"))
            markerDescriptor = BitmapDescriptorFactory.defaultMarker(80.0f);
        else if (gravity.equals("3"))
            markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        else if (gravity.equals("4"))
            markerDescriptor = BitmapDescriptorFactory.defaultMarker(35.0f);
        else if (gravity.equals("5"))
            markerDescriptor = BitmapDescriptorFactory.defaultMarker(3.0f);

        return markerDescriptor;
    }

    public void onSelectArea() {

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                vector.add(latLng);
            }
        });
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
            if (lineOptions == null)
                Toast.makeText(mContext, "Já se encontra no local!", Toast.LENGTH_LONG).show();
            else
                mMapPollyLine = mMap.addPolyline(lineOptions);
        }
    }

    public class OwnIconRendered extends DefaultClusterRenderer<MarkerClass> {

        IconGenerator mClusterIconGenerator;

        public OwnIconRendered(Context context, GoogleMap map,
                               ClusterManager<MarkerClass> clusterManager) {
            super(context, map, clusterManager);

            mClusterIconGenerator = new IconGenerator(context);
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerClass item, MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);

            if (!item.getmTitle().equals(""))
                markerOptions.title("Report: " + item.getmTitle());
            else
                markerOptions.title("Quick Report");

            //  markerOptions.snippet(item.getSnippet());
            BitmapDescriptor markerDescriptor = null;

            switch (item.getmGravity()) {
                case "1":
                    if (item.getmCreator_username().equals(mUsername)) {
                        if (item.getmStatus().equals("standby"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g1standbymine));
                        else if (item.getmStatus().equals("open"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g1openmine));
                        else if (item.getmStatus().equals("wip"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g1wipmine));
                        else if (item.getmStatus().equals("closed"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g1closedmine));
                    } else {
                        if (item.getmStatus().equals("standby"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g1standby));
                        else if (item.getmStatus().equals("open"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g1open));
                        else if (item.getmStatus().equals("wip"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g1wip));
                        else if (item.getmStatus().equals("closed"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g1closed));
                    }
                    break;
                case "2":
                    if (item.getmCreator_username().equals(mUsername)) {
                        if (item.getmStatus().equals("standby"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g2standbymine));
                        else if (item.getmStatus().equals("open"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g2openmine));
                        else if (item.getmStatus().equals("wip"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g2wipmine));
                        else if (item.getmStatus().equals("closed"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g2closedmine));
                    } else {
                        if (item.getmStatus().equals("standby"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g2standby));
                        else if (item.getmStatus().equals("open"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g2open));
                        else if (item.getmStatus().equals("wip"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g2wip));
                        else if (item.getmStatus().equals("closed"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g2closed));
                    }
                    break;
                case "3":
                    if (item.getmCreator_username().equals(mUsername)) {
                        if (item.getmStatus().equals("standby"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g3standbymine));
                        else if (item.getmStatus().equals("open"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g3openmine));
                        else if (item.getmStatus().equals("wip"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g3wipmine));
                        else if (item.getmStatus().equals("closed"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g3closedmine));
                    } else {
                        if (item.getmStatus().equals("standby"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g3standby));
                        else if (item.getmStatus().equals("open"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g3open));
                        else if (item.getmStatus().equals("wip"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g3wip));
                        else if (item.getmStatus().equals("closed"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g3closed));
                    }
                    break;
                case "4":
                    if (item.getmCreator_username().equals(mUsername)) {
                        if (item.getmStatus().equals("standby"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g4standbymine));
                        else if (item.getmStatus().equals("open"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g4openmine));
                        else if (item.getmStatus().equals("wip"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g4wipmine));
                        else if (item.getmStatus().equals("closed"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g4closedmine));
                    } else {
                        if (item.getmStatus().equals("standby"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g4standby));
                        else if (item.getmStatus().equals("open"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g4open));
                        else if (item.getmStatus().equals("wip"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g4wip));
                        else if (item.getmStatus().equals("closed"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g4closed));
                    }
                    break;
                case "5":
                    if (item.getmCreator_username().equals(mUsername)) {
                        if (item.getmStatus().equals("standby"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g5standbymine));
                        else if (item.getmStatus().equals("open"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g5openmine));
                        else if (item.getmStatus().equals("wip"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g5wipmine));
                        else if (item.getmStatus().equals("closed"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g5closedmine));
                    } else {
                        if (item.getmStatus().equals("standby"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g5standby));
                        else if (item.getmStatus().equals("open"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g5open));
                        else if (item.getmStatus().equals("wip"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g5wip));
                        else if (item.getmStatus().equals("closed"))
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g5closed));
                    }
            }
        }

        @Override
        protected int getColor(int clusterSize) {
            return Color.parseColor("#AD363B");
        }

    }

    public class OwnIconRenderedWorker extends DefaultClusterRenderer<TaskClass> {

        public OwnIconRenderedWorker(Context context, GoogleMap map,
                                     ClusterManager<TaskClass> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(TaskClass item, MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);

            markerOptions.title("Marker " + item.getPosition());
            markerOptions.snippet(item.getSnippet());
            BitmapDescriptor markerDescriptor = null;

            switch (item.getmGravity()) {
                case "1":
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g1wip));
                    break;
                case "2":
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g2wip));
                    break;
                case "3":
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g3wip));
                    break;
                case "4":
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g4wip));
                    break;
                case "5":
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.g5wip));

            }
        }

        @Override
        protected int getColor(int clusterSize) {
            return Color.parseColor("#2F975F");
        }
    }
}