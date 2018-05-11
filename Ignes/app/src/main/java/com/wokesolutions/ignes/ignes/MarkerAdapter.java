package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
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
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        final View view = layoutInflater.inflate(R.layout.feed_marker_item, parent, false);

        final ViewHolder viewHolder = new ViewHolder(view);

       /* viewHolder.button_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent i =new Intent(parent.getContext(), MarkerActivity.class);
                i.putExtra("title", )
                parent.getContext().startActivity(i);



            }
        });*/
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final MarkerClass markerItem = mList.get(position);

        ImageView image = holder.marker_image;
        final TextView title = holder.marker_title;
        TextView username = holder.marker_username;
        TextView date = holder.marker_date;
        TextView address = holder.marker_address;
        TextView gravity = holder.marker_gravity;
        TextView status = holder.marker_status;
        Button more_button = holder.button_more;

        if (!markerItem.getmTitle().isEmpty())
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

        more_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(holder.itemViewContext, MarkerActivity.class);
                i.putExtra("markerAddress", markerItem.getmAddress());
                i.putExtra("markerDate", markerItem.getmDate());
                i.putExtra("markerDescription", markerItem.getmDescription());
                i.putExtra("markerGravity", markerItem.getmGravity());
                i.putExtra("markerImg", markerItem.getmImg_bitmap());
                i.putExtra("markerUsername", markerItem.getmUsername());
                i.putExtra("markerTitle", markerItem.getmTitle());
                i.putExtra("markerStatus", markerItem.getmStatus());

                holder.itemViewContext.startActivity(i);
            }
        });


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
            marker_status = itemView.findViewById(R.id.feed_status_marker);
            button_more = itemView.findViewById(R.id.button_more);

        }


    }
}
