package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ApplicationActivity  extends AppCompatActivity {

    private Context mContext;
    private MarkerClass mMarker;
    private ArrayList<ApplicationClass> mArrayList;
    private ListView listview;
    private TextView mReportTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applications);

        mContext = this;
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_applications);
        setToolbar(myToolbar);

        Intent intent = getIntent();
        String reportId = intent.getExtras().getString("ReportId");
        mMarker = MapActivity.mReportMap.get(reportId);
        mArrayList = mMarker.getmArrayApplications();

        mReportTitle = findViewById(R.id.report_title);
        mReportTitle.setText(mMarker.getmTitle());

        listview = findViewById(R.id.listview);
        listview.setAdapter(new MyAdapter(mContext, mArrayList,reportId));
    }

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ignesred);
    }

    private class MyAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<ApplicationClass> applications;
        private String reportId;

        public MyAdapter(Context context, ArrayList<ApplicationClass> applications, String reportId) {
            this.context = context;
            this.applications = applications;
            this.reportId = reportId;
        }

        @Override
        public int getCount() {
            return applications.size();
        }

        @Override
        public Object getItem(int position) {
            return applications.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = (View) inflater.inflate(
                        R.layout.activity_feed_orgs_listitem, null);
            }

            TextView orgname = convertView.findViewById(R.id.org_name);
            orgname.setText(applications.get(position).getmNameOrg());
            Button accept = convertView.findViewById(R.id.org_accept);

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    RequestsVolley.reportAcceptApplicationRequest(applications.get(position).getmNIFOrg(), reportId, mContext);
                }
            });

            return convertView;
        }
    }
}
