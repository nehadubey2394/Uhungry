package com.uhungry.listner;

import org.json.JSONArray;

public interface AddRemoveGroceryButtonListener {

	public abstract void onDoneButtonClick(int position, JSONArray jsonArray,String s);

}
