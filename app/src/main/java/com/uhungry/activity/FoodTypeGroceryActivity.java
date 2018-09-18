package com.uhungry.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
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
import com.uhungry.adapter.KichensTypeGroceryListAdapter;
import com.uhungry.aidl_classes.IabBroadcastReceiver;
import com.uhungry.aidl_classes.IabHelper;
import com.uhungry.aidl_classes.IabResult;
import com.uhungry.aidl_classes.Inventory;
import com.uhungry.aidl_classes.Purchase;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.model.Ingredients;
import com.uhungry.model.SubIngModel;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodTypeGroceryActivity extends AppCompatActivity implements View.OnClickListener /*,IabBroadcastReceiver.IabBroadcastListener*/ {
    //  private RecyclerView rvFtgrocery;
    private ArrayList<Ingredients> groceries ;
    private TextView tvEmptyList;
    private ExpandableListView lvFTgrocery;
    private KichensTypeGroceryListAdapter listAdapter;

    private String message = "",Id = "", endDate,orderId,TAG = "Uhungry?";

    // Will the subscription auto-renew?
   /*  boolean mAutoRenewEnabled = false;
    private final String SKU_PLAN = "com.uhungry.subscription.final5";

    private final int RC_REQUEST = 10001;
    IabHelper mHelper;
    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_type_grocery);

        bindView();
    }

    private void bindView(){

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            // FoodType  foodType = (FoodType) extras.getSerializable("foodType");
            Id = extras.getString("foodTypeId");
        }
        TextView tvTitle = (TextView) findViewById(R.id.actionbarLayout_title);
        ImageView actionbar_btton_back = (ImageView) findViewById(R.id.actionbar_btton_back);
        actionbar_btton_back.setVisibility(View.VISIBLE);
        tvTitle.setText(getString(R.string.title_generic_kitchen));

        Button btnFTdone = (Button) findViewById(R.id.btnFTdone);
        tvEmptyList = (TextView) findViewById(R.id.tvEmptyList);

       /* String base64EncodedPublicKey = Constant.RSA_public_key;
        mHelper = new IabHelper(FoodTypeGroceryActivity.this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;
                mBroadcastReceiver = new IabBroadcastReceiver(FoodTypeGroceryActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }

        });
*/
        groceries = new ArrayList<>();

        lvFTgrocery = (ExpandableListView) findViewById(R.id.lvFTgrocery);
        listAdapter = new KichensTypeGroceryListAdapter(FoodTypeGroceryActivity.this,groceries);
        lvFTgrocery.setAdapter(listAdapter);

        lvFTgrocery.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                Ingredients ingredients = groceries.get(groupPosition);
                ArrayList<SubIngModel> arrayList = ingredients.getArrayList();

                if (ingredients.isChecked.equals("0")){

                    if (arrayList.size()==0){
                        ingredients.isChecked = "1";
                        ingredients.isSubItemChecked = false;
                        listAdapter.notifyDataSetChanged();
                    }

                }else {

                    if (arrayList.size()==0){
                        ingredients.isChecked = "0";
                        ingredients.isSubItemChecked = false;
                        listAdapter.notifyDataSetChanged();

                    }
                }

                return false;
            }
        });

        // Listview Group expanded listener
        lvFTgrocery.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        // Listview Group collasped listener
        lvFTgrocery.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        lvFTgrocery.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition,
                                        long id) {
                Ingredients ingredients = groceries.get(groupPosition);
                ArrayList<SubIngModel> arrayList = ingredients.getArrayList();
                SubIngModel subIngModel = arrayList.get(childPosition);
                String subIngId = "";

                if (subIngModel.isSubIngSelected.equals("0")){
                    subIngModel.isSubIngSelected = "1";
                    ingredients.isSubItemChecked = true;
                    listAdapter.notifyDataSetChanged();

                }else {
                    subIngModel.isSubIngSelected = "0";
                    ingredients.isSubItemChecked = false;
                    listAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        actionbar_btton_back.setOnClickListener(this);
        btnFTdone.setOnClickListener(this);

        if(AppUtility.isNetworkAvailable(FoodTypeGroceryActivity.this)) {
            getIngredientsListTask(Id);
        }
        else {
            AppUtility.showAlertDialog_SingleButton(FoodTypeGroceryActivity.this,getResources().getString(R.string.network_error),"Alert!","Ok");
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.actionbar_btton_back:
                finish();
                overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                break;

            case R.id.btnFTdone :
                List<Map<String,String>> list = new ArrayList<Map<String, String>>();
                list.clear();
                for (Ingredients ingredients : groceries){
                    HashMap<String,String> hashMap = new HashMap<>();

                    if (ingredients.isChecked.equals("1") || ingredients.isSubItemChecked){
                        String   subIngId = "";
                       /* int cId = Integer.parseInt(ingredients.cId);
                        int fId = Integer.parseInt("0");
                        int ingId = Integer.parseInt(ingredients.itemId);*/
                        String cId = ingredients.cId;
                        String fId = "0";
                        String ingId = ingredients.itemId;

                        //   hashMap.put("cId", cId);
                        //   hashMap.put("fId", fId);

                        if (ingredients.getArrayList().size()!=0){
                            subIngId = "";
                            for (int j = 0; j < ingredients.getArrayList().size(); j++) {
                                SubIngModel subItem = ingredients.getArrayList().get(j);

                                if (subItem.isSubIngSelected.equals("1")){
                                    if(subIngId.equals("")){
                                        subIngId =  subItem.subIngId;
                                    }
                                    else {
                                        subIngId = subIngId + "," + subItem.subIngId;
                                    }
                                    hashMap.put("ingId",ingId);
                                    hashMap.put("SubIngId", subIngId);
                                }

                            }
                        }else {
                            hashMap.put("ingId",ingId);
                            hashMap.put("SubIngId", subIngId);
                        }
                        if (hashMap.size()!=0)
                            list.add(hashMap);
                    }
                }

                JSONArray jsonArray = new JSONArray(list);

                if(AppUtility.isNetworkAvailable(FoodTypeGroceryActivity.this)) {

                    AddIngredients(jsonArray);

                  /*  if (Uhungry.sessionManager.isSubcribed()) {
                        if (orderId.equals("") || orderId.equals( Uhungry.sessionManager.getTransectionId())){
                            AddIngredients(jsonArray);
                        }else {
                            AppUtility.showDialog(FoodTypeGroceryActivity.this,"Subscription has already taken from this google id, use another to get new subscription","Alert!","Ok");
                        }

                    }else {

                        if (Uhungry.sessionManager.getPurchasedBy().equals("2") || Uhungry.sessionManager.getPurchasedBy().equals("")){
                            new AppUtility().showSubcriptionDialog(this, new AppUtility.SubcriptionListner() {
                                @Override
                                public void onSubcribeClick() {
                                    callInAppPurches();
                                }
                            });
                        }else {
                            AppUtility.showAlertDialog_SingleButton(FoodTypeGroceryActivity.this,getResources().getString(R.string.alert_subscription),"Alert!","Ok");
                        }
                    }*/
                }
                else {
                    AppUtility.showAlertDialog_SingleButton(FoodTypeGroceryActivity.this,getResources().getString(R.string.network_error),"Alert","Ok");
                }

                break;
        }
    }


    public void getIngredientsListTask(String Id) {
        final ProgressDialog customDialog = new ProgressDialog(FoodTypeGroceryActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.BASE_URL_USER+"genericKitchen?"/*+"foodId="+Id*/,
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
                                tvEmptyList.setVisibility(View.GONE);
                                lvFTgrocery.setVisibility(View.VISIBLE);
                                groceries.clear();

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
                                                subItem.subIngId = jsonObject2.getString("id").trim();
                                                subItem.subIngName = jsonObject2.getString("name").trim();
                                                subItem.subIngImage = jsonObject2.getString("image").trim();
                                                subItem.isSubIngSelected = "1";
                                                arrayList.add(subItem);
                                            }
                                        }
                                        item.setArrayList(arrayList);
                                        item.itemId = jsonObject.getString("ingId").trim();
                                        //   item.fIngId = jsonObject.getString("fIngId").trim();
                                        //   item.fId = jsonObject.getString("foodId").trim();
                                        item.itemName = jsonObject.getString("ingName").trim();
                                        item.itemImg = jsonObject.getString("image").trim();
                                        //    item.cId = jsonObject.getString("cId").trim();
                                        //    item.cName = jsonObject.getString("cName").trim();
                                        item.isChecked = "1";

                                        groceries.add(item);

                                    }
                                    listAdapter.notifyDataSetChanged();

                                }

                            }
                            else
                            {
                                tvEmptyList.setVisibility(View.VISIBLE);
                                lvFTgrocery.setVisibility(View.GONE);
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
                        Toast.makeText(FoodTypeGroceryActivity.this, "‘Ooops! Something went wrong", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(FoodTypeGroceryActivity.this, "Already logged in on another device", Toast.LENGTH_LONG).show();
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(FoodTypeGroceryActivity.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,1));
        requestQueue.add(stringRequest);
    }

    private void AddIngredients(final JSONArray array) {
        final ProgressDialog customDialog = new ProgressDialog(this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+"addGenericKitchen",

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
                                //   AppUtility.showToast(FoodTypeGroceryActivity.this,"Ingredients added successfully",0);
                                // lvFTgrocery.notify();
                                Intent intent = new Intent(FoodTypeGroceryActivity.this, WellcomeActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                                finish();
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(FoodTypeGroceryActivity.this,message,"Error","Ok");

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
                        AppUtility.showAlertDialog_SingleButton(FoodTypeGroceryActivity.this,"Something went wrong, please check after some time.","Error","Ok");
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();

                header.put("ingData", array.toString());
                header.put("fId", Id);
                //header.put("tag", "2");

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

        RequestQueue requestQueue = Volley.newRequestQueue(FoodTypeGroceryActivity.this);
        requestQueue.add(stringRequest);
    }


/*    private void callInAppPurches() {

        String payload = "";
        try {
            mHelper.launchSubscriptionPurchaseFlow(FoodTypeGroceryActivity.this, SKU_PLAN, RC_REQUEST, mPurchaseFinishedListener, payload);
        }
        catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }
    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                //complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

                *//*
                 * Check for items we own. Notice that for each purchase, we check
                 * the developer payload to see if it's correct! See
                 * verifyDeveloperPayload().
                 *//*

            // // Check for gas delivery -- if we own gas, we should fill up the
            // tank immediately
            Purchase gasPurchase = inventory.getPurchase(SKU_PLAN);
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {

                try {
                    mHelper.consumeAsync(inventory.getPurchase(SKU_PLAN),
                            mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    };

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            //   AppUtility.showAlertDialog_SingleButton(RecipeDetailActivity.this,"Subscription details == "+purchase+"\n"+"Order Id - "+purchase.getOrderId(),"Transaction Details", "Ok");

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                // complain("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }


            if (purchase.getSku().equals(SKU_PLAN)) {
                alert("Thank you for this subscription!");
                mAutoRenewEnabled = purchase.isAutoRenewing();
                //  Toast.makeText(FoodTypeGroceryActivity.this, "OnIabPurchaseFinishedListener Subscription Purchased successfully",
                // Toast.LENGTH_LONG).show();

                try {
                    orderId = purchase.getOrderId();
                    long   purchaseTime = purchase.getPurchaseTime();
                    String token = purchase.getToken();

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String startDate = dateFormat.format(new Date(purchaseTime));

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(purchaseTime);
                    cal.add(Calendar.MONTH, 1);
                    Date date = cal.getTime();
                    endDate = dateFormat.format(date);

                    // AppUtility.showAlertDialog_SingleButton(RecipeDetailActivity.this,
                    //   "subscriptionId == "+orderId+"\n"+"token - "+token, "OnIabPurchaseFinishedListener", "Ok");
                    Uhungry.sessionManager.setIsSubcribed(true);
                    Uhungry.sessionManager.setEndDate(endDate);
                    Uhungry.sessionManager.setPurchasedBy("2");
                    updateSubscriptionToServer();

                    // getTransectionDetail(purchase.getPackageName(), purchase.getSku(), purchase.getToken());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "OnConsumeFinishedListener :=== " + result + ", purchase: " + purchase);

            // Toast.makeText(RecipeDetailActivity.this, "Subscription Purchased successfully", Toast.LENGTH_LONG).show();

            if(purchase!=null){

                if (purchase.getSku().equals(SKU_PLAN)) {
                    orderId = purchase.getOrderId();
                    long   purchaseTime = purchase.getPurchaseTime();
                    String token = purchase.getToken();

                    Uhungry.sessionManager.setIsSubcribed(true);
                    Uhungry.sessionManager.setPurchasedBy("2");
                    // Uhungry.sessionManager.setTransectionId(orderId);

                    if (orderId.equals( Uhungry.sessionManager.getTransectionId())){
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String startDate = dateFormat.format(new Date(purchaseTime));

                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(purchaseTime);
                        cal.add(Calendar.MONTH, 1);
                        Date date = cal.getTime();
                        endDate = dateFormat.format(date);
                        Uhungry.sessionManager.setEndDate(endDate);
                        // AppUtility.showAlertDialog_SingleButton(RecipeDetailActivity.this,
                        //   "subscriptionId == "+orderId+"\n"+"token - "+token, "OnIabPurchaseFinishedListener", "Ok");
                        updateSubscriptionToServer();

                    }else {
                        // AppUtility.showDialog(RecipeDetailActivity.this,"Subscription has already taken from this google id, use another to get new subscription","Alert!","Ok");
                    }

                }
                //updateSubscriptionToServer();
                // getTransectionDetail(purchase.getPackageName(),purchase.getSku(),purchase.getToken());
               *//* AppUtility.showAlertDialog_SingleButton(RecipeDetailActivity.this,
                        "details == "+purchase+"\n"+"Order Id - "+purchase.getOrderId(),
                        "OnConsumeFinishedListener", "Ok");*//*
            }

            if (mHelper == null) return;

        }
    };

    *//** Verifies the developer payload of a purchase. *//*
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }


    void complain(String message) {
        Log.e(TAG, "**** Subscription Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        android.support.v7.app.AlertDialog.Builder bld = new android.support.v7.app.AlertDialog.Builder(FoodTypeGroceryActivity.this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.i(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    private void updateSubscriptionToServer() {
        final ProgressDialog customDialog = new ProgressDialog(this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+"userSubscription",

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
                                //  AppUtility.showToast(FoodTypeGroceryActivity.this,"You are subcribed successfully...",0);
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(FoodTypeGroceryActivity.this,message,"Error","Ok");
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
                        AppUtility.showAlertDialog_SingleButton(FoodTypeGroceryActivity.this,"Something went wrong, please check after some time.","Error","Ok");
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                // header.put("amount", "$1.99");
                header.put("endDate", endDate);
                //   header.put("transactionId", "");
                header.put("purchasedBy", "2");
                header.put("transactionId", orderId);

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

        RequestQueue requestQueue = Volley.newRequestQueue(FoodTypeGroceryActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void receivedBroadcast() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }*/



    //previous with lib
        /*  @Override
      public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
          Toast.makeText(FoodTypeGroceryActivity.this, "Billing Purchased successfully", Toast.LENGTH_LONG).show();
          Uhungry.sessionManager.setIsSubcribed(true);
          Uhungry.sessionManager.setEndDate(endDate);
          Uhungry.sessionManager.setPurchasedBy("2");
          AppUtility.showAlertDialog_SingleButton(FoodTypeGroceryActivity.this, "Transaction Details = " + details + " " + "productId = " + productId, "Transaction Details", "Ok");

          updateSubscriptionToServer();

          Log.d("TransactionDetails", "=====: " + details);

          AppUtility.showAlertDialog_SingleButton(FoodTypeGroceryActivity.this,
                  "productId = "+productId+"\n"+"TransactionDetails = "+details + "\n",
                  "onProductPurchased", "Ok");
      }

      @Override
      public void onPurchaseHistoryRestored() {
          //       AppUtility.showToast(WellcomeActivity.this,"onPurchaseHistoryRestored: " ,0);

          for(String sku : bp.listOwnedProducts())
              Log.d("====", "Owned Managed Product: " + sku);
          for(String sku : bp.listOwnedSubscriptions())
              Log.d("====", "Owned Subscription: " + sku);
      }

      @Override
      public void onBillingError(int errorCode, @Nullable Throwable error) {
          Toast.makeText(this, "Error:"+errorCode+" error = "+error, Toast.LENGTH_SHORT).show();
          AppUtility.showAlertDialog_SingleButton(FoodTypeGroceryActivity.this, "error details == "+error, "Transaction Details", "Ok");

      }

      @Override
      public void onBillingInitialized() {

      }*/
}
