package com.uhungry.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uhungry.R;
import com.uhungry.adapter.PreferenceListAdapter;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.model.FoodType;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PreferenceActivity extends AppCompatActivity implements View.OnClickListener{
    private ArrayList<FoodType> foodTypes;
    private PreferenceListAdapter preferenceListAdapter;
    private RecyclerView rvPreference;
    private TextView tvEmptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        bindView();
    }

    private void  bindView(){
        foodTypes = new ArrayList<>();

        TextView tvTitle = (TextView) findViewById(R.id.actionbarLayout_title);
        tvEmptyList = (TextView) findViewById(R.id.tvEmptyList);
        ImageView actionbar_btton_back = (ImageView) findViewById(R.id.actionbar_btton_back);
        CheckBox chbAll = (CheckBox) findViewById(R.id.chbAll);
        chbAll.setVisibility(View.VISIBLE);
        Button btnPrefDone = (Button) findViewById(R.id.btnPrefDone);
        actionbar_btton_back.setVisibility(View.VISIBLE);
        tvTitle.setText(getString(R.string.title_pref));

        rvPreference = (RecyclerView) findViewById(R.id.rvPreference);

        preferenceListAdapter = new PreferenceListAdapter(PreferenceActivity.this,foodTypes,btnPrefDone,chbAll);

        rvPreference.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(PreferenceActivity.this);
        rvPreference.setLayoutManager(linearLayoutManager2);
        rvPreference.setAdapter(preferenceListAdapter);

        if(AppUtility.isNetworkAvailable(PreferenceActivity.this)) {
            GetFoodTypeListTask();
        }
        else {
            AppUtility.showAlertDialog_SingleButton(PreferenceActivity.this,getResources().getString(R.string.network_error),"Alert!","Ok");
        }

        actionbar_btton_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.actionbar_btton_back:
                finish();
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                break;
        }
    }

    public void GetFoodTypeListTask() {
        final ProgressDialog customDialog = new ProgressDialog(PreferenceActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.BASE_URL_USER+"getAllFoodTypes",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();

                        System.out.println("category response" + response);
                        JSONObject jsonObj;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("success")) {
                                tvEmptyList.setVisibility(View.GONE);
                                rvPreference.setVisibility(View.VISIBLE);
                                foodTypes.clear();
                                JSONArray jsonArray = jsonObj.getJSONArray("data");

                                if(jsonArray != null && jsonArray.length() > 0 ) {

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        FoodType item = new FoodType();
                                        item.foodId = jsonObject.getString("foodId").trim();
                                        item.foodName = jsonObject.getString("foodName").trim();
                                        item.isChecked = jsonObject.getString("isSelected").trim();
                                     //   if(jsonObject.getInt("isSelected") != 0) Uhungry.prefCount++;

                                        foodTypes.add(item);

                                    }
                                    preferenceListAdapter.notifyDataSetChanged();
                                }
                            }
                            else {
                                tvEmptyList.setVisibility(View.VISIBLE);
                                rvPreference.setVisibility(View.GONE);
                                customDialog.cancel();
                                //AppUtility.showAlertDialog_SingleButton(PreferenceActivity.this,message,"Alert!","Ok");
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
                        Toast.makeText(PreferenceActivity.this, "‘Ooops! Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                // header.put("dataStatus", dataStatus);
                return header;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String AuthToken = Uhungry.sessionManager.getAuthToken();
                if (!AuthToken.equals("")){

                    header.put(Constant.AUTHTOKEN, AuthToken);
                }else {
                    Toast.makeText(PreferenceActivity.this, "Already logged in on another device", Toast.LENGTH_LONG).show();
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(PreferenceActivity.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,1));
        requestQueue.add(stringRequest);
    }

}
