package com.uhungry.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uhungry.R;
import com.uhungry.adapter.FavListAdapter;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.helper.Helper;
import com.uhungry.listner.CustomAdapterButtonListener;
import com.uhungry.model.Discover;
import com.uhungry.model.Recipies;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HistoryFragment extends Fragment implements View.OnClickListener , CustomAdapterButtonListener{
    private Context mContex;
    private static LinearLayout actionBar;
    private RecyclerView rycHistoryList;
    private TextView tvNoRecored;
    private ArrayList<Recipies> arrayList;
    private FavListAdapter listAdapter;

    public HistoryFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, LinearLayout view) {
        HistoryFragment fragment = new HistoryFragment();
        actionBar = view;
        Bundle args = new Bundle();
        args.putString("ARG_PARAM", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("ARG_PARAM");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.history_fregment, container, false);
        bindView(rootView);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContex = context;
    }

    private void bindView(View rootView){
        arrayList = new ArrayList<>();
        listAdapter = new FavListAdapter(getActivity(),arrayList,"HistoryFragment");;
        TextView tvTitle = (TextView) rootView.findViewById(R.id.actionbarLayout_title);
        tvNoRecored = (TextView) rootView.findViewById(R.id.tvNoRecored);
        ImageView actionbar_btton_back = (ImageView) rootView.findViewById(R.id.actionbar_btton_back);
        actionbar_btton_back.setVisibility(View.VISIBLE);
        tvTitle.setText(getString(R.string.title_history));
        rycHistoryList = (RecyclerView) rootView.findViewById(R.id.rycHistoryList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContex);
        rycHistoryList.setLayoutManager(layoutManager);
        rycHistoryList.setHasFixedSize(true);
        rycHistoryList.setAdapter(listAdapter);
        listAdapter.setCustomListener(HistoryFragment.this);
        actionbar_btton_back.setOnClickListener(this);

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(AppUtility.isNetworkAvailable(getActivity())) {
            getHistoryRecipies();
        }
        else {
            AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.actionbar_btton_back :
                actionBar.setVisibility(View.VISIBLE);
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                getActivity().overridePendingTransition(0,0);
                break;
        }
    }

    public void getHistoryRecipies() {

        final ProgressDialog customDialog = new ProgressDialog(getActivity());
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.BASE_URL_USER+"getHistory" ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObj;
                        try {
                            String fName = "";
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");
                            String userstatus = jsonObj.getString("userstatus");
                            if (userstatus.equals("1")){
                                if (status.equalsIgnoreCase("success")) {

                                    customDialog.cancel();
                                    tvNoRecored.setVisibility(View.GONE);
                                    rycHistoryList.setVisibility(View.VISIBLE);
                                    arrayList.clear();

                                    JSONArray jsonArray = jsonObj.getJSONArray("data");

                                    if(jsonArray != null && jsonArray.length() > 0 ) {

                                        for (int i = 0; i < jsonArray.length(); i++) {

                                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                                            Recipies item = new Recipies();

                                            item.recipeId = jsonObject.getString("recipeId").trim();
                                            item.recName = jsonObject.getString("title").trim();
                                            item.time = jsonObject.getString("estimatedTime").trim();
                                            item.recipeImg = jsonObject.getString("recImage").trim();

                                            arrayList.add(item);

                                        }
                                        listAdapter.notifyDataSetChanged();
                                    }

                                }else {
                                    customDialog.cancel();
                                    tvNoRecored.setVisibility(View.VISIBLE);
                                    rycHistoryList.setVisibility(View.GONE);
                                }
                            }else{
                                AppUtility.sessionExpire(getActivity(), "User currently inactive from admin.", "Alert!","Ok");
                            }

                        } catch (Exception ex) {
                            Log.e("TAG",ex.toString());
                            ex.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        String errorMessage = "Something went wrong";
                        customDialog.cancel();
                        String result = new String(networkResponse.data);
                        try {
                            JSONObject response = new JSONObject(result);
                            String message = response.getString("message");
                            Log.e("Error Message", message);

                            if (message.equals("Invalid Auth Token")){
                                AppUtility.sessionExpire(getActivity(), "Your Session is expired, please login again.", "Alert!","Ok");
                            }else
                                AppUtility.showAlertDialog_SingleButton(getActivity(), Helper.error_Messages(error),"Error","Ok");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("Error", errorMessage);
                        error.printStackTrace();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String AuthToken = Uhungry.sessionManager.getAuthToken();
                if (!AuthToken.equals("")){

                    header.put(Constant.AUTHTOKEN, AuthToken);
                }else {
                    Toast.makeText(getActivity(), "Already logged in on another device", Toast.LENGTH_LONG).show();
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,1));
        requestQueue.add(stringRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //actionBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //  actionBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onButtonClick(int position, String buttonText, int selectedCount) {
        if(AppUtility.isNetworkAvailable(getActivity())) {
            getHistoryRecipies();
        }
        else {
            AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
        }
    }

    @Override
    public void onFilterApply(ArrayList<Discover> arrayList, boolean b) {

    }
}
