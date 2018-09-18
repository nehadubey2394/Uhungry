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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.swipe.SwipeLayout;
import com.squareup.picasso.Picasso;
import com.uhungry.R;
import com.uhungry.activity.RecipeDetailActivity;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.listner.CustomAdapterButtonListener;
import com.uhungry.model.Recipies;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FavListAdapter extends RecyclerView.Adapter<FavListAdapter.ViewHolder> {
    private Activity context;
    private ArrayList<Recipies> recipies;
    private String tag;
    private int lastPosition = -1;
    private CustomAdapterButtonListener customAdapterButtonListener = null;
    // Constructor of the class
    public FavListAdapter(Activity context, ArrayList<Recipies> recipies,String tag) {
        this.context = context;
        this.recipies = recipies;
        this.tag = tag;
    }

    public void setCustomListener(CustomAdapterButtonListener customAdapterListner){
        this.customAdapterButtonListener = customAdapterListner;
    }

    @Override
    public int getItemCount() {
        return recipies.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fav_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {

        Recipies list =  recipies.get(listPosition);
        holder.tvFavRecName.setText(list.recName);
        holder.tvFavTime.setText(list.time);

        if (!list.recipeImg.equals("")){
            Picasso.with(context).load(list.recipeImg).fit().into(holder.ivFavRecImg);
        }else {
            holder.ivFavRecImg.setImageResource(R.drawable.recipe_default);
        }

        if(listPosition >lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context,
                    R.anim.up_from_bottom);
            holder.itemView.startAnimation(animation);
            lastPosition = listPosition;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView ivFavRecImg;
        private TextView tvFavRecName,tvFavTime;
        private SwipeLayout sample1;
        private RelativeLayout lyMain;
        private LinearLayout lyRemove;
        private ViewHolder(View itemView)
        {
            super(itemView);
            ivFavRecImg = (ImageView) itemView.findViewById(R.id.ivFavRecImg);
            tvFavRecName = (TextView) itemView.findViewById(R.id.tvFavRecName);
            tvFavTime = (TextView) itemView.findViewById(R.id.tvFavTime);
            lyMain = (RelativeLayout) itemView.findViewById(R.id.lyMain);
            lyRemove = (LinearLayout) itemView.findViewById(R.id.lyRemove);

            sample1 = (SwipeLayout) itemView.findViewById(R.id.sample1);
            sample1.setShowMode(SwipeLayout.ShowMode.PullOut);
            sample1.addDrag(SwipeLayout.DragEdge.Left, sample1.findViewById(R.id.right_swipe_wrapper));
            // sample1.addDrag(SwipeLayout.DragEdge.Right, sample1.findViewById(R.id.left_swipe_wrapper));

            sample1.addRevealListener(R.id.delete, new SwipeLayout.OnRevealListener() {
                @Override
                public void onReveal(View child, SwipeLayout.DragEdge edge, float fraction, int distance) {
                }
            });

            lyMain.setOnClickListener(this);
            lyRemove.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Recipies list = recipies.get(getAdapterPosition());
            switch (view.getId()) {
                case R.id.lyMain:
                    Intent intent = new Intent(context, RecipeDetailActivity.class);
                    intent.putExtra("recipeId",list.recipeId);
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                    break;
                case R.id.lyRemove:
                    if (AppUtility.isNetworkAvailable(context)) {
                        removeRecipe(list, getAdapterPosition());
                    } else {
                        AppUtility.showAlertDialog_SingleButton(context, context.getResources().getString(R.string.network_error), "Alert", "Ok");
                    }
                    break;
            }
        }
    }

    private void removeRecipe(final Recipies list,final int pos) {
        final ProgressDialog customDialog = new ProgressDialog(context);
        customDialog.show();

        String urlStr = "";
        if (tag.equals("HistoryFragment")){
            urlStr = "removeHistory";
        }else {
            urlStr = "removeFavourites";
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+urlStr,

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
                            //   AppUtility.showToast(context,"Removed successfully",0);

                            if (pos==0){
                                if (customAdapterButtonListener != null) {
                                    customAdapterButtonListener.onButtonClick(pos,"",0);
                                }
                            }else {
                                recipies.remove(pos);
                                notifyDataSetChanged();
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