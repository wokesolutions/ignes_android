package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;


public class FeedActivity extends AppCompatActivity {

    public static final int REPORT_ACTIVITY = 1;

    Map<String, MarkerClass> markerMap;
    Map<String, TaskClass> taskMap;


    private RecyclerView recyclerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mMenu;
    private LinearLayout mLoggoutButton;
    private LinearLayout mMapButton;

    private TextView mLocality;
    private TextView mOrganization;
    private TextView mUsername;

    private LinearLayout mProfileButton;
    private Context mContext;
    private LinearLayout mWorkRoomButton;
    private Location mCurrentLocation;
    private SharedPreferences sharedPref;
    private String mRole;

    private MarkerAdapter markerAdapter;
    private TaskAdapter taskAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);
        mRole = sharedPref.getString("userLevel", "");

        if (mRole.equals("USER"))
            setContentView(R.layout.activity_feed);
        else if (mRole.equals("WORKER")) {
            setTheme(R.style.WorkerTheme);
            setContentView(R.layout.worker_tasks);
        }


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


        if (mRole.equals("WORKER")) {
            taskMap = MapActivity.mWorkerTaskMap;
            taskAdapter = new TaskAdapter(this, taskMap);
            recyclerView.setAdapter(taskAdapter);
            getSupportActionBar().setIcon(R.drawable.ignesworkergreen);
            worker_menuButtons();

            mUsername = findViewById(R.id.feed_worker);
            mUsername.setText(MapActivity.mUsername);

            mOrganization = findViewById(R.id.feed_org);
            mOrganization.setText(sharedPref.getString("org_name", ""));

        } else if (mRole.equals("USER")) {
            markerMap = MapActivity.mReportMap;
            markerAdapter = new MarkerAdapter(this, markerMap);
            recyclerView.setAdapter(markerAdapter);
            getSupportActionBar().setIcon(R.drawable.ignesred);
            user_menuButtons();
            mLocality = findViewById(R.id.feed_address);

            if (markerMap.isEmpty())
                mLocality.setText("There are no reports to list in this area...");

            else {
                String firstKey = (String) markerMap.keySet().iterator().next();
                mLocality.setText(markerMap.get(firstKey).getmLocality());
                mCurrentLocation = MapActivity.mCurrentLocation;
            }
        }
        mContext = this;


    }


    private void user_menuButtons() {

        mLoggoutButton = (LinearLayout) findViewById(R.id.botao_logout);
        mLoggoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeedActivity.this, LogoutActivity.class));
                finish();
            }
        });

        mProfileButton = (LinearLayout) findViewById(R.id.botao_profile);
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeedActivity.this, ProfileActivity.class));
            }
        });

        mMapButton = (LinearLayout) findViewById(R.id.menu_button_map);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /*----- About Menu Bar -----*/
    private void worker_menuButtons() {

        mLoggoutButton = (LinearLayout) findViewById(R.id.botao_logout);
        mLoggoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeedActivity.this, LogoutActivity.class));
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

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMenu.onOptionsItemSelected(item))
            return true;

        if (item.getItemId() == R.id.refreshicon) {
            recreate();
           /* for (int i = 0; i < 4; i++)
                markerAdapter.notifyItemChanged(i);*/
        }

        if (item.getItemId() == R.id.reporticon) {
            onReport();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onReport() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Report");
        mBuilder.setIcon(R.drawable.ocorrenciared);

        LayoutInflater inflater = FeedActivity.this.getLayoutInflater();
        final View mView = inflater.inflate(R.layout.report_choice, null);
        mBuilder.setView(mView);
        final AlertDialog alert = mBuilder.create();

        alert.show();

        if (mCurrentLocation != null) {
            Button mFast = (Button) mView.findViewById(R.id.report_fast_button);
            mFast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(FeedActivity.this, ReportFormActivity.class);
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
                    Intent i = new Intent(FeedActivity.this, ReportFormActivity.class);
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
                Intent i = new Intent(FeedActivity.this, ReportFormActivity.class);
                i.putExtra("TYPE", "detailed");

                if (mCurrentLocation != null)
                    i.putExtra("LOCATION", mCurrentLocation);

                alert.dismiss();
                startActivity(i);
            }
        });
    }

}
