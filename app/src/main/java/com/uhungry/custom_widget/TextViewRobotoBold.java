package com.uhungry.custom_widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;


public class TextViewRobotoBold extends AppCompatTextView {

    public TextViewRobotoBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewRobotoBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public TextViewRobotoBold(Context context) {
        super(context);
        init();
    }
    private void init() {
        if (!isInEditMode()) {
            Typeface mycustomfont = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto_Bold.ttf");
            setTypeface(mycustomfont);
        }
    }
}
