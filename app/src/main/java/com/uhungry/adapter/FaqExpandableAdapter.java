package com.uhungry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.uhungry.R;
import com.uhungry.adapter.viewholder.FaqAnswerViewHolder;
import com.uhungry.adapter.viewholder.FaqViewHolder;
import com.uhungry.model.Faq;
import com.uhungry.model.FaqAwns;

import java.util.List;

public class FaqExpandableAdapter extends ExpandableRecyclerViewAdapter<FaqViewHolder, FaqAnswerViewHolder> {

  public FaqExpandableAdapter(List<? extends ExpandableGroup> groups) {
    super(groups);
  }

  @Override
  public FaqViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_faq, parent, false);
    return new FaqViewHolder(view);
  }

  @Override
  public FaqAnswerViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_row_item, parent, false);
    return new FaqAnswerViewHolder(view);
  }

  @Override
  public void onBindChildViewHolder(FaqAnswerViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {

    final FaqAwns artist = ((Faq) group).getItems().get(childIndex);
    holder.setAns(artist.getName());
  }

  @Override
  public void onBindGroupViewHolder(FaqViewHolder holder, int flatPosition, ExpandableGroup group) {
    holder.setGenreTitle(group);
  }
}
