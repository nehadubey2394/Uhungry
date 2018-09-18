package com.uhungry.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.uhungry.adapter.FoodTypeListAdapter;
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

public class FoodTypeFragment extends Fragment {

    private Context mContex;
    private ArrayList<FoodType> foodTypes;
    private TextView tvEmptyList;
    private FoodTypeListAdapter foodTypeListAdapter;
    private RecyclerView rvFoodType;

    public FoodTypeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FoodTypeFragment newInstance(String param1) {
        FoodTypeFragment fragment = new FoodTypeFragment();
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
        View rootView = inflater.inflate(R.layout.foodtype_fragment, container, false);
        bindView(rootView);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContex = context;
    }

    private void bindView(View rootView){
        foodTypes = new ArrayList<>();
        TextView tvTitle = (TextView) rootView.findViewById(R.id.actionbarLayout_title);
        tvEmptyList = (TextView) rootView.findViewById(R.id.tvEmptyList);
        ImageView ivFoodTypeBack = (ImageView) rootView.findViewById(R.id.ivFoodTypeBack);
        Button btnNext = (Button) rootView.findViewById(R.id.btnNext);
        CheckBox chbAll = (CheckBox) rootView.findViewById(R.id.chbAll);
        tvTitle.setText(getString(R.string.title_cuisines));
        rvFoodType = (RecyclerView) rootView.findViewById(R.id.rvFoodType);

        foodTypeListAdapter = new FoodTypeListAdapter(getActivity(),foodTypes,btnNext,chbAll);

        rvFoodType.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        rvFoodType.setLayoutManager(linearLayoutManager2);
        rvFoodType.setAdapter(foodTypeListAdapter);

        ivFoodTypeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // getFragmentManager().popBackStack();
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(AppUtility.isNetworkAvailable(getActivity())) {
            GetFoodTypeListTask();
        }
        else {
            AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        foodTypeListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void GetFoodTypeListTask() {
        final ProgressDialog customDialog = new ProgressDialog(getActivity());
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

                                foodTypes.clear();
                                JSONArray jsonArray = jsonObj.getJSONArray("data");

                                if(jsonArray != null && jsonArray.length() > 0 ) {

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        FoodType item = new FoodType();
                                        item.foodId = jsonObject.getString("foodId").trim();
                                        item.foodName = jsonObject.getString("foodName").trim();
                                        item.isChecked = "0";

                                        foodTypes.add(item);

                                    }
                                    foodTypeListAdapter.notifyDataSetChanged();
                                }
                            }
                            else {
                                tvEmptyList.setVisibility(View.VISIBLE);
                                rvFoodType.setVisibility(View.GONE);
                                customDialog.cancel();
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
                        Toast.makeText(getActivity(), "‘Ooops! Something went wrong", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getActivity(), "Already logged in on another device", Toast.LENGTH_LONG).show();
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,1));
        requestQueue.add(stringRequest);
    }

}
