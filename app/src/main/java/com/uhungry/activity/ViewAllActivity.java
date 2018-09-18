package com.uhungry.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uhungry.R;
import com.uhungry.adapter.DiscoverChefListAdapter;
import com.uhungry.adapter.DiscoverVideoListAdapter;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.listner.EndlessRecyclerViewScrollListener;
import com.uhungry.model.Discover;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewAllActivity extends AppCompatActivity {
    private ArrayList<Discover> discoverArrayList;
    private DiscoverChefListAdapter chefListAdapter;
    private DiscoverVideoListAdapter videoListAdapter;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        bindView();
    }
    private void bindView() {
        Intent i = getIntent();
        //  discoverArrayList = (ArrayList<Discover>) i.getSerializableExtra("discover");
        discoverArrayList = new ArrayList<>();
        discoverArrayList.clear();
        key = i.getStringExtra("key");
        RecyclerView rvGrid = (RecyclerView) findViewById(R.id.rvGrid);
        chefListAdapter = new DiscoverChefListAdapter(ViewAllActivity.this, discoverArrayList, "activity");
        videoListAdapter = new DiscoverVideoListAdapter(ViewAllActivity.this, discoverArrayList, "activity");

        TextView tvTitle = (TextView) findViewById(R.id.actionbarLayout_title);
        ImageView actionbar_btton_back = (ImageView) findViewById(R.id.actionbar_btton_back);
        actionbar_btton_back.setVisibility(View.VISIBLE);

        if (key.equals("1")) {
            tvTitle.setText(getString(R.string.text_featured_chefs));
        } else if (key.equals("2")) {
            tvTitle.setText(getString(R.string.text_videos));
        } else if (key.equals("3")) {
            tvTitle.setText(getString(R.string.text_uhungry_content));
        } else {
            tvTitle.setText(getString(R.string.text_news));
        }


        GridLayoutManager layoutManager = new GridLayoutManager(ViewAllActivity.this, 2);
        rvGrid.setItemAnimator(new DefaultItemAnimator());
        rvGrid.setLayoutManager(layoutManager);

        if (key.equals("2")) {
            rvGrid.setAdapter(videoListAdapter);
        } else {
            rvGrid.setAdapter(chefListAdapter);
        }

        if (AppUtility.isNetworkAvailable(ViewAllActivity.this)) {
            getDiscoverData("0");
        } else {
            AppUtility.showAlertDialog_SingleButton(ViewAllActivity.this, getResources().getString(R.string.network_error), "Alert!", "Ok");
        }


        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };

        rvGrid.addOnScrollListener(scrollListener);

        actionbar_btton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);

            }
        });
    }

    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        String page = String.valueOf(offset);

        if(AppUtility.isNetworkAvailable(ViewAllActivity.this)) {
            getDiscoverData(page);
        }
        else {
            AppUtility.showAlertDialog_SingleButton(ViewAllActivity.this,getResources().getString(R.string.network_error),"Alert!","Ok");
        }

    }

    private void getDiscoverData(final String page) {
        final ProgressDialog customDialog = new ProgressDialog(ViewAllActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+"getDiscoverData",

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
                                JSONArray jsonArray = jsonObj.getJSONArray("discoverData");
                                if(jsonArray != null && jsonArray.length() > 0 ) {

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        Discover item = new Discover();

                                        item.id = jsonObject.getString("id").trim();
                                        item.title = jsonObject.getString("title").trim();
                                        item.contentType = jsonObject.getString("contentType").trim();
                                        item.content = jsonObject.getString("content").trim();
                                        item.contentImage = jsonObject.getString("contentImage").trim();

                                        discoverArrayList.add(item);
                                    }
                                    if (key.equals("2"))
                                        videoListAdapter.notifyDataSetChanged();
                                    else
                                        chefListAdapter.notifyDataSetChanged();
                                }
                            }else {
                               /* if (page.equals("0")){
                                    tvExploreEmptyList.setVisibility(View.VISIBLE);
                                    rvExploreEvent.setVisibility(View.GONE);
                                    tvExploreEmptyList.setText("No Event available");
                                }*/
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
                        AppUtility.showAlertDialog_SingleButton(ViewAllActivity.this,"Something went wrong, please check after some time.","Error","Ok");
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("page", page);
                header.put("disCid", key);

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

        RequestQueue requestQueue = Volley.newRequestQueue(ViewAllActivity.this);
        requestQueue.add(stringRequest);
    }
}

