package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.Map;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    RequestQueue queue;
    private Context mContext;
    private Map<String, TaskClass> mMap;

    TaskAdapter(Context context, Map<String, TaskClass> map) {
        mContext = context;
        mMap = map;
        queue = Volley.newRequestQueue(mContext);

    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        final View view = layoutInflater.inflate(R.layout.worker_feed_marker, parent, false);

        final TaskAdapter.ViewHolder viewHolder = new TaskAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final TaskAdapter.ViewHolder holder, int position) {

        Object[] keys = mMap.keySet().toArray();

        final TaskClass taskItem = mMap.get(keys[position]);

        if (taskItem.getmImg_bitmap() == null)
            thumbnailRequest((String) keys[position], taskItem, position);

        ImageView image = holder.task_image;
        final TextView title = holder.task_title;
        TextView username = holder.task_username;
        TextView date = holder.task_date;
        TextView address = holder.task_address;
        TextView description = holder.task_description;
        TextView gravity = holder.task_gravity;
        Button notes_button = holder.button_notes;
        TextView gravity_title = holder.task_gravity_title;
        ImageView img_status = holder.task_status_image;
        TextView indications = holder.task_indications;
        TextView contact = holder.task_contact;
        LinearLayout directions_button = holder.task_directions_button;
        LinearLayout contacts_layout = holder.task_contacts_layout;
        LinearLayout indications_layout = holder.task_indications_layout;

        if (!taskItem.getmTitle().isEmpty())
            title.setText(taskItem.getmTitle());
        else
            title.setVisibility(View.GONE);

        username.setText(taskItem.getmCreator_username());
        date.setText(taskItem.getmDate());
        address.setText(taskItem.getmAddress());

        if (!taskItem.getIndications().equals(""))
            indications.setText(taskItem.getIndications());
        else
            indications_layout.setVisibility(View.GONE);

        if (!taskItem.getmDescription().equals(""))
            description.setText(taskItem.getmDescription());
        else
            description.setVisibility(View.GONE);

        if (!taskItem.getPhoneNumber().equals(""))
            contact.setText(taskItem.getPhoneNumber());
        else
            contacts_layout.setVisibility(View.GONE);

        if (!taskItem.getmGravity().equals("0"))
            gravity.setText(taskItem.getmGravity());
        else {
            gravity.setVisibility(View.GONE);
            gravity_title.setVisibility(View.GONE);
        }

        if (taskItem.getmGravity().equals("1"))
            gravity_title.setTextColor(Color.parseColor("#E0DCBE"));
        if (taskItem.getmGravity().equals("2"))
            gravity_title.setTextColor(Color.parseColor("#CFD7C7"));
        if (taskItem.getmGravity().equals("3"))
            gravity_title.setTextColor(Color.parseColor("#70A9A1"));
        if (taskItem.getmGravity().equals("4"))
            gravity_title.setTextColor(Color.parseColor("#40798C"));
        if (taskItem.getmGravity().equals("5"))
            gravity_title.setTextColor(Color.parseColor("#0B2027"));

        if (taskItem.getmStatus().equals("CLOSE"))
            img_status.setImageResource(R.drawable.lockclose);

        if (taskItem.getmStatus().equals("OPEN"))
            img_status.setImageResource(R.drawable.lockopen);

        image.setImageBitmap(taskItem.getmImg_bitmap());

        notes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(holder.itemViewContext, NoteActivity.class);

                i.putExtra("TaskClass", taskItem.getmId());

                holder.itemViewContext.startActivity(i);
            }
        });

        directions_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity.getDirections(MapActivity.mLatLng, taskItem.getPosition());
                ((FeedActivity) mContext).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMap.size();
    }

    private void thumbnailRequest(String reportId, TaskClass task, final int position) {

        RequestsVolley.thumbnailRequest(reportId, null, position, mContext, null, task, this);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView task_status_image;
        ImageView task_image;
        TextView task_title;
        TextView task_address;
        TextView task_username;
        TextView task_date;
        TextView task_gravity;
        TextView task_gravity_title;
        TextView task_description;
        Button button_notes;
        TextView task_indications;
        Context itemViewContext;
        TextView task_contact;
        LinearLayout task_directions_button;
        LinearLayout task_contacts_layout;
        LinearLayout task_indications_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            itemViewContext = itemView.getContext();
            task_image = itemView.findViewById(R.id.feed_image_marker);
            task_title = itemView.findViewById(R.id.feed_title_marker);
            task_description = itemView.findViewById(R.id.feed_description_marker);
            task_address = itemView.findViewById(R.id.feed_address_marker);
            task_date = itemView.findViewById(R.id.feed_date_marker);
            task_username = itemView.findViewById(R.id.feed_username_marker);
            task_gravity = itemView.findViewById(R.id.feed_gravity_marker);
            button_notes = itemView.findViewById(R.id.button_notes);
            task_gravity_title = itemView.findViewById(R.id.feed_gravity_title);
            task_status_image = itemView.findViewById(R.id.feed_lock_img);
            task_indications = itemView.findViewById(R.id.feed_info_marker);
            task_contact = itemView.findViewById(R.id.feed_contact);
            task_directions_button = itemView.findViewById(R.id.gps_directions);
            task_contacts_layout = itemView.findViewById(R.id.contacts_layout);
            task_indications_layout = itemView.findViewById(R.id.indications_layout);
        }
    }
}
