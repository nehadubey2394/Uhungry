package com.uhungry.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uhungry.R;
import com.uhungry.activity.FaqActivity;
import com.uhungry.activity.HelpActivity;
import com.uhungry.activity.SettingActivity;
import com.uhungry.activity.SubcriptionActivity;
import com.uhungry.activity.TermsAndConditionActivity;
import com.uhungry.model.NavigationItem;
import com.uhungry.utils.Uhungry;

import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.getColor;
import static com.uhungry.utils.Uhungry.sessionManager;


public class NavigationMenuAdapter extends RecyclerView.Adapter<NavigationMenuAdapter.ViewHolder> {
    private Activity context;
    private ArrayList<NavigationItem> navigationItems;
    private DrawerLayout drawer;
    private String sSelect = "";
    // Constructor of the class
    public NavigationMenuAdapter(Activity context, ArrayList<NavigationItem> navigationItems,DrawerLayout drawer) {
        this.context = context;
        this.drawer = drawer;
        this.navigationItems = navigationItems;
    }

    @Override
    public int getItemCount() {
        return navigationItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nav_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {

        final NavigationItem item = navigationItems.get(listPosition);

        holder.tvMenuItemName.setText(item.itemName);
        holder.ivMenuItem.setImageResource(item.itemImg);

        if (!sSelect.equals("")){
            if (item.itemName.equals(sSelect)){
                holder.ivMenuItem.setColorFilter(getColor(context, R.color.white));
                holder.tvMenuItemName.setTextColor(getColor(context, R.color.white));
                holder.rlItem.setBackgroundColor(getColor(context, R.color.colorPrimary));
                holder.line.setVisibility(View.INVISIBLE);
            }else {
                holder.line.setVisibility(View.VISIBLE);
                holder.ivMenuItem.setColorFilter(getColor(context, R.color.gray));
                holder.tvMenuItemName.setTextColor(getColor(context, R.color.colorPrimary));
                holder.rlItem.setBackgroundColor(getColor(context, R.color.half_white));
            }
        }
        /*if (!list.recImg.equals("")){
            Picasso.with(context).load(list.recImg).fit().into(holder.ivRecImg);
        }*/

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvMenuItemName;
        AppCompatImageView ivMenuItem;
        RelativeLayout rlItem;
        View line;
        private ViewHolder(View itemView)
        {
            super(itemView);

            tvMenuItemName = (TextView) itemView.findViewById(R.id.tvMenuItemName);
            ivMenuItem = (AppCompatImageView) itemView.findViewById(R.id.ivMenuItem);
            rlItem = (RelativeLayout) itemView.findViewById(R.id.rlItem);
            line = (View) itemView.findViewById(R.id.line);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            drawer.closeDrawers();
            sSelect = "";
            NavigationItem item = navigationItems.get(getAdapterPosition());

            switch(getAdapterPosition()) {
               /* case 0:
                    sSelect = item.itemName;
                    notifyDataSetChanged();
                    Intent intent = new Intent(context, SubcriptionActivity.class);
                    context.startActivity(intent);
                    break;*/
                case 0:
                    sSelect = item.itemName;
                    notifyDataSetChanged();
                    Intent i = new Intent(context, TermsAndConditionActivity.class);
                    context.startActivity(i);

                    break;

                case 1:
                    sSelect = item.itemName;
                    notifyDataSetChanged();
                    Intent intent1 = new Intent(context, SettingActivity.class);
                    context.startActivity(intent1);
                    context.finish();
                    break;

                case 2:
                    sSelect = item.itemName;
                    notifyDataSetChanged();
                    Intent intent2 = new Intent(context, HelpActivity.class);
                    intent2.putExtra("message","help");
                    context.startActivity(intent2);
                    //      context.finish();
                    break;

                case 3:
                    sSelect = item.itemName;
                    notifyDataSetChanged();
                    Intent intent3 = new Intent(context, FaqActivity.class);
                    context.startActivity(intent3);
                    break;

                case 4:
                    sSelect = item.itemName;
                    notifyDataSetChanged();
                    sessionManager.logout();
                   /* sessionManager.setIsSubcribedTemp(false);
                    sessionManager.setTempEndDate("");
                    sessionManager.setPurchasedBy("2");
                    sessionManager.setTempTransectionId("");*/
                    context.overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                    break;
                default:
            }

        }


    }

}