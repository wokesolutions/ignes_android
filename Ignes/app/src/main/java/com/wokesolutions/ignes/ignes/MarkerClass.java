package com.wokesolutions.ignes.ignes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerClass implements ClusterItem {

    private LatLng mPosition;
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

    private String mVote;

    public MarkerClass(double lat, double lng, String status, String address,
                       String date, String username, String description, String gravity,
                       String title, String likes, String dislikes, String locality, String marker_id) {

        mPosition = new LatLng(lat, lng);
        mStatus = status;
        mAddress = address;
        mDate = date;
        mCreator_username = username;
        mDescription = description;
        mGravity = gravity;
        mTitle = title;
        mImage_bitmap = null;
        mLikes = likes;
        mDislikes = dislikes;
        mLocality = locality;
        mImgbyte = null;

        mVote ="";
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getmLocality() {
        return mLocality;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmAddress() {
        return mAddress;
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

    public void setmLikes(String mLikes) {
        this.mLikes = mLikes;
    }

    public void setmDislikes(String mDislikes) {
        this.mDislikes = mDislikes;
    }

    public Bitmap makeImg(byte[] thumbnail) {
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

    public String getmVote() {
        return mVote;
    }

    public void setmVote(String mVote) {
        this.mVote = mVote;
    }
}