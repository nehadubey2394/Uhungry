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
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uhungry.R;
import com.uhungry.model.Discover;
import com.uhungry.utils.AppUtility;

import java.util.ArrayList;


public class DiscoverVideoListAdapter extends RecyclerView.Adapter<DiscoverVideoListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Discover> chefses;
    private String tag;

    // Constructor of the class
    public DiscoverVideoListAdapter(Context context, ArrayList<Discover> chefses, String tag) {
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discover_video, parent, false);

        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discover_all_video, parent, false);

        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {

        Discover list =  chefses.get(listPosition);
        holder.tvName.setText(list.title);

        if (!list.content.isEmpty() && !list.contentImage.isEmpty() && list.contentType.equals("video")){
            Picasso.with(context).load(list.contentImage).fit().into(holder.videoView);
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivPlay,videoView;
        private TextView tvName;
        private ViewHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            ivPlay = (ImageView) itemView.findViewById(R.id.ivPlay);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            videoView = (ImageView) itemView.findViewById(R.id.videoView);

            ivPlay.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Discover chefs = chefses.get(getAdapterPosition());
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