package com.uhungry.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uhungry.R;
import com.uhungry.listner.CustomAdapterButtonListener;
import com.uhungry.model.Discover;
import java.util.ArrayList;


public class DiscoverFilterListAdapter extends RecyclerView.Adapter<DiscoverFilterListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Discover> arrayList;
    private Button btnApply;
    private LinearLayout btDialogCross;
    private CustomAdapterButtonListener customButtonListener = null;
    // Constructor of the class
    public DiscoverFilterListAdapter(Context context, ArrayList<Discover> arrayList, Button btnApply, LinearLayout lyDialogCross) {
        this.context = context;
        this.arrayList = arrayList;
        this.btnApply = btnApply;
        this.btDialogCross=lyDialogCross;
    }
    public void setCustomListener(CustomAdapterButtonListener customButtonListener){
        this.customButtonListener = customButtonListener;
    }
    // get the size of the list
    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discover_filter, parent, false);
        return new ViewHolder(view);
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder,  int position) {
        final Discover  discover =  arrayList.get(position);
        holder.tvItemName.setText(discover.disCName);

        if (discover.isChacked){

            holder.ivFdChecbox.setImageResource(R.drawable.checked_icon);
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

            btnApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /*ArrayList<Discover> tmp = new ArrayList<>();
                    tmp.clear();
                    for(Discover tmpObj:arrayList){
                        if(tmpObj.isChacked)
                            tmp.add(tmpObj);
                    }*/
                    if (customButtonListener != null) {
                        customButtonListener.onFilterApply(arrayList,true);
                    }
                }
            });

            btDialogCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (customButtonListener != null) {
                        customButtonListener.onButtonClick(0,"",0);
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            Discover discover =  arrayList.get(getAdapterPosition());

            if (!discover.isChacked){
                discover.isChacked = true;
            }
            else {
                discover.isChacked = false;

            }
            btnApply.setEnabled(true);
            btnApply.setAlpha(1.0f);
            notifyItemChanged(getAdapterPosition());
        }

    }
}