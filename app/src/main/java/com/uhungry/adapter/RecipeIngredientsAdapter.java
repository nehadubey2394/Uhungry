package com.uhungry.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.uhungry.R;
import com.uhungry.model.RecipeDetailIngList;

import java.util.ArrayList;


public class RecipeIngredientsAdapter extends RecyclerView.Adapter<RecipeIngredientsAdapter.ViewHolder> {
    private Activity context;
    private ArrayList<RecipeDetailIngList> arrayList;
    private int lastPosition = -1;

    public RecipeIngredientsAdapter(Activity context, ArrayList<RecipeDetailIngList> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_ingredients, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        RecipeDetailIngList ingList = arrayList.get(listPosition);
        if (ingList.isSelected.equals("0")){
            Typeface mtypeFace = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Roboto_Bold.ttf");
            holder.textView.setTypeface(mtypeFace);
        }

        // holder.textView.setTextColor(ContextCompat.getColor(context,R.color.app_black));


        holder.textView.setText(ingList.ingName);

        if(listPosition >lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context,
                    R.anim.item_animation_fall_down);
            holder.itemView.startAnimation(animation);
            lastPosition = listPosition;
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;

        private ViewHolder(View itemView)
        {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.textView);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {


        }


    }

}