package com.uhungry.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.uhungry.R;
import com.uhungry.adapter.NavigationMenuAdapter;
import com.uhungry.aidl_classes.IabBroadcastReceiver;
import com.uhungry.aidl_classes.IabHelper;
import com.uhungry.aidl_classes.IabResult;
import com.uhungry.aidl_classes.Inventory;
import com.uhungry.aidl_classes.Purchase;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.fragment.DiscoverFragment;
import com.uhungry.fragment.FavouritesFragment;
import com.uhungry.fragment.HistoryFragment;
import com.uhungry.fragment.IngredientsFragment;
import com.uhungry.fragment.RecipeFragment;
import com.uhungry.fragment.WellcomeFragment;
import com.uhungry.helper.Constant;
import com.uhungry.model.NavigationItem;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WellcomeActivity extends AppCompatActivity implements View.OnClickListener/*,IabBroadcastReceiver.IabBroadcastListener*/ {
    private ImageView ivRecipeList,ivHomeProfile,ivDiscover,ivMyProfile;
    public static boolean isRegistration = false;
    private DrawerLayout drawer;
    private LinearLayout mainView,homeActionbarLayout;
    ArrayList<NavigationItem> navigationItems = new ArrayList<>();
    private String message = "", endDate,orderId="",TAG = "Uhungry?";

   /* private String TransectionId = "";
    //  private BillingProcessor bp;
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
        setContentView(R.layout.activity_wellcome);

        //  bp = new BillingProcessor(this, Constant.RSA_public_key, this);
        // bp.initialize();
        //  bp.purchase(this, PRODUCT_ID);

        init();
        bindView();
    }


    private void init(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }


    public void showSubscriptionDialog(){
        //  if (orderId.equals(Uhungry.sessionManager.getTransectionId())){
        if(AppUtility.isNetworkAvailable(WellcomeActivity.this)) {
            new AppUtility().showSubcriptionDialog(this, new AppUtility.SubcriptionListner() {
                @Override
                public void onSubcribeClick() {
                    //callInAppPurches();
                    Intent intent = new Intent(WellcomeActivity.this, SubcriptionActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);

                }
            });
        } else {
            AppUtility.showAlertDialog_SingleButton(WellcomeActivity.this,getResources().getString(R.string.network_error),"Alert","Ok");
        }
       /* } else {
            AppUtility.showDialog(WellcomeActivity.this,"Subscription has already taken from this google id, use another to get new subscription","Alert!","Ok");
        }*/



       /* if(!bp.isPurchased(PRODUCT_ID)){
            new AppUtility().showSubcriptionDialog(this, new AppUtility.SubcriptionListner() {
                @Override
                public void onSubcribeClick() {
                    if(!bp.isPurchased(PRODUCT_ID)){
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.MONTH, 1);

                        Date date = cal.getTime();
                        endDate = dateFormat.format(date);

                        bp.purchase(WellcomeActivity.this, PRODUCT_ID);
                    }
                }
            });
        }else Toast.makeText(this, "You have allready subscribed for this month.", Toast.LENGTH_SHORT).show();
*/
    }

    private void bindView(){
        ivHomeProfile = (ImageView) findViewById(R.id.ivHomeProfile);
        ivDiscover = (ImageView) findViewById(R.id.ivDiscover);
        ivMyProfile = (ImageView) findViewById(R.id.ivMyProfile);
        ivRecipeList = (ImageView) findViewById(R.id.ivRecipeList);
        ImageView actionbar_profile_menu = (ImageView) findViewById(R.id.actionbar_profile_menu);
        ImageView ivHistory = (ImageView) findViewById(R.id.ivHistory);
        homeActionbarLayout = (LinearLayout) findViewById(R.id.homeActionbar);
        homeActionbarLayout.setVisibility(View.GONE);
        mainView = (LinearLayout) findViewById(R.id.mainView);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

      /*  String base64EncodedPublicKey = Constant.RSA_public_key;
        mHelper = new IabHelper(WellcomeActivity.this, base64EncodedPublicKey);
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
                mBroadcastReceiver = new IabBroadcastReceiver(WellcomeActivity.this);
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


        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("message")) {
            message = extras.getString("message");
            if (message.equals("SettingActivity")){
                replaceFragment(FavouritesFragment.newInstance(""), false, R.id.lyHomeContainer);
                ivMyProfile.setImageResource(R.drawable.active_profile_icon);
                homeActionbarLayout.setVisibility(View.VISIBLE);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }else  if (!message.equals("")){
                addFragment(WellcomeFragment.newInstance(message,orderId), false, R.id.lyHomeContainer);
                if (message.equals("User registration successfully done")){
                    ivHomeProfile.setImageResource(R.drawable.active_fridge_icon);
                }else {
                    ivRecipeList.setImageResource(R.drawable.active_fork_icon);
                }

            }
        }else {
            addFragment(RecipeFragment.newInstance(""), false, R.id.lyHomeContainer);
            ivRecipeList.setImageResource(R.drawable.active_fork_icon);
        }


        if (message.equals("User registration successfully done")){
            // ivHomeProfile.setEnabled(false);
            isRegistration = true;
        }else {
            isRegistration = false;
            // ivHomeProfile.setEnabled(true);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        ImageView ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        TextView tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        TextView tvProfileEmail = (TextView) findViewById(R.id.tvProfileEmail);
        TextView tvProfileNo = (TextView) findViewById(R.id.tvProfileNo);

        if (!Uhungry.sessionManager.getProfileImage().equals("") || !Uhungry.sessionManager.getProfileImage().isEmpty()){
            Picasso.with(WellcomeActivity.this).load(Uhungry.sessionManager.getProfileImage()).fit().into(ivProfilePic);

        }else {
            ivProfilePic.setImageDrawable(getResources().getDrawable(R.drawable.user_placeholder));
        }
        if (!Uhungry.sessionManager.getFullName().equals("") || !Uhungry.sessionManager.getFullName().isEmpty()){
            tvProfileName.setText(Uhungry.sessionManager.getFullName());
        }
        if (!Uhungry.sessionManager.getUserEmail().equals("") || !Uhungry.sessionManager.getUserEmail().isEmpty()){
            tvProfileEmail.setText(Uhungry.sessionManager.getUserEmail());
        }else {
            tvProfileEmail.setText("NA");
        }
        if (!Uhungry.sessionManager.getContactNo().equals("") || !Uhungry.sessionManager.getContactNo().isEmpty()){
            tvProfileNo.setText(Uhungry.sessionManager.getContactNo());
        }else {
            tvProfileNo.setText("NA");
        }

        // navigationView.setNavigationItemSelectedListener(this);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawer.setScrimColor(getResources().getColor(android.R.color.transparent));
        navigationView.setItemIconTintList(null);

        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mainView.setTranslationX(slideOffset * drawerView.getWidth());
                drawer.bringChildToFront(drawerView);
                drawer.requestLayout();
            }
        });

        addItems();

        RecyclerView rycslidermenu = (RecyclerView)findViewById(R.id.rycslidermenu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(WellcomeActivity.this);
        rycslidermenu.setLayoutManager(layoutManager);
        NavigationMenuAdapter listAdapter = new NavigationMenuAdapter(WellcomeActivity.this, navigationItems,drawer);

        rycslidermenu.setAdapter(listAdapter);

        ivHomeProfile.setOnClickListener(this);
        ivDiscover.setOnClickListener(this);
        ivMyProfile.setOnClickListener(this);
        ivRecipeList.setOnClickListener(this);
        actionbar_profile_menu.setOnClickListener(this);
        ivHistory.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.actionbar_profile_menu :

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                else {
                    drawer.openDrawer(GravityCompat.START);
                }

                break;

            case R.id.ivHistory :
                homeActionbarLayout.setVisibility(View.GONE);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                addFragment(HistoryFragment.newInstance("",homeActionbarLayout), true, R.id.lyHomeContainer);
                break;

            case R.id.ivHomeProfile :
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                homeActionbarLayout.setVisibility(View.GONE);
                ivHomeProfile.setImageResource(R.drawable.active_fridge_icon);
                ivDiscover.setImageResource(R.drawable.inactive_discover_icon);
                ivMyProfile.setImageResource(R.drawable.inactive_profile_icon);
                ivRecipeList.setImageResource(R.drawable.inactive_fork_icon);

                if (isRegistration){
                    replaceFragment(WellcomeFragment.newInstance(message,orderId), false, R.id.lyHomeContainer);
                }else {
                    replaceFragment(IngredientsFragment.newInstance("",orderId), false, R.id.lyHomeContainer);
                }

                break;
            case R.id.ivDiscover :
                replaceFragment(DiscoverFragment.newInstance(""), false, R.id.lyHomeContainer);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                homeActionbarLayout.setVisibility(View.GONE);
                ivDiscover.setImageResource(R.drawable.active_discover_icon);
                ivHomeProfile.setImageResource(R.drawable.inactive_fridge_icon);
                ivMyProfile.setImageResource(R.drawable.inactive_profile_icon);
                ivRecipeList.setImageResource(R.drawable.inactive_fork_icon);

                if (Uhungry.sessionManager.isSubcribed()) {
                    // if (orderId.equals("") || orderId.equals( Uhungry.sessionManager.getTransectionId())){
                    Log.d("Already Have subs","Welcome");
                       /* replaceFragment(DiscoverFragment.newInstance(""), false, R.id.lyHomeContainer);
                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        homeActionbarLayout.setVisibility(View.GONE);
                        ivDiscover.setImageResource(R.drawable.active_discover_icon);
                        ivHomeProfile.setImageResource(R.drawable.inactive_fridge_icon);
                        ivMyProfile.setImageResource(R.drawable.inactive_profile_icon);
                        ivRecipeList.setImageResource(R.drawable.inactive_fork_icon);*/
                  /*  }else {
                        AppUtility.showDialog(WellcomeActivity.this,"Subscription has already taken from this google id, use another to get new subscription","Alert!","Ok");
                    }*/

                }else {
                    showSubscriptionDialog();
                 /*   if (Uhungry.sessionManager.getPurchasedBy().equals("2") || Uhungry.sessionManager.getPurchasedBy().equals("")){
                        showSubscriptionDialog();
                    }else {
                        AppUtility.showAlertDialog_SingleButton(WellcomeActivity.this,getString(R.string.alert_subscription),"Alert!","Ok");
                    }*/
                }


                break;

            case R.id.ivMyProfile :
                replaceFragment(FavouritesFragment.newInstance(""), false, R.id.lyHomeContainer);
                ivMyProfile.setImageResource(R.drawable.active_profile_icon);
                homeActionbarLayout.setVisibility(View.VISIBLE);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                ivHomeProfile.setImageResource(R.drawable.inactive_fridge_icon);
                ivDiscover.setImageResource(R.drawable.inactive_discover_icon);
                ivRecipeList.setImageResource(R.drawable.inactive_fork_icon);
                if (Uhungry.sessionManager.isSubcribed()) {
                    //     if (orderId.equals("") || orderId.equals( Uhungry.sessionManager.getTransectionId())){
                    Log.d("Already Have subs","Welcome");
                      /*  replaceFragment(FavouritesFragment.newInstance(""), false, R.id.lyHomeContainer);
                        ivMyProfile.setImageResource(R.drawable.active_profile_icon);
                        homeActionbarLayout.setVisibility(View.VISIBLE);
                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        ivHomeProfile.setImageResource(R.drawable.inactive_fridge_icon);
                        ivDiscover.setImageResource(R.drawable.inactive_discover_icon);
                        ivRecipeList.setImageResource(R.drawable.inactive_fork_icon);*/

                 /*   }else {
                        AppUtility.showDialog(WellcomeActivity.this,"Subscription has already taken from this google id, use another to get new subscription","Alert!","Ok");
                    }*/
                }else {
                    showSubscriptionDialog();
                  /*  if (Uhungry.sessionManager.getPurchasedBy().equals("2") || Uhungry.sessionManager.getPurchasedBy().equals("")){
                        showSubscriptionDialog();
                    }else {
                        AppUtility.showAlertDialog_SingleButton(WellcomeActivity.this,getString(R.string.alert_subscription),"Alert!","Ok");
                    }*/
                }


               /* Intent intent = new Intent(WellcomeActivity.this, PofileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                finish();
*/
                break;
            case R.id.ivRecipeList :
                replaceFragment(RecipeFragment.newInstance(""), false, R.id.lyHomeContainer);
                homeActionbarLayout.setVisibility(View.GONE);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                ivHomeProfile.setImageResource(R.drawable.inactive_fridge_icon);
                ivDiscover.setImageResource(R.drawable.inactive_discover_icon);
                ivMyProfile.setImageResource(R.drawable.inactive_profile_icon);
                ivRecipeList.setImageResource(R.drawable.active_fork_icon);
                break;
        }
    }

    private void addItems(){
        NavigationItem item;
        for(int i=0;i<5;i++) {
            item = new NavigationItem();
            switch (i) {
              /*  case 0:
                    item.itemName = "Subscription";
                    item.itemImg = R.drawable.inactive_subscribe_icon;

                    break;*/
                case 0:
                    item.itemName = "Terms & Conditions";
                    item.itemImg = R.drawable.inactive_term_conditions_icon;

                    break;
                case 1:
                    item.itemName = "Edit Profile";
                    item.itemImg = R.drawable.edit_profile_icon;

                    break;

                case 2:
                    item.itemName = "Help";
                    item.itemImg = R.drawable.inactive_faq;
                    break;

                case 3:
                    item.itemName = "FAQs";
                    item.itemImg = R.drawable.inactive_help_icon;
                    break;

                case 4:
                    item.itemName = "Logout";
                    item.itemImg = R.drawable.inactive_logout_icon;

                    break;

            }
            navigationItems.add(item);
        }
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        FragmentManager fm = getSupportFragmentManager();

        int i = fm.getBackStackEntryCount();

        while(i>0){
            fm.popBackStackImmediate();
            i--;
        }
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped)
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStackName).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    public void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right);
            transaction.add(containerId, fragment, backStackName); //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();
        int i = fm.getBackStackEntryCount();
        if (i>0 ) {
            fm.popBackStackImmediate();
        }
        else {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finish();
                return;
            }else {
                this.doubleBackToExitPressedOnce = true;
                AppUtility.showToast(this, "Please click back again to exit", 1);
            }

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 1000);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(mBroadcastReceiver);
    }


   /* private void callInAppPurches() {

        String payload = "";
        try {
            mHelper.launchSubscriptionPurchaseFlow(WellcomeActivity.this, SKU_PLAN, RC_REQUEST, mPurchaseFinishedListener, payload);
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
                    TransectionId = orderId;
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
                    // Uhungry.sessionManager.setPurchasedBy("2");
                    // Uhungry.sessionManager.setTransectionId(orderId);
                    TransectionId = orderId;
                    if (orderId.equals( Uhungry.sessionManager.getTransectionId())){
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String startDate = dateFormat.format(new Date(purchaseTime));

                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(purchaseTime);
                        cal.add(Calendar.MONTH, 1);
                        Date date = cal.getTime();
                        endDate = dateFormat.format(date);
                        //  Uhungry.sessionManager.setEndDate(endDate);
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
        android.support.v7.app.AlertDialog.Builder bld = new android.support.v7.app.AlertDialog.Builder(WellcomeActivity.this);
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
                                AppUtility.showAlertDialog_SingleButton(WellcomeActivity.this,message,"Error","Ok");
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
                        AppUtility.showAlertDialog_SingleButton(WellcomeActivity.this,"Something went wrong, please check after some time.","Error","Ok");
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

        RequestQueue requestQueue = Volley.newRequestQueue(WellcomeActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void receivedBroadcast() {

    }*/

}
