package com.uhungry.adapter.viewholder;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import com.uhungry.R;
import com.uhungry.model.Faq;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class FaqViewHolder extends GroupViewHolder {

  private TextView genreName,tv_id;
  private ImageView arrow;

  public FaqViewHolder(View itemView) {
    super(itemView);
    genreName = (TextView) itemView.findViewById(R.id.list_item_genre_name);
    tv_id = (TextView) itemView.findViewById(R.id.tv_id);
    arrow = (ImageView) itemView.findViewById(R.id.list_item_genre_arrow);
  }

  public void setGenreTitle(ExpandableGroup genre) {
    if (genre instanceof Faq) {
      genreName.setText(genre.getTitle());
      tv_id.setText(((Faq) genre).id);
    }
  }

  @Override
  public void expand() {
    animateExpand();
  }

  @Override
  public void collapse() {
    animateCollapse();
  }

  private void animateExpand() {
    RotateAnimation rotate =
        new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
    rotate.setDuration(300);
    rotate.setFillAfter(true);
    arrow.setAnimation(rotate);
  }

  private void animateCollapse() {
    RotateAnimation rotate =
        new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
    rotate.setDuration(300);
    rotate.setFillAfter(true);
    arrow.setAnimation(rotate);
  }
}
