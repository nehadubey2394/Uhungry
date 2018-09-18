package com.uhungry.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uhungry.R;
import com.uhungry.adapter.CustomPagerAdapter;

public class HelpActivity extends AppCompatActivity {
    private LinearLayout ll_dots;
    private String message = "help";
    int[] slider_image_list = {
            R.drawable.welcome_screen,
            R.drawable.set_up_kitchen_screen,
            R.drawable.my_grocery_sceeen,
            R.drawable.discover,
            R.drawable.favourite,
            R.drawable.recipe,
            R.drawable.recipe_detail,
            R.drawable.steps,
            R.drawable.steps,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_help);


        Intent intent = getIntent();
        if (intent != null) {
            message = intent.getStringExtra("message");
        }

        CustomPagerAdapter pageAdapter = new CustomPagerAdapter(HelpActivity.this, slider_image_list,message);

        ll_dots = (LinearLayout) findViewById(R.id.ll_dots);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(pageAdapter);
        addBottomDots(0);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==slider_image_list.length-1){
                    if (message.equals("User registration successfully done")){

                        Intent intent = new Intent(HelpActivity.this, WellcomeActivity.class);
                        intent.putExtra("message",message);
                        startActivity(intent);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                        finish();
                    }else {
                        finish();
                        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                    }
                }
                else {
                    addBottomDots(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[slider_image_list.length-1];

        ll_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.parseColor("#e27070"));
            ll_dots.addView(dots[i]);
        }

        if (dots.length >0){
            dots[currentPage].setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public void onBackPressed() {

    }
}
