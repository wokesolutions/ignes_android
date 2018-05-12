package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        final View view = layoutInflater.inflate(R.layout.feed_marker_item, parent, false);

        final ViewHolder viewHolder = new ViewHolder(view);

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
        TextView likes = holder.marker_likes;
        TextView dislikes = holder.marker_dislikes;
        TextView gravity_title = holder.marker_gravity_title;
        // TextView comments = holder.marker_comments;

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

        status.setText(markerItem.getmStatus());
        likes.setText(markerItem.getmLikes());
        dislikes.setText(markerItem.getmDislikes());
        //  comments.setText(markerItem.getmComments());

        image.setImageBitmap(markerItem.getmImg_bitmap());

        more_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(holder.itemViewContext, MarkerActivity.class);

                i.putExtra("markerAddress", markerItem.getmAddress());
                i.putExtra("markerDate", markerItem.getmDate());
                i.putExtra("markerDescription", markerItem.getmDescription());
                i.putExtra("markerGravity", markerItem.getmGravity());
                i.putExtra("markerImg", markerItem.getmImg_byte());
                i.putExtra("markerUsername", markerItem.getmCreator_username());
                i.putExtra("markerTitle", markerItem.getmTitle());
                i.putExtra("markerStatus", markerItem.getmStatus());
                i.putExtra("markerLikes", markerItem.getmLikes());
                i.putExtra("markerDislikes", markerItem.getmDislikes());

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
        TextView marker_likes;
        TextView marker_dislikes;
        //TextView marker_comments;
        TextView marker_address;
        TextView marker_username;
        TextView marker_date;
        TextView marker_gravity;
        TextView marker_status;
        TextView marker_gravity_title;
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
            marker_likes = itemView.findViewById(R.id.feed_likes_number);
            marker_dislikes = itemView.findViewById(R.id.feed_dislikes_number);
            marker_gravity_title = itemView.findViewById(R.id.feed_gravity_title);
            // marker_comments = itemView.findViewById(R.id.feed_comments_number);

        }
    }
}