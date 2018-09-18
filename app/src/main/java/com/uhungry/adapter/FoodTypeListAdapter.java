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

import com.uhungry.R;
import com.uhungry.activity.FoodTypeGroceryActivity;
import com.uhungry.model.FoodType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class FoodTypeListAdapter extends RecyclerView.Adapter<FoodTypeListAdapter.ViewHolder> {
    private Activity context;
    private ArrayList<FoodType> arrayList;
    private HashMap<String,FoodType> hashMap;
    private Button done;
    private CheckBox chbAll;
    private String foodTypeId = "";
    private int lastPosition = -1;
    // Constructor of the class
    public FoodTypeListAdapter(Activity context, ArrayList<FoodType> arrayList, Button doneButton, CheckBox chbAll) {
        this.context = context;
        this.arrayList = arrayList;
        hashMap = new HashMap<>();
        done=doneButton;
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
        if(position >lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context,
                    R.anim.item_animation_fall_down);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }

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

            chbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        for (int i=0;i<arrayList.size();i++){
                            FoodType foodType =  arrayList.get(i);
                            foodType.isChecked = "1";
                            hashMap.put(foodType.foodId, foodType);
                        }
                    }else {
                        for (int i=0;i< arrayList.size();i++){
                            FoodType foodType =  arrayList.get(i);
                            foodType.isChecked = "0";
                            hashMap.remove(foodType.foodId);
                        }
                    }

                    notifyDataSetChanged();
                    updateButtonUi();
                }

            });

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
                    Intent intent = new Intent(context, FoodTypeGroceryActivity.class);
                    intent.putExtra("foodTypeId",foodTypeId);
                    context.startActivity(intent);
                    context. overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                }
            });

        }

        @Override
        public void onClick(View view) {
            FoodType foodType =  arrayList.get(getAdapterPosition());

            if (foodType.isChecked.equals("0")){
                    foodType.isChecked = "1";
                    hashMap.put(foodType.foodId, foodType);
            }
            else {
                foodType.isChecked = "0";
                hashMap.remove(foodType.foodId);
            }

            notifyItemChanged(getAdapterPosition());
            updateButtonUi();
        }

        private void updateButtonUi(){
            if(hashMap.size()>0){
                done.setEnabled(true);
                done.setAlpha(1.0f);
                done.setVisibility(View.VISIBLE);

            }else {
                done.setVisibility(View.GONE);
            }
        }
    }
}
