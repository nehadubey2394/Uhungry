package com.uhungry.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uhungry.R;
import com.uhungry.activity.PreferenceActivity;
import com.uhungry.adapter.FavListAdapter;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.helper.Helper;
import com.uhungry.listner.CustomAdapterButtonListener;
import com.uhungry.model.Discover;
import com.uhungry.model.FoodType;
import com.uhungry.model.Recipies;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FavouritesFragment extends Fragment implements View.OnClickListener,CustomAdapterButtonListener {
    private Context mContex;
    private ArrayList<Recipies> favrecipiesList,tempArrayList;
    private RecyclerView rycFavRecipeList;
    private FavListAdapter favRecipeListAdapter;
    private TextView tvNoRecored,tvCuisiinesName;
    private EditText etFavSearch;
    private ImageView ivSearchCross,ivSearch;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FavouritesFragment newInstance(String param1) {
        FavouritesFragment fragment = new FavouritesFragment();
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
        View rootView = inflater.inflate(R.layout.favourite_fragment, container, false);
        bindView(rootView);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContex = context;
    }

    private void bindView(View rootView){
        favrecipiesList = new ArrayList<>();
        tempArrayList = new ArrayList<>();
        favRecipeListAdapter = new FavListAdapter(getActivity(),tempArrayList,"");
        tvNoRecored = (TextView) rootView.findViewById(R.id.tvNoRecored);
        tvCuisiinesName = (TextView) rootView.findViewById(R.id.tvCuisiinesName);
        TextView  tvViewMore = (TextView) rootView.findViewById(R.id.tvViewMore);
        etFavSearch = (EditText) rootView.findViewById(R.id.etFavSearch);
        ivSearchCross = (ImageView) rootView.findViewById(R.id.ivSearchCross);
        ivSearch = (ImageView) rootView.findViewById(R.id.ivSearch);
        RelativeLayout rlViewMore = (RelativeLayout) rootView.findViewById(R.id.rlViewMore);

        rycFavRecipeList = (RecyclerView)rootView.findViewById(R.id.rycFavRecipeList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContex);
        rycFavRecipeList.setLayoutManager(layoutManager);
        rycFavRecipeList.setHasFixedSize(true);
        rycFavRecipeList.setAdapter(favRecipeListAdapter);

        favRecipeListAdapter.setCustomListener(FavouritesFragment.this);

        etFavSearch.clearFocus();
        AppUtility.closeKeyboard(mContex,etFavSearch.getWindowToken());
        addTextListener();

        rlViewMore.setOnClickListener(this);
        ivSearchCross.setOnClickListener(this);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(AppUtility.isNetworkAvailable(getActivity())) {
            getFavRecipies();
        }
        else {
            AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
        }
    }

    public void addTextListener(){

        etFavSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {
                String s = etFavSearch.getText().toString().trim();
                tempArrayList.clear();
                if (favrecipiesList.size()!=0){
                    if (query.length() == 0 || s.equals(" ") || s.equals("")) {
                        rycFavRecipeList.setVisibility(View.VISIBLE);
                        tvNoRecored.setVisibility(View.GONE);
                        ivSearchCross.setVisibility(View.GONE);
                        ivSearch.setVisibility(View.VISIBLE);
                        tempArrayList.addAll(favrecipiesList);
                        favRecipeListAdapter.notifyDataSetChanged();
                    }
                    else {
                        for (int j = 0; j < favrecipiesList.size(); j++) {

                            final String text = favrecipiesList.get(j).recName;

                            if (text.toLowerCase().contains(query) || text.toLowerCase().matches(String.valueOf(query))) {
                                ivSearchCross.setVisibility(View.VISIBLE);
                                ivSearch.setVisibility(View.GONE);
                                rycFavRecipeList.setVisibility(View.VISIBLE);
                                tvNoRecored.setVisibility(View.GONE);
                                tempArrayList.add(favrecipiesList.get(j));
                                favRecipeListAdapter.notifyDataSetChanged();
                            }else {
                                if (tempArrayList.size()==0){
                                    rycFavRecipeList.setVisibility(View.GONE);
                                    tvNoRecored.setVisibility(View.VISIBLE);
                                    tvNoRecored.setText(R.string.text_no_recipe_in_fav);
                                }else {
                                    rycFavRecipeList.setVisibility(View.VISIBLE);
                                    tvNoRecored.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }else {
                    tvNoRecored.setText(R.string.text_no_recipe_in_fav);
                }
            }
        });
    }

    public void getFavRecipies() {

        final ProgressDialog customDialog = new ProgressDialog(getActivity());
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.BASE_URL_USER+"getFavRecipe" ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {
                            String fName = "";
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");
                            String userstatus = jsonObj.getString("userstatus");

                            if (userstatus.equals("1")){
                                if (status.equalsIgnoreCase("success")) {

                                    customDialog.dismiss();
                                    tvNoRecored.setVisibility(View.GONE);
                                    rycFavRecipeList.setVisibility(View.VISIBLE);
                                    tempArrayList.clear();
                                    favrecipiesList.clear();

                                    JSONArray jsonArray = jsonObj.getJSONArray("recipe");
                                    JSONArray cuisinesArray = jsonObj.getJSONArray("cuisines");

                                    if(cuisinesArray != null && cuisinesArray.length() > 0 ) {
                                        for (int j = 0; j < cuisinesArray.length(); j++) {
                                            JSONObject object = cuisinesArray.getJSONObject(j);
                                            //  item = gson.fromJson(response,FoodType.class);
                                            FoodType item = new FoodType();

                                            item.foodId = object.getString("fId").trim();
                                            item.foodName = object.getString("foodName").trim();

                                            if (fName.equals("")){
                                                fName = object.getString("foodName").trim();
                                            }else {
                                                fName = fName + ", " + object.getString("foodName").trim();
                                            }
                                        }
                                        if (!fName.equals("")){
                                            tvCuisiinesName.setVisibility(View.VISIBLE);
                                            tvCuisiinesName.setText(fName);
                                        }
                                    }
                                    if(jsonArray != null && jsonArray.length() > 0 ) {

                                        for (int i = 0; i < jsonArray.length(); i++) {

                                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                                            Recipies item = new Recipies();

                                            item.recipeId = jsonObject.getString("recipeId").trim();
                                            item.recName = jsonObject.getString("title").trim();
                                            item.time = jsonObject.getString("estimatedTime").trim();
                                            item.recipeImg = jsonObject.getString("recImage").trim();

                                            tempArrayList.add(item);
                                            favrecipiesList.add(item);

                                        }
                                        String size = String.valueOf(jsonArray.length())+" Results";
                                        favRecipeListAdapter.notifyDataSetChanged();
                                    }
                                    else {
                                        tvNoRecored.setVisibility(View.VISIBLE);
                                        rycFavRecipeList.setVisibility(View.GONE);
                                    }
                                }else {
                                    customDialog.dismiss();
                                    tvNoRecored.setVisibility(View.VISIBLE);
                                    rycFavRecipeList.setVisibility(View.GONE);
                                    //AppUtility.showAlertDialog_SingleButton(getActivity(), message, "Alert!","Ok");

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
                        String errorMessage = "Request timeout error";
                        customDialog.cancel();
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
                            AppUtility.showAlertDialog_SingleButton(getActivity(), Helper.error_Messages(error),"Error","Ok");
                        ;
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rlViewMore :
                Intent intent = new Intent(getActivity(), PreferenceActivity.class);
                startActivityForResult(intent,100);
                getActivity().overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);

                break;

            case R.id.ivSearchCross :
                etFavSearch.clearFocus();
                etFavSearch.setText("");
                ivSearchCross.setVisibility(View.GONE);
                ivSearch.setVisibility(View.VISIBLE);
                AppUtility.closeKeyboard(mContex,etFavSearch.getWindowToken());

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
            if(AppUtility.isNetworkAvailable(getActivity())) {
                getFavRecipies();
            }
            else {
                AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppUtility.closeKeyboard(mContex,etFavSearch.getWindowToken());

    }

    @Override
    public void onButtonClick(int position, String buttonText, int selectedCount) {
        if (position==0)
        {
            tempArrayList.clear();
            favrecipiesList.clear();
        }
        if(AppUtility.isNetworkAvailable(getActivity())) {
            getFavRecipies();
        }
        else {
            AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
        }
    }

    @Override
    public void onFilterApply(ArrayList<Discover> arrayList,boolean b) {

    }
}
