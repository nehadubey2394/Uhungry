package com.uhungry.model;

import java.util.ArrayList;


public class Ingredients  {

   public String itemId;
   public String itemImg;
   public String itemName,isMyIng;
   public boolean isExpand = false,isSubItemChecked = false;
   public String isChecked = "false";
   public String cId,fId,isAdded;

   public String fIngId;

   public ArrayList<SubIngModel> getArrayList() {
      return arrayList;
   }

   public void setArrayList(ArrayList<SubIngModel> arrayList) {
      this.arrayList = arrayList;
   }

   public String cName;

   ArrayList<SubIngModel> arrayList;



}
