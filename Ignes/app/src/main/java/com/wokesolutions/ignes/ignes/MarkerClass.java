package com.wokesolutions.ignes.ignes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerClass implements ClusterItem {

    private LatLng mPosition;
    private String mStatus, mAddress, mDate, mCreator_username,
            mDescription, mGravity, mTitle, mLikes, mDislikes, mLocality;
    private byte[] mImgbyte;
    private byte[] mAvatarbyte;
    private Bitmap mImage_bitmap;
    private Bitmap mAvatar_bitmap;
    private String mId, mVote, mPoints, mCategory;
    private boolean mIsArea;
    private boolean mIsClicked;
    private boolean mApplicationRequested;


    public MarkerClass(double lat, double lng, String status, String address,
                       String date, String username, String description, String gravity,
                       String title, String likes, String dislikes, String locality, boolean isArea,
                       boolean isClicked, String points, String category, String marker_id) {

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
        mAvatarbyte = null;
        mId = marker_id;
        mIsArea = isArea;
        mIsClicked = isClicked;
        mPoints = points;
        mCategory = category;
        mVote = "";
        mApplicationRequested = false;
    }

    public void setmApplicationRequested(boolean mApplicationRequested) {
        this.mApplicationRequested = mApplicationRequested;
    }

    public boolean getmApplicationRequested(){
        return mApplicationRequested;
    }

    public String getmCategory() {
        return mCategory;
    }

    public String getmPoints() {
        return mPoints;
    }

    public boolean mIsArea() {
        return mIsArea;
    }

    public boolean mIsClicked() {
        return mIsClicked;
    }

    public void setmIsClicked(boolean click) {
        mIsClicked = click;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getmId() {
        return mId;
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

        String title = "Title: " + mTitle;
        String gravity = "Gravity: " + mGravity;

        if (mTitle.equals(""))
            title = "";

        String mFinal = title + "\n" + gravity + "\n" + "Posted by: " + mCreator_username + "\n" + "Date: " + mDate;

        return mFinal;
    }

    public String getmDislikes() {
        return mDislikes;
    }

    public void setmDislikes(String mDislikes) {
        this.mDislikes = mDislikes;
    }

    public String getmLikes() {
        return mLikes;
    }

    public void setmLikes(String mLikes) {
        this.mLikes = mLikes;
    }

    public Bitmap makeImg(byte[] thumbnail) {
        mImgbyte = thumbnail;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        mImage_bitmap = BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length, options);

        return mImage_bitmap;
    }

    public Bitmap makeAvatar(byte[] avatar) {
        mAvatarbyte = avatar;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        mAvatar_bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length, options);

        return mAvatar_bitmap;
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

    public byte[] getmAvatarbyte() {
        return mAvatarbyte;
    }
}