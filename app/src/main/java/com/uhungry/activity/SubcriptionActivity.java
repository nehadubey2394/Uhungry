package com.uhungry.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uhungry.R;
import com.uhungry.aidl_classes.IabBroadcastReceiver;
import com.uhungry.aidl_classes.IabHelper;
import com.uhungry.aidl_classes.IabResult;
import com.uhungry.aidl_classes.Inventory;
import com.uhungry.aidl_classes.Purchase;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SubcriptionActivity extends AppCompatActivity implements View.OnClickListener,IabBroadcastReceiver.IabBroadcastListener{
    // private BillingProcessor bp;
    private String message = "", endDate,orderId="",TAG = "Uhungry?";
    // Will the subscription auto-renew?
    boolean mAutoRenewEnabled = false,isError = false;
    private boolean isInFront = false;
    private final String SKU_PLAN = "com.uhungry.monthly.subscription.final";
    private final int RC_REQUEST = 10001;
    IabHelper mHelper;
    Button btnSubcribe;
    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcription);

        TextView tvTitle = (TextView) findViewById(R.id.actionbarLayout_title);
        ImageView actionbar_btton_back = (ImageView) findViewById(R.id.actionbar_btton_back);
        actionbar_btton_back.setVisibility(View.VISIBLE);
        btnSubcribe = (Button) findViewById(R.id.btnSubcribe);
        tvTitle.setText(R.string.title_subscription);

        String base64EncodedPublicKey = Constant.RSA_public_key;
        mHelper = new IabHelper(SubcriptionActivity.this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        //isInFront = true;

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }
                isError = false;
                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;
                mBroadcastReceiver = new IabBroadcastReceiver(SubcriptionActivity.this);
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


        //bp = new BillingProcessor(this, Constant.RSA_public_key, this);
        //  bp.initialize();

        actionbar_btton_back.setOnClickListener(this);
        btnSubcribe.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSubcribe :

                if(AppUtility.isNetworkAvailable(SubcriptionActivity.this)) {

                    if (!isError){
                        boolean b = Uhungry.sessionManager.isSubcribed();

                        if (Uhungry.sessionManager.isSubcribed()) {
                            if (orderId.equals("") || orderId.equals( Uhungry.sessionManager.getTransectionId())){
                                AppUtility.showDialog(SubcriptionActivity.this,"You Already have Subscription Plan For this Month","Alert!","Ok");

                            }else {
                                AppUtility.showDialog(SubcriptionActivity.this,"Subscription has already taken from this google id, use another to get new subscription","Alert!","Ok");
                            }

                        }else {

                            if (Uhungry.sessionManager.getPurchasedBy().equals("2") || Uhungry.sessionManager.getPurchasedBy().equals("")){
                           /* new AppUtility().showSubcriptionDialog(this, new AppUtility.SubcriptionListner() {
                                @Override
                                public void onSubcribeClick() {
                                    callInAppPurches();
                                    // updateSubscriptionToServer();
                                }
                            });*/
                                callInAppPurches();
                            }else {
                                AppUtility.showAlertDialog_SingleButton(SubcriptionActivity.this,getResources().getString(R.string.alert_subscription),"Alert!","Ok");
                            }
                        }
                    }else {
                        AppUtility.showDialog(SubcriptionActivity.this,"Please Check Whether Your Google Account Configured or Not?","Alert!","Ok");
                    }
                }
                else {
                    AppUtility.showAlertDialog_SingleButton(SubcriptionActivity.this,getResources().getString(R.string.network_error),"Alert","Ok");
                }

               /* if (Uhungry.sessionManager.isSubcribed()) {
                    AppUtility.showDialog(SubcriptionActivity.this,"You Already have Subscription Plan For this Month","Alert!","Ok");
                }else {
                    if (Uhungry.sessionManager.getPurchasedBy().equals("2") || Uhungry.sessionManager.getPurchasedBy().equals("")){
                        if (orderId.equals( Uhungry.sessionManager.getTransectionId()) || orderId.equals("") ||
                                orderId==null) {
                            new AppUtility().showSubcriptionDialog(this, new AppUtility.SubcriptionListner() {
                                @Override
                                public void onSubcribeClick() {

                                    callInAppPurches();
                                    // updateSubscriptionToServer();
                                }
                            });

                        }else {
                            AppUtility.showDialog(SubcriptionActivity.this,"Subscription has already taken from this google id, use another to get new subscription","Alert!","Ok");

                        }

                    }else {
                        AppUtility.showAlertDialog_SingleButton(SubcriptionActivity.this,getResources().getString(R.string.alert_subscription),"Alert!","Ok");
                    }
                }*/

                break;
            case R.id.actionbar_btton_back :
                finish();
                break;
        }
    }

    private void callInAppPurches() {

        String payload = "";
        try {
            mHelper.launchSubscriptionPurchaseFlow(SubcriptionActivity.this, SKU_PLAN, RC_REQUEST, mPurchaseFinishedListener, payload);
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
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

                /*
                 * Check for items we own. Notice that for each purchase, we check
                 * the developer payload to see if it's correct! See
                 * verifyDeveloperPayload().
                 */

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


    /*{
"orderId":"GPA.3334-4681-6481-51860",
"packageName":"com.uhungry",
"productId":"com.uhungry.subscription9",
"purchaseTime":1509975847029,
"purchaseState":0,
"purchaseToken":"ifkadgooamlennfajgnemihd.AO-J1OxHpiQt1A2nX6wJkKpwqbqMcSBPVeOdc9BBPN1Amx2OlZyx9vDyGml6AiW5fhQkTUNalBg_VQtee16CxIlZT7shme45pCOVvXj-NXvxHMUPU4pSduCzZMVRtL6oGOfVOXgxNFVb",
"autoRenewing":true
}*/


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
                complain("Error in purchasing. Authenticity verification failed.");

                return;
            }


            if (purchase.getSku().equals(SKU_PLAN)) {
                // if (isInFront){
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

                    // if (orderId.equals( Uhungry.sessionManager.getTransectionId())){
                    Uhungry.sessionManager.setIsSubcribed(true);
                    //}

                    Uhungry.sessionManager.setIsSubcribedTemp(true);
                    Uhungry.sessionManager.setTempEndDate(endDate);
                    Uhungry.sessionManager.setPurchasedBy("2");
                    Uhungry.sessionManager.setTempTransectionId(orderId);

                    // AppUtility.showAlertDialog_SingleButton(RecipeDetailActivity.this,
                    //   "subscriptionId == "+orderId+"\n"+"token - "+token, "OnIabPurchaseFinishedListener", "Ok");

                    updateSubscriptionToServer();

                    // getTransectionDetail(purchase.getPackageName(), purchase.getSku(), purchase.getToken());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //}
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

                    //Uhungry.sessionManager.setPurchasedBy("2");
                    // Uhungry.sessionManager.setTransectionId(orderId);

                    // if (orderId.equals( Uhungry.sessionManager.getTransectionId())){
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String startDate = dateFormat.format(new Date(purchaseTime));

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(purchaseTime);
                    cal.add(Calendar.MONTH, 1);
                    Date date = cal.getTime();
                    endDate = dateFormat.format(date);
                    Uhungry.sessionManager.setEndDate(endDate);
                    Uhungry.sessionManager.setIsSubcribed(true);

                    // AppUtility.showAlertDialog_SingleButton(RecipeDetailActivity.this,
                    //   "subscriptionId == "+orderId+"\n"+"token - "+token, "OnIabPurchaseFinishedListener", "Ok");
                    //  updateSubscriptionToServer();

                    //       }

                }
                //updateSubscriptionToServer();
                // getTransectionDetail(purchase.getPackageName(),purchase.getSku(),purchase.getToken());
               /* AppUtility.showAlertDialog_SingleButton(RecipeDetailActivity.this,
                        "details == "+purchase+"\n"+"Order Id - "+purchase.getOrderId(),
                        "OnConsumeFinishedListener", "Ok");*/
            }

            if (mHelper == null) return;

          /*  if (result.isSuccess()) {

                alert("Thank you for this subscription!");
            }
            else {
                complain("Error while consuming: " + result);
            }*/
            // getSongsListFromAPI();
        }
    };

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }


    void complain(String message) {
        Log.e(TAG, "**** Subscription Error: " + message);
        if (message.equals("Problem setting up in-app billing: IabResult: Error checking for billing v3 support. (response: 3:Billing Unavailable)"))
        {
            message = "There is a problem occurred in your google account setting, please sync your google account.";

        }
        isError = true;
        // btnSubcribe.setEnabled(false);
        if (isInFront){
            alert("Error: " + message);
        }
    }

    void alert(String message) {
        android.support.v7.app.AlertDialog.Builder bld = new android.support.v7.app.AlertDialog.Builder(SubcriptionActivity.this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

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

                                Uhungry.sessionManager.setIsSubcribedTemp(false);
                                Uhungry.sessionManager.setTempEndDate("");
                                Uhungry.sessionManager.setPurchasedBy("2");
                                Uhungry.sessionManager.setTempTransectionId("");

                                finish();
                                overridePendingTransition(R.anim.exit_to_right,R.anim.enter_from_left);
                                //  AppUtility.showAlertDialog_SingleButton(SubcriptionActivity.this,message,"Alert","Ok");
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(SubcriptionActivity.this,message,"Error","Ok");
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
                        AppUtility.showAlertDialog_SingleButton(SubcriptionActivity.this,"Something went wrong, please check after some time.","Error","Ok");
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

        RequestQueue requestQueue = Volley.newRequestQueue(SubcriptionActivity.this);
        requestQueue.add(stringRequest);
    }


    @Override
    public void receivedBroadcast() {

    }


    public void showAlertDialog(String msg, String title) {
        View DialogView = View.inflate(SubcriptionActivity.this, R.layout.dialog_recipe_layout, null);

        final Dialog alertDailog = new Dialog(SubcriptionActivity.this, android.R.style.Theme_Light);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregisterReceiver(mBroadcastReceiver);
    }


    @Override
    public void onResume() {
        super.onResume();
        isInFront = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isInFront = false;
    }
}
