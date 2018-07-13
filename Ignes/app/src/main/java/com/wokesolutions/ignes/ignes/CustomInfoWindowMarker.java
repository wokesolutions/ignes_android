package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.wokesolutions.ignes.ignes.MarkerActivity;
import com.wokesolutions.ignes.ignes.MarkerClass;
import com.wokesolutions.ignes.ignes.R;

import org.json.JSONArray;
import org.json.JSONException;

public class CustomInfoWindowMarker implements GoogleMap.InfoWindowAdapter {

    private Context mContext;


    public CustomInfoWindowMarker(Context context) {
        mContext = context;

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }


    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity) mContext).getLayoutInflater()
                .inflate(R.layout.map_marker_info_window, null);

        TextView title = view.findViewById(R.id.title_marker);
        TextView address = view.findViewById(R.id.address_marker);
        title.setText(marker.getTitle());
        String add = marker.getSnippet().split("#%")[1];
        address.setText(add);
        return view;
    }
}