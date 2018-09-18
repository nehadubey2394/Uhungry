package com.uhungry.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.uhungry.adapter.FaqExpandableAdapter;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.model.Faq;
import com.uhungry.model.FaqAwns;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaqActivity extends AppCompatActivity implements View.OnClickListener{
    private FaqExpandableAdapter adapter;
    private ArrayList<Faq> faqs ;
    private  RecyclerView recyclerView;
    private TextView tvEmptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        faqs = new ArrayList<>();
        tvEmptyList = (TextView) findViewById(R.id.tvEmptyList);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        TextView tvTitle = (TextView) findViewById(R.id.actionbarLayout_title);
        ImageView actionbar_btton_back = (ImageView) findViewById(R.id.actionbar_btton_back);
        actionbar_btton_back.setVisibility(View.VISIBLE);
        tvTitle.setText(getString(R.string.title_faq));

        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        recyclerView.setLayoutManager(layoutManager);
        if(AppUtility.isNetworkAvailable(FaqActivity.this)) {
            getFaq();
        }
        else {
            AppUtility.showAlertDialog_SingleButton(FaqActivity.this,getResources().getString(R.string.network_error),"Alert!","Ok");
        }

        actionbar_btton_back.setOnClickListener(this);
    }

    public void getFaq() {
        final ProgressDialog customDialog = new ProgressDialog(FaqActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.BASE_URL_USER+"FAQ?"/*+"foodId="+Id*/,
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
                            String description="";
                            if (status.equalsIgnoreCase("success")) {

                                faqs.clear();

                                JSONArray jsonArray = jsonObj.getJSONArray("data");
                                if(jsonArray != null && jsonArray.length() > 0 ) {
                                    tvEmptyList.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        JSONArray desArray = jsonObject.getJSONArray("description");
                                        List<FaqAwns> items = new ArrayList<>();

                                        for (int j = 0; j < desArray.length(); j++) {
                                            description = desArray.getString(j);
                                            FaqAwns faqAwns = new FaqAwns(description,true);
                                            items.add(faqAwns);
                                        }
                                        //String id = jsonObject.getString("id");
                                        String id = String.valueOf(i+1);
                                        String title = jsonObject.getString("title").trim();

                                        Faq item = new Faq(id,title, items, R.drawable.drop_down_icon);
                                        faqs.add(item);

                                    }
                                    adapter = new FaqExpandableAdapter(faqs);
                                    recyclerView.setAdapter(adapter);
                                    // adapter.notifyDataSetChanged();

                                }else {
                                    tvEmptyList.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                }

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
                        Toast.makeText(FaqActivity.this, "‘Ooops! Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                return header;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String AuthToken = Uhungry.sessionManager.getAuthToken();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }else {
                    Toast.makeText(FaqActivity.this, "Already logged in on another device", Toast.LENGTH_LONG).show();
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(FaqActivity.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,1));
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.actionbar_btton_back:
                finish();
                overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                break;
        }
    }
}
