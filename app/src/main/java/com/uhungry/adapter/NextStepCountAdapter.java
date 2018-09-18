package com.uhungry.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uhungry.R;
import com.uhungry.listner.CustomAdapterButtonListener;
import com.uhungry.model.RecipeSteps;

import java.util.ArrayList;


public class NextStepCountAdapter extends RecyclerView.Adapter<NextStepCountAdapter.ViewHolder> {
    private Activity context;
    private ArrayList<RecipeSteps> recipeStepses;
    private CustomAdapterButtonListener customButtonListener = null;

    // Constructor of the class
    public NextStepCountAdapter(Activity context, ArrayList<RecipeSteps> recipeStepses) {
        this.context = context;
        this.recipeStepses = recipeStepses;
    }
    public void setCustomListener(CustomAdapterButtonListener customButtonListener){
        this.customButtonListener = customButtonListener;
    }

    @Override
    public int getItemCount() {
        return recipeStepses.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_steps_count, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {

        final RecipeSteps item = recipeStepses.get(listPosition);

        if (item.isViewd){
            holder.ivBubble.setImageResource(R.color.green);
        }else {
            holder.ivBubble.setImageResource(R.drawable.bg_field_gray_circle);
        }


    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivBubble;

        private ViewHolder(View itemView)
        {
            super(itemView);
            ivBubble = (ImageView) itemView.findViewById(R.id.ivBubble);
           // itemView.setOnClickListener(this);
            ivBubble.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (customButtonListener != null) {
                        customButtonListener.onButtonClick(getAdapterPosition(),"",0);
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }

}