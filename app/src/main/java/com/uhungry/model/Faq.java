package com.uhungry.model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Faq extends ExpandableGroup<FaqAwns> {

    private int iconResId;
    public String title,id;

    public Faq(String id,String title, List<FaqAwns> items, int iconResId) {
        super(title, items);
        this.iconResId = iconResId;
        this.id = id;
    }

    public int getIconResId() {
        return iconResId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Faq)) return false;
        Faq genre = (Faq) o;
        return getIconResId() == genre.getIconResId();
    }

    @Override
    public int hashCode() {
        return getIconResId();
    }
}


