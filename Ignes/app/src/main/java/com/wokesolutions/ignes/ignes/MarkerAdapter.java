package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.ViewHolder> {

    private Context mContext;
    private Map<String, MarkerClass> mMap;
    RequestQueue queue;

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

        if(markerItem.getmImg_bitmap()== null)
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


        if(markerItem.getmStatus().equals("CLOSE"))
            img_status.setImageResource(R.drawable.lockclose);
        if(markerItem.getmStatus().equals("OPEN"))
            img_status.setImageResource(R.drawable.lockopen);


        int total = Integer.parseInt(markerItem.getmLikes()) + Integer.parseInt(markerItem.getmDislikes());
        interacts.setText("" + total);

        image.setImageBitmap(markerItem.getmImg_bitmap());

        more_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(holder.itemViewContext, MarkerActivity.class);

                i.putExtra("markerAddress", markerItem.getmAddress());
                i.putExtra("markerDate", markerItem.getmDate());
                i.putExtra("markerDescription", markerItem.getmDescription());
                i.putExtra("markerGravity", markerItem.getmGravity());
                i.putExtra("markerImg", markerItem.getmImgbyte());
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
        return mMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView marker_status_image;
        ImageView marker_image;
        TextView marker_title;
        TextView marker_interacts;
        TextView marker_address;
        TextView marker_username;
        TextView marker_date;
        TextView marker_gravity;
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
            button_more = itemView.findViewById(R.id.button_more);
            marker_gravity_title = itemView.findViewById(R.id.feed_gravity_title);
            marker_interacts = itemView.findViewById(R.id.feed_total_number);
            marker_status_image = itemView.findViewById(R.id.feed_lock_img);

          /*  // Handle item click and set the selection
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Redraw the old selection and the new
                   int selectedItem = getLayoutPosition();
                    notifyItemChanged(selectedItem);
                }
            });*/

        }
    }

    private void thumbnailRequest(String reportId, MarkerClass marker, final int position) {

        final String report = reportId;
        final MarkerClass item = marker;

        ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            // If no connectivity, cancel task and update Callback with null data.
            Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_LONG).show();
            return;
        }


        String url = "https://hardy-scarab-200218.appspot.com/api/report/thumbnail/"+report;

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        System.out.println("OK: " + response);
                        try {

                            String base64 = response.getString("report_thumbnail");
                            byte[] data = Base64.decode(base64, Base64.DEFAULT);
                            item.makeImg(data);

                            notifyItemChanged(position);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        System.out.println("ERRO DO LOGIN: " + response);


                    }
                }
        ) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

                if (response.statusCode == 200) {

                    try {
                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONObject jsonobject = new JSONObject(json);



                        return Response.success(jsonobject, HttpHeaderParser.parseCacheHeaders(response));

                    } catch (Exception e) {
                        e.printStackTrace();
                        return Response.error(new VolleyError(String.valueOf(response.statusCode)));
                    }
                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*2,
                1,  // maxNumRetries = 0 means no retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(postRequest);

    }

}