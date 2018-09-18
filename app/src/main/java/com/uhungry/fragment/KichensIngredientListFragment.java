package com.uhungry.fragment;

import android.content.Context;
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
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ExpandableListView;
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
import com.uhungry.activity.AllStepsActivity;
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

public class KichensIngredientListFragment extends Fragment implements View.OnClickListener ,
        CustomAdapterButtonListener {

    private Context mContex;
    private ArrayList<Category> categoryArrayList;
    private ArrayList<Ingredients> ingredientses;
    private CategoryAdapter categoryAdapter;
    private RecyclerView recyclerCategory;
    private ExpandableListView lvExpandable;
    private LinearLayout lycat;
    private TextView tvCatEmptyList,tvEmptyList;
    private EditText etSearch;
    private String categoryId="",type="",sSearch="",mParam1,orderId;
    private boolean isSearching = false,isDone = false;
    private ExpandableListAdapter expandableListAdapter;
    private ProgressDialog customDialog;
    private ImageView ivSearchCross,ivSearch;
    private int catIngCount = 0;
    private RequestQueue requestQueue;


    public KichensIngredientListFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static KichensIngredientListFragment newInstance(String param1,String orderId) {
        KichensIngredientListFragment fragment = new KichensIngredientListFragment();
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
            mParam1 = getArguments().getString("ARG_PARAM");
            orderId = getArguments().getString("orderId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.all_grocery_fregment, container, false);
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|
        // WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        customDialog = new ProgressDialog(mContex);
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
        categoryAdapter = new CategoryAdapter(getActivity(), categoryArrayList);
        tvCatEmptyList = (TextView) rootView.findViewById(R.id.tvCatEmptyList);
        tvEmptyList = (TextView) rootView.findViewById(R.id.tvEmptyList);
        etSearch = (EditText) rootView.findViewById(R.id.etSearch);
        lycat = (LinearLayout) rootView.findViewById(R.id.lycat);
        AppUtility.closeKeyboard(mContex,etSearch.getWindowToken());

        if (mParam1.equals("1")){
            mParam1 = "1";
        }else {
            mParam1 = "0";
        }
        ivSearchCross = (ImageView) rootView.findViewById(R.id.ivSearchCross);
        ivSearch = (ImageView) rootView.findViewById(R.id.ivSearch);

        tvEmptyList.setText(R.string.text_no_ingredients_found);

        recyclerCategory = (RecyclerView) rootView.findViewById(R.id.rvCategory);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerCategory.setItemAnimator(new DefaultItemAnimator());
        recyclerCategory.setAdapter(categoryAdapter);
        recyclerCategory.setLayoutManager(layoutManager);
        categoryAdapter.setCustomListener(KichensIngredientListFragment.this);

        lvExpandable = (ExpandableListView) rootView.findViewById(R.id.lvExpandable);
        // lvExpandable.setSelected(false);
        expandableListAdapter = new ExpandableListAdapter(getActivity(), ingredientses);
        lvExpandable.setAdapter(expandableListAdapter);
        // lvExpandable.setOnScrollListener(new EndlessListScrollListener());
        srollList();

        lvExpandable.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                if (ingredientses.size()!=0){
                    Ingredients ingredients = ingredientses.get(groupPosition);
                    ArrayList<SubIngModel> arrayList = ingredients.getArrayList();

                    if (ingredients.isChecked.equals("0")){
                        if(AppUtility.isNetworkAvailable(mContex)) {
                            if (Uhungry.sessionManager.isSubcribed() ) {
                                // if (orderId.equals("") || orderId.equals( Uhungry.sessionManager.getTransectionId())){
                                ingredients.isChecked = "1";
                                expandableListAdapter.notifyDataSetChanged();

                                List<Map<String,String>> list = new ArrayList<Map<String, String>>();

                                if (arrayList.size()==0){
                                    ingredients.isExpand = false;
                                    Map<String,String> keyvaluePair = new HashMap<String, String>();
                                    keyvaluePair.put("ingId",ingredients.itemId);
                                    keyvaluePair.put("cId", ingredients.cId);
                                    //  keyvaluePair.put("fId", ingredients.fId);
                                    keyvaluePair.put("SubIngId", "");
                                    //  list.add(keyvaluePair);
                                    JSONObject jsonArray = new JSONObject(keyvaluePair);
                                    type = "1";
                                    addIngredients(jsonArray,ingredients.cId);
                                }
                               /* }else {
                                    AppUtility.showDialog(mContex,"Subscription has already taken from this google id, use another to get new subscription","Alert!","Ok");
                                }*/

                            }else {
                                ((WellcomeActivity)getActivity()).showSubscriptionDialog();
                              /*  if (Uhungry.sessionManager.getPurchasedBy().equals("2") || Uhungry.sessionManager.getPurchasedBy().equals("")){
                                    ((WellcomeActivity)getActivity()).showSubscriptionDialog();
                                }else {
                                    AppUtility.showAlertDialog_SingleButton(getActivity(),getString(R.string.alert_subscription),"Alert!","Ok");
                                }*/
                            }
                        }else {
                            AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert","Ok");
                        }
                    }else {
                        if(AppUtility.isNetworkAvailable(mContex)) {

                            if (Uhungry.sessionManager.isSubcribed()) {
                                //     if (orderId.equals("") || orderId.equals( Uhungry.sessionManager.getTransectionId())){
                                ingredients.isChecked = "0";
                                expandableListAdapter.notifyDataSetChanged();

                                List<Map<String,String>> list = new ArrayList<Map<String, String>>();

                                if (arrayList.size()==0){
                                    Map<String,String> keyvaluePair = new HashMap<String, String>();
                                    keyvaluePair.put("ingId",ingredients.itemId);
                                    keyvaluePair.put("cId", ingredients.cId);
                                    //   keyvaluePair.put("fId", ingredients.fId);
                                    keyvaluePair.put("SubIngId", "");
                                    // list.add(keyvaluePair);
                                    JSONObject jsonArray = new JSONObject(keyvaluePair);
                                    type = "2";
                                    addIngredients(jsonArray,ingredients.cId);
                                }
                               /* }else {
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

                        } else {
                            AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
                        }
                    }

                }
                return false;
            }
        });

        // Listview Group expanded listener
        lvExpandable.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Ingredients ingredients = ingredientses.get(groupPosition);
                ingredients.isExpand = true;
            }
        });

        // Listview Group collasped listener
        lvExpandable.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Ingredients ingredients = ingredientses.get(groupPosition);
                ingredients.isExpand = false;

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
                                // if (orderId.equals("") || orderId.equals( Uhungry.sessionManager.getTransectionId())){
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
                                //keyvaluePair.put("fId", ingredients.fId);
                                keyvaluePair.put("subIngId", subIngId);
                                // list.add(keyvaluePair);
                                JSONObject jsonArray = new JSONObject(keyvaluePair);

                                type = "1";
                                addIngredients(jsonArray,ingredients.cId);
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
                        } else {
                            AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
                        }

                    }else {

                        if(AppUtility.isNetworkAvailable(mContex)) {

                            if (Uhungry.sessionManager.isSubcribed()) {
                                //   if (orderId.equals("") || orderId.equals( Uhungry.sessionManager.getTransectionId())){
                                subIngModel.isSubIngSelected = "0";
                                expandableListAdapter.notifyDataSetChanged();

                                subIngId = subIngId + "," + subIngModel.subIngId;

                                subIngId =  subIngId.replace(","+subIngId, "");
                                subIngId =  subIngId.replace(subIngId+",", "");
                                subIngId =  subIngId.replace(subIngId , "");

                                List<Map<String,String>> list = new ArrayList<Map<String, String>>();
                                Map<String,String> keyvaluePair = new HashMap<String, String>();
                                keyvaluePair.put("ingId",ingredients.itemId);
                                keyvaluePair.put("cId", ingredients.cId);
                                //   keyvaluePair.put("fId", ingredients.fId);
                                keyvaluePair.put("subIngId", subIngModel.subIngId);
                                list.add(keyvaluePair);

                                JSONObject jsonArray = new JSONObject(keyvaluePair);
                                type = "2";
                                addIngredients(jsonArray,ingredients.cId);
                              /*  }else {
                                    AppUtility.showDialog(mContex,"Subscription has already taken from this google id, use another to get new subscription","Alert!","Ok");
                                }*/

                            }else {
                                ((WellcomeActivity)getActivity()).showSubscriptionDialog();
                              /*  if (Uhungry.sessionManager.getPurchasedBy().equals("2") || Uhungry.sessionManager.getPurchasedBy().equals("")){
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


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = etSearch.getText().toString().trim();
                sSearch = s;
                callSearchApi(charSequence, s);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        ivSearch.setOnClickListener(this);
        ivSearchCross.setOnClickListener(this);
        actionDone();

    }

    private void srollList(){
        lvExpandable.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                lvExpandable.setOnScrollListener(new EndlessListScrollListener(i));
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isSearching = false;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if(AppUtility.isNetworkAvailable(getActivity())) {
            //  GetCategoryListTask();
            getAllIngredientsListTask("0","","");
        }
        else {
            AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
        }
    }


    private void callSearchApi(CharSequence query, String s) {
        if (query.length() != 0 && !s.equals(" ") && !s.equals("")) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            isSearching = true;
            isDone = false;
            ivSearch.setVisibility(View.GONE);
            ivSearchCross.setVisibility(View.VISIBLE);
            if(AppUtility.isNetworkAvailable(getActivity())) {
                ingredientses.clear();
                //  expandableListAdapter.notifyDataSetChanged();
                getAllIngredientsListTask("0",s,"");
            } else {
                AppUtility.showAlertDialog_SingleButton(getActivity(),getResources().getString(R.string.network_error),"Alert!","Ok");
            }

        }else {
            ivSearchCross.setVisibility(View.GONE);
            ivSearch.setVisibility(View.VISIBLE);
            isSearching = false;
            AppUtility.closeKeyboard(mContex,etSearch.getWindowToken());
            //   getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            ingredientses.clear();
            if(AppUtility.isNetworkAvailable(getActivity())) {
                getAllIngredientsListTask("0",s,"");
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
                    isDone = true;
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    handled = true;
                    // AppUtility.closeKeyboard(mContex,etSearch.getWindowToken());
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
                break;

            case R.id.ivSearchCross :
                AppUtility.closeKeyboard(getActivity(),etSearch.getWindowToken());
                etSearch.clearFocus();
                etSearch.setText("");
                break;

        }
    }

    private void addIngredients(final JSONObject array, final String CatId ) {
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

                                if (isSearching && !isDone){
                                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                                }else {
                                    AppUtility.closeKeyboard(mContex,etSearch.getWindowToken());
                                    //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                }
                                etSearch.clearFocus();
                                // lvExpandable.clearFocus();
                                //   AppUtility.showToast(getActivity(),message,0);

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

                                if (isSearching){
                                    getAllIngredientsListTask("0",sSearch,"");
                                }
                                else  {
                                    getAllIngredientsListTask("0",sSearch,categoryId);
                                }
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
                header.put("tag", "1");

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

    public void getAllIngredientsListTask(final String pageNo,final String searchText, final String catId) {

        //Progress.DisplayLoader(mContex);

        if (!isSearching){
            if (customDialog != null) {
                customDialog.show();
            }
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constant.BASE_URL_USER+"getAllIngredients" ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!isSearching)
                        {
                            if (customDialog != null) {
                                customDialog.cancel();
                            }
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
                            String s = sessionManager.getTempTransectionId();
                            boolean d = sessionManager.isTempSubcribed();

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
                                    recyclerCategory.setVisibility(View.VISIBLE);
                                    categoryArrayList.clear();
                                    JSONArray jsonCatArray = jsonObj.getJSONArray("category");

                                    if(jsonCatArray != null && jsonCatArray.length() > 0 ) {
                                        lycat.setVisibility(View.VISIBLE);
                                        tvCatEmptyList.setVisibility(View.GONE);
                                        recyclerCategory.setVisibility(View.VISIBLE);
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
                                            if(subIngDataArray != null && subIngDataArray.length() > 0 ) {

                                                for (int j = 0; j < subIngDataArray.length(); j++) {
                                                    SubIngModel subItem = new SubIngModel();
                                                    JSONObject jsonObject2 = subIngDataArray.getJSONObject(j);
                                                    subItem.subIngId = jsonObject2.getString("subIngId").trim();
                                                    subItem.subIngName = jsonObject2.getString("subIngName").trim();
                                                    subItem.subIngImage = jsonObject2.getString("subIngImage").trim();
                                                    subItem.isSubIngSelected = jsonObject2.getString("isSelected").trim();
                                                    subItem.isMyIng = jsonObject2.getString("isMyIng").trim();

                                                    if (!jsonObject2.getString("isMyIng").equals("2")){
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
                                            item.isMyIng = jsonObject.getString("isMyIng").trim();
                                            item.cName = jsonObject.getString("cName").trim();
                                            item.isChecked = jsonObject.getString("isSelected").trim();
                                            item.isAdded = jsonObject.getString("isAdded").trim();

                                            if (!jsonObject.getString("isMyIng").equals("2")){
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
                        String errorMessage = "Request timeout error";
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
                header.put("page", pageNo);
                header.put("searchText", searchText);
                header.put("limit", "10");
                header.put("categoryId", catId);
                header.put("tag", "1");
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

    @Override
    public void onButtonClick(int position, String buttonText, int selectedCount) {
        categoryId = buttonText;
        etSearch.clearFocus();
        etSearch.setText("");
        getAllIngredientsListTask("0","",buttonText);
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

        EndlessListScrollListener() {
        }
        EndlessListScrollListener(int visibleThreshold) {
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
                    getAllIngredientsListTask(String.valueOf(currentPage + 1),sSearch,categoryId);
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
