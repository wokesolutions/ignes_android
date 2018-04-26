package com.wokesolutions.ignes.ignes;

import android.Manifest;
import android.app.Activity;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private String SERVER_ERROR = "java.io.IOException: HTTP error code: 500";
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;

    // Declare a variable for the cluster manager.
    private ClusterManager<MyItem> mClusterManager;

    private MapTask mMapTask = null;
    private Geocoder mCoder;

    private List<MyItem> mReportList;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mMenu;
    private Button mLoggout;
    private Button mReport;
    private Button mFilter;
    private Location mCurrentLocation;
    private ImageView mImage;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mCoder = new Geocoder(this);

        mCurrentLocation = null;
        mReportList = new LinkedList<MyItem>();
        context = this;

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(myToolbar);

        /*----- About Menu Bar -----*/
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_map);

        mMenu = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ignesred);


        menuButtons();
        /*---------------------------------------------------------------------------------*/
        mReport = (Button) findViewById(R.id.reporticon);
        mFilter = (Button) findViewById(R.id.filtericon);

        /*----- About Google Maps -----*/
        checkLocationPermission();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        /*----------------------------------------------------------------------------------*/
    }

    /*----- About Google Maps -----*/

    private void setUpCluster(LatLng latLng) {
        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
       // System.out.print("ahdgfhjsgdzfjhsgdhfdjs" + latLng);

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        System.out.println("TAMANHO DA LISTA" + mReportList.size());


        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < mReportList.size(); i++) {
            System.out.println("LISTA NA POSICAO " + i + "-->" + mReportList.get(i).mPosition);
            mClusterManager.addItem(mReportList.get(i));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("ON RESUMEE!");
        if(mCurrentLocation != null) {
            mMapTask = new MapTask(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 10000);
            mMapTask.execute((Void) null);
        }
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
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                        mMapTask = new MapTask(loc.latitude,loc.longitude, 10000);
                        mMapTask.execute((Void) null);
                    }
                }
            });
        }

    }

    /*----- About Menu Bar -----*/
    private void menuButtons() {
        mLoggout = (Button) findViewById(R.id.botao_logout);
        mLoggout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this, LogoutActivity.class));
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

        if (item.getItemId() == R.id.reporticon) {
            onReport();
            return true;
        }
        if (item.getItemId() == R.id.filtericon)
            filterTask();

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


        //----FAST-----
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
                startActivity(i);

            }
        });

        //----LONG-----
        Button mLong = (Button) mView.findViewById(R.id.report_long_button);
        mLong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reportType.putString("Type", "long");
                Intent i = new Intent(MapActivity.this, ReportFormActivity.class);
                i.putExtra("TYPE", "detailed");
                alert.dismiss();
                startActivity(i);

            }
        });
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

        //----SEARCH-----
        final EditText mSearchText = (EditText) mView.findViewById(R.id.search_location_text);

        Button mSearch = (Button) mView.findViewById(R.id.search_filters_button);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = mSearchText.getText().toString();
                System.out.println(address);

                try {
                    List<Address> addresses = mCoder.getFromLocationName(address, 1);
                    double lat = addresses.get(0).getLatitude();
                    double lng = addresses.get(0).getLongitude();
                    mMapTask = new MapTask(lat, lng, 10000);
                    mMapTask.execute((Void) null);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public class MyItem implements ClusterItem {
        private final LatLng mPosition;

        public MyItem(double lat, double lng) {
            mPosition = new LatLng(lat, lng);
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }
    }

    public class MapTask extends AsyncTask<Void, Void, String> {

        double mLat;
        double mLng;
        int mRadius;

        MapTask(double lat, double lng, int radius) {
            mLat = lat;
            mLng = lng;
            mRadius = radius;

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

                URL url = new URL("https://hardy-scarab-200218.appspot.com/api/report/getwithinradius?"
                        + "lat=" + mLat + "&" + "lng=" + mLng + "&" + "radius=" + mRadius);

                String s = RequestsREST.doGET(url, null);

                return s;


            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            mMapTask = null;

            System.out.println(result);

            try {
                List<MyItem> temp = new LinkedList<MyItem>();

                JSONArray jsonarray = new JSONArray(result);

                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    double lat = Double.parseDouble(jsonobject.getString("report_lat"));
                    double lgn = Double.parseDouble(jsonobject.getString("report_lng"));

                    System.out.println(lat + "sfsdfdsxdsf "+lgn);
                    MyItem report = new MyItem(lat, lgn);

                    if(!temp.contains(report))
                        temp.add(report);
                }
                mReportList = temp;

                setUpCluster(new LatLng(mLat,mLng));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (result.equals("OK")) {


                System.out.println("MAP LOADED");

            } else if (result.equals(SERVER_ERROR)) {
                System.out.println("ERRO NO MAP LOADED");
            }


        }
    }


}