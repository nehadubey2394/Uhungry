package com.uhungry.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.uhungry.R;
import com.uhungry.adapter.RecipeIngredientsAdapter;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.model.RecipeDetailIngList;
import com.uhungry.model.RecipeSteps;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.uhungry.utils.Uhungry.sessionManager;

public class RecipeDetailActivity extends AppCompatActivity implements View.OnClickListener /*,IabBroadcastReceiver.IabBroadcastListener*/ {
    private String recipeId;
    TextView tvRDName, tvRDTime, tvPercentage, tvRdDescription;
    ImageView ivRecipeImg;
    private ArrayList<RecipeDetailIngList> ingredients;
    private ArrayList<RecipeSteps> recipeSteps;
    private RecipeIngredientsAdapter adapter;

    /*private String message = "", endDate,orderId="",TAG = "Uhungry?";
    // Will the subscription auto-renew?
    boolean mAutoRenewEnabled = false;
    private final String SKU_PLAN = "com.uhungry.subscription.final5";

    private final int RC_REQUEST = 10001;
    IabHelper mHelper;
    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        bindView();
    }

    private void bindView() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recipeId = extras.getString("recipeId");
        }
        ingredients = new ArrayList<>();
        recipeSteps = new ArrayList<>();

     /*   String base64EncodedPublicKey = Constant.RSA_public_key;
        mHelper = new IabHelper(RecipeDetailActivity.this, base64EncodedPublicKey);
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
                mBroadcastReceiver = new IabBroadcastReceiver(RecipeDetailActivity.this);
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

        });*/


        TextView tvTitle = (TextView) findViewById(R.id.actionbarLayout_title);
        ImageView actionbar_btton_back = (ImageView) findViewById(R.id.actionbar_btton_back);
        actionbar_btton_back.setVisibility(View.VISIBLE);
        tvTitle.setText(getString(R.string.title_recipe_detail));

        Button btnImHungry = (Button) findViewById(R.id.btnImHungry);
        RecyclerView listview = (RecyclerView) findViewById(R.id.listview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(RecipeDetailActivity.this);
        listview.setLayoutManager(layoutManager);
        adapter = new RecipeIngredientsAdapter(RecipeDetailActivity.this, ingredients);
        listview.setAdapter(adapter);
        listview.setNestedScrollingEnabled(true);

        tvRDName = (TextView) findViewById(R.id.tvRDName);
        tvPercentage = (TextView) findViewById(R.id.tvPercentage);
        tvRDTime = (TextView) findViewById(R.id.tvRDTime);
        tvRdDescription = (TextView) findViewById(R.id.tvRdDescription);
        ivRecipeImg = (ImageView) findViewById(R.id.ivRecipeImg);

        if (AppUtility.isNetworkAvailable(RecipeDetailActivity.this)) {
            getRecipeDetail();
        } else {
            AppUtility.showAlertDialog_SingleButton(RecipeDetailActivity.this, getResources().getString(R.string.network_error), "Alert!", "Ok");
        }

        actionbar_btton_back.setOnClickListener(this);
        btnImHungry.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.actionbar_btton_back :
                finish();
                overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                break;

            case R.id.btnImHungry :
                //"transactionId":"GPA.3347-7018-1893-21111"
                if(AppUtility.isNetworkAvailable(RecipeDetailActivity.this)) {

                    if (sessionManager.isSubcribed()) {
                        // if (orderId.equals("") || orderId.equals( Uhungry.sessionManager.getTransectionId())){
                        showDialog();
                       /* }else {
                            AppUtility.showDialog(RecipeDetailActivity.this,"Subscription has already taken from this google id, use another to get new subscription","Alert!","Ok");
                        }*/
                    }else {

                        String value =  sessionManager.getFirstRecipe();

                        if ( sessionManager.getFirstRecipe().equals("NA")) {
                            showDialog();
                        }else {
                            new AppUtility().showSubcriptionDialog(this, new AppUtility.SubcriptionListner() {
                                @Override
                                public void onSubcribeClick() {
                                    Intent intent = new Intent(RecipeDetailActivity.this, SubcriptionActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                                }
                            });
                        }



                      /*  if (Uhungry.sessionManager.getPurchasedBy().equals("2") || Uhungry.sessionManager.getPurchasedBy().equals("")){
                            new AppUtility().showSubcriptionDialog(this, new AppUtility.SubcriptionListner() {
                                @Override
                                public void onSubcribeClick() {
                                    callInAppPurches();
                                }
                            });
                        }else {
                            AppUtility.showAlertDialog_SingleButton(RecipeDetailActivity.this,getResources().getString(R.string.alert_subscription),"Alert!","Ok");
                        }*/
                    }
                }
                else {
                    AppUtility.showAlertDialog_SingleButton(RecipeDetailActivity.this,getResources().getString(R.string.network_error),"Alert","Ok");
                }

                break;

        }
    }

    public  void showDialog() {
        View DialogView = View.inflate(RecipeDetailActivity.this, R.layout.dialog_recipe_layout, null);

        final Dialog alertDailog = new Dialog(RecipeDetailActivity.this, android.R.style.Theme_Light);
        alertDailog.setCanceledOnTouchOutside(false);
        alertDailog.setCancelable(false);

        alertDailog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDailog.getWindow().getAttributes().windowAnimations = R.style.InOutAnimation;
        alertDailog.setContentView(DialogView);

        Button btnYes = (Button) DialogView.findViewById(R.id.btnYes);
        Button btnNo = (Button) DialogView.findViewById(R.id.btnNo);
        ImageView ivCross = (ImageView) DialogView.findViewById(R.id.ivCross);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDailog.cancel();
                // Sorting
                Collections.sort(recipeSteps, new Comparator<RecipeSteps>() {
                    @Override
                    public int compare(RecipeSteps a, RecipeSteps b)
                    {
                        return a.stepNo > b.stepNo ? +1 : a.stepNo < b.stepNo ? -1 : 0;
                    }
                });

                if (recipeSteps.size()!=0){
                    Intent intent = new Intent(RecipeDetailActivity.this, AllStepsActivity.class);
                    intent.putExtra("recipeId",recipeId);
                    intent.putExtra("stepsList",recipeSteps);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                }else {
                    AppUtility.showDialog(RecipeDetailActivity.this,"Steps are not Available for this Recipe","Alert","Ok");
                }

            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDailog.cancel();
                Intent intent = new Intent(RecipeDetailActivity.this, MissingIngredientsActivity.class);
                intent.putExtra("commingFrom","RecipeDetailActivity");
                intent.putExtra("recipeId",recipeId);
                intent.putExtra("stepsList",recipeSteps);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
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

    private void getRecipeDetail() {
        final ProgressDialog customDialog = new ProgressDialog(RecipeDetailActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+"recipeDetail",

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
                                ingredients.clear();
                                JSONObject object = jsonObj.getJSONObject("data");
                                tvRDName.setText(object.getString("title").trim());
                                tvRdDescription.setText(object.getString("description").trim());
                                tvPercentage.setText("You have " + object.getString("percentage").trim()+
                                        "% of the Ingredients in this Recipe");

                                tvRDTime.setText(object.getString("estimatedTime").trim());

                                String imgUrl = object.getString("recImage").trim();

                                if (!imgUrl.equals(""))
                                    Picasso.with(RecipeDetailActivity.this).load(imgUrl).
                                            placeholder(ivRecipeImg.getDrawable()).fit().into(ivRecipeImg);
                                else
                                    ivRecipeImg.setImageResource(R.drawable.recipe_default);

                                JSONArray ingredientsArray = object.getJSONArray("ingredients");

                                if(ingredientsArray != null && ingredientsArray.length() > 0 ) {

                                    for (int i = 0; i < ingredientsArray.length(); i++) {

                                        JSONObject ingredientsObject = ingredientsArray.getJSONObject(i);
                                        RecipeDetailIngList ingList = new RecipeDetailIngList();
                                        ingList.ingName = ingredientsObject.getString("ing").trim();
                                        ingList.isSelected = ingredientsObject.getString("isSelected").trim();
                                        ingredients.add(ingList);

                                    }
                                    adapter.notifyDataSetChanged();
                                }

                                JSONArray stepsJsonArray = object.getJSONArray("steps");
                                if(stepsJsonArray != null && stepsJsonArray.length() > 0 ) {

                                    for (int i = 0; i < stepsJsonArray.length(); i++) {
                                        JSONObject  object1 = stepsJsonArray.getJSONObject(i);
                                        RecipeSteps steps = new RecipeSteps();
                                        steps.stepDes = object1.getString("description").trim();
                                        steps.contentType = object1.getString("contentType").trim();
                                        steps.stepContent = object1.getString("stepContent").trim();
                                        steps.contentThumb = object1.getString("contentThumb").trim();
                                        steps.stepNo = Integer.parseInt(object1.getString("stepNo").trim());
                                        steps.stepCount = 0;
                                        if (steps.stepNo==1)
                                            steps.isViewd = true;
                                        else
                                            steps.isViewd = false;

                                        recipeSteps.add(steps);
                                    }
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
                        AppUtility.showAlertDialog_SingleButton(RecipeDetailActivity.this,"Something went wrong, please check after some time.","Error","Ok");
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();

                header.put("recipeId", RecipeDetailActivity.this.recipeId);

                return header;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String AuthToken = sessionManager.getAuthToken();
                if (!AuthToken.equals("")){

                    header.put(Constant.AUTHTOKEN, AuthToken);
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(RecipeDetailActivity.this);
        requestQueue.add(stringRequest);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregisterReceiver(mBroadcastReceiver);
    }


   /* private void callInAppPurches() {

        String payload = "";
        try {
            mHelper.launchSubscriptionPurchaseFlow(RecipeDetailActivity.this, SKU_PLAN, RC_REQUEST, mPurchaseFinishedListener, payload);
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
                Log.d(TAG, "We have subs. Consuming it.");
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


    *//*{
"orderId":"GPA.3334-4681-6481-51860",
"packageName":"com.uhungry",
"productId":"com.uhungry.subscription9",
"purchaseTime":1509975847029,
"purchaseState":0,
"purchaseToken":"ifkadgooamlennfajgnemihd.AO-J1OxHpiQt1A2nX6wJkKpwqbqMcSBPVeOdc9BBPN1Amx2OlZyx9vDyGml6AiW5fhQkTUNalBg_VQtee16CxIlZT7shme45pCOVvXj-NXvxHMUPU4pSduCzZMVRtL6oGOfVOXgxNFVb",
"autoRenewing":true
}*//*


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
                //   Toast.makeText(RecipeDetailActivity.this, "OnIabPurchaseFinishedListener Subscription Purchased successfully",
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
                    //  Uhungry.sessionManager.setPurchasedBy("2");
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

          *//*  if (result.isSuccess()) {

                alert("Thank you for this subscription!");
            }
            else {
                complain("Error while consuming: " + result);
            }*//*
            // getSongsListFromAPI();
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
        android.support.v7.app.AlertDialog.Builder bld = new android.support.v7.app.AlertDialog.Builder(RecipeDetailActivity.this);
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
                                Uhungry.sessionManager.setIsSubcribed(true);
                                Uhungry.sessionManager.setEndDate(endDate);
                                Uhungry.sessionManager.setPurchasedBy("2");
                                Uhungry.sessionManager.setTransectionId(orderId);
                                // AppUtility.showToast(RecipeDetailActivity.this,"You are subcribed successfully...",0);
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(RecipeDetailActivity.this,message,"Error","Ok");
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
                        AppUtility.showAlertDialog_SingleButton(RecipeDetailActivity.this,"Something went wrong, please check after some time.","Error","Ok");
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

        RequestQueue requestQueue = Volley.newRequestQueue(RecipeDetailActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void receivedBroadcast() {

    }*/


  /*  public void showSubscriptionDialog(){
        if(!bp.isPurchased(PRODUCT_ID)){
            new AppUtility().showSubcriptionDialog(this, new AppUtility.SubcriptionListner() {
                @Override
                public void onSubcribeClick() {
                    if(!bp.isPurchased(PRODUCT_ID)){
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.MONTH, 1);
                        Date date = cal.getTime();
                        endDate = dateFormat.format(date);
                       // updateSubscriptionToServer();
                        bp.purchase(RecipeDetailActivity.this, PRODUCT_ID);
                    }
                }
            });
        }else Toast.makeText(this, "You have allready subscribed for this month.", Toast.LENGTH_SHORT).show();

    }*/

}
