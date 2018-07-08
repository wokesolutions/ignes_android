package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

import java.util.List;
import java.util.Map;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    RequestQueue queue;
    private Context mContext;
    private Map<String, TaskClass> mMap;
    private String newStatus;
    private String mReportID;
    final String[] values = new String[]{"Aberto","Em progresso", "Fechado"};

    TaskAdapter(Context context, Map<String, TaskClass> map) {
        mContext = context;
        mMap = map;
        queue = Volley.newRequestQueue(mContext);
        newStatus = "";

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

        mReportID = (String) keys[position];

        if (taskItem.getmImg_bitmap() == null)
            thumbnailRequest(mReportID, taskItem, position);

        ImageView image = holder.task_image;
        final TextView title = holder.task_title;
        TextView username = holder.task_username;
        TextView date = holder.task_date;
        TextView address = holder.task_address;
        TextView description = holder.task_description;
        TextView gravity = holder.task_gravity;
        Button notes_button = holder.button_notes;
        final Button status_button = holder.button_status;
        TextView gravity_title = holder.task_gravity_title;
        final ImageView img_status = holder.task_status_image;
        TextView indications = holder.task_indications;
        TextView contact = holder.task_contact;
        Button directions_button = holder.task_directions_button;
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

        if (taskItem.getmStatus().equals("OPEN")) {
            img_status.setImageResource(R.drawable.lockopen);
        }
        status_button.setText("Aberto");

        image.setImageBitmap(taskItem.getmImg_bitmap());

        notes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(holder.itemViewContext, NoteActivity.class);

                i.putExtra("TaskClass", taskItem.getmId());

                holder.itemViewContext.startActivity(i);
            }
        });

        status_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setTitle("Change Report Status");
                mBuilder.setIcon(R.drawable.ocorrenciared);

                mBuilder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        System.out.println("The wrong button was tapped: " + values[whichButton]);
                        newStatus = values[whichButton];
                        System.out.println("New status: " + newStatus);
                    }
                });

                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (newStatus.equals("Em progresso")) {
                            RequestsVolley.changeRepStatusWIPRequest(mReportID,mContext);
                            status_button.setText("Em progresso");
                        }
                        else if(newStatus.equals("Fechado")) {
                            status_button.setText("Fechado");
                            img_status.setImageResource(R.drawable.lockclose);

                        }
                        else if(newStatus.equals("Aberto")) {
                            status_button.setText("Aberto");
                            img_status.setImageResource(R.drawable.lockopen);
                        }
                    }
                });

                mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                }).show();
            }
        });

        directions_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               MapActivity.getDirections(taskItem.getPosition(), taskItem);
               MapActivity.mGoogleMapsButtonLayout.setVisibility(View.VISIBLE);
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

        ImageView task_status_image, task_image;
        TextView task_title, task_address, task_username, task_date, task_gravity,
                task_gravity_title, task_description, task_indications, task_contact;
        Button button_notes, task_directions_button, button_status;
        Context itemViewContext;
        LinearLayout task_contacts_layout, task_indications_layout;

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
            button_status = itemView.findViewById(R.id.button_status);
            task_gravity_title = itemView.findViewById(R.id.feed_gravity_title);
        //    task_status_image = itemView.findViewById(R.id.feed_lock_img);
            task_indications = itemView.findViewById(R.id.feed_info_marker);
            task_contact = itemView.findViewById(R.id.feed_contact);
            task_directions_button = itemView.findViewById(R.id.gps_directions);
            task_contacts_layout = itemView.findViewById(R.id.contacts_layout);
            task_indications_layout = itemView.findViewById(R.id.indications_layout);
        }
    }
}
