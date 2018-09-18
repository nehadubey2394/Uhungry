package com.uhungry.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.uhungry.R;


/**
 * Created by mindiii on 28/8/17.
 */

public class UhungryProgressBar extends ProgressBar {

    public UhungryProgressBar(Context context) {
        this(context, null);
    }

    public UhungryProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.colorPrimary),
                android.graphics.PorterDuff.Mode.MULTIPLY);
    }
}
