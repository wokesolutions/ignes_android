package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.ViewHolder> {

    public static Map<String, MarkerClass> mMap;
    RequestQueue queue;
    private Context mContext;
    private boolean mIsProfile;
    private ArrayList<ApplicationClass> mArrayList;
    private int isReady;
    private String mCurrentUser;
    private ProfileActivity mProfileActivity;
    private FeedActivity mFeedActivity;

    MarkerAdapter(Context context, Map<String, MarkerClass> map, FeedActivity feedActivity, ProfileActivity profileActivity, String currentUser) {
        mContext = context;
        mMap = map;
        queue = Volley.newRequestQueue(mContext);
        mProfileActivity = profileActivity;
        mIsProfile = (mProfileActivity != null);
        mFeedActivity = feedActivity;
        mArrayList = new ArrayList<>();
        isReady = 0;
        mCurrentUser = currentUser;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        final View view = layoutInflater.inflate(R.layout.feed_marker_item, parent, false);

        final ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Object[] keys = mMap.keySet().toArray();

        final MarkerClass markerItem = mMap.get(keys[position]);

        if (markerItem.getmImg_bitmap() == null)
            thumbnailRequest((String) keys[position], markerItem, position);

        ImageView avatar = holder.user_avatar;
        ImageView image = holder.marker_image;
        final TextView title = holder.marker_title;
        TextView username = holder.marker_username;
        TextView date = holder.marker_date;
        TextView address = holder.marker_address;
        TextView gravity = holder.marker_gravity;
        Button more_button = holder.button_more;
        LinearLayout layout = holder.marker_item_layout;
        TextView gravity_title = holder.marker_gravity_title;
        TextView interacts = holder.marker_interacts;
        TextView interacts_text = holder.marker_interactions_text;
        Button delete = holder.button_delete;

        if (!markerItem.getmTitle().isEmpty())
            title.setText(markerItem.getmTitle());
        else
            title.setVisibility(View.GONE);

        username.setText(markerItem.getmCreator_username());
        date.setText(markerItem.getmDMY());
        address.setText(markerItem.getmAddress());

        if (!markerItem.getmGravity().equals("0"))
            gravity.setText(markerItem.getmGravity());
        else {
            gravity.setVisibility(View.GONE);
            gravity_title.setVisibility(View.GONE);
        }
        if (markerItem.getmGravity().equals("1"))
            gravity_title.setTextColor(Color.parseColor("#E0DCBE"));
        if (markerItem.getmGravity().equals("2"))
            gravity_title.setTextColor(Color.parseColor("#CFD7C7"));
        if (markerItem.getmGravity().equals("3"))
            gravity_title.setTextColor(Color.parseColor("#70A9A1"));
        if (markerItem.getmGravity().equals("4"))
            gravity_title.setTextColor(Color.parseColor("#40798C"));
        if (markerItem.getmGravity().equals("5"))
            gravity_title.setTextColor(Color.parseColor("#0B2027"));

        int total = Integer.parseInt(markerItem.getmLikes()) + Integer.parseInt(markerItem.getmDislikes());
        interacts.setText("" + total);

        if (total == 1)
            interacts_text.setText(R.string.person_interacted_with_report);

        image.setImageBitmap(markerItem.getmImg_bitmap());

        if (markerItem.getmAvatar_bitmap() != null)
            avatar.setImageBitmap(markerItem.getmAvatar_bitmap());

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(holder.itemViewContext, MarkerActivity.class);

                i.putExtra("MarkerClass", markerItem.getmId());
                i.putExtra("IsProfile", mIsProfile);

                holder.itemViewContext.startActivity(i);
            }
        });

        more_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);

                LayoutInflater inflater = LayoutInflater.from(mContext);
                final View mView = inflater.inflate(R.layout.report_more_layout, null);
                mBuilder.setView(mView);
                final AlertDialog alert = mBuilder.create();
                alert.show();

                final LinearLayout delete_layout = mView.findViewById(R.id.delete_layout);

                if (mCurrentUser.equals(markerItem.getmCreator_username())) {
                    delete_layout.setVisibility(View.VISIBLE);
                    delete_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buildAlertMessage(markerItem, alert);
                        }
                    });

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mMap.size();
    }

    private void thumbnailRequest(String reportId, MarkerClass marker, final int position) {

        RequestsVolley.thumbnailRequest(reportId, marker, position, mContext, this, null, null, null);
    }

    private void buildAlertMessage(final MarkerClass marker, final AlertDialog alertDialog) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setIcon(R.drawable.deletecomment);

        builder.setMessage("Eliminar ocorrência?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        if (mIsProfile)
                            RequestsVolley.reportDeleteRequest(marker.getmId(), null, mProfileActivity, mContext, alertDialog);
                        else
                            RequestsVolley.reportDeleteRequest(marker.getmId(), mFeedActivity, null, mContext, alertDialog);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView marker_status_image, marker_image, user_avatar;
        TextView marker_title, marker_interacts, marker_address, marker_username, marker_date,
                marker_gravity, marker_gravity_title, marker_interactions_text;
        Button button_more, button_applications, button_delete;
        LinearLayout marker_item_layout;
        Context itemViewContext;

        public ViewHolder(View itemView) {
            super(itemView);
            itemViewContext = itemView.getContext();
            marker_image = itemView.findViewById(R.id.feed_image_marker);
            marker_title = itemView.findViewById(R.id.feed_title_marker);
            marker_address = itemView.findViewById(R.id.feed_address_marker);
            marker_date = itemView.findViewById(R.id.feed_date_marker);
            marker_username = itemView.findViewById(R.id.feed_username_marker);
            marker_gravity = itemView.findViewById(R.id.feed_gravity_marker);
            button_more = itemView.findViewById(R.id.button_more);
            marker_gravity_title = itemView.findViewById(R.id.feed_gravity_title);
            marker_interacts = itemView.findViewById(R.id.feed_total_number);
            marker_interactions_text = itemView.findViewById(R.id.feed_report_interactions);
            button_delete = itemView.findViewById(R.id.button_delete);
            marker_item_layout = itemView.findViewById(R.id.marker_item_layout);
            //  marker_status_image = itemView.findViewById(R.id.feed_lock_img);

            user_avatar = itemView.findViewById(R.id.avatar_icon_marker);
        }
    }

}