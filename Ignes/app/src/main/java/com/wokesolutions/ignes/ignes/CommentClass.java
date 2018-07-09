package com.wokesolutions.ignes.ignes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.app.AppCompatActivity;

public class CommentClass extends AppCompatActivity {

    public String mId;
    public String mDate;
    public String mOwner;
    public String mText;
    public String mDMY;
    public String mHours;
    private byte[] mAvatarbyte;
    private Bitmap mAvatar_bitmap;
    private RoundedBitmapDrawable mAvatar_rounded;


    public CommentClass(String id, String date, String owner, String text){

        mId = id;
        mDate =date;
        mOwner = owner;
        mText = text;
        String [] tokens = mDate.split(" ");
        mDMY = tokens[0];
        mHours = tokens[1];
        mAvatarbyte = null;
    }

    public String getmDMY() {
        return mDMY;
    }

    public String getmHours() {
        return mHours;
    }

    public String getmId() {
        return mId;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmOwner() {
        return mOwner;
    }

    public String getmText() {
        return mText;
    }

    public Bitmap makeAvatar(byte[] avatar) {
        mAvatarbyte = avatar;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        mAvatar_bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length, options);

        return mAvatar_bitmap;
    }
    public Bitmap getmAvatar_bitmap() {
        return mAvatar_bitmap;
    }

    public void setAvatar(RoundedBitmapDrawable avatar) {
        mAvatar_rounded = avatar;
    }

    public RoundedBitmapDrawable getmAvatar_rounded() {
        return mAvatar_rounded;
    }
}
