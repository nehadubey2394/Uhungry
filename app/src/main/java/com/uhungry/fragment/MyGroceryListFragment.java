package com.uhungry.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uhungry.R;
import com.uhungry.activity.WellcomeActivity;
import com.uhungry.adapter.CategoryAdapter;
import com.uhungry.adapter.ExpandableListAdapter;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.helper.Helper;
import com.uhungry.listner.CustomAdapterButtonListener;
import com.uhungry.model.Category;
import com.uhungry.model.Discover;
import com.uhungry.model.Ingredients;
import com.uhungry.model.SubIngModel;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.uhungry.utils.Uhungry.sessionManager;

public class MyGroceryListFragment extends Fragment implements View.OnClickListener ,CustomAdapterButtonListener {
    private Context mContex;
    private ArrayList<Category> categoryArrayList;
    private ArrayList<Ingredients> ingredientses;
    private CategoryAdapter categoryAdapter;
    private RecyclerView recyclerCategory;
    private ExpandableListView lvExpandable;
    private TextView tvCatEmptyList,tvEmptyList;
    private EditText etSearch;
    private String categoryId="",type = "",sSearch="",orderId;
    private boolean isSearching = false;
    private LinearLayout lycat;
    private ExpandableListAdapter expandableListAdapter;
    private static ImageView ivSend;
    private ProgressDialog customDialog;
    private ImageView ivSearchCross,ivSearch;
    private int catIngCount = 0;

    public MyGroceryListFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MyGroceryListFragment newInstance(String param1,ImageView view,String orderId) {
        MyGroceryListFragment fragment = new MyGroceryListFragment();
        ivSend = view;
        Bundle args = new Bundle();
        args.putString("ARG_PARAM", param1);
        args.putString("orderId", orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("ARG_PARAM");
            orderId = getArguments().getString("orderId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.all_grocery_fregment, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        bindView(rootView);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContex = context;
    }

    private void bindView(View rootView){
        categoryArrayList = new ArrayList<>();
        ingredientses = new ArrayList<>();
        customDialog = new ProgressDialog(mContex);

        ivSearch = (ImageView) rootView.findViewById(R.id.ivSearch);
        ivSearchCross = (ImageView) rootView.findViewById(R.id.ivSearchCross);
        tvCatEmptyList = (TextView) rootView.findViewById(R.id.tvCatEmptyList);
        tvEmptyList = (TextView) rootView.findViewById(R.id.tvEmptyList);
        etSearch = (EditText) rootView.findViewById(R.id.etSearch);
        lycat = (LinearLayout) rootView.findViewById(R.id.lycat);
        categoryAdapter = new CategoryAdapter(getActivity(), categoryArrayList);

        tvEmptyList.setText(R.string.text_no_grocery_found);

        recyclerCategory = (RecyclerView) rootView.findViewById(R.id.rvCategory);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerCategory.setItemAnimator(new DefaultItemAnimator());
        recyclerCategory.setAdapter(categoryAdapter);
        recyclerCategory.setLayoutManager(layoutManager);
        categoryAdapter.setCustomListener(MyGroceryListFragment.this);

        lvExpandable = (ExpandableListView) rootView.findViewById(R.id.lvExpandable);
        expandableListAdapter = new ExpandableListAdapter(getActivity(), ingredientses);
        lvExpandable.setAdapter(expandableListAdapter);

        lvExpandable.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                lvExpandable.setOnScrollListener(new EndlessListScrollListener(i));
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });


        lvExpandable.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                try {
                    Ingredients ingredients = ingredientses.get(groupPosition);
                    ArrayList<SubIngModel> arrayList = ingredients.getArrayList();

                    if (ingredients.isChecked.equals("0")){
                        if(AppUtility.isNetworkAvailable(mContex)) {

                            if (Uhungry.sessionManager.isSubcribed()) {
                                //   if (orderId.equals("") || orderId.equals( Uhungry.sessionManager.getTransectionId())){
                                ingredients.isChecked = "1";
                                expandableListAdapter.notifyDataSetChanged();

                                //  List<Map<String,String>> list = new ArrayList<Map<String, String>>();

                                if (arrayList.size()==0){
                                    Map<String,String> keyvaluePair = new HashMap<String, String>();
                                    keyvaluePair.put("ingId",ingredients.itemId);
                                    keyvaluePair.put("cId", ingredients.cId);
                                    keyvaluePair.put("fId", ingredients.fId);
                                    keyvaluePair.put("SubIngId", "");
                                    //list.add(keyvaluePair);
                                    JSONObject jsonArray = new JSONObject(keyvaluePair);
                                    type = "1";
                                    addIngredients(jsonArray,ingredients.cId);
                                }
                               /* }else {
                                    AppUtility.showDialog(mContex,"Subscription has already taken from this google id, use another to get new subscription","Alert!","Ok");
                                }*/

                            }else {
                                ((WellcomeActivity)getActivity()).showSubscriptionDialog();
                               /* if (Uhungry.sessionManager.getPurchasedBy().equals("2") || Uhungry.sessionManager.getPurchasedBy().equals("")){
                                    ((WellcomeActivity)getActivity()).showSubscriptionDialog();
                                }else {
                                    AppUtility.showAlertDialog_SingleButton(getActivity(),getString(R.string.alert_subscription),"Alert!","Ok");
                                }*/
                            }
                        }
                        else {AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");}

                    }else {
                        if(AppUtility.isNetworkAvailable(mContex)) {

                            if (Uhungry.sessionManager.isSubcribed()) {
                                //   if (orderId.equals("") || orderId.equals( Uhungry.sessionManager.getTransectionId())){
                                ingredients.isChecked = "0";
                                expandableListAdapter.notifyDataSetChanged();
                                //   List<Map<String,String>> list = new ArrayList<Map<String, String>>();

                                if (arrayList.size()==0){
                                    Map<String,String> keyvaluePair = new HashMap<String, String>();
                                    keyvaluePair.put("ingId",ingredients.itemId);
                                    keyvaluePair.put("cId", ingredients.cId);
                                    keyvaluePair.put("fId", ingredients.fId);
                                    keyvaluePair.put("SubIngId", "");
                                    // list.add(keyvaluePair);
                                    JSONObject jsonArray = new JSONObject(keyvaluePair);
                                    type = "2";
                                    addIngredients(jsonArray,ingredients.cId);
                                }
                              /*  }else {
                                    AppUtility.showDialog(mContex,"Subscription has already taken from this google id, use another to get new subscription","Alert!","Ok");
                                }*/

                            }else {
                                ((WellcomeActivity)getActivity()).showSubscriptionDialog();
                               /* if (Uhungry.sessionManager.getPurchasedBy().equals("2") || Uhungry.sessionManager.getPurchasedBy().equals("")){
                                    ((WellcomeActivity)getActivity()).showSubscriptionDialog();
                                }else {
                                    AppUtility.showAlertDialog_SingleButton(getActivity(),getString(R.string.alert_subscription),"Alert!","Ok");
                                }*/
                            }
                        }
                        else {AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");}
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        });

        // Listview Group expanded listener
        lvExpandable.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        // Listview Group collasped listener
        lvExpandable.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
           /* Toast.makeText(getApplicationContext(),listDataHeader.get(groupPosition) + " Collapsed",Toast.LENGTH_SHORT).show(); */

            }
        });

        lvExpandable.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition,
                                        long id) {
                try {
                    Ingredients ingredients = ingredientses.get(groupPosition);
                    ArrayList<SubIngModel> arrayList = ingredients.getArrayList();
                    SubIngModel subIngModel = arrayList.get(childPosition);
                    String subIngId = "";

                    if (subIngModel.isSubIngSelected.equals("0")){
                        if(AppUtility.isNetworkAvailable(mContex)) {

                            if (Uhungry.sessionManager.isSubcribed()) {
                                //   if (orderId.equals("") || orderId.equals( Uhungry.sessionManager.getTransectionId())){
                                subIngModel.isSubIngSelected = "1";
                                expandableListAdapter.notifyDataSetChanged();

                                if(subIngId.equals("")){
                                    subIngId =  subIngModel.subIngId;
                                } else {
                                    subIngId = subIngId + "," + subIngModel.subIngId;
                                }

                                List<Map<String,String>> list = new ArrayList<Map<String, String>>();
                                Map<String,String> keyvaluePair = new HashMap<String, String>();
                                keyvaluePair.put("ingId",ingredients.itemId);
                                keyvaluePair.put("cId", ingredients.cId);
                                keyvaluePair.put("fId", ingredients.fId);
                                keyvaluePair.put("subIngId", subIngId);
                                //list.add(keyvaluePair);
                                JSONObject jsonArray = new JSONObject(keyvaluePair);

                                type = "1";
                                addIngredients(jsonArray,ingredients.cId);
                              /*  }else {
                                    AppUtility.showDialog(mContex,"Subscription has already taken from this google id, use another to get new subscription","Alert!","Ok");
                                }*/

                            }else {
                                ((WellcomeActivity)getActivity()).showSubscriptionDialog();
                               /* if (Uhungry.sessionManager.getPurchasedBy().equals("2") || Uhungry.sessionManager.getPurchasedBy().equals("")){
                                    ((WellcomeActivity)getActivity()).showSubscriptionDialog();
                                }else {
                                    AppUtility.showAlertDialog_SingleButton(getActivity(),getString(R.string.alert_subscription),"Alert!","Ok");
                                }*/
                            }
                        }
                        else {AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");}


                    }else {
                        if(AppUtility.isNetworkAvailable(mContex)) {

                            if (Uhungry.sessionManager.isSubcribed()) {
                                // if (orderId.equals("") || orderId.equals( Uhungry.sessionManager.getTransectionId())){
                                subIngModel.isSubIngSelected = "0";
                                expandableListAdapter.notifyDataSetChanged();

                                List<Map<String,String>> list = new ArrayList<Map<String, String>>();
                                Map<String,String> keyvaluePair = new HashMap<String, String>();
                                keyvaluePair.put("ingId",ingredients.itemId);
                                keyvaluePair.put("cId", ingredients.cId);
                                keyvaluePair.put("fId", ingredients.fId);
                                keyvaluePair.put("subIngId", subIngModel.subIngId);
                                // list.add(keyvaluePair);

                                JSONObject jsonArray = new JSONObject(keyvaluePair);
                                type = "2";
                                addIngredients(jsonArray,ingredients.cId);
                              /*  }else {
                                    AppUtility.showDialog(mContex,"Subscription has already taken from this google id, use another to get new subscription","Alert!","Ok");
                                }*/

                            }else {
                                ((WellcomeActivity)getActivity()).showSubscriptionDialog();
                                /*if (Uhungry.sessionManager.getPurchasedBy().equals("2") || Uhungry.sessionManager.getPurchasedBy().equals("")){
                                    ((WellcomeActivity)getActivity()).showSubscriptionDialog();
                                }else {
                                    AppUtility.showAlertDialog_SingleButton(getActivity(),getString(R.string.alert_subscription),"Alert!","Ok");
                                }*/
                            }
                        }
                        else {AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");}
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        });

        ivSearch.setOnClickListener(this);
        ivSearchCross.setOnClickListener(this);
        ivSend.setOnClickListener(this);
        actionDone();

        etSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {
                callSearchApi(query);
            }
        });

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        if(AppUtility.isNetworkAvailable(getActivity())) {
            //GetCategoryListTask();
            GetAllIngredientsListTask("0","","");
        }
        else {
            AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
        }
    }


    private void callSearchApi(CharSequence query) {
        String s = etSearch.getText().toString().trim();
        sSearch = s;
        if (query.length() != 0 && !s.equals(" ") && !s.equals("")) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            isSearching = true;
            ivSearch.setVisibility(View.GONE);
            ivSearchCross.setVisibility(View.VISIBLE);
            ivSend.setEnabled(false);
            ivSend.setAlpha(0.5f);
            if(AppUtility.isNetworkAvailable(getActivity())) {
                ingredientses.clear();
                expandableListAdapter.notifyDataSetChanged();
                GetAllIngredientsListTask("0",s,"");
            } else {
                AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
            }

        }else {
            ivSearchCross.setVisibility(View.GONE);
            ivSearch.setVisibility(View.VISIBLE);
            isSearching = false;
            ivSend.setEnabled(true);
            ivSend.setAlpha(1f);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            ingredientses.clear();
            if(AppUtility.isNetworkAvailable(getActivity())) {
                GetAllIngredientsListTask("0",s,"");
            } else {
                AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
            }

        }
    }

    private void actionDone(){
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    AppUtility.closeKeyboard(mContex,etSearch.getWindowToken());
                    // isSearching = false;
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    handled = true;
                }
                return handled;
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.ivSearch :
                AppUtility.closeKeyboard(getActivity(),etSearch.getWindowToken());
                ivSend.setEnabled(true);
                ivSend.setAlpha(1f);
                break;

            case R.id.ivSearchCross :
                AppUtility.closeKeyboard(getActivity(),etSearch.getWindowToken());
                etSearch.clearFocus();
                etSearch.setText("");
                break;

            case R.id.ivSend :
                ArrayList<String> list = new ArrayList<String>();
                list.clear();
                for (int i = 0; i<ingredientses.size(); i++){
                    String subName = "";
                    Ingredients item = ingredientses.get(i);

                    if (item.getArrayList().size()==0){
                        list.add(i,i+1+". "+item.itemName);

                    }else {
                        for (int j = 0; j<item.getArrayList().size(); j++){
                            SubIngModel subIngModel = item.getArrayList().get(j);

                            if (subName.equals("")){
                                subName = subIngModel.subIngName;
                            }else {
                                subName = subName + "," + subIngModel.subIngName;
                            }
                        }
                        list.add(i,i+1+". "+ item.itemName+"\n"+"("+subName+")");
                    }

                }

                StringBuilder sb = new StringBuilder();
                for (String s : list)
                {
                    sb.append(s);
                    sb.append("\n");
                }
                System.out.println("list ===="+sb.toString());

                if (list.size()!=0){
                    showChoserDialog(sb.toString());
                }else {
                    showDialog("You don't have any item in your list.","Alert!");
                }


                break;
        }
    }

    public void showDialog(String msg, String title) {
        View DialogView = View.inflate(getActivity(), R.layout.dialog_recipe_layout, null);

        final Dialog alertDailog = new Dialog(getActivity(), android.R.style.Theme_Light);
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
    }

    public  void showChoserDialog(final String list) {
        View DialogView = View.inflate(getActivity(), R.layout.dialog_for_selection, null);

        final Dialog alertDailog = new Dialog(getContext(), android.R.style.Theme_Light);
        alertDailog.setCanceledOnTouchOutside(true);
        alertDailog.setCancelable(true);

        alertDailog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDailog.getWindow().getAttributes().windowAnimations = R.style.TopBottomAnimation;
        alertDailog.setContentView(DialogView);

        TextView tvDialogEmail = (TextView) DialogView.findViewById(R.id.tvDialogEmail);
        TextView tvDialogSms = (TextView) DialogView.findViewById(R.id.tvDialogSms);
        LinearLayout lyDailogCancel = (LinearLayout) DialogView.findViewById(R.id.lyDailogCancel);


        lyDailogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDailog.cancel();
            }
        });
        tvDialogEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDailog.cancel();

                Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO);
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent.setType("message/rfc822");
                emailIntent.setData(Uri.parse("mailto:" + Uhungry.sessionManager.getUserEmail()));
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {Uhungry.sessionManager.getUserEmail()});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My grocery list");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, list);

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail using..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    AppUtility.showToast(mContex,"There is no email client installed.",0);
                }
            }
        });
        tvDialogSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDailog.cancel();
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", "");
                smsIntent.putExtra("sms_body","My grocery list"+"\n"+list);

                try {
                    startActivity(smsIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    AppUtility.showToast(mContex,"There is no SMS client installed.",0);
                }

            }
        });

        alertDailog.show();
    }


    public void GetAllIngredientsListTask(final String pageNo,final String searchText, final String catId) {

        if (!isSearching){
            customDialog.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constant.BASE_URL_USER+"getAllIngredients" ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!isSearching)
                        {
                            customDialog.dismiss();
                        }

                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {
                            int subIngCount = 0;
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");
                            String userstatus = jsonObj.getString("userstatus");

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
                                    if (pageNo.equals("0") /*&& !isSearching*/){
                                        ingredientses.clear();
                                    }
                                    tvEmptyList.setVisibility(View.GONE);
                                    lvExpandable.setVisibility(View.VISIBLE);

                                    categoryArrayList.clear();
                                    JSONArray jsonCatArray = jsonObj.getJSONArray("category");

                                    if(jsonCatArray != null && jsonCatArray.length() > 0 ) {
                                        tvCatEmptyList.setVisibility(View.GONE);
                                        recyclerCategory.setVisibility(View.VISIBLE);
                                        lycat.setVisibility(View.VISIBLE);
                                        ivSend.setVisibility(View.VISIBLE);

                                        for (int i = 0; i < jsonCatArray.length(); i++) {

                                            JSONObject jsonObject = jsonCatArray.getJSONObject(i);

                                            Category item = new Category();
                                            item.id = jsonObject.getString("cId").trim();
                                            item.image = jsonObject.getString("cImage").trim();
                                            item.name = jsonObject.getString("cName").trim();
                                            item.count = jsonObject.getString("count").trim();
                                            item.isSelected = jsonObject.getString("isSelected").trim();

                                            categoryArrayList.add(item);

                                        }
                                        categoryAdapter.notifyDataSetChanged();
                                    }else {
                                        lycat.setVisibility(View.GONE);
                                    }

                                    //  ingredientses.clear();
                                    JSONArray jsonArray = jsonObj.getJSONArray("data");

                                    if(jsonArray != null && jsonArray.length() > 0 ) {

                                        for (int i = 0; i < jsonArray.length(); i++) {

                                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                                            Ingredients  item = new Ingredients();


                                            JSONArray subIngDataArray = jsonObject.getJSONArray("subIngData");
                                            ArrayList<SubIngModel> arrayList = new ArrayList<>();
                                            //       arrayList.clear();
                                            if(subIngDataArray != null && subIngDataArray.length() > 0 ) {

                                                for (int j = 0; j < subIngDataArray.length(); j++) {
                                                    SubIngModel subItem = new SubIngModel();
                                                    JSONObject jsonObject2 = subIngDataArray.getJSONObject(j);
                                                    subItem.subIngId = jsonObject2.getString("subIngId").trim();
                                                    subItem.subIngName = jsonObject2.getString("subIngName").trim();
                                                    subItem.subIngImage = jsonObject2.getString("subIngImage").trim();
                                                    subItem.isSubIngSelected = jsonObject2.getString("isSelected").trim();

                                                    subItem.isMyIng = jsonObject2.getString("isMyIng").trim();

                                                    if (!jsonObject2.getString("isMyIng").equals("1")){
                                                        arrayList.add(subItem);
                                                    }
                                                }
                                            }
                                            subIngCount = arrayList.size();
                                            item.setArrayList(arrayList);
                                            item.itemId = jsonObject.getString("ingId").trim();
                                            item.itemName = jsonObject.getString("ingName").trim();
                                            item.itemImg = jsonObject.getString("ingImage").trim();
                                            item.cId = jsonObject.getString("cId").trim();
                                            item.fId = "0";
                                            item.cName = jsonObject.getString("cName").trim();
                                            item.isChecked = jsonObject.getString("isSelected").trim();
                                            item.isMyIng = jsonObject.getString("isMyIng").trim();
                                            item.isAdded = jsonObject.getString("isAdded").trim();

                                            if (!jsonObject.getString("isMyIng").equals("1")){
                                                if (jsonObject.getString("isAdded").equals("0")){
                                                    ingredientses.add(item);
                                                }else if (jsonObject.getString("isAdded").equals("1") && arrayList.size()!=0){
                                                    ingredientses.add(item);
                                                }
                                            }
                                        }

                                        expandableListAdapter.notifyDataSetChanged();
                                        if (subIngCount!=0){
                                            catIngCount = subIngCount+(ingredientses.size()-1);
                                        }else {
                                            catIngCount = subIngCount+ingredientses.size();
                                        }
                                    }
                                    else if(message.equals("no result")){
                                        AppUtility.showAlertDialog_SingleButton(getActivity(),message,"Alert!","Ok");
                                    }
                                }
                                else {
                                    if (pageNo.equals("0") /*&& !isSearching*/){
                                        customDialog.cancel();
                                        tvEmptyList.setVisibility(View.VISIBLE);
                                        if (isSearching)
                                            tvEmptyList.setText(getString(R.string.text_no_ing_found));
                                        else
                                            tvEmptyList.setText(getString(R.string.text_no_ingredients_found));
                                        lvExpandable.setVisibility(View.GONE);
                                        lycat.setVisibility(View.GONE);
                                        // ivSend.setVisibility(View.GONE);
                                        ivSend.setEnabled(false);
                                        ivSend.setAlpha(0.5f);
                                    }
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
                        String errorMessage = "Unknown error";
                        customDialog.cancel();
                        lvExpandable.setVisibility(View.GONE);
                        tvEmptyList.setVisibility(View.VISIBLE);

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
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("page", pageNo);header.put("searchText", searchText);
                header.put("limit", "10");
                header.put("categoryId", catId);
                header.put("tag", "2");
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

        Uhungry.getInstance().cancelAllPendingRequests();
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000,0,1));
        Uhungry.getInstance().addToRequestQueue(stringRequest);
    }


    private void addIngredients(final JSONObject array, final String CatId) {

        final ProgressDialog customDialog = new ProgressDialog(mContex);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+"addGrocery",

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

                                if (isSearching){
                                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                                }else {
                                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                }                                etSearch.clearFocus();
                                lvExpandable.clearFocus();
                                // AppUtility.showToast(getActivity(),message,0);

                                for (int i = 0; i < categoryArrayList.size(); i++) {
                                    Category category = categoryArrayList.get(i);
                                    String sCategoryId = category.id;
                                    int sCount = Integer.parseInt(category.count);

                                    if (sCategoryId.equals(CatId)){
                                        if (type.equals("1")){
                                            sCount ++;
                                            category.count = String.valueOf(sCount);
                                            categoryArrayList.set(i,category);
                                        }else {
                                            sCount --;
                                            category.count = String.valueOf(sCount);
                                            categoryArrayList.set(i,category);
                                        }
                                        categoryAdapter.notifyDataSetChanged();
                                    }
                                }
                                if (catIngCount==1){
                                    categoryId = "";
                                }

                                if (isSearching)
                                    GetAllIngredientsListTask("0",sSearch,"");
                                else
                                    GetAllIngredientsListTask("0",sSearch,categoryId);
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(getActivity(),message,"Error","Ok");

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
                        AppUtility.showAlertDialog_SingleButton(getActivity(),Helper.error_Messages(error),"Error","Ok");
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();

                header.put("ingData", array.toString());
                header.put("type", type);
                header.put("tag", "2");

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

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000,0,1));
        requestQueue.add(stringRequest);
    }


    @Override
    public void onButtonClick(int position, String buttonText, int selectedCount) {
        etSearch.setText("");
        if (!buttonText.equals(""))
        {
            ivSend.setEnabled(false);
            ivSend.setAlpha(0.5f);
        }else {
            ivSend.setEnabled(true);
            ivSend.setAlpha(1f);
        }

        categoryId = buttonText;
        ingredientses.clear();
        etSearch.clearFocus();
        GetAllIngredientsListTask("0","",buttonText);
    }

    @Override
    public void onFilterApply(ArrayList<Discover> arrayList, boolean b) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class EndlessListScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 10;
        private int currentPage = -1;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessListScrollListener() {
        }
        public EndlessListScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // I load the next page of gigs using a background task,
                // but you can call any function here.

                if(AppUtility.isNetworkAvailable(getActivity())) {
                    GetAllIngredientsListTask(String.valueOf(currentPage + 1),sSearch,categoryId);
                }
                else {
                    AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
                }
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
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
