package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MarkerActivity extends AppCompatActivity {

    public static EditText marker_comment;
    private static String markerID;
    private ImageView marker_image, marker_status_image;
    private TextView marker_title, marker_description, marker_address, marker_username, marker_date,
            marker_gravity, marker_status, marker_likes, marker_dislikes, marker_comments_number, marker_gravity_title;
    private TextView comment_owner, comment_date, comment_text;
    private Button marker_button_likes, marker_button_dislikes, marker_button_post_comment;
    private ProgressBar mProgressBar;
    private MarkerClass mMarker;
    private int mLikes, mDislikes;
    private boolean mTouchLike, mTouchDislike;
    private LinearLayout mListCommentsLayout, mNumbCommentsLayout;
    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_complete);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_marker);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ignesred);

        mContext = this;
        mProgressBar = findViewById(R.id.marker_progress_likes);
        mListCommentsLayout = findViewById(R.id.list_comments_layout);
        mNumbCommentsLayout = findViewById(R.id.comments_layout);

        marker_button_likes = findViewById(R.id.likes_button);
        marker_button_dislikes = findViewById(R.id.dislikes_button);
        marker_button_post_comment = findViewById(R.id.marker_comment_post_button);
        marker_status_image = findViewById(R.id.marker_lock_img);
        marker_image = findViewById(R.id.marker_image);
        marker_title = findViewById(R.id.marker_title);
        marker_description = findViewById(R.id.marker_description);
        marker_address = findViewById(R.id.marker_address);
        marker_date = findViewById(R.id.marker_date);
        marker_username = findViewById(R.id.marker_reporter_username);
        marker_gravity = findViewById(R.id.marker_gravity);
        marker_status = findViewById(R.id.marker_status);
        marker_likes = findViewById(R.id.marker_likes_number);
        marker_dislikes = findViewById(R.id.marker_dislikes_number);
        marker_comments_number = findViewById(R.id.marker_comments_number);
        marker_gravity_title = findViewById(R.id.marker_gravity_title);
        marker_comment = findViewById(R.id.marker_comment);
        marker_comment.setFocusable(false);
        marker_comment.setFocusableInTouchMode(true);

        Intent intent = getIntent();

        markerID = intent.getExtras().getString("MarkerClass");
        final boolean isProfile = intent.getExtras().getBoolean("IsProfile");

        if (isProfile)
            mMarker = ProfileActivity.markerMap.get(markerID);
        else
            mMarker = MapActivity.mReportMap.get(markerID);

        Log.e("MAPPPPAAA ", mMarker + "     " + MapActivity.mReportMap.get(markerID));

        marker_image.setImageBitmap(mMarker.getmImg_bitmap());

        String title = mMarker.getmTitle();
        if (!title.equals(""))
            marker_title.setText(title);
        else
            marker_title.setVisibility(View.GONE);

        String description = mMarker.getmDescription();
        if (!description.equals(""))
            marker_description.setText(description);
        else
            marker_description.setVisibility(View.GONE);

        marker_address.setText(mMarker.getmAddress());
        marker_date.setText(mMarker.getmDate());
        marker_username.setText(mMarker.getmCreator_username());

        String gravity = mMarker.getmGravity();

        if (!gravity.equals("0"))
            marker_gravity.setText(gravity);
        else {
            marker_gravity.setVisibility(View.GONE);
            marker_gravity_title.setVisibility(View.GONE);
        }

        String status = mMarker.getmStatus();
        marker_status.setText(status);

        if (status.equals("CLOSE"))
            marker_status_image.setImageResource(R.drawable.lockclose);
        if (status.equals("OPEN"))
            marker_status_image.setImageResource(R.drawable.lockopen);

        marker_likes.setText(mMarker.getmLikes());
        marker_dislikes.setText(mMarker.getmDislikes());

        mLikes = Integer.parseInt(mMarker.getmLikes());
        mDislikes = Integer.parseInt(mMarker.getmDislikes());

        int totalProgress = mLikes + mDislikes;

        if (totalProgress == 0)
            mProgressBar.setMax(1);
        else
            mProgressBar.setMax(totalProgress);

        mProgressBar.setProgress(mLikes);

        if (mMarker.getmVote().equals("up"))
            mTouchLike = true;

        if (mMarker.getmVote().equals("down"))
            mTouchDislike = true;

        Log.e("LIKES DISLIKES HERE-> ", mMarker.getmLikes() + "    " + mMarker.getmDislikes());

        if (mTouchLike)
            marker_button_likes.setBackgroundResource(R.drawable.upicongrey);
        else
            marker_button_likes.setBackgroundResource(R.drawable.upicon);

        if (mTouchDislike)
            marker_button_dislikes.setBackgroundResource(R.drawable.downicongrey);
        else
            marker_button_dislikes.setBackgroundResource(R.drawable.downicon);

        RequestsVolley.reportCommentsRequest(markerID, "", mContext, this);

        mNumbCommentsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListCommentsLayout.setVisibility(View.VISIBLE);
            }
        });
        setPostComment(markerID);

        setButtonLikes();

        setButtonDislikes();
    }

    private void setButtonDislikes() {

        marker_button_dislikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTouchDislike) {
                    mDislikes--;
                    mMarker.setmVote("neutro");
                    FeedActivity.votesMap.put(markerID, "neutral");
                    marker_button_dislikes.setBackgroundResource(R.drawable.downicon);

                } else {
                    mDislikes++;
                    if (mTouchLike) {
                        mLikes--;
                        marker_button_likes.setBackgroundResource(R.drawable.upicon);
                        mTouchLike = false;
                    }
                    mMarker.setmVote("down");
                    FeedActivity.votesMap.put(markerID, "down");
                    marker_button_dislikes.setBackgroundResource(R.drawable.downicongrey);
                }

                mProgressBar.setMax(mLikes + mDislikes);
                mProgressBar.setProgress(mLikes);
                marker_likes.setText("" + mLikes);
                marker_dislikes.setText("" + mDislikes);
                mMarker.setmLikes(String.valueOf(mLikes));
                mMarker.setmDislikes(String.valueOf(mDislikes));

                mTouchDislike = !mTouchDislike;
            }
        });


    }

    private void setButtonLikes() {
        marker_button_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTouchLike) {
                    mLikes--;
                    mMarker.setmVote("neutro");
                    FeedActivity.votesMap.put(markerID, "neutral");
                    marker_button_likes.setBackgroundResource(R.drawable.upicon);

                } else {
                    mLikes++;
                    if (mTouchDislike) {
                        mDislikes--;
                        marker_button_dislikes.setBackgroundResource(R.drawable.downicon);
                        mTouchDislike = false;
                    }
                    mMarker.setmVote("up");
                    FeedActivity.votesMap.put(markerID, "up");
                    marker_button_likes.setBackgroundResource(R.drawable.upicongrey);
                }
                mProgressBar.setMax(mLikes + mDislikes);
                mProgressBar.setProgress(mLikes);
                marker_dislikes.setText("" + mDislikes);
                marker_likes.setText("" + mLikes);
                mMarker.setmDislikes(String.valueOf(mDislikes));
                mMarker.setmLikes(String.valueOf(mLikes));
                Log.e("LIKES DISLIKES HERE-> ", mMarker.getmLikes() + "    " + mMarker.getmDislikes());

                mTouchLike = !mTouchLike;

            }
        });
    }

    private void setPostComment(final String id) {

        marker_button_post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("COMMMMENNNNTTT" + marker_comment.getText().toString());
                String text = marker_comment.getText().toString();

                if (!text.equals("")) {
                    RequestsVolley.postCommentRequest(id, text, mContext, MarkerActivity.this);
                } else
                    Toast.makeText(mContext, "Empty comment", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setListComments(JSONArray comments) {

        ArrayList<CommentClass> arrayList = new ArrayList<CommentClass>();
        ListView listview = (ListView) findViewById(R.id.listview_comments);
        CommentClass commentClass;

        try {

            JSONArray jsonarray = comments;

            for (int i = 0; i < jsonarray.length(); i++) {

                JSONObject jsonobject = jsonarray.getJSONObject(i);

                String commentID = jsonobject.getString("ReportComment");

                String textComment = jsonobject.getString("reportcomment_text");
                String ownerComment = jsonobject.getString("reportcomment_user");
                String dateComment = jsonobject.getString("reportcomment_time");

                commentClass = new CommentClass(commentID, dateComment, ownerComment, textComment);

                arrayList.add(commentClass);
            }

            listview.setAdapter(new MarkerActivity.MyAdapter(mContext, arrayList));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //array ReportComment - id do comment
    // reportcomment_text , user, time
    // user_profpictn

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class MyAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<CommentClass> comments;

        public MyAdapter(Context context, ArrayList<CommentClass> comments) {
            this.context = context;
            this.comments = comments;
        }

        @Override
        public int getCount() {

            marker_comments_number.setText("" + comments.size());
            return comments.size();

        }

        @Override
        public Object getItem(int position) {
            return comments.get(position);
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
                        R.layout.activity_comment_item, null);
            }

            comment_owner = convertView.findViewById(R.id.comment_ownername);
            comment_date = convertView.findViewById(R.id.comment_date);
            comment_text = convertView.findViewById(R.id.comment_text);

            comment_text.setText(comments.get(position).mText);
            comment_date.setText(comments.get(position).mDate);
            comment_owner.setText(comments.get(position).mOwner);

            return convertView;
        }


    }
}
