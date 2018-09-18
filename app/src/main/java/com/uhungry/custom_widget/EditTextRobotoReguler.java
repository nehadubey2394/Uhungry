package com.uhungry.custom_widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;


public class EditTextRobotoReguler extends AppCompatEditText {

    private Context context;
    private AttributeSet attrs;
    private int defStyle;

    public EditTextRobotoReguler(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public EditTextRobotoReguler(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        this.attrs=attrs;
        init();
    }

    public EditTextRobotoReguler(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=context;
        this.attrs=attrs;
        this.defStyle=defStyle;
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
