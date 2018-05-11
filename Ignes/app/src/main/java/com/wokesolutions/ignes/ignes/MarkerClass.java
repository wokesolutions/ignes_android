package com.wokesolutions.ignes.ignes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private final String mGravity;
    private final String mTitle;
    private final byte[] mImg_byte;
    private final String mLikes;
    private final String mDislikes;
    private Bitmap mImage_bitmap;


    public MarkerClass(double lat, double lng, String status, String address,
                       String date, String username, String description, String gravity,
                       String title, byte[] img_byte, String likes, String dislikes) {
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
        mImg_byte = img_byte;
        mImage_bitmap = makeImg();
        mLikes = likes;
        mDislikes = dislikes;

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

    public String getmGravity() {
        return mGravity;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getSnippet() {
        return "Address: " + mAddress + "\n" + "Posted by: " + mUsername + "\n" + "Creation Date: " + mDate;
    }

    public String getmDislikes() {
        return mDislikes;
    }

    public String getmLikes() {
        return mLikes;
    }

    public byte[] getmImg_byte() {
        return mImg_byte;
    }

    private Bitmap makeImg() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        mImage_bitmap = BitmapFactory.decodeByteArray(mImg_byte, 0, mImg_byte.length, options);

        return mImage_bitmap;
    }

    public Bitmap getmImg_bitmap() {
        return mImage_bitmap;
    }
}