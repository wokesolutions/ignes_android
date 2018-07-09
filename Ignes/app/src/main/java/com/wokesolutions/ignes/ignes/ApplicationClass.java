package com.wokesolutions.ignes.ignes;

public class ApplicationClass {

    private String mNameOrg;
    private String mBudget;
    private String mInfo;
    private String mEmailOrg;
    private String mNIFOrg;
    private String mPhone;
    private String mReportId;
    private boolean mRequested;

    public ApplicationClass (String nameOrg, String budget, String info, String emailOrg,
                             String nifOrg, String phoneOrg, String reportId){

        mNameOrg = nameOrg;
        mBudget = budget;
        mInfo =info;
        mEmailOrg =emailOrg;
        mNIFOrg = nifOrg;
        mPhone = phoneOrg;
        mRequested = false;
        mReportId = reportId;
    }

    public String getmReportId() {
        return mReportId;
    }

    public void setmRequested(boolean mRequested) {
        this.mRequested = mRequested;
    }

    public boolean getmRequested(){
        return mRequested;
    }

    public String getmPhone() {
        return mPhone;
    }

    public String getmNameOrg() {
        return mNameOrg;
    }

    public String getmBudget() {
        return mBudget;
    }

    public String getmEmailOrg() {
        return mEmailOrg;
    }

    public String getmInfo() {
        return mInfo;
    }

    public String getmNIFOrg() {
        return mNIFOrg;
    }
}
