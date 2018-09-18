package com.uhungry.custom_widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

/**
 * Created by mindiii on 3/10/17.
 */

public class ButtonRobotoReguler extends AppCompatButton{

     public ButtonRobotoReguler(Context context) {
        super(context);
        init();
    }

    public ButtonRobotoReguler(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtonRobotoReguler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface font= Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto_Regular.ttf");
        this.setTypeface(font);
    }
    @Override
    public void setTypeface(Typeface tf, int style) {
        tf= Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto_Regular.ttf");
        super.setTypeface(tf, style);
    }

    @Override
    public void setTypeface(Typeface tf) {
        tf= Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto_Regular.ttf");
        super.setTypeface(tf);
    }
}
