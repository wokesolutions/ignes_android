package com.wokesolutions.ignes.ignes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v4.view.GravityCompat;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int REPORT_ACTIVITY = 1;
    public static final int GPS_ACTIVITY = 2;
    public static ArrayList<MarkerClass> mReportList;
    private String SERVER_ERROR = "java.io.IOException: HTTP error code: 500";
    private String NO_CONTENT_ERROR = "java.io.IOException: HTTP error code: 204";
    private String NOT_FOUND_ERROR = "java.io.IOException: HTTP error code: 204";
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    // Declare a variable for the cluster manager.
    private ClusterManager<MarkerClass> mClusterManager;
    private MapTask mMapTask = null;
    private Geocoder mCoder;
    private LocationManager mManager;

    private boolean mGps;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mMenu;
    private Button mLoggoutButton;
    private Button mFeedButton;

    private Button mReport;
    private Button mFilter;
    private Location mCurrentLocation;
    private ImageView mImage;
    private Context context;
    private FeedActivity feedActivity;

    private boolean ola;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        String languageToLoad = "pt_PT";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);

        mCoder = new Geocoder(this, Locale.getDefault());

        mCurrentLocation = null;
        mReportList = new ArrayList<>();
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
        mFilter = (Button) findViewById(R.id.searchicon);

        /*----- About Google Maps -----*/
        checkLocationPermission();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mGps = mManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        /*----------------------------------------------------------------------------------*/
    }

    /*----- About Google Maps -----*/

    private void setUpCluster(LatLng latLng) {
        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        // System.out.print("ahdgfhjsgdzfjhsgdhfdjs" + latLng);

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MarkerClass>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        System.out.println("TAMANHO DA LISTA" + mReportList.size());

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < mReportList.size(); i++) {
            System.out.println("LISTA NA POSICAO " + i + "-->" + mReportList.get(i).getPosition());
            mClusterManager.addItem(mReportList.get(i));
            mClusterManager.setRenderer(new OwnIconRendered(context, mMap, mClusterManager));
            // feedActivity.addMarker(mReportList.get(i));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("ON RESUMEE!");
        if (mCurrentLocation != null) {
            /*mMapTask = new MapTask(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 10000);
            mMapTask.execute((Void) null);*/
            setMarkers(readFromFile(context), mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void setMarkers(String markers, double lat, double lng) {
        try {
            ArrayList<MarkerClass> temp = new ArrayList<>();

            JSONArray jsonarray = new JSONArray(markers);

            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                double latitude = Double.parseDouble(jsonobject.getString("report_lat"));
                double longitude = Double.parseDouble(jsonobject.getString("report_lng"));
                String status = jsonobject.getString("report_status");
                String address = jsonobject.getString("report_address");
                String date = jsonobject.getString("report_creationtimeformatted");

                byte[] img_byte = Base64.decode(jsonobject.getString("thumbnail"), Base64.DEFAULT);

                //String name = "";
                //if (jsonobject.getString("report_username") != null)
                String name = jsonobject.getString("report_username");
                int gravity = 0;
                if (jsonobject.has("report_gravity"))
                    gravity = jsonobject.getInt("report_gravity");
                String description = "";
                if (jsonobject.has("report_description"))
                    description = jsonobject.getString("report_description");
                String title = "";
                if (jsonobject.has("report_title"))
                    title = jsonobject.getString("report_title");


                MarkerClass report = new MarkerClass(latitude, longitude, status, address, date, name, description, gravity, title, img_byte);

                if (!temp.contains(report))
                    temp.add(report);
            }
            mReportList = temp;

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
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_ACTIVITY);
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
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("ignes_markers", Context.MODE_PRIVATE));
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

        System.out.println("DENTRO DO FICHEIRO: " + ret);
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
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                        mMapTask = new MapTask(loc.latitude, loc.longitude, 10000);
                        mMapTask.execute((Void) null);
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
                        return true;
                    }
                });
            }
        }
    }

    /*----- About Menu Bar -----*/
    private void menuButtons() {
        mLoggoutButton = (Button) findViewById(R.id.botao_logout);
        mLoggoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this, LogoutActivity.class));
                finish();
            }
        });

        mFeedButton = (Button) findViewById(R.id.menu_button_feed);
        mFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this, FeedActivity.class));
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
        if (item.getItemId() == R.id.searchicon)
            filterTask();
        if (item.getItemId() == R.id.refreshicon)
            recreate();


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
                    //startActivity(i);
                    startActivityForResult(i, REPORT_ACTIVITY);

                }
            });
        } else {
            Toast.makeText(context, "You should enable your gps to do report something", Toast.LENGTH_LONG).show();

        }
        //----LONG-----
        Button mLong = (Button) mView.findViewById(R.id.report_long_button);
        mLong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reportType.putString("Type", "long");
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
                System.out.println("VOLTEI DO REPORT");
                if (resultCode == RESULT_OK) {

                    recreate();
                    System.out.println("DEU OK");


                } else
                    System.out.println("DEU CANCELLED");
                break;
            case GPS_ACTIVITY:

                /*final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Refresh");
                builder.setIcon(R.drawable.refresh);
                builder.setMessage("Refresh the map to show the markers in your area")
                        .setCancelable(false)
                        .setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                recreate();

                            }
                        });

                final AlertDialog alert = builder.create();
                alert.show();*/


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Refreshing...");
                builder.setIcon(R.drawable.refresh);
                builder.setMessage("");


                builder.setCancelable(true);

                final AlertDialog dlg = builder.create();

                dlg.show();

                final Timer t = new Timer();
                t.schedule(new TimerTask() {
                    public void run() {
                        dlg.dismiss(); // when the task active then close the dialog
                        t.cancel();// also just top the timer thread, otherwise, you may receive a crash report
                    }
                }, 3000); // after 3 second (or 3000 miliseconds), the task will be active.
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
                alert.dismiss();

                try {
                    List<Address> addresses = mCoder.getFromLocationName(address, 1);
                    if (addresses.size() > 0) {
                        double lat = addresses.get(0).getLatitude();
                        double lng = addresses.get(0).getLongitude();
                        mMapTask = new MapTask(lat, lng, 10000);
                        mMapTask.execute((Void) null);
                    } else
                        Toast.makeText(context, "Can't find location, please try a more detailed one", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    class OwnIconRendered extends DefaultClusterRenderer<MarkerClass> {

        public OwnIconRendered(Context context, GoogleMap map,
                               ClusterManager<MarkerClass> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerClass item, MarkerOptions markerOptions) {
            //  markerOptions.icon(item.getIcon());
            // markerOptions.snippet(item.getSnippet());
            markerOptions.title("Marker " + item.getPosition());
            markerOptions.snippet(item.getSnippet());

            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }

    public class MapTask extends AsyncTask<Void, Void, String> {

        double mLat;
        double mLng;
        int mRadius;
        String mLocality;

        MapTask(double lat, double lng, int radius) {

            try {
                List<Address> addresses = mCoder.getFromLocation(lat, lng, 1);
                mLocality = addresses.get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }

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

                URL url = new URL("https://hardy-scarab-200218.appspot.com/api/report/getinlocation?"
                        + "location=" + mLocality);

                String s = RequestsREST.doGET(url, null);

                return s;


            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            mMapTask = null;

            System.out.println("RESPOSTA DO POSTEXECUTE " + result);


            if (result.equals(SERVER_ERROR)) {

                Toast.makeText(context, "Can't connect to server", Toast.LENGTH_LONG).show();
                System.out.println("SERVER ERROR");

            } else if (result.equals(NO_CONTENT_ERROR)) {

                Toast.makeText(context, "No reports to show in this area", Toast.LENGTH_LONG).show();
                System.out.println("NADA A MOSTRAR NA ZONA");

            } else if (result.equals(NOT_FOUND_ERROR)) {

                Toast.makeText(context, "Can't connect to server", Toast.LENGTH_LONG).show();
                System.out.println("NOT FOUND ERROR");

            } else {

                setMarkers(result, mLat, mLng);
                writeToFile(result, context);

            }


        }
    }


}