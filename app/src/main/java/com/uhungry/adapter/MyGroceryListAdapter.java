package com.uhungry.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uhungry.R;
import com.uhungry.listner.AddRemoveGroceryButtonListener;
import com.uhungry.model.MyGrocery;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MyGroceryListAdapter extends RecyclerView.Adapter<MyGroceryListAdapter.ViewHolder> {
    private Activity context;
    private ArrayList<MyGrocery> arrayList;
    private Button done;
    private String CategoryID = "",selectedId="",newId="";
    private HashMap<String,MyGrocery> hashMap;
    private AddRemoveGroceryButtonListener buttonListener = null;
    private int selectedCount = 0,count = 0;

    // Constructor of the class
    public MyGroceryListAdapter(Activity context, ArrayList<MyGrocery> arrayList, Button doneButton) {
        this.context = context;
        this.arrayList = arrayList;
        done=doneButton;
        hashMap = new HashMap<>();
    }

    public void setListener(AddRemoveGroceryButtonListener customButtonListener){
        this.buttonListener = customButtonListener;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredents_list, parent, false);
        return new ViewHolder(view);
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder,  int position) {
        final MyGrocery grocery =  arrayList.get(position);
        holder.tvItemName.setText(grocery.ingName);
        // holder.tvItemQuntity.setText(ingredients.itemQuantity);
        if (!grocery.ingImage.equals("")){
            Picasso.with(context).load(grocery.ingImage).fit().into(holder.ivGrocey);
        }

        if (grocery.isChecked.equals("1")){
            hashMap.put(grocery.ingId,grocery);
            hashMap.put(grocery.cId,grocery);
            count++;
            selectedCount = count;
            holder.ivChecbox.setImageResource(R.drawable.checked_icon);
        } else {
            holder.ivChecbox.setImageResource(R.drawable.unchecked_icon);
        }

    }

    // Static inner class to initialize the views of rows
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivGrocey,ivChecbox;
        private TextView tvItemName,tvItemQuntity;

        private ViewHolder(View itemView) {
            super(itemView);
            ivGrocey = (ImageView) itemView.findViewById(R.id.ivGrocey);
            ivChecbox = (ImageView) itemView.findViewById(R.id.ivChecbox);
            tvItemName = (TextView) itemView.findViewById(R.id.tvItemName);
            tvItemQuntity = (TextView) itemView.findViewById(R.id.tvItemQuntity);
            ivChecbox.setOnClickListener(this);


            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Iterator it = hashMap.entrySet().iterator();
                    List<Map<String,Integer>> list = new ArrayList<Map<String, Integer>>();

                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        int key = Integer.parseInt(pair.getKey().toString());

                        MyGrocery grocery = (MyGrocery) pair.getValue();

                        int cId = Integer.parseInt(grocery.cId);
                        int fId = Integer.parseInt("0");
                        int ingId = Integer.parseInt(grocery.ingId);

                        Map<String,Integer> keyvaluePair = new HashMap<String, Integer>();
                        keyvaluePair.put("ingId",ingId);
                        keyvaluePair.put("cId", cId);
                        keyvaluePair.put("fId", fId);
                        list.add(keyvaluePair);
                        // it.remove();
                    }
                    JSONArray jsonArray = new JSONArray(list);
                  //  if (jsonArray!=null && jsonArray.length() > 0)
                 //   {
                        buttonListener.onDoneButtonClick(getAdapterPosition(),jsonArray,"MyGrocery");
                  //  }else {
                   //     AppUtility.showToast(context,"select atleast one ingredient.",0);
                   // }

                }
            });
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case  R.id.ivChecbox :

                    MyGrocery ingredients = arrayList.get(getAdapterPosition());
                    if (ingredients.isChecked.equals("0")){
                        selectedCount ++;
                        ingredients.isChecked = "1";
                        hashMap.put(ingredients.ingId, ingredients);
                        hashMap.put(ingredients.cId, ingredients);
                        notifyItemChanged(getAdapterPosition());
                        updateButtonUi();
                    }
                    else if (ingredients.isChecked.equals("1")){
                        ingredients.isChecked = "0";
                        selectedCount--;
                        updateButtonUi();
                        notifyItemChanged(getAdapterPosition());
                        hashMap.remove(ingredients.ingId);
                        hashMap.remove(ingredients.cId);

                    }



                    break;
            }
        }

        private void updateButtonUi(){
            if(hashMap.size()>0 ){
                done.setEnabled(true);
                done.setVisibility(View.VISIBLE);
                done.setAlpha(1.0f);
            }else {
                done.setEnabled(false);
                done.setAlpha(0.5f);
                done.setVisibility(View.GONE);
            }
        }
    }

}
