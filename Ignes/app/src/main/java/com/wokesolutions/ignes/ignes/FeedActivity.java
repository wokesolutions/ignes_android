package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class FeedActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Map<String, MarkerClass> markerMap;
    Map<String, TaskClass> taskMap;

    private RecyclerView recyclerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mMenu;
    private LinearLayout mLoggoutButton, mMapButton;
    private TextView mAlertMessage, mOrganization, mUsername;
    private LinearLayout mProfileButton, mSettingsButton;
    private Context mContext;
    private Location mCurrentLocation;
    private SharedPreferences sharedPref;
    private String mRole, mToken;
    private MarkerAdapter markerAdapter;
    private TaskAdapter taskAdapter;
    private Spinner address_spinner;
    private ArrayList<String> localities_array;
    private Geocoder mGeocoder;


    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);

        sharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);
        mRole = sharedPref.getString("userRole", "");
        mToken = sharedPref.getString("token", "");
        mContext = this;
        mGeocoder = new Geocoder(this, Locale.getDefault());

        if (mRole.equals("USER"))
            setContentView(R.layout.activity_feed);
        else if (mRole.equals("WORKER")) {
            setTheme(R.style.WorkerTheme);
            setContentView(R.layout.worker_tasks);
        }

        MapActivity.votesMap.clear();

        recyclerView = (RecyclerView) findViewById(R.id.feed_recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        recyclerView.setNestedScrollingEnabled(false);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_feed);
        setSupportActionBar(myToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_feed);

        mMenu = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        general_menuButtons();

        if (mRole.equals("WORKER")) {
            taskMap = MapActivity.mWorkerTaskMap;
            taskAdapter = new TaskAdapter(this, taskMap);
            recyclerView.setAdapter(taskAdapter);
            getSupportActionBar().setIcon(R.drawable.ignesworkergreen);

            mUsername = findViewById(R.id.feed_worker);
            mUsername.setText(MapActivity.mUsername);

            mOrganization = findViewById(R.id.feed_org);
            mOrganization.setText(sharedPref.getString("org_name", ""));

            if (taskMap.isEmpty()) {
                TextView zero_tasks = findViewById(R.id.feed_worker_no_tasks);
                zero_tasks.setVisibility(View.VISIBLE);
            }

        } else if (mRole.equals("USER")) {
            if (MapActivity.isSearch)
                markerMap = MapActivity.mSearchMap;
            else
                markerMap = MapActivity.mReportMap;

            address_spinner = findViewById(R.id.feed_address_spinner);
            address_spinner.setOnItemSelectedListener(this);
            localities_array = new ArrayList<>();
            markerAdapter = new MarkerAdapter(this, markerMap, false);
            recyclerView.setAdapter(markerAdapter);
            getSupportActionBar().setIcon(R.drawable.ignesred);

            user_menuButtons();
            mAlertMessage = findViewById(R.id.alert);

            if (markerMap.isEmpty())
                mAlertMessage.setText(R.string.no_reports_to_list_area);
            else {
                String firstKey = (String) markerMap.keySet().iterator().next();
                mCurrentLocation = MapActivity.mCurrentLocation;
                localities_array.add(markerMap.get(firstKey).getmLocality());
            }
            RequestsVolley.userLocalitiesRequest(mContext, this);
        }
    }

    private void general_menuButtons() {
        mLoggoutButton = (LinearLayout) findViewById(R.id.botao_logout);
        mLoggoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestsVolley.logoutRequest(mToken, mContext, FeedActivity.this, 0);
            }
        });

        mMapButton = (LinearLayout) findViewById(R.id.menu_button_map);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSettingsButton = (LinearLayout) findViewById(R.id.botao_settings);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(FeedActivity.this, SettingsActivity.class));
                finish();
            }
        });
    }

    private void user_menuButtons() {

        mProfileButton = (LinearLayout) findViewById(R.id.botao_profile);
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeedActivity.this, ProfileActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (mRole.equals("USER")) {
            inflater.inflate(R.menu.menu, menu);

            MenuItem item2 = menu.findItem(R.id.searchicon);
            item2.setVisible(false);
        } else if (mRole.equals("WORKER")) {
            inflater.inflate(R.menu.worker_menu, menu);
        }

        MenuItem item1 = menu.findItem(R.id.username);
        item1.setVisible(false);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!MapActivity.votesMap.isEmpty())
            try {

                JSONObject json = new JSONObject();

                for (String key : MapActivity.votesMap.keySet()) {
                    json.put(key, MapActivity.votesMap.get(key));
                }

                RequestsVolley.sendAllVotesRequest(json, mToken, mContext);
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMenu.onOptionsItemSelected(item))
            return true;

        if (item.getItemId() == R.id.refreshicon) {
            recreate();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setListLocalities(JSONArray comments) {
        try {
            JSONArray jsonarray = comments;

            for (int i = 0; i < jsonarray.length(); i++) {
                localities_array.add(jsonarray.getString(i));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, localities_array);
            address_spinner.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (position != 0) {
            try {
                List<Address> addresses = mGeocoder.getFromLocationName(localities_array.get(position), 1);
                double lat = addresses.get(0).getLatitude();
                double lng = addresses.get(0).getLongitude();
                System.out.println("ON ITEM SELECTED: " + addresses.get(0));

                //MapActivity.locationReportsRequest(lat, lng, localities_array.get(position), mToken, "");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
