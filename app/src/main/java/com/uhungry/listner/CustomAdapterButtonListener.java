package com.uhungry.listner;

import com.uhungry.model.Discover;

import java.util.ArrayList;

public interface CustomAdapterButtonListener {

	void onButtonClick(int position, String buttonText, int selectedCount);
	void onFilterApply(ArrayList<Discover> arrayList,boolean b);

}
