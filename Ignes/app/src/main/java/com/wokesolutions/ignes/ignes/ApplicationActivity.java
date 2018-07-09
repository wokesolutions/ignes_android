package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

public class ApplicationActivity extends AppCompatActivity {

    private Context mContext;
    private ArrayList<ApplicationClass> mApplications;
    private ListView listview;
    private boolean isRecreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applications);

        mContext = this;
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_applications);
        setToolbar(myToolbar);

        isRecreate = false;

        mApplications = ProfileActivity.mApplicationsArray;

        listview = findViewById(R.id.listview);

        listview.setAdapter(new MyAdapter(mContext, mApplications));
    }

    public void onBackPressed() {
        if (isRecreate)
            recreate();
        else
            finish();
    }

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ignesred);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class MyAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<ApplicationClass> applications;

        public MyAdapter(Context context, ArrayList<ApplicationClass> applications) {
            this.context = context;
            this.applications = applications;
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
            MarkerClass markerClass = MapActivity.mReportMap.get(applications.get(position).getmReportId());
            TextView orgname = convertView.findViewById(R.id.org_name);
            TextView reportdate = convertView.findViewById(R.id.report_date);
            TextView reportitle = convertView.findViewById(R.id.report_title);

            orgname.setText(applications.get(position).getmNameOrg());
            reportdate.setText(markerClass.getmDMY());
            reportitle.setText(markerClass.getmTitle());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContentView(R.layout.activity_application_complete);

                    final ApplicationClass applicationClass = applications.get(position);
                    final MarkerClass markerClass = MapActivity.mReportMap.get(applicationClass.getmReportId());

                    Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_app_complete);
                    setSupportActionBar(myToolbar);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setIcon(R.drawable.ignesred);
                    isRecreate = true;

                    TextView orgname = findViewById(R.id.application_orgname);
                    TextView markername = findViewById(R.id.marker_name);
                    TextView orgnif = findViewById(R.id.org_nif);
                    TextView orgemail = findViewById(R.id.org_email);
                    TextView orginfo = findViewById(R.id.org_information);
                    TextView orgbudget = findViewById(R.id.org_budget);
                    Button applicationYes = findViewById(R.id.application_yes);

                    orgname.setText(applicationClass.getmNameOrg());
                    markername.setText(markerClass.getmTitle());
                    orgnif.setText(applicationClass.getmNIFOrg());
                    orgemail.setText(applicationClass.getmEmailOrg());
                    orginfo.setText(applicationClass.getmInfo());
                    orgbudget.setText(applicationClass.getmBudget());

                    applicationYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RequestsVolley.reportAcceptApplicationRequest(markerClass.getmId(), applicationClass.getmNIFOrg(), ApplicationActivity.this,  mContext);
                        }
                    });
                }
            });

            return convertView;
        }

    }

}
