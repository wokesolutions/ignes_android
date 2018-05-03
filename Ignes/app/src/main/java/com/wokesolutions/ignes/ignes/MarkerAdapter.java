package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.speech.tts.TextToSpeech;
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

    MarkerAdapter (Context context, ArrayList<MarkerClass> list){
        mContext =context;
        mList = list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view =layoutInflater.inflate(R.layout.feed_marker_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MarkerClass markerItem =mList.get(position);

        ImageView image = holder.marker_image;
        TextView title = holder.marker_title;

        title.setText("By: "+markerItem.getmUsername());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView marker_image;
        TextView marker_title;

        public ViewHolder(View itemView) {
            super(itemView);

            marker_image = itemView.findViewById(R.id.feed_image_marker);
            marker_title = itemView.findViewById(R.id.feed_title_marker);
        }


    }
}
