package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {

    private boolean mBackBool;
    private LinearLayout mNoteitem;
    private Button mAddNote_button;
    private String mUsername;
    private SharedPreferences mSharedPref;
    private TaskClass mTask;
    private Context mContext;
    private TextView mTaskTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worker_notes);

        mBackBool = false;


        mContext = this;

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_notes);
        setToolbar(myToolbar);

        Intent intent = getIntent();

        String taskID = intent.getExtras().getString("TaskClass");
        mTask = MapActivity.mWorkerTaskMap.get(taskID);

        // mNoteitem = findViewById(R.id.worker_note_item);
        mAddNote_button = findViewById(R.id.note_add);
        mSharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);
        mUsername = mSharedPref.getString("username", "ERROR");
        mTaskTitle = findViewById(R.id.note_task);

        if (!mTask.getmTitle().equals(""))
            mTaskTitle.setText(mTask.getmTitle());
        else
            mTaskTitle.setVisibility(View.GONE);


        final ListView listview = (ListView) findViewById(R.id.listview);
        String[] values = new String[]{"Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile"};

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }
      /*  final StableArrayAdapter adapter = new StableArrayAdapter(this,
                worker_list_note_item, list);*/
        listview.setAdapter(new MyAdapter(this, list));

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                final String item = (String) parent.getItemAtPosition(position);

                view.animate().setDuration(200).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                mBackBool = true;
                                setContentView(R.layout.worker_note_item);
                                Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_note);
                                setToolbar(myToolbar);
                            }
                        });
            }
        });

        mAddNote_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackBool = true;
                setContentView(R.layout.worker_new_note);

                Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_new_note);
                setToolbar(myToolbar);

                Button done_button = findViewById(R.id.note_done_button);

                done_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final EditText newNoteBox = findViewById(R.id.note_new_text);
                        final String newNote = newNoteBox.getText().toString();

                        System.out.println("NOTA ENVIADA: " + newNote);
                        RequestsVolley.addNoteRequest(newNote, mTask.getmId(), mContext, NoteActivity.this);
                    }
                });
            }
        });
    }

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ignesworkergreen);
    }

    @Override
    public void onBackPressed() {

        if (mBackBool)
            recreate();
        else
            finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class MyAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<String> mobiles;

        public MyAdapter(Context context, ArrayList<String> mobiles) {
            this.context = context;
            this.mobiles = mobiles;
        }

        @Override
        public int getCount() {
            return mobiles.size();
        }

        @Override
        public Object getItem(int position) {
            return mobiles.get(position);
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
                        R.layout.worker_list_note_item, null);
            }

            TextView name = (TextView) convertView.findViewById(R.id.note_text);

            name.setText(mobiles.get(position));

            return convertView;
        }
    }
}