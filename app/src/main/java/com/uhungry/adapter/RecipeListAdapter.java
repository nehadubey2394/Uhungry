package com.uhungry.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.swipe.SwipeLayout;
import com.squareup.picasso.Picasso;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.uhungry.R;
import com.uhungry.activity.RecipeDetailActivity;
import com.uhungry.activity.WellcomeActivity;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.listner.CustomAdapterButtonListener;
import com.uhungry.model.Recipies;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecipeListAdapter extends RecyclerItemAdapter implements StickyRecyclerHeadersAdapter {
    private Activity context;
    private ArrayList<Recipies> recipies;
    private TextView tvResultCount;
    private int lastPosition = -1,count;
    private CustomAdapterButtonListener customAdapterButtonListener = null;
    // Constructor of the class
    public RecipeListAdapter(Activity context, ArrayList<Recipies> recipies,TextView tvResultCount ) {
        this.context = context;
        this.recipies = recipies;
        this.tvResultCount = tvResultCount;
    }

    public void setCustomListener(CustomAdapterButtonListener customAdapterListner){
        this.customAdapterButtonListener = customAdapterListner;
    }


    private static class ItemHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView header;
        ItemHeaderViewHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView.findViewById(R.id.titleTextView);
        }
    }


    @Override
    public long getHeaderId(int position) {
        Recipies menu =  recipies.get(position);
        //return menu.cusineType.hashCode();
        if (menu.cusineType!=null)
            return Math.abs(menu.cusineType.hashCode());
        else
            return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_header, parent, false);
        return new ItemHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHeaderViewHolder) {
            if (getItem(position).cusineType != null) {
                String header = String.valueOf(getItem(position).cusineType);
                ((ItemHeaderViewHolder) holder).header.setText(header);
            }else {
                ((ItemHeaderViewHolder) holder).header.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resipe_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {

        Recipies list = recipies.get(position);
        MyViewHolder holder = ((MyViewHolder)h);

        holder.tvPercentage.setText(String.format("%s%%", list.percentage));
        holder.tvRecipeName.setText(list.recName);
        count = Integer.parseInt(Recipies.count);
        if (!list.time.equals(""))
            holder.tvTime.setText(list.time);
        else
            holder.tvTime.setText("NA");

        if (!list.recipeImg.equals("")){
            Picasso.with(context).load(list.recipeImg).fit().into(holder.ivRecImg);
        }else {
            holder.ivRecImg.setImageResource(R.drawable.recipe_default);
        }

        if (list.isFav.equals("1")){
            holder.ivFav.setImageResource(R.drawable.favourite_selected_icon);
        }else {
            holder.ivFav.setImageResource(R.drawable.favourite_icon);
        }

        if(position >lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context,
                    R.anim.item_animation_slide_from_bottom);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
    }


    @Override
    public int getItemCount() {
        return recipies.size();
    }

    public Recipies getItem(int position) {
        return recipies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }


    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivRecImg,ivFav;
        private TextView tvPercentage,tvRecipeName,tvTime;
        private SwipeLayout sample1;
        private LinearLayout lyRemove,lyFav;
        private RelativeLayout lyMain;
        private MyViewHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            ivRecImg = (ImageView) itemView.findViewById(R.id.ivRecImg);
            ivFav = (ImageView) itemView.findViewById(R.id.ivFav);
            tvPercentage = (TextView) itemView.findViewById(R.id.tvPercentage);
            tvRecipeName = (TextView) itemView.findViewById(R.id.tvRecipeName);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            lyRemove = (LinearLayout) itemView.findViewById(R.id.lyRemove);
            lyFav = (LinearLayout) itemView.findViewById(R.id.lyFav);
            lyMain = (RelativeLayout) itemView.findViewById(R.id.lyMain);

            lyFav.setOnClickListener(this);
            lyRemove.setOnClickListener(this);
            lyMain.setOnClickListener(this);

            sample1 = (SwipeLayout) itemView.findViewById(R.id.sample1);
            sample1.setShowMode(SwipeLayout.ShowMode.PullOut);
            sample1.addDrag(SwipeLayout.DragEdge.Left, sample1.findViewById(R.id.right_swipe_wrapper));
            sample1.addDrag(SwipeLayout.DragEdge.Right, sample1.findViewById(R.id.left_swipe_wrapper));

            sample1.addRevealListener(R.id.delete, new SwipeLayout.OnRevealListener() {
                @Override
                public void onReveal(View child, SwipeLayout.DragEdge edge, float fraction, int distance) {

                }
            });
        }

        @Override
        public void onClick(View view) {
            Recipies list = recipies.get(getAdapterPosition());
            switch (view.getId()){
                case  R.id.lyFav :
                    if(AppUtility.isNetworkAvailable(context)) {
                        AddFav(list);
                      /*  if (Uhungry.sessionManager.isSubcribed()) {
                        }else {
                            ((WellcomeActivity)context).showSubscriptionDialog();
                        }*/
                    }
                    else {
                        AppUtility.showAlertDialog_SingleButton(context,context.getResources().getString(R.string.network_error),"Alert","Ok");
                    }
                    break;
                case  R.id.lyRemove :
                    removeRecipe(list,getAdapterPosition());

                   /* if(AppUtility.isNetworkAvailable(context)) {
                        if (Uhungry.sessionManager.isSubcribed()) {
                        }else {
                            ((WellcomeActivity)context).showSubscriptionDialog();
                        }
                    }
                    else {
                        AppUtility.showAlertDialog_SingleButton(context,context.getResources().getString(R.string.network_error),"Alert","Ok");
                    }*/
                    break;

                case  R.id.lyMain :
                    //if (Uhungry.sessionManager.isSubcribed()) {
                    Intent intent = new Intent(context, RecipeDetailActivity.class);
                    intent.putExtra("recipeId",list.recipeId);
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                    // }else {
                    //     ((WellcomeActivity)context).showSubscriptionDialog();
                    // }
                    break;
            }
        }
    }


    private void AddFav(final Recipies list) {
        final ProgressDialog customDialog = new ProgressDialog(context);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+"addFavourites",

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
                            if (list.isFav.equals("0")){
                                list.isFav = "1";
                                // AppUtility.showToast(context,"Added to favorites",0);
                            }else {
                                list.isFav = "0";
                                //  AppUtility.showToast(context,"Remove from favorites",0);
                            }

                            notifyDataSetChanged();

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
                String isFav= "";
                if (list.isFav.equals("0")){
                    isFav = "1";
                }else {
                    isFav = "0";
                }
                header.put("recipeId", list.recipeId);
                header.put("isFav", isFav);

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

    private void removeRecipe(final Recipies list,final int pos)
    {
        final ProgressDialog customDialog = new ProgressDialog(context);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+"removeRecipe",

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {
                            count--;
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");
                            //   int count = Integer.parseInt(list.count);

                            if (pos==0){
                                if (customAdapterButtonListener != null) {
                                    customAdapterButtonListener.onButtonClick(pos,"",0);
                                }
                            }else {
                                recipies.remove(pos);
                                notifyDataSetChanged();
                                list.count = String.valueOf(count);
                            }


                            tvResultCount.setText(count+" Results");

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

                header.put("recipeId", list.recipeId);

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