package com.uhungry.activity;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.uhungry.R;
import com.uhungry.adapter.NextStepCountAdapter;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.listner.CustomAdapterButtonListener;
import com.uhungry.model.Discover;
import com.uhungry.model.RecipeSteps;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AllStepsActivity extends AppCompatActivity implements View.OnClickListener,CustomAdapterButtonListener {
    private TextView tvStepsDescription,tvTitle;
    private ArrayList<RecipeSteps> recipeStepses;
    private NextStepCountAdapter listAdapter;
    private Button btnStepsDone;
    private String recipeId,type,content,isFav;
    private RequestQueue requestQueue;
    private CheckBox chbLikeYes,chbLikeNo;
    private ImageView ivPlay,stepsVideoView,ivSteps,btnNext,btnPrevious;
    private RelativeLayout lyStepsVideo;
    private int currentPosition=0,tempPos=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pagger);
        setupView();

    }

    private void setupView() {
        Intent i = getIntent();
        recipeStepses = (ArrayList<RecipeSteps>) i.getSerializableExtra("stepsList");

        recipeId = i.getStringExtra("recipeId");
        tvStepsDescription = (TextView) findViewById(R.id.tvStepsDescription);
        LinearLayout lyStepsCross = (LinearLayout) findViewById(R.id.lyStepsCross);
        lyStepsCross.setVisibility(View.VISIBLE);
        btnStepsDone = (Button) findViewById(R.id.btnStepsDone);

        btnNext = (ImageView) findViewById(R.id.btnNext);
        btnPrevious = (ImageView) findViewById(R.id.btnPrevious);
        //btnPrevious.setEnabled(false);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        stepsVideoView = (ImageView) findViewById(R.id.stepsVideoView);
        ivSteps = (ImageView) findViewById(R.id.ivSteps);
        lyStepsVideo = (RelativeLayout) findViewById(R.id.lyStepsVideo);

        tvTitle = (TextView) findViewById(R.id.actionbarLayout_title);
        ImageView actionbar_btton_back = (ImageView) findViewById(R.id.actionbar_btton_back);
        actionbar_btton_back.setVisibility(View.VISIBLE);

        listAdapter = new NextStepCountAdapter(AllStepsActivity.this, recipeStepses);
        listAdapter.setCustomListener(AllStepsActivity.this);

        if (recipeStepses.size()!=0){
            tvStepsDescription.setText(recipeStepses.get(0).stepDes);
            tvTitle.setText("Step "+recipeStepses.get(0).stepNo);
            type = recipeStepses.get(0).contentType;
            content = recipeStepses.get(0).stepContent;

            if (recipeStepses.get(0).contentType.equals("image")){
                lyStepsVideo.setVisibility(View.VISIBLE);
                ivPlay.setVisibility(View.GONE);
                ivSteps.setVisibility(View.GONE);
                Picasso.with(AllStepsActivity.this).load(recipeStepses.get(0).stepContent).fit().into(stepsVideoView);
            }
            else if (recipeStepses.get(0).contentType.equals("video")){
                lyStepsVideo.setVisibility(View.VISIBLE);
                ivPlay.setVisibility(View.VISIBLE);
                ivSteps.setVisibility(View.GONE);
                Picasso.with(AllStepsActivity.this).load(recipeStepses.get(0).contentThumb).fit().into(stepsVideoView);

            }else {
                lyStepsVideo.setVisibility(View.GONE);
                ivPlay.setVisibility(View.GONE);
                ivSteps.setVisibility(View.VISIBLE);
            }
        }else {
            tvTitle.setText("Steps");
            AppUtility.showDialog(AllStepsActivity.this,"Steps are not Available for this Recipe","Alert","Ok");
            //  AppUtility.showAlertDialog_SingleButton(AllStepsActivity.this,"Steps are not Available for this Recipe","Alert!","Ok");
        }

        //addItems();

        RecyclerView rvBubble = (RecyclerView)findViewById(R.id.rvBubble);
        LinearLayoutManager layoutManager = new LinearLayoutManager(AllStepsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvBubble.setLayoutManager(layoutManager);
        rvBubble.setAdapter(listAdapter);

        btnStepsDone.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        lyStepsCross.setOnClickListener(this);
        actionbar_btton_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.actionbar_btton_back :
                finish();
                overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                break;

            case  R.id.lyStepsCross :
                View DialogView = View.inflate(AllStepsActivity.this, R.layout.dialog_recipe_layout, null);

                final Dialog alertDailog = new Dialog(AllStepsActivity.this, android.R.style.Theme_Light);
                alertDailog.setCanceledOnTouchOutside(false);
                alertDailog.setCancelable(false);

                alertDailog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDailog.getWindow().getAttributes().windowAnimations = R.style.InOutAnimation;
                alertDailog.setContentView(DialogView);

                Button btnYes = (Button) DialogView.findViewById(R.id.btnYes);
                Button btnNo = (Button) DialogView.findViewById(R.id.btnNo);
                ImageView ivCross = (ImageView) DialogView.findViewById(R.id.ivCross);
                TextView tv_msg = (TextView) DialogView.findViewById(R.id.tv_msg);
                TextView tv_title = (TextView) DialogView.findViewById(R.id.tv_title);

                tv_msg.setText(getString(R.string.text_leave_steps));
                tv_title.setText("Alert!");

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDailog.cancel();
                        Intent intent = new Intent(AllStepsActivity.this, WellcomeActivity.class);
                        startActivity(intent);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDailog.cancel();
                    }
                });

                ivCross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDailog.cancel();
                    }
                });

                alertDailog.show();

              /*  AlertDialog.Builder builder1 = new AlertDialog.Builder(AllStepsActivity.this);
                builder1.setTitle("Alert");
                builder1.setMessage(getString(R.string.text_leave_steps));
                builder1.setCancelable(true);
                builder1.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = new Intent(AllStepsActivity.this, WellcomeActivity.class);
                                startActivity(intent);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                            }
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
                break;

            case  R.id.btnStepsDone :
                Uhungry.sessionManager.setFirstRecipe(recipeId);
                showAlertDialog(getString(R.string.text_remove_ing),"Alert");

               /* AlertDialog.Builder builder2 = new AlertDialog.Builder(AllStepsActivity.this);
                builder2.setTitle("Alert");
                builder2.setMessage(getString(R.string.text_remove_ing));
                builder2.setCancelable(true);
                builder2.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = new Intent(AllStepsActivity.this, MissingIngredientsActivity.class);
                                intent.putExtra("commingFrom","AllStepsActivity");
                                intent.putExtra("recipeId",recipeId);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                            }
                        });
                builder2.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                showDialog();
                            }
                        });

                AlertDialog alert2 = builder2.create();
                alert2.show();
*/
                break;

            case  R.id.ivPlay :
                if (type.equals("video")){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.parse(content);
                    i.setDataAndType(data, "video/mp4");
                    i.putExtra (MediaStore.EXTRA_FINISH_ON_COMPLETION, false);

                    try {
                        startActivity(i);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        AppUtility.showToast(AllStepsActivity.this,"There is no video player installed in your phone.",0);
                    }
                }
                break;

            case  R.id.chbLikeYes :
                chbLikeNo.setChecked(false);
                break;

            case  R.id.chbLikeNo :
                chbLikeYes.setChecked(false);
                break;

            case  R.id.btnPrevious :

                if(currentPosition>0) {
                    currentPosition--;
                    updateUI();
                }

                break;

            case  R.id.btnNext :
                Uhungry.sessionManager.setFirstRecipe(recipeId);
                if(recipeStepses.size()>currentPosition){
                    currentPosition++;
                    tempPos = currentPosition+1;
                    updateUI();
                }

                break;
        }
    }

    public void showAlertDialog(String msg, String title) {
        View DialogView = View.inflate(AllStepsActivity.this, R.layout.dialog_recipe_layout, null);

        final Dialog alertDailog = new Dialog(AllStepsActivity.this, android.R.style.Theme_Light);
        alertDailog.setCanceledOnTouchOutside(false);
        alertDailog.setCancelable(false);

        alertDailog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDailog.getWindow().getAttributes().windowAnimations = R.style.InOutAnimation;
        alertDailog.setContentView(DialogView);

        Button btnYes = (Button) DialogView.findViewById(R.id.btnYes);
        Button btnNo = (Button) DialogView.findViewById(R.id.btnNo);
        ImageView ivCross = (ImageView) DialogView.findViewById(R.id.ivCross);
        TextView tv_msg = (TextView) DialogView.findViewById(R.id.tv_msg);
        TextView tv_title = (TextView) DialogView.findViewById(R.id.tv_title);

        tv_msg.setText(msg);
        tv_title.setText(title);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDailog.cancel();
                Intent intent = new Intent(AllStepsActivity.this, MissingIngredientsActivity.class);
                intent.putExtra("commingFrom","AllStepsActivity");
                intent.putExtra("recipeId",recipeId);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDailog.cancel();
                showDialog();
            }
        });

        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDailog.cancel();
            }
        });

        alertDailog.show();
    }


    private void updateUI(){

        int recipeSize = recipeStepses.size();

        for(int i=0; i<recipeStepses.size(); i++){
            RecipeSteps item = recipeStepses.get(i);

            if(i<=currentPosition){
                item.isViewd = true;
                type = item.contentType;
                content = item.stepContent;
            }else {
                item.isViewd = false;
            }

            listAdapter.notifyDataSetChanged();
        }

        if(recipeSize==1){
            btnPrevious.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
            listAdapter.notifyDataSetChanged();
        }else if(currentPosition==0){
            btnPrevious.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
        }else if(currentPosition==recipeSize-1){
            btnNext.setVisibility(View.GONE);
            btnPrevious.setVisibility(View.VISIBLE);
        }else if(currentPosition>0 && currentPosition<recipeSize-1){
            btnNext.setVisibility(View.VISIBLE);
            btnPrevious.setVisibility(View.VISIBLE);
        }

        if(currentPosition==(recipeSize-1))
            btnStepsDone.setVisibility(View.VISIBLE);
        else
            btnStepsDone.setVisibility(View.GONE);

        RecipeSteps item = recipeStepses.get(currentPosition);
        tvStepsDescription.setText(item.stepDes);
        tvTitle.setText(String.format("Step %s", String.valueOf(item.stepNo)));

        switch (item.contentType) {
            case "image":
                lyStepsVideo.setVisibility(View.VISIBLE);
                ivPlay.setVisibility(View.GONE);
                ivSteps.setVisibility(View.GONE);
                Picasso.with(AllStepsActivity.this).load(item.stepContent).fit().into(stepsVideoView);

                break;
            case "video":
                lyStepsVideo.setVisibility(View.VISIBLE);
                ivPlay.setVisibility(View.VISIBLE);
                ivSteps.setVisibility(View.GONE);
                Picasso.with(AllStepsActivity.this).load(item.contentThumb).fit().into(stepsVideoView);

                break;
            default:
                lyStepsVideo.setVisibility(View.GONE);
                ivPlay.setVisibility(View.GONE);
                ivSteps.setVisibility(View.VISIBLE);
                break;
        }
    }

    public  void showDialog() {
        View DialogView = View.inflate(AllStepsActivity.this, R.layout.dialog_like_layout, null);

        final Dialog alertDailog = new Dialog(AllStepsActivity.this, android.R.style.Theme_Light);
        alertDailog.setCanceledOnTouchOutside(true);
        alertDailog.setCancelable(true);

        alertDailog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDailog.getWindow().getAttributes().windowAnimations = R.style.InOutAnimation;
        alertDailog.setContentView(DialogView);

        chbLikeYes = (CheckBox)DialogView. findViewById(R.id.chbLikeYes);
        chbLikeNo = (CheckBox) DialogView.findViewById(R.id.chbLikeNo);

        chbLikeYes.setOnClickListener(this);
        chbLikeNo.setOnClickListener(this);

        ImageView ivCross = (ImageView) DialogView.findViewById(R.id.ivCross);
        Button btnDone = (Button) DialogView.findViewById(R.id.btnDone);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AppUtility.isNetworkAvailable(AllStepsActivity.this)) {
                    alertDailog.cancel();
                    if (!chbLikeYes.isChecked() &&  !chbLikeNo.isChecked()){
                        AppUtility.showToast(AllStepsActivity.this,"Please select your choice",0);
                    }else {
                        if(AppUtility.isNetworkAvailable(AllStepsActivity.this)) {
                            if (!chbLikeYes.isChecked() &&  !chbLikeNo.isChecked()){
                                AppUtility.showToast(AllStepsActivity.this,"Please select your choice",0);
                            }else {
                                alertDailog.cancel();
                                if (chbLikeYes.isChecked()){
                                    isFav = "1";
                                    AddFav();
                                }else {
                                    isFav = "0";
                                    AddFav();
                                }
                                saveRecipeInHistory();

                                // saveRecipeInHistory();
                            }
                        }
                        else {
                            AppUtility.showAlertDialog_SingleButton(AllStepsActivity.this,getResources().getString(R.string.network_error),"Alert!","Ok");
                        }


                      /*  if (chbLikeYes.isChecked()){
                            AddFav();
                        }

                        if(AppUtility.isNetworkAvailable(AllStepsActivity.this)) {

                            if (Uhungry.sessionManager.isSubcribed()) {
                                saveRecipeInHistory();
                            }else {

                            }
                        }
                        else {
                            AppUtility.showAlertDialog_SingleButton(AllStepsActivity.this,getResources().getString(R.string.network_error),"Alert","Ok");
                        }*/
                    }
                }
                else {
                    AppUtility.showAlertDialog_SingleButton(AllStepsActivity.this,getResources().getString(R.string.network_error),"Alert!","Ok");
                }

            }
        });

        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDailog.cancel();
            }
        });

        alertDailog.show();
    }

    private void saveRecipeInHistory() {
        final ProgressDialog customDialog = new ProgressDialog(this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+"saveRecipe",

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("success")) {

                                if (Uhungry.sessionManager.isSubcribed()) {
                                    //   AppUtility.showToast(AllStepsActivity.this,"Recipe added to history",0);
                                    Intent intent = new Intent(AllStepsActivity.this, WellcomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                                    finish();

                                }else {
                                    new AppUtility().showSubcriptionDialog(AllStepsActivity.this, new AppUtility.SubcriptionListner() {
                                        @Override
                                        public void onSubcribeClick() {
                                            Intent intent = new Intent(AllStepsActivity.this, SubcriptionActivity.class);
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                                        }
                                    });
                                }
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(AllStepsActivity.this,message,"Error","Ok");

                            }


                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        customDialog.cancel();
                        AppUtility.showAlertDialog_SingleButton(AllStepsActivity.this,"Something went wrong, please check after some time.","Error","Ok");
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();

                header.put("recipeId", recipeId);

                return header;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String AuthToken = Uhungry.sessionManager.getAuthToken();
                if (!AuthToken.equals("")){

                    header.put(Constant.AUTHTOKEN, AuthToken);
                }
                return header;
            }
        };

        requestQueue = Volley.newRequestQueue(AllStepsActivity.this);
        requestQueue.add(stringRequest);
    }

    private void AddFav() {
        final ProgressDialog customDialog = new ProgressDialog(AllStepsActivity.this);
        //  customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+"addFavourites",

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  customDialog.cancel();
                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  customDialog.cancel();
                        AppUtility.showAlertDialog_SingleButton(AllStepsActivity.this,"Something went wrong, please check after some time.","Error","Ok");
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();

                header.put("isFav", isFav);
                header.put("recipeId", recipeId);

                return header;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String AuthToken = Uhungry.sessionManager.getAuthToken();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }
                return header;
            }
        };

        requestQueue = Volley.newRequestQueue(AllStepsActivity.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000,0,1));
        requestQueue.add(stringRequest);
    }


    @Override
    public void onButtonClick(int position, String buttonText, int selectedCount) {
        if (position<=tempPos) {
            currentPosition = position;
            updateUI();
        }
    }

    @Override
    public void onFilterApply(ArrayList<Discover> arrayList, boolean b) {

    }
}
