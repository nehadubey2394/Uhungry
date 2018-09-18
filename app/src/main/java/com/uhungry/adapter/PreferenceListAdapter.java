package com.uhungry.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uhungry.R;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.model.FoodType;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class PreferenceListAdapter extends RecyclerView.Adapter<PreferenceListAdapter.ViewHolder> {
    private Activity context;
    private ArrayList<FoodType> arrayList;
    private HashMap<String,FoodType> hashMap;
    private Button done;
    private String foodTypeId = "";
    private boolean isChanged = false;
    private CheckBox chbAll;
    private int lastPosition = -1;
    // Constructor of the class
    public PreferenceListAdapter(Activity context, ArrayList<FoodType> arrayList, Button doneButton, CheckBox chbAll) {
        this.context = context;
        this.arrayList = arrayList;
        hashMap = new HashMap<>();
        this.done=doneButton;
        this.chbAll=chbAll;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_foodtype, parent, false);
        return new ViewHolder(view);
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder,  int position) {
        final FoodType foodType =  arrayList.get(position);
        holder.tvItemName.setText(foodType.foodName);
        // done.setEnabled(true);
        // done.setAlpha(1.0f);

        if (foodType.isChecked.equals("1")){
            holder.ivFdChecbox.setImageResource(R.drawable.checked_icon);
            hashMap.put(foodType.foodId,foodType);

            if(foodTypeId.equals("")){
                foodTypeId = foodType.foodId;
            }else
            {
                if(!foodTypeId.contains(foodType.foodId))
                    foodTypeId = foodTypeId + "," + foodType.foodId;
            }
        }
        else {
            holder.ivFdChecbox.setImageResource(R.drawable.unchecked_icon);
        }

        if(position >lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context,
                    R.anim.item_animation_slide_from_bottom);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

    // Static inner class to initialize the views of rows
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvItemName;
        private ImageView ivFdChecbox;

        private ViewHolder(View itemView) {
            super(itemView);
            tvItemName = (TextView) itemView.findViewById(R.id.tvFoodTypeName);
            ivFdChecbox = (ImageView) itemView.findViewById(R.id.ivFdChecbox);
            itemView.setOnClickListener(this);

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    foodTypeId="";
                    // ArrayList<String> newArrayList = new ArrayList<>();
                    Iterator it = hashMap.entrySet().iterator();
                    List<Map<String,Integer>> list = new ArrayList<Map<String, Integer>>();

                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        pair.getKey();

                        if(foodTypeId.equals("")){
                            foodTypeId =  String.valueOf(pair.getKey());
                        } else {
                            foodTypeId = foodTypeId + "," + String.valueOf(pair.getKey());
                        }
                        it.remove();
                    }

                    if(AppUtility.isNetworkAvailable(context)) {
                        AddFoodType();
                    }
                    else {
                        AppUtility.showAlertDialog_SingleButton(context,context.getResources().getString(R.string.network_error),"Alert!","Ok");
                    }

                }
            });

            chbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        for (int i=0;i<arrayList.size();i++){
                            FoodType foodType =  arrayList.get(i);
                            isChanged = true;
                            foodType.isChecked = "1";
                            hashMap.put(foodType.foodId, foodType);
                        }
                    }else {
                        for (int i=0;i< arrayList.size();i++){
                            FoodType foodType =  arrayList.get(i);
                            isChanged = false;
                            foodType.isChecked = "0";
                            hashMap.remove(foodType.foodId);
                        }
                    }

                    notifyDataSetChanged();
                    updateButtonUi();
                }

            });
        }

        @Override
        public void onClick(View view) {
            FoodType foodType =  arrayList.get(getAdapterPosition());

            if (foodType.isChecked.equals("0")){
                isChanged = true;
                foodType.isChecked = "1";
                hashMap.put(foodType.foodId, foodType);
            }
            else{
                foodType.isChecked = "0";
                hashMap.remove(foodType.foodId);
                isChanged = true;

            }
            notifyItemChanged(getAdapterPosition());
            updateButtonUi();
        }

        private void updateButtonUi(){
            if(hashMap.size()>0 && isChanged){
                done.setEnabled(true);
                done.setAlpha(1.0f);
                done.setVisibility(View.VISIBLE);

            }else {
                done.setVisibility(View.GONE);
            }
        }
    }

    private void AddFoodType() {
        final ProgressDialog customDialog = new ProgressDialog(context);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+"updateFoodType",

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
                                AppUtility.showToast(context,"Cuisines updated successfully",0);

                               /* Intent intent = new Intent();
                                intent.putExtra("isDataChanged",true);
                                context.setResult(Activity.RESULT_OK,intent);
                                context.finish();
                                context. overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);*/
                                context.finish();
                                context.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(context,message,"Error","Ok");

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
                        AppUtility.showAlertDialog_SingleButton(context,"Something went wrong, please check after some time.","Error","Ok");
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("fId", foodTypeId);
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

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}
