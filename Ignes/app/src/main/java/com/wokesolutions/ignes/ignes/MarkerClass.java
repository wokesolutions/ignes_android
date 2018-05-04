package com.wokesolutions.ignes.ignes;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerClass implements ClusterItem {

    private final LatLng mPosition;
    private final String mStatus;
    private final String mAddress;
    private final String mDate;
    private final String mUsername;
    private final double mLatitude;
    private final double mLongitude;
    private final String mDescription;
    private final int mGravity;
    private final String mTitle;

    public MarkerClass(double lat, double lng, String status, String address, String date, String username, String description, int gravity, String title) {
        mPosition = new LatLng(lat, lng);
        mStatus = status;
        mAddress = address;
        mDate = date;
        mUsername = username;
        mLatitude = lat;
        mLongitude = lng;
        mDescription = description;
        mGravity = gravity;
        mTitle = title;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmAddress() {
        return mAddress;
    }

    public LatLng getmPosition() {
        return mPosition;
    }

    public String getmStatus() {
        return mStatus;
    }

    public String getmUsername() {
        return mUsername;
    }

    public String getmDescription() {
        return mDescription;
    }

    public int getmGravity() {
        return mGravity;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getSnippet() {
        return "Address: " + mAddress + "\n" + "Posted by: " + mUsername + "\n" + "Creation Date: " + mDate;
    }
}