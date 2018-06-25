package com.wokesolutions.ignes.ignes;

public class CommentClass {

    public String mId;
    public String mDate;
    public String mOwner;
    public String mText;

    public CommentClass(String id, String date, String owner, String text){

        mId = id;
        mDate =date;
        mOwner = owner;
        mText = text;
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
}
