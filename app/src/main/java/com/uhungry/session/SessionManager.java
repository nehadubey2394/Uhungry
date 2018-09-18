package com.uhungry.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.uhungry.activity.LoginActivity;
import com.uhungry.helper.Constant;
import com.uhungry.utils.Uhungry;

public class SessionManager {
    public static SessionManager instance = null;
    private Context _context;
    private SharedPreferences mypref ;
    private SharedPreferences.Editor editor;
    private SharedPreferences mypref2 ;
    private SharedPreferences.Editor editor2;
    private static final String PREF_NAME = "Uhungry";
    private static final String PREF_NAME2 = "UhungryCookies";
    private static final String PREF_NAME3 = "UhungryTempCookies";
    private static final String IS_LOGGEDIN = "isLoggedIn";

    public SessionManager(Context context){
        this._context = context;
        mypref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = mypref.edit();
        editor.apply();
        mypref2 = _context.getSharedPreferences(PREF_NAME2, Context.MODE_PRIVATE);
        editor2 = mypref2.edit();
        editor2.apply();

    }

    public static SessionManager getInstance() {
        if ((instance instanceof SessionManager)) {
            return instance;
        }
        instance = new SessionManager(Uhungry.getInstance().getApplicationContext());
        return instance;
    }


    public void createSession(String userId,String sEmail,String userName,String sSocialId,String sUserProfilePic,
                              String contactNo,String password,String sLoginSatus, String sAuthToken) {

        editor.putString("id", userId);
        editor.putString("email", sEmail);
        editor.putString("fullName", userName);
        editor.putString("profileImage", sUserProfilePic);
        editor.putString("contactNo", contactNo);
        editor.putString("pass", password);
        editor.putString("socialId", sSocialId);
        editor.putString(Constant.STATUS, sLoginSatus);
        editor.putString(Constant.AUTHTOKEN,sAuthToken);
        editor.putBoolean(IS_LOGGEDIN, true);
        editor.commit();
    }

    public void setPassword(String password){
        editor2.putString("password", password);
        this.editor2.commit();
    }

    public void setEmail(String sEmail){
        editor2.putString("email", sEmail);
        this.editor2.commit();
    }

    public void setEndDate(String date){
        editor.putString("enddate", date);
        this.editor.commit();
    }

    public void setPurchasedBy(String purchasedBy){
        editor.putString("purchasedBy", purchasedBy);
        this.editor.commit();
    }

    public void setFirstRecipe(String recipeId){
        editor.putString("recipeId", recipeId);
        this.editor.commit();
    }

    public void setTransectionId(String transactionId){
        editor.putString("transactionId", transactionId);
        this.editor.commit();
    }

    public String getPassword(){
        return mypref2.getString("password", "");
    }

    public String getEmail(){
        return mypref2.getString("email", "");
    }


    public String getAuthToken(){
        return mypref.getString(Constant.AUTHTOKEN, "");
    }

    public boolean isSubcribed(){
        return mypref.getBoolean("subcription",false);
    }

    public void setIsSubcribed(boolean value){
        editor.putBoolean("subcription", true);
        this.editor.commit();
    }

    public void setIsSubcribedTemp(boolean value){
        editor2.putBoolean("tempSubs", true);
        this.editor2.commit();
    }

    public void setTempTransectionId(String transactionId){
        editor2.putString("tempTransactionId", transactionId);
        this.editor2.commit();
    }

    public void setTempEndDate(String date){
        editor2.putString("tempEndDate", date);
        this.editor2.commit();
    }

    public String getTempTransectionId(){
        return mypref2.getString("tempTransactionId", "");
    }
    public String getTempEndDate(){
        return mypref2.getString("tempEndDate", "");
    }

    public boolean isTempSubcribed(){
        return mypref2.getBoolean("tempSubs",false);
    }


    public String getPurchasedBy(){
        return mypref.getString("purchasedBy", "");
    }

    public String getFirstRecipe(){
        return mypref.getString("recipeId", "");
    }

    public String getTransectionId(){
        return mypref.getString("transactionId", "");
    }

    public String getEndDate(){
        return mypref.getString("enddate", "");
    }

    public String getUserEmail(){
        return mypref.getString("email", "");
    }

    public String getUserPassword(){
        return mypref.getString("pass", "");
    }

    public String getUserId(){
        return mypref.getString("id", "");
    }

    public String getUserName(){
        return mypref.getString("userName", "");
    }

    public String getFullName(){
        return mypref.getString("fullName", "");
    }

    public String getContactNo(){
        return mypref.getString("contactNo", "");
    }

    public void setIsRememberMe(boolean value){
        editor2.putBoolean("isRememberMe", value);
        this.editor2.commit();
    }

    public String getSocialId(){
        return mypref.getString("socialId", "");
    }
    public String getProfileImage(){

        return mypref.getString("profileImage", "");
    }

    public boolean isLoggedIn(){
        return mypref.getBoolean(IS_LOGGEDIN,false);
    }

    public boolean isUserRememberMe(){
        return mypref2.getBoolean("isRememberMe",false);
    }


    public void logout(){
        editor.clear();
        editor.commit();
        //  FacebookSdk.sdkInitialize(this._context);
        //   LoginManager.getInstance().logOut();
        Intent showLogin = new Intent(_context,LoginActivity.class);
        showLogin.putExtra("from","session");
        showLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(showLogin);

    }
}
