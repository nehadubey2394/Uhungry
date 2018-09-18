package com.uhungry.custom_widget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.uhungry.R;
import com.uhungry.helper.ProgressBarHelper;


public class Progress {

    public static ProgressDialog dialog;
    private static ProgressBarHelper progressBarHelper;

    public static void DisplayLoader(Context ctn){

        Activity activity = (Activity) ctn;

        if(progressBarHelper==null){
            progressBarHelper = new ProgressBarHelper(activity);
        }
        progressBarHelper.setText(R.string.text_loading).show();

    }
    public static void HideLoader(Context ctn) {

        if(progressBarHelper!=null)
            progressBarHelper.hide();

    }
}
