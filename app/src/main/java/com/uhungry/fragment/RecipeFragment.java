package com.uhungry.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;
import com.uhungry.R;
import com.uhungry.adapter.RecipeListAdapter;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.helper.Helper;
import com.uhungry.listner.CustomAdapterButtonListener;
import com.uhungry.listner.EndlessRecyclerViewScrollListener;
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
import static com.uhungry.utils.Uhungry.sessionManager;


public class RecipeFragment extends Fragment implements CustomAdapterButtonListener {
    private Context mContex;
    private TextView tvNoRecored,tvResultCount;
    private ArrayList<Recipies> recipiesList;
    private RecyclerView rycRecipeList;
    private RecipeListAdapter recipeListAdapter;
    AdView adView;

    public RecipeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RecipeFragment newInstance(String param1) {
        RecipeFragment fragment = new RecipeFragment();
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
        View rootView = inflater.inflate(R.layout.recipe_fragment, container, false);
        bindView(rootView);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContex = context;
    }

    private void bindView(View rootView){
        recipiesList = new ArrayList<>();

        TextView actionbarLayout_title = (TextView) rootView.findViewById(R.id.actionbarLayout_title);
        tvNoRecored = (TextView) rootView.findViewById(R.id.tvNoRecored);
        tvResultCount = (TextView) rootView.findViewById(R.id.tvResultCount);
        actionbarLayout_title.setText("Recipes");

        recipeListAdapter = new RecipeListAdapter(getActivity(),recipiesList,tvResultCount);

        //load ad
        // MobileAds.initialize(getApplicationContext(),"ca-app-pub-5766942206949971/8197963263");
        adView = (AdView) rootView.findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        loadAdMob();

        rycRecipeList = (RecyclerView)rootView.findViewById(R.id.rycRecipeList);
        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(recipeListAdapter);
        rycRecipeList.addItemDecoration(headersDecor);

        // Add decoration for dividers between list items
        rycRecipeList.addItemDecoration(new DividerItemDecoration(mContex, LinearLayoutManager.VERTICAL));

        StickyRecyclerHeadersTouchListener touchListener = new StickyRecyclerHeadersTouchListener(rycRecipeList, headersDecor);
        rycRecipeList.addOnItemTouchListener(touchListener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContex);
        rycRecipeList.setLayoutManager(layoutManager);
//        rycRecipeList.setHasFixedSize(true);
        rycRecipeList.setAdapter(recipeListAdapter);
        recipeListAdapter.setCustomListener(RecipeFragment.this);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount>9)
                    loadNextDataFromApi(page);
            }
        };

        rycRecipeList.addOnScrollListener(scrollListener);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(AppUtility.isNetworkAvailable(getActivity())) {
            getRecipies("1");
        }
        else {
            AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
        }
    }

    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        String page = String.valueOf(offset+1);

        if(AppUtility.isNetworkAvailable(mContex)) {
            getRecipies(page);
        }
        else {
            AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
        }

    }

    private void loadAdMob(){
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i("Ads", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.i("Ads", "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.i("Ads", "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.i("Ads", "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Log.i("Ads", "onAdClosed");
            }
        });

    }

    public void getRecipies(final String page) {

        final ProgressDialog customDialog = new ProgressDialog(getActivity());
        customDialog.show();

        String url = Uri.parse(Constant.BASE_URL_USER+"recipeListNew")
                .buildUpon()
                .appendQueryParameter("page", page)
                //  .appendQueryParameter("limit", "5")
                .build().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {
                            int count=0;
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String userstatus = jsonObj.getString("userstatus");
                            String message = jsonObj.getString("message");

                            JSONObject subscriptionObj = jsonObj.getJSONObject("subscription");
                            String  isSubscribed = subscriptionObj.getString("isSubscribed");
                            String   purchasedBy = subscriptionObj.getString("purchasedBy");
                            String   endDate = subscriptionObj.getString("endDate");
                            String transactionId = subscriptionObj.getString("transactionId");

                           /* if (isSubscribed.equals("1"))
                                sessionManager.setIsSubcribed(true);
                            else
                                sessionManager.setIsSubcribed(false);*/
                            if (sessionManager.isTempSubcribed() && !sessionManager.getTempTransectionId().equals("")
                                    && isSubscribed.equals("0")){
                                updateSubscriptionToServer();
                            }

                            if (sessionManager.isTempSubcribed() || isSubscribed.equals("1")){
                                sessionManager.setIsSubcribed(true);
                            }else {
                                sessionManager.setIsSubcribed(false);
                            }


                            sessionManager.setPurchasedBy(purchasedBy);
                            sessionManager.setEndDate(endDate);
                            sessionManager.setTransectionId(transactionId);

                            if (userstatus.equals("1")){
                                if (status.equalsIgnoreCase("success")) {
                                    customDialog.dismiss();

                                    JSONArray jsonArray = jsonObj.getJSONArray("data");

                                    if(jsonArray != null && jsonArray.length() > 0 ) {
                                        count = Integer.parseInt(jsonObj.getString("count"));

                                        if (message.equals("generic"))
                                        {
                                            for (int i = 0; i < jsonArray.length(); i++) {

                                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                                Recipies item = new Recipies();

                                                item.recipeId = jsonObject.getString("recipeId").trim();
                                                item.recName = jsonObject.getString("title").trim();
                                                item.time = jsonObject.getString("estimatedTime").trim();
                                                item.percentage = jsonObject.getString("percentage").trim();
                                                item.recipeImg = jsonObject.getString("recImage").trim();
                                                item.foodTypeId = jsonObject.getString("foodTypeId").trim();
                                                item.isFav = jsonObject.getString("isFav").trim();

                                                recipiesList.add(item);

                                            }
                                            String size = String.valueOf(jsonArray.length())+" Results";
                                            Recipies.count = String.valueOf(count);

                                            tvResultCount.setText(size);
                                            recipeListAdapter.notifyDataSetChanged();
                                        }
                                        else {

                                            if (count==0){
                                                tvNoRecored.setVisibility(View.VISIBLE);
                                                rycRecipeList.setVisibility(View.GONE);
                                            }else {
                                                tvNoRecored.setVisibility(View.GONE);
                                                rycRecipeList.setVisibility(View.VISIBLE);
                                            }
                                            for (int i = 0; i < jsonArray.length(); i++) {

                                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                                JSONObject cuisineObject = jsonObject.getJSONObject("cuisine");

                                                JSONArray recipiesArray = cuisineObject.getJSONArray("recipies");

                                                if(recipiesArray != null && recipiesArray.length() > 0 ) {
                                                    tvNoRecored.setVisibility(View.GONE);
                                                    rycRecipeList.setVisibility(View.VISIBLE);

                                                    for (int j = 0; j < recipiesArray.length(); j++) {

                                                        JSONObject object = recipiesArray.getJSONObject(j);

                                                        Recipies item = new Recipies();
                                                        item.foodTypeId = cuisineObject.getString("id").trim();
                                                        item.cusineType = cuisineObject.getString("name").trim();

                                                        item.recipeId = object.getString("recipeId").trim();
                                                        item.recName = object.getString("title").trim();
                                                        item.time = object.getString("estimatedTime").trim();
                                                        item.percentage = object.getString("percentage").trim();
                                                        item.recipeImg = object.getString("recImage").trim();
                                                        item.isFav = object.getString("isFav").trim();
                                                        Recipies.count = String.valueOf(count);
                                                        recipiesList.add(item);
                                                    }
                                                }
                                            }
                                            String size = count+" Results";
                                            tvResultCount.setText(size);
                                            recipeListAdapter.notifyDataSetChanged();
                                        }
                                    }

                                }else {
                                    customDialog.dismiss();
                                    tvNoRecored.setVisibility(View.VISIBLE);
                                    rycRecipeList.setVisibility(View.GONE);
                                    //  AppUtility.showAlertDialog_SingleButton(getActivity(), message, "Alert!","Ok");
                                }
                            }else{
                                AppUtility.sessionExpire(getActivity(), "User currently inactive from admin.", "Alert!","Ok");
                            }

                        } catch (Exception ex) {
                            Log.e("TAG",ex.toString());
                            if (customDialog!=null)
                                customDialog.dismiss();
                            AppUtility.showAlertDialog_SingleButton(getActivity(), "Something went wrong!","Alert!","Ok");

                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        String errorMessage = "Request timeout error";
                        customDialog.cancel();
                        tvNoRecored.setVisibility(View.VISIBLE);
                        rycRecipeList.setVisibility(View.GONE);

                        if (networkResponse!=null){
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
                        }else
                            Toast.makeText(mContex, errorMessage, Toast.LENGTH_SHORT).show();
                        // AppUtility.showAlertDialog_SingleButton(getActivity(), Helper.error_Messages(error),"Error","Ok");


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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000,0,1));
        requestQueue.add(stringRequest);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onButtonClick(int position, String buttonText, int selectedCount) {
        if(AppUtility.isNetworkAvailable(mContex)) {
            getRecipies("1");
        }
        else {
            AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
        }
    }

    @Override
    public void onFilterApply(ArrayList<Discover> arrayList, boolean b) {

    }

    private void updateSubscriptionToServer() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+"userSubscription",

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("success")) {
                                Uhungry.sessionManager.setIsSubcribed(true);
                                Uhungry.sessionManager.setEndDate(sessionManager.getTempEndDate());
                                Uhungry.sessionManager.setPurchasedBy("2");
                                Uhungry.sessionManager.setTransectionId(sessionManager.getTempTransectionId());

                                Uhungry.sessionManager.setIsSubcribedTemp(false);
                                Uhungry.sessionManager.setTempEndDate("");
                                Uhungry.sessionManager.setPurchasedBy("2");
                                Uhungry.sessionManager.setTempTransectionId("");
                                //  AppUtility.showAlertDialog_SingleButton(SubcriptionActivity.this,message,"Alert","Ok");
                            }


                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                // header.put("amount", "$1.99");
                header.put("endDate", sessionManager.getTempEndDate());
                //   header.put("transactionId", "");
                header.put("purchasedBy", "2");
                header.put("transactionId", sessionManager.getTempTransectionId());

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

        RequestQueue requestQueue = Volley.newRequestQueue(mContex);
        //requestQueue.add(stringRequest);
        Uhungry.getInstance().addToRequestQueue(stringRequest);
    }

}
