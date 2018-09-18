package com.uhungry.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uhungry.R;
import com.uhungry.activity.LoginActivity;
import com.uhungry.activity.SplashActivity;
import com.uhungry.activity.WellcomeActivity;

public class CustomPagerAdapter extends PagerAdapter {
    // Declare Variables
    private Activity context;
    private int[] images;
    private String message;

    public CustomPagerAdapter(Activity context, int[] images,String message) {
        this.context = context;
        this.images = images;
        this.message = message;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImageView image;
        View view;
        LinearLayout lyCross;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.pager_item, container,
                false);

        // Locate the ImageView in viewpager_item.xml
        image = (ImageView) itemView.findViewById(R.id.image);
        view = (View) itemView.findViewById(R.id.view);
        lyCross = (LinearLayout) itemView.findViewById(R.id.lyCross);
        // Capture position and set to the ImageView
        image.setImageResource(images[position]);

        switch (position){
            case 0 :
                view.setEnabled(true);
                lyCross.setVisibility(View.GONE);
                break;
            case 1 :
                lyCross.setVisibility(View.VISIBLE);
                view.setEnabled(false);
                break;
            case 2 :
                lyCross.setVisibility(View.VISIBLE);
                view.setEnabled(false);

                break;
            case 3 :
                lyCross.setVisibility(View.VISIBLE);
                view.setEnabled(false);

                break;
            case  4 :
                lyCross.setVisibility(View.VISIBLE);
                view.setEnabled(false);

                break;
            case 5:
                lyCross.setVisibility(View.VISIBLE);
                view.setEnabled(false);
                break;
            case 6:
                lyCross.setVisibility(View.VISIBLE);
                view.setEnabled(false);
                break;
            case 7:
                lyCross.setVisibility(View.VISIBLE);
                view.setEnabled(false);
               /* if (message.equals("User registration successfully done")){

                    Intent intent = new Intent(context, WellcomeActivity.class);
                    intent.putExtra("message",message);
                    context.startActivity(intent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                    context.finish();
                }else {
                    context.finish();
                    context.overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                }*/
                break;
          /*  case 8:
                lyCross.setVisibility(View.VISIBLE);
                view.setEnabled(false);
                if (message.equals("User registration successfully done")){

                    Intent intent = new Intent(context, WellcomeActivity.class);
                    intent.putExtra("message",message);
                    context.startActivity(intent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                    context.finish();
                }else {
                    context.finish();
                    context.overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                }
                break;*/
        }

        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.equals("User registration successfully done")){
                    Intent intent = new Intent(context, WellcomeActivity.class);
                    intent.putExtra("message",message);
                    context.startActivity(intent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                    context.finish();
                }else {
                    context.finish();
                    context.overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                }

            }
        });
        lyCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.equals("User registration successfully done")){
                    Intent intent = new Intent(context, WellcomeActivity.class);
                    intent.putExtra("message",message);
                    context.startActivity(intent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                    context.finish();
                   /* Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                    context.finish();
                    context.overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                }else if (message.equals("session")){
                    Intent showLogin = new Intent(context,LoginActivity.class);
                    showLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    showLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    showLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(showLogin);
                    context.overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);*/
                }else {
                    context.finish();
                    context.overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                }
               /* AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("Alert");
                builder1.setMessage(context.getString(R.string.text_leave_help));
                builder1.setCancelable(true);
                builder1.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                context.finish();
                                context.overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);                            }
                        });
                builder1.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();*/

            }
        });
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}
