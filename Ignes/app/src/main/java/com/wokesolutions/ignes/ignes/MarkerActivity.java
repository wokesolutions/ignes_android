package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class MarkerActivity extends AppCompatActivity {

    public static EditText marker_comment;
    private static String markerID;
    public ImageView marker_image, marker_status_image;
    private TextView marker_title, marker_description, marker_address, marker_username, marker_date,
            marker_gravity, marker_status, marker_likes, marker_dislikes, marker_comments_number,
            marker_gravity_title, marker_category;
    private TextView comment_owner, comment_date, comment_text;
    private ImageView comment_ownerpic;
    private Button marker_button_likes, marker_button_dislikes, marker_button_post_comment;
    private ProgressBar mProgressBar;
    private MarkerClass mMarker, mSecondMarker;
    private int mLikes, mDislikes;
    private boolean mTouchLike, mTouchDislike;
    private LinearLayout mNumbCommentsLayout, mCommentPostLayout;
    private Context mContext;
    public ArrayList<CommentClass> arrayList;
    private ListView listview;
    private boolean isClicked;

    public static void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_complete);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_marker);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ignesred);

        mContext = this;
        isClicked = false;
        mProgressBar = findViewById(R.id.marker_progress_likes);
        // mListCommentsLayout = findViewById(R.id.list_comments_layout);
        mNumbCommentsLayout = findViewById(R.id.comments_layout);
        mCommentPostLayout = findViewById(R.id.comment_post_layout);

        marker_button_likes = findViewById(R.id.likes_button);
        marker_button_dislikes = findViewById(R.id.dislikes_button);
        marker_button_post_comment = findViewById(R.id.marker_comment_post_button);
      //  marker_status_image = findViewById(R.id.marker_lock_img);
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
        marker_category = findViewById(R.id.marker_category);

        arrayList = new ArrayList<>();
        listview = (ListView) findViewById(R.id.listview_comments);

        Intent intent = getIntent();

        markerID = intent.getExtras().getString("MarkerClass");
        final boolean isProfile = intent.getExtras().getBoolean("IsProfile");

        if (isProfile) {
            mMarker = MapActivity.userMarkerMap.get(markerID);
            mSecondMarker = MapActivity.mReportMap.get(markerID);
        }
        else {
            mMarker = MapActivity.mReportMap.get(markerID);
            mSecondMarker = MapActivity.userMarkerMap.get(markerID);
        }

        Log.e("MAPPPPAAA ", mMarker + "     " + MapActivity.mReportMap.get(markerID));

        //TODO
        Bitmap bitmap = mMarker.getmImg_bitmap();
        if(bitmap==null)
            RequestsVolley.thumbnailRequest(mMarker.getmId(), mMarker, -1, mContext,
                    null, null, null, MarkerActivity.this);
        marker_image.setImageBitmap(mMarker.getmImg_bitmap());

        String title = mMarker.getmTitle();
        if (!title.equals(""))
            marker_title.setText(title);
        else
            marker_title.setVisibility(View.GONE);

        String category = mMarker.getmCategory();
        marker_category.setText(category);

        String description = mMarker.getmDescription();
        if (!description.equals(""))
            marker_description.setText(description);
        else
            marker_description.setVisibility(View.GONE);

        marker_address.setText(mMarker.getmAddress());
        marker_date.setText(mMarker.getmDMY());
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
                if (isClicked)
                    mCommentPostLayout.setVisibility(View.GONE);
                else
                    mCommentPostLayout.setVisibility(View.VISIBLE);

                isClicked = !isClicked;
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
                    MapActivity.votesMap.put(markerID, "neutral");
                    marker_button_dislikes.setBackgroundResource(R.drawable.downicon);

                } else {
                    mDislikes++;
                    if (mTouchLike) {
                        mLikes--;
                        marker_button_likes.setBackgroundResource(R.drawable.upicon);
                        mTouchLike = false;
                    }
                    mMarker.setmVote("down");
                    MapActivity.votesMap.put(markerID, "down");
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
                    MapActivity.votesMap.put(markerID, "neutral");
                    marker_button_likes.setBackgroundResource(R.drawable.upicon);

                } else {
                    mLikes++;
                    if (mTouchDislike) {
                        mDislikes--;
                        marker_button_dislikes.setBackgroundResource(R.drawable.downicon);
                        mTouchDislike = false;
                    }
                    mMarker.setmVote("up");
                    MapActivity.votesMap.put(markerID, "up");
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

                String text = marker_comment.getText().toString();

                if (!text.equals("")) {

                    RequestsVolley.postCommentRequest(id, text, mContext, MarkerActivity.this);

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = sdf.format(c.getTime());

                    arrayList.add(new CommentClass(mMarker.getmId(), strDate, mMarker.getmCreator_username(), text));

                    MyAdapter myAdapter = new MarkerActivity.MyAdapter(mContext, arrayList);
                    listview.setAdapter(myAdapter);
                    setListViewHeightBasedOnChildren(listview);
                    myAdapter.notifyDataSetChanged();

                } else
                    Toast.makeText(mContext, "Empty comment", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setListComments(JSONArray comments) {
        CommentClass commentClass;

        try {
            JSONArray jsonarray = comments;

            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);

                String commentID = jsonobject.getString("comment");
                String textComment = jsonobject.getString("text");
                String ownerComment = jsonobject.getString("username");
                String dateComment = jsonobject.getString("creationtime");

                commentClass = new CommentClass(commentID, dateComment, ownerComment, textComment);
                byte [] bytes = Base64.decode(jsonobject.getString("profpic"), Base64.DEFAULT);

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                RoundedBitmapDrawable roundedBitmap = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                roundedBitmap.setCircular(true);

                commentClass.setAvatar(roundedBitmap);
                //commentClass.makeAvatar(bytes);
                arrayList.add(commentClass);
            }
            listview.setAdapter(new MarkerActivity.MyAdapter(mContext, arrayList));
            setListViewHeightBasedOnChildren(listview);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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
            comment_ownerpic = convertView.findViewById(R.id.comment_ownerpic);

            comment_text.setText(comments.get(position).mText);
            comment_date.setText(comments.get(position).mDMY);
            comment_owner.setText(comments.get(position).mOwner);
            //comment_ownerpic.setImageBitmap(comments.get(position).getmAvatar_bitmap());
            comment_ownerpic.setImageDrawable(comments.get(position).getmAvatar_rounded());

            return convertView;
        }


    }
}
