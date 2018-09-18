package com.uhungry.custom_widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;


public class EditTextRobotoLight extends AppCompatEditText {

    private Context context;
    private AttributeSet attrs;
    private int defStyle;

    public EditTextRobotoLight(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public EditTextRobotoLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        this.attrs=attrs;
        init();
    }

    public EditTextRobotoLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=context;
        this.attrs=attrs;
        this.defStyle=defStyle;
        init();
    }

    private void init() {
        Typeface font= Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto_Light.ttf");
        this.setTypeface(font);
    }
    @Override
    public void setTypeface(Typeface tf, int style) {
        tf= Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto_Light.ttf");
        super.setTypeface(tf, style);
    }

    @Override
    public void setTypeface(Typeface tf) {
        tf= Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto_Light.ttf");
        super.setTypeface(tf);
    }

}
