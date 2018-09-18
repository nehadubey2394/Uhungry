package com.uhungry.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.uhungry.R;

public class FaqAnswerViewHolder extends ChildViewHolder {

  private TextView childTextView;

  public FaqAnswerViewHolder(View itemView) {
    super(itemView);
    childTextView = (TextView) itemView.findViewById(R.id.tv_desc);
  }

  public void setAns(String name) {
    childTextView.setText(name);
  }
}
