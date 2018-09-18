package com.uhungry.utils;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.uhungry.session.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.fabric.sdk.android.Fabric;

public class Uhungry extends Application {

    public static Uhungry instance = null;
    public static SessionManager sessionManager;
    public static final String TAG = Uhungry.class.getSimpleName();
    private RequestQueue mRequestQueue;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }


    public static Uhungry getInstance()
    {
        if (instance != null) {
            return instance;
        }
        return new Uhungry();
    }


    @Override
    public void onCreate()
    {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;
        sessionManager = new SessionManager(instance.getApplicationContext());

       /* try{
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String str1 = df.format(c.getTime());
            Date date1 = df.parse(str1);

            String str2 = sessionManager.getEndDate();
            Date date2 = df.parse(str2);

            if (date1.compareTo(date2)<=0)
            {
                sessionManager.setIsSubcribed(false);
                System.out.println("date2 is Greater than my date1");
            }

        }catch (ParseException e1){
            e1.printStackTrace();
        }*/
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null){
            //req.setRetryPolicy(new DefaultRetryPolicy(10000,0,1));
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    public void cancelAllPendingRequests() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }


}
