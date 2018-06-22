package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Map;

public class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.ViewHolder> {

    RequestQueue queue;
    private Context mContext;
    private Map<String, MarkerClass> mMap;

    MarkerAdapter(Context context, Map<String, MarkerClass> map) {
        mContext = context;
        mMap = map;
        queue = Volley.newRequestQueue(mContext);
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

        ImageView image = holder.marker_image;
        final TextView title = holder.marker_title;
        TextView username = holder.marker_username;
        TextView date = holder.marker_date;
        TextView address = holder.marker_address;
        TextView gravity = holder.marker_gravity;
        Button more_button = holder.button_more;
        TextView gravity_title = holder.marker_gravity_title;
        TextView interacts = holder.marker_interacts;
        ImageView img_status = holder.marker_status_image;

        if (!markerItem.getmTitle().isEmpty())
            title.setText(markerItem.getmTitle());
        else
            title.setVisibility(View.GONE);

        username.setText(markerItem.getmCreator_username());
        date.setText(markerItem.getmDate());
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


        if (markerItem.getmStatus().equals("CLOSE"))
            img_status.setImageResource(R.drawable.lockclose);
        if (markerItem.getmStatus().equals("OPEN"))
            img_status.setImageResource(R.drawable.lockopen);


        int total = Integer.parseInt(markerItem.getmLikes()) + Integer.parseInt(markerItem.getmDislikes());
        interacts.setText("" + total);

        image.setImageBitmap(markerItem.getmImg_bitmap());

        more_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(holder.itemViewContext, MarkerActivity.class);

                i.putExtra("MarkerClass", markerItem.getmId());

                holder.itemViewContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMap.size();
    }

    private void thumbnailRequest(String reportId, MarkerClass marker, final int position) {

        RequestsVolley.thumbnailRequest(reportId, marker, position, mContext, this, null, null);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView marker_status_image, marker_image;
        TextView marker_title, marker_interacts, marker_address, marker_username, marker_date,
                marker_gravity, marker_gravity_title;
        Button button_more;
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
            marker_status_image = itemView.findViewById(R.id.feed_lock_img);
        }
    }

}