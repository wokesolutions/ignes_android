package com.wokesolutions.ignes.ignes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


public class MarkerClass implements ClusterItem {

    private LatLng mPosition;

    private String mMarker_ID;
    private String mStatus;
    private String mAddress;
    private String mDate;
    private String mCreator_username;
    private String mDescription;
    private String mGravity;
    private String mTitle;
    private String mLikes;
    private String mDislikes;

    private String mLocality;

    private byte[] mImgbyte;

    private Bitmap mImage_bitmap;

    private double mLatitude;
    private double mLongitude;


    public MarkerClass(double lat, double lng, String status, String address,
                       String date, String username, String description, String gravity,
                       String title, String likes, String dislikes, String locality, String marker_id) {

        mPosition = new LatLng(lat, lng);
        mStatus = status;
        mAddress = address;
        mDate = date;
        mCreator_username = username;
        mLatitude = lat;
        mLongitude = lng;
        mDescription = description;
        mGravity = gravity;
        mTitle = title;
        mImage_bitmap = null;
        mLikes = likes;
        mDislikes = dislikes;
        mLocality = locality;
        mMarker_ID = marker_id;
        mImgbyte = null;

    }
    public String getMarkerID(){
        return mMarker_ID;
    }
    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public String getmLocality() {
        return mLocality;
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

    public String getmCreator_username() {
        return mCreator_username;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmGravity() {
        return mGravity;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getSnippet() {
        return "Address: " + mAddress + "\n" + "Posted by: " + mCreator_username + "\n" + "Creation Date: " + mDate;
    }

    public String getmDislikes() {
        return mDislikes;
    }

    public String getmLikes() {
        return mLikes;
    }

    public Bitmap makeImg( byte[] thumbnail) {
        mImgbyte = thumbnail;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        mImage_bitmap = BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length, options);

        return mImage_bitmap;
    }

    public byte[] getmImgbyte() {
        return mImgbyte;
    }

    public Bitmap getmImg_bitmap() {
        return mImage_bitmap;
    }

}