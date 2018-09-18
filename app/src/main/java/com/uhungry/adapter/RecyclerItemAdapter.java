package com.uhungry.adapter;

import android.support.v7.widget.RecyclerView;

import com.uhungry.model.Recipies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class RecyclerItemAdapter extends RecyclerView.Adapter {

    List<Recipies> items = new ArrayList<>();
    RecyclerItemAdapter(){
        setHasStableIds(true);
    }


    public void add(Recipies object) {
        items.add(object);
        notifyDataSetChanged();
    }

    public void add(int index, Recipies object) {
        items.add(index, object);
        notifyDataSetChanged();
    }

    public void addAll(Collection collection) {
        if (collection != null) {
            items.addAll(collection);
            notifyDataSetChanged();
        }
    }

    public void addAll(Recipies... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(Recipies object) {
        items.remove(object);
        notifyDataSetChanged();
    }

}
