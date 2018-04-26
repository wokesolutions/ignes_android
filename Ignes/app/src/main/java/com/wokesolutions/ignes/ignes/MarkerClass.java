package com.wokesolutions.ignes.ignes;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerClass implements ClusterItem {

    private final LatLng mPosition;

    public MarkerClass(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}

