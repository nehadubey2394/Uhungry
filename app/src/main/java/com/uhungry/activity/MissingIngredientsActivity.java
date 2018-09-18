package com.uhungry.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.model.Ingredients;
import com.uhungry.model.RecipeSteps;
import com.uhungry.model.SubIngModel;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MissingIngredientsActivity extends AppCompatActivity implements View.OnClickListener{
    private ArrayList<Ingredients> arrayList ;
    private TextView tvEmpty;
    private String recId = "",commingFrom,isFav;
    private ExpandableListView lvMissingIng;
    private KichensTypeGroceryListAdapter adapter;
    private Button btnMissIngdone;
    private ArrayList<RecipeSteps> recipeStepses;
    private CheckBox chbLikeYes,chbLikeNo;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missing_ingredients);
        bindView();
    }

    private void bindView() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            commingFrom = extras.getString("commingFrom");
            recId = extras.getString("recipeId");
            if (commingFrom.equals("RecipeDetailActivity")){
                recipeStepses = (ArrayList<RecipeSteps>) extras.getSerializable("stepsList");
            }
        }

        requestQueue = Volley.newRequestQueue(MissingIngredientsActivity.this);
        //Intent i = getIntent();
        // recipeStepses = (ArrayList<RecipeSteps>) i.getSerializableExtra("");
        //recId = i.getStringExtra("recipeId");


        TextView tvTitle = (TextView) findViewById(R.id.actionbarLayout_title);
        ImageView actionbar_btton_back = (ImageView) findViewById(R.id.actionbar_btton_back);
        actionbar_btton_back.setVisibility(View.VISIBLE);

        if (commingFrom.equals("RecipeDetailActivity"))
            tvTitle.setText(getString(R.string.title_missing_ing));
        else
            tvTitle.setText(getString(R.string.title_remove_ing));

        btnMissIngdone = (Button) findViewById(R.id.btnMissIngdone);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);

        arrayList = new ArrayList<>();

        lvMissingIng = (ExpandableListView) findViewById(R.id.lvMissingIng);
        adapter = new KichensTypeGroceryListAdapter(MissingIngredientsActivity.this, arrayList);
        lvMissingIng.setAdapter(adapter);

        lvMissingIng.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                Ingredients ingredients = arrayList.get(groupPosition);
                ArrayList<SubIngModel> arrayList = ingredients.getArrayList();

                if (ingredients.isChecked.equals("0")){

                    if (arrayList.size()==0){
                        btnMissIngdone.setVisibility(View.VISIBLE);
                        ingredients.isChecked = "1";
                        ingredients.isSubItemChecked = false;
                        adapter.notifyDataSetChanged();
                    }

                }else {

                    if (arrayList.size()==0){
                        ingredients.isChecked = "0";
                        ingredients.isSubItemChecked = false;
                        adapter.notifyDataSetChanged();

                    }
                }

                return false;
            }
        });

        // Listview Group expanded listener
        lvMissingIng.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        // Listview Group collasped listener
        lvMissingIng.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        lvMissingIng.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition,
                                        long id) {
                Ingredients ingredients = arrayList.get(groupPosition);
                ArrayList<SubIngModel> arrayList = ingredients.getArrayList();
                SubIngModel subIngModel = arrayList.get(childPosition);
                String subIngId = "";

                if (subIngModel.isSubIngSelected.equals("0")){
                    btnMissIngdone.setVisibility(View.VISIBLE);
                    subIngModel.isSubIngSelected = "1";
                    ingredients.isSubItemChecked = true;
                    adapter.notifyDataSetChanged();

                }else {
                    subIngModel.isSubIngSelected = "0";
                    ingredients.isSubItemChecked = false;
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        actionbar_btton_back.setOnClickListener(this);
        btnMissIngdone.setOnClickListener(this);

        if(AppUtility.isNetworkAvailable(MissingIngredientsActivity.this)) {
            GetIngredientsListTask();
        }
        else {
            AppUtility.showAlertDialog_SingleButton(MissingIngredientsActivity.this,getResources().getString(R.string.network_error),"Alert!","Ok");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.actionbar_btton_back:
                onBackPressed();
                //finish();
                // overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                break;

            case  R.id.chbLikeYes :
                chbLikeNo.setChecked(false);
                break;

            case  R.id.chbLikeNo :
                chbLikeYes.setChecked(false);
                break;

            case R.id.btnMissIngdone :
                List<Map<String,String>> list = new ArrayList<Map<String, String>>();
                list.clear();
                for (Ingredients ingredients : arrayList){
                    HashMap<String,String> hashMap = new HashMap<>();

                    if (ingredients.isChecked.equals("1") || ingredients.isSubItemChecked){
                        String   subIngId = "";
                        String ingId = ingredients.itemId;

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

                if (jsonArray!=null && jsonArray.length()!=0){
                    if(AppUtility.isNetworkAvailable(MissingIngredientsActivity.this)) {
                        if (commingFrom.equals("RecipeDetailActivity"))
                            AddIngredients(jsonArray);
                        else
                            removeRecipe(jsonArray);
                    }
                    else {
                        AppUtility.showAlertDialog_SingleButton(MissingIngredientsActivity.this,getResources().getString(R.string.network_error),"Alert!","Ok");
                    }
                }else {
                    AppUtility.showToast(MissingIngredientsActivity.this,"You have't select any ingredient",0);
                }

                break;
        }
    }


    public void showAlertDialog(String msg, String title) {
        View DialogView = View.inflate(MissingIngredientsActivity.this, R.layout.dialog_recipe_layout, null);

        final Dialog alertDailog = new Dialog(MissingIngredientsActivity.this, android.R.style.Theme_Light);
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
                Collections.sort(recipeStepses, new Comparator<RecipeSteps>() {
                    @Override
                    public int compare(RecipeSteps a, RecipeSteps b)
                    {
                        return a.stepNo > b.stepNo ? +1 : a.stepNo < b.stepNo ? -1 : 0;
                    }
                });

                if (recipeStepses.size()!=0){
                    Intent intent = new Intent(MissingIngredientsActivity.this, AllStepsActivity.class);
                    intent.putExtra("recipeId",recId);
                    intent.putExtra("stepsList",recipeStepses);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                    finish();
                }else {
                    AppUtility.showDialog(MissingIngredientsActivity.this,"Steps are not Available for this Recipe","Alert","Ok");

                }

            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDailog.cancel();
                Intent intent = new Intent(MissingIngredientsActivity.this, WellcomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                finish();
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

    public  void showDialog() {
        View DialogView = View.inflate(MissingIngredientsActivity.this, R.layout.dialog_like_layout, null);

        final Dialog alertDailog = new Dialog(MissingIngredientsActivity.this, android.R.style.Theme_Light);
        alertDailog.setCanceledOnTouchOutside(true);
        alertDailog.setCancelable(true);

        alertDailog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDailog.getWindow().getAttributes().windowAnimations = R.style.InOutAnimation;
        alertDailog.setContentView(DialogView);

        chbLikeYes = (CheckBox)DialogView. findViewById(R.id.chbLikeYes);
        chbLikeNo = (CheckBox) DialogView.findViewById(R.id.chbLikeNo);

        chbLikeYes.setOnClickListener(this);
        chbLikeNo.setOnClickListener(this);

        ImageView ivCross = (ImageView) DialogView.findViewById(R.id.ivCross);
        Button btnDone = (Button) DialogView.findViewById(R.id.btnDone);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AppUtility.isNetworkAvailable(MissingIngredientsActivity.this)) {
                    if (!chbLikeYes.isChecked() &&  !chbLikeNo.isChecked()){
                        AppUtility.showToast(MissingIngredientsActivity.this,"Please select your choice",0);
                    }else {
                        alertDailog.cancel();
                        if (chbLikeYes.isChecked()){
                            isFav = "1";
                            AddFav();
                        }else {
                            isFav = "0";
                            AddFav();
                        }
                        saveRecipeInHistory();

                        // saveRecipeInHistory();
                    }
                }
                else {
                    AppUtility.showAlertDialog_SingleButton(MissingIngredientsActivity.this,getResources().getString(R.string.network_error),"Alert!","Ok");
                }

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


    public void GetIngredientsListTask() {
        final ProgressDialog customDialog = new ProgressDialog(MissingIngredientsActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.BASE_URL_USER+"getMissingIngs?"+"recipeId="+recId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String userstatus = jsonObj.getString("userstatus");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("success")) {
                                tvEmpty.setVisibility(View.GONE);
                                lvMissingIng.setVisibility(View.VISIBLE);
                                arrayList.clear();

                                JSONArray jsonArray = jsonObj.getJSONArray("data");
                                if(jsonArray != null && jsonArray.length() > 0 ) {

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        Ingredients  item = new Ingredients();

                                        JSONArray subIngDataArray = jsonObject.getJSONArray("subIngData");
                                        ArrayList<SubIngModel> subIngModelArrayList = new ArrayList<>();
                                        //       arrayList.clear();
                                        if(subIngDataArray != null && subIngDataArray.length() > 0 ) {

                                            for (int j = 0; j < subIngDataArray.length(); j++) {
                                                SubIngModel subItem = new SubIngModel();
                                                JSONObject jsonObject2 = subIngDataArray.getJSONObject(j);
                                                subItem.subIngId = jsonObject2.getString("id").trim();
                                                subItem.subIngName = jsonObject2.getString("name").trim();
                                                subItem.subIngImage = jsonObject2.getString("image").trim();
                                                subItem.isSubIngSelected = "0";
                                                subIngModelArrayList.add(subItem);
                                            }
                                        }
                                        item.setArrayList(subIngModelArrayList);
                                        item.itemId = jsonObject.getString("ingId").trim();
                                        item.itemName = jsonObject.getString("ingName").trim();
                                        item.itemImg = jsonObject.getString("image").trim();
                                        item.isChecked = "0";

                                        arrayList.add(item);

                                    }
                                    adapter.notifyDataSetChanged();

                                }

                            }
                            else
                            {
                                customDialog.cancel();
                                tvEmpty.setVisibility(View.VISIBLE);
                                lvMissingIng.setVisibility(View.GONE);
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
                        Toast.makeText(MissingIngredientsActivity.this, "‘Ooops! Something went wrong", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(MissingIngredientsActivity.this, "Already logged in on another device", Toast.LENGTH_LONG).show();
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MissingIngredientsActivity.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,1));
        requestQueue.add(stringRequest);
    }

    private void AddIngredients(final JSONArray array) {
        final ProgressDialog customDialog = new ProgressDialog(this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+"addMissingIngs",

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
                                AppUtility.showToast(MissingIngredientsActivity.this,"Selected ingredients added to grocery list",0);
                                // lvFTgrocery.notify();

                                showAlertDialog(getString(R.string.do_you_want_try_rec_still),"Alert!");

                            /*    AlertDialog.Builder builder1 = new AlertDialog.Builder(MissingIngredientsActivity.this);
                                builder1.setTitle("Alert");
                                builder1.setMessage("Do you Want to Try this Recipe Still?");
                                builder1.setCancelable(true);
                                builder1.setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                Collections.sort(recipeStepses, new Comparator<RecipeSteps>() {
                                                    @Override
                                                    public int compare(RecipeSteps a, RecipeSteps b)
                                                    {
                                                        return a.stepNo > b.stepNo ? +1 : a.stepNo < b.stepNo ? -1 : 0;
                                                    }
                                                });

                                                Intent intent = new Intent(MissingIngredientsActivity.this, AllStepsActivity.class);
                                                intent.putExtra("recipeId",recId);
                                                intent.putExtra("stepsList",recipeStepses);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                                                finish();
                                            }
                                        });
                                builder1.setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                Intent intent = new Intent(MissingIngredientsActivity.this, WellcomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                                                finish();

                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
*/
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(MissingIngredientsActivity.this,message,"Error","Ok");

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
                        AppUtility.showAlertDialog_SingleButton(MissingIngredientsActivity.this,"Oop! Something went wrong, please check after some time.","Error","Ok");
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();

                header.put("ingData", array.toString());

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

        RequestQueue requestQueue = Volley.newRequestQueue(MissingIngredientsActivity.this);
        requestQueue.add(stringRequest);
    }

    private void removeRecipe(final JSONArray array) {
        final ProgressDialog customDialog = new ProgressDialog(MissingIngredientsActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+"removeIngredients",

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
                            showDialog();
                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        customDialog.cancel();
                        AppUtility.showAlertDialog_SingleButton(MissingIngredientsActivity.this,"Something went wrong, please check after some time.","Error","Ok");
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("recipeId", recId);
                header.put("ingData", array.toString());
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

        RequestQueue requestQueue = Volley.newRequestQueue(MissingIngredientsActivity.this);
        requestQueue.add(stringRequest);
    }

    private void saveRecipeInHistory() {
        final ProgressDialog customDialog = new ProgressDialog(this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+"saveRecipe",

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
                                if (Uhungry.sessionManager.isSubcribed()) {
                                    Intent intent = new Intent(MissingIngredientsActivity.this, WellcomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                                    finish();
                                }else {
                                    new AppUtility().showSubcriptionDialog(MissingIngredientsActivity.this, new AppUtility.SubcriptionListner() {
                                        @Override
                                        public void onSubcribeClick() {
                                            Intent intent = new Intent(MissingIngredientsActivity.this, SubcriptionActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                                        }
                                    });
                                }
                                //   AppUtility.showToast(AllStepsActivity.this,"Recipe added to history",0);
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(MissingIngredientsActivity.this,message,"Error","Ok");

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
                        AppUtility.showAlertDialog_SingleButton(MissingIngredientsActivity.this,"Something went wrong, please check after some time.","Error","Ok");
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();

                header.put("recipeId", recId);

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

        requestQueue.add(stringRequest);
    }

    private void AddFav() {
        final ProgressDialog customDialog = new ProgressDialog(MissingIngredientsActivity.this);
        // customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+"addFavourites",

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  customDialog.cancel();
                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   customDialog.cancel();
                        AppUtility.showAlertDialog_SingleButton(MissingIngredientsActivity.this,"Something went wrong, please check after some time.","Error","Ok");
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();

                header.put("isFav", isFav);
                header.put("recipeId", recId);

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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000,0,1));
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        if (!Uhungry.sessionManager.isSubcribed()){
            Intent intent = new Intent(MissingIngredientsActivity.this, WellcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
            finish();
        }else {
            finish();
            overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
        }

    }
}
