package com.uhungry.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uhungry.R;
import com.uhungry.listner.CustomAdapterButtonListener;
import com.uhungry.model.Category;
import java.util.ArrayList;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Activity context;
    private String CategoryID = "";
    private ArrayList<Category> itemList;
    private CustomAdapterButtonListener customButtonListener = null;

    // Constructor of the class
    public CategoryAdapter(Activity context, ArrayList<Category> itemList) {
        this.context = context;
        this.itemList = itemList;
    }
    public void setCustomListener(CustomAdapterButtonListener customButtonListener){
        this.customButtonListener = customButtonListener;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        Category category = itemList.get(listPosition);

        holder.tvCategoryName.setText(category.name);
        holder.tvCatCount.setText(category.count);

        String image =  category.image;

        if (!image.equals("") && !image.isEmpty()){
            Picasso.with(context).load(image).fit().into(holder.ivCategory);

        }


        if (category.isSelected.equals("1")){
            holder.transperentView.setVisibility(View.VISIBLE);

        } else {
            holder.transperentView.setVisibility(View.GONE);
        }

    }

    // Static inner class to initialize the views of rows
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvCategoryName,tvCatCount;
        View transperentView;
        ImageView ivCategory;

        ViewHolder(View itemView) {
            super(itemView);
            tvCategoryName = (TextView) itemView.findViewById(R.id.tvCategoryName);
            tvCatCount = (TextView) itemView.findViewById(R.id.tvCatCount);
            transperentView = (View) itemView.findViewById(R.id.transperentView);
            ivCategory = (ImageView) itemView.findViewById(R.id.ivCategory);
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            Category category = itemList.get(getAdapterPosition());

            if (customButtonListener != null) {
                if (category.isSelected.equals("0")){

                    // if(CategoryID.equals("")){
                    category.isSelected = "1";
                    CategoryID = category.id;
                    customButtonListener.onButtonClick(getAdapterPosition(), CategoryID,0);

                }
                else {
                    category.isSelected = "0";
                    String  catId = category.id;

                    CategoryID =  CategoryID.replace(catId , "");
                    customButtonListener.onButtonClick(getAdapterPosition(), CategoryID,0);
                    // selectedView.setVisibility(View.GONE);

                }
                notifyItemChanged(getAdapterPosition());


            }
        }
    }

}
