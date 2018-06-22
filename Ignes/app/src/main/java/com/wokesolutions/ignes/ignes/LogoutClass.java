package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.content.Context;

public class LogoutClass {

    public LogoutClass(Activity activity, Context context, String token, int request){

        RequestsVolley.logoutRequest(token, context, activity,request);

    }

}
