package com.wokesolutions.ignes.ignes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity {

    ArrayList<MarkerClass> markerList;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sidefeed);

        recyclerView = (RecyclerView) findViewById(R.id.feed_recyclerview);

        LinearLayoutManager manager = new LinearLayoutManager(this);

        RecyclerView.LayoutManager rvLayoutManager = manager;

        recyclerView.setLayoutManager(manager);

        markerList = MapActivity.mReportList;

        MarkerAdapter markerAdapter = new MarkerAdapter(this, markerList);

        recyclerView.setAdapter(markerAdapter);


    }
}
