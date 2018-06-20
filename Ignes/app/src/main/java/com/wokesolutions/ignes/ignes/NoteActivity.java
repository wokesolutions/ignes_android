package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoteActivity extends Activity {

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

        mContext = this;

        Intent intent = getIntent();
        Gson gson = new Gson();
        String json = intent.getExtras().getString("TaskClass");
        mTask = gson.fromJson(json, TaskClass.class);

        mBackBool = false;
       // mNoteitem = findViewById(R.id.worker_note_item);
        mAddNote_button = findViewById(R.id.note_add);
        mSharedPref = getSharedPreferences("Shared", Context.MODE_PRIVATE);
        mUsername = mSharedPref.getString("username", "ERROR");
        mTaskTitle = findViewById(R.id.note_task);

        if(!mTask.getmTitle().equals(""))
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
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_2, list);
        listview.setAdapter(adapter);

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


                              /*  list.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1)*/
                                ;


                            }
                        });
            }

        });

        mAddNote_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setTitle("New Note");
                mBuilder.setIcon(R.drawable.addnoteicon);

                LayoutInflater inflater = NoteActivity.this.getLayoutInflater();
                final View mView = inflater.inflate(R.layout.worker_new_note, null);
                mBuilder.setView(mView);
                final AlertDialog alert = mBuilder.create();

                alert.show();

                Button done_button = mView.findViewById(R.id.note_done_button);
                final String newNote = mView.findViewById(R.id.note_new_text).toString();

                done_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestsVolley.addNoteRequest (newNote, mTask.getmId(), mContext);
                    }
                });
            }
        });


    }

    public void onBackPressed() {

        if (mBackBool)
            recreate();
        else
            finish();
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}