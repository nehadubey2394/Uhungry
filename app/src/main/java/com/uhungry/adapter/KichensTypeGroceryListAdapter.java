package com.uhungry.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uhungry.R;
import com.uhungry.model.Ingredients;
import com.uhungry.model.SubIngModel;

import java.util.ArrayList;


public class KichensTypeGroceryListAdapter extends BaseExpandableListAdapter {
    private Activity activity;
    private ArrayList<Ingredients> parentArrayList;
    private String uri = "";

    public KichensTypeGroceryListAdapter(Activity activity, ArrayList<Ingredients> parents) {
        this.parentArrayList = parents;
        this.activity = activity;
    }


    @Override
    public int getGroupCount() {
        return this.parentArrayList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<SubIngModel> childtems = parentArrayList.get(groupPosition).getArrayList();
        return childtems.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.parentArrayList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<SubIngModel> childtems = parentArrayList.get(groupPosition).getArrayList();
        return childtems.get(childPosition);

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_ingredents_list, null);
            holder = new ViewHolder();
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
        holder.ivGrocey = (ImageView) convertView.findViewById(R.id.ivGrocey);
        holder.ivChecbox = (ImageView) convertView.findViewById(R.id.ivChecbox);
        holder.ivDropDown = (ImageView) convertView.findViewById(R.id.ivDropDown);


        Ingredients ingredients = parentArrayList.get(groupPosition);
        ArrayList<SubIngModel> arrayList = ingredients.getArrayList();

        if (arrayList.size()==0){
            holder.ivChecbox.setVisibility(View.VISIBLE);
            holder.ivDropDown.setVisibility(View.GONE);

            if (ingredients.isChecked.equals("0")){
                holder.ivChecbox.setImageResource(R.drawable.unchecked_icon);
            }else {
                holder.ivChecbox.setImageResource(R.drawable.checked_icon);
            }
        }else {
            holder.ivChecbox.setVisibility(View.GONE);
            holder.ivDropDown.setVisibility(View.VISIBLE);
        }

        if (ingredients.isExpand){
            holder.ivDropDown.setRotation(180);
        }else {
            holder.ivDropDown.setRotation(360);
        }

        uri = ingredients.itemImg;

        if (!ingredients.itemImg.equals("")){
            Picasso.with(activity).load(ingredients.itemImg).fit().into(holder.ivGrocey);
        }
        holder.tvItemName.setText(ingredients.itemName);
        //  Toast.makeText(activity, ingredients.itemName, Toast.LENGTH_SHORT).show();

        return convertView;

    }
    private class ViewHolder {
        ImageView ivGrocey,ivChecbox,ivDropDown;
        TextView tvItemName;
    }


    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ViewHolder2 holder;

        final SubIngModel subIngModel = (SubIngModel) getChild(groupPosition, childPosition);


        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_sub_ingredents_list, null);

            holder = new ViewHolder2();
        /*find layout components id*/

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder2) convertView.getTag();
        }

        holder.tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
        holder.ivGrocey = (ImageView) convertView.findViewById(R.id.ivGrocey);
        holder.ivChecbox = (ImageView) convertView.findViewById(R.id.ivChecbox);
        holder.tvItemName.setText(subIngModel.subIngName);

        Picasso.with(activity).load(uri).fit().into(holder.ivGrocey);

        if (subIngModel.isSubIngSelected.equals("0")){
            holder.ivChecbox.setImageResource(R.drawable.unchecked_icon);

        }else {
            holder.ivChecbox.setImageResource(R.drawable.checked_icon);
        }

        return convertView;
    }

    private class ViewHolder2 {
        ImageView ivChecbox,ivGrocey;
        TextView tvItemName;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
