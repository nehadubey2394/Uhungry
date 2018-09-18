package com.uhungry.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uhungry.R;
import com.uhungry.model.Discover;
import com.uhungry.utils.AppUtility;

import java.util.ArrayList;


public class DiscoverChefListAdapter extends RecyclerView.Adapter<DiscoverChefListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Discover> chefses;
    private String tag;
    // Constructor of the class
    public DiscoverChefListAdapter(Context context, ArrayList<Discover> chefses, String tag) {
        this.context = context;
        this.chefses = chefses;
        this.tag = tag;
    }

    @Override
    public int getItemCount() {
        return chefses.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (tag.equals("fragment")){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discover_chef, parent, false);

        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discover_all_chef, parent, false);

        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {

        Discover list =  chefses.get(listPosition);
        holder.tvChefName.setText(list.title);
      /*  holder.tvTime.setText(list.cheftime);
        holder.tvAddress.setText(list.address);
        holder.ratingChef.setRating(3.0f);*/
        if (list.contentType.equals("image")){
            holder.lyVideo.setVisibility(View.GONE);
            holder.tvNewsDes.setVisibility(View.GONE);
            holder.ivChef.setVisibility(View.VISIBLE);

            if (!list.content.equals("")){
                Picasso.with(context).load(list.content).fit().into(holder.ivChef);
            }
        }else if (list.contentType.equals("text")){
            holder.lyVideo.setVisibility(View.GONE);
            holder.ivChef.setVisibility(View.GONE);
            holder.tvNewsDes.setVisibility(View.VISIBLE);
            holder.tvNewsDes.setText(list.content);
        }else {
            holder.lyVideo.setVisibility(View.VISIBLE);
            holder.ivChef.setVisibility(View.GONE);
            holder.tvNewsDes.setVisibility(View.GONE);

            if (!list.contentImage.equals("")){
                Picasso.with(context).load(list.contentImage).fit().into(holder.videoView);
            }
        }


    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivChef,videoView;
        private TextView tvChefName,tvNewsDes,tvTime,tvAddress;
        private RelativeLayout lyVideo;
        private ViewHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            tvChefName = (TextView) itemView.findViewById(R.id.tvChefName);
            tvNewsDes = (TextView) itemView.findViewById(R.id.tvNewsDes);
            ivChef = (ImageView) itemView.findViewById(R.id.ivChef);
            videoView = (ImageView) itemView.findViewById(R.id.videoView);
            lyVideo = (RelativeLayout) itemView.findViewById(R.id.lyVideo);
          /*  tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            ratingChef = (RatingBar) itemView.findViewById(R.id.ratingChef);
*/
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Discover chefs = chefses.get(getAdapterPosition());
            if (chefs.contentType.equals("video")){
                Intent i = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse(chefs.content);
                i.setDataAndType(data, "video/mp4");
                i.putExtra (MediaStore.EXTRA_FINISH_ON_COMPLETION, false);

                try {
                    context.startActivity(i);
                }catch (Exception e)
                {
                    e.printStackTrace();
                    AppUtility.showToast(context,"There is no video player installed in your phone.",0);
                }
            }

        }
    }

}