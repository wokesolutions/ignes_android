package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<MarkerClass> mList;

    MarkerAdapter(Context context, ArrayList<MarkerClass> list) {
        mContext = context;
        mList = list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(R.layout.feed_marker_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MarkerClass markerItem = mList.get(position);

        ImageView image = holder.marker_image;
        TextView title = holder.marker_title;
        TextView username = holder.marker_username;
        TextView date = holder.marker_date;
        //  TextView description = holder.marker_description;
        TextView address = holder.marker_address;
        TextView gravity = holder.marker_gravity;
        TextView status = holder.marker_status;

        if(!markerItem.getmTitle().isEmpty())
             title.setText(markerItem.getmTitle());
        else
            title.setVisibility(View.GONE);

        username.setText(markerItem.getmUsername());
        date.setText(markerItem.getmDate());
        //  description.setText(markerItem.getmDescription());
        address.setText(markerItem.getmAddress());
        gravity.setText("" + markerItem.getmGravity());
        status.setText(markerItem.getmStatus());

        image.setImageBitmap(markerItem.getmImg_bitmap());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView marker_image;
        TextView marker_title;
        // TextView marker_description;
        TextView marker_address;
        TextView marker_username;
        TextView marker_date;
        TextView marker_gravity;
        TextView marker_status;


        public ViewHolder(View itemView) {
            super(itemView);

            marker_image = itemView.findViewById(R.id.feed_image_marker);
            marker_title = itemView.findViewById(R.id.feed_title_marker);
            // marker_description = itemView.findViewById(R.id.feed_description_marker);
            marker_address = itemView.findViewById(R.id.feed_address_marker);
            marker_date = itemView.findViewById(R.id.feed_date_marker);
            marker_username = itemView.findViewById(R.id.feed_username_marker);
            marker_gravity = itemView.findViewById(R.id.feed_gravity_marker);
            marker_status = itemView.findViewById(R.id.feed_status_marker);

        }


    }
}
