package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {

    private ArrayList<RowClass> arrayList;
    private ListView listview;
    private Context mContext;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mMenu;
    private LinearLayout mLoggoutButton, mFeedButton, mSettingsButton, mMapButton,
            mContactsButton, mProfileButton, mUserPlaceLayout;
    private SharedPreferences sharedPref;
    private String mUsername, mToken;
    private TextView mUsernameBoard, mUserPlaceBoard, mUserPointsBoard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        mContext = this;
        arrayList = new ArrayList<>();
        listview = (ListView) findViewById(R.id.listview_leaderboard);

        sharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);
        mUsername = sharedPref.getString("username", "ERROR");
        mToken = sharedPref.getString("token", "");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_leaderboard);

        setSupportActionBar(myToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_leaderboard);

        mMenu = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ignesred);

        menuButtons();

        mUserPlaceLayout = findViewById(R.id.user_place_layout);
        mUsernameBoard = findViewById(R.id.user_username);
        mUserPlaceBoard = findViewById(R.id.user_position);
        mUserPointsBoard = findViewById(R.id.user_points);

        RequestsVolley.leaderboardRequest(mContext, this);
    }

    private void menuButtons() {
        mLoggoutButton = (LinearLayout) findViewById(R.id.botao_logout);
        mLoggoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestsVolley.logoutRequest(mToken, mContext, LeaderboardActivity.this, 0);
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
                startActivity(new Intent(LeaderboardActivity.this, FeedActivity.class));
                finish();
            }
        });

        mSettingsButton = (LinearLayout) findViewById(R.id.botao_settings);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LeaderboardActivity.this, SettingsActivity.class));
                finish();
            }
        });

        mProfileButton = (LinearLayout) findViewById(R.id.botao_profile);
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LeaderboardActivity.this, ProfileActivity.class));
                finish();
            }
        });

        mContactsButton = (LinearLayout) findViewById(R.id.botao_contacts);
        mContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LeaderboardActivity.this, ContactsActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem item0 = menu.findItem(R.id.username);
        item0.setVisible(false);
        MenuItem item1 = menu.findItem(R.id.refreshicon);
        item1.setVisible(false);
        MenuItem item2 = menu.findItem(R.id.searchicon);
        item2.setVisible(false);
        MenuItem item3 = menu.findItem(R.id.reporticon);
        item3.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMenu.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    public void setLeaderboard(JSONArray jsonArray) {
        RowClass rowClass;
        arrayList.clear();
        try {
            JSONArray jsonarray = jsonArray;

            for (int i = 0; i < jsonarray.length(); i++) {

                JSONObject jsonobject = jsonarray.getJSONObject(i);

                String points = jsonobject.getString("points");
                String username = jsonobject.getString("username");
                String position = jsonobject.getString("place");

                rowClass = new RowClass(points, username, position);

                System.out.println("USER POSITION" + position);

                if(Integer.parseInt(position) == 0){
                    mUserPlaceLayout.setVisibility(View.VISIBLE);
                    mUserPointsBoard.setText(points);
                    mUserPlaceBoard.setText(position);
                    mUsernameBoard.setText(username);
                }

                arrayList.add(rowClass);
                listview.setAdapter(new LeaderboardActivity.MyAdapter(mContext, arrayList));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class MyAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<RowClass> row;

        public MyAdapter(Context context, ArrayList<RowClass> row) {
            this.context = context;
            this.row = row;
        }

        @Override
        public int getCount() {
            return row.size();
        }

        @Override
        public Object getItem(int position) {
            return row.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = (View) inflater.inflate(
                        R.layout.layout_leaderboard, null);
            }

            TextView textpoints = (TextView) convertView.findViewById(R.id.leaderboard_points);
            TextView textusername = (TextView) convertView.findViewById(R.id.leaderboard_username);
            TextView textposition = (TextView) convertView.findViewById(R.id.leaderboard_position);

            textpoints.setText(row.get(position).getPoints());
            textusername.setText(row.get(position).getUsername());
            textposition.setText(row.get(position).getPosition());

            return convertView;
        }
    }

    private class RowClass {
        private String points;
        private String username;
        private String position;

        public RowClass(String points, String username, String position) {
            this.points = points;
            this.username = username;
            this.position = position;
        }

        public String getPoints() {
            return points;
        }

        public String getUsername() {
            return username;
        }

        public String getPosition() {
            return position;
        }
    }
}
