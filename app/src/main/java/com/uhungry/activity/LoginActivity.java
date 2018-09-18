package com.uhungry.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.uhungry.R;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import vollyemultipart.VolleyMultipartRequest;
import vollyemultipart.VolleySingleton;

import static com.uhungry.utils.Uhungry.sessionManager;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText etLoginEmail,etLoginPassword;
    private RelativeLayout lyKeepSignIn;
    private ImageView chbRemeberMe;
    private boolean rememberMe = false;
    private CallbackManager callbackManager;
    private String mDeviceToken = "", sEmail = "", sPassword = "",sUserImageUrl,fullName,sSocialId="",sSocialType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindViews();
    }

    private void bindViews(){

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        etLoginEmail = (EditText) findViewById(R.id.etLoginEmail);
        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
        chbRemeberMe = (ImageView) findViewById(R.id.chbRemeberMe);
        lyKeepSignIn = (RelativeLayout)findViewById(R.id.lyKeepSignIn);
        TextView tvFb = (TextView) findViewById(R.id.tvFb);
        TextView tvForgetPass = (TextView) findViewById(R.id.tvForgetPass);
        TextView tvSignIn = (TextView) findViewById(R.id.tvSignIn);
        LinearLayout lyFbLogin = (LinearLayout) findViewById(R.id.lyFbLogin);
        LinearLayout lyDontHaveAccount = (LinearLayout) findViewById(R.id.lyDontHaveAccount);

        // tvFb.setEnabled(false);
        // tvFb.setClickable(false);

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try {
                            sSocialId = object.getString("id");
                            if (object.has("email")) {
                                sEmail = object.getString("email");
                            }else {
                                sEmail = sSocialId+"@facebook.com";
                            }
                            String sFirstname = object.getString("first_name");
                            String sLastname = object.getString("last_name");
                            String gender = object.getString("gender");
                            fullName = sFirstname+" "+sLastname;

                            mDeviceToken = FirebaseInstanceId.getInstance().getToken();

                            //   sUserImageUrl = object.getString("picture");
                            sUserImageUrl = "https://graph.facebook.com/" + sSocialId + "/picture?type=large";

                            if (AppUtility.isNetworkAvailable(LoginActivity.this)) {
                                GetRegistrationTask();
                            } else {
                                AppUtility.showAlertDialog_SingleButton(LoginActivity.this,getResources().getString(R.string.network_error),"Alert!","Ok");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name,gender, email,picture");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                AppUtility.showToast(LoginActivity.this, "Facebook login cancelled", Toast.LENGTH_SHORT);
            }

            @Override
            public void onError(FacebookException exception) {
                AppUtility.showToast(LoginActivity.this, "Error in Facebook login", Toast.LENGTH_SHORT);
            }

        });

        if(sessionManager.isUserRememberMe()){
            String email = sessionManager.getEmail();
            String pass = sessionManager.getPassword();
            if (!email.isEmpty()){
                etLoginEmail.setText(email);
                etLoginPassword.setText(pass);
                rememberMe = true;
                chbRemeberMe.setImageResource(R.drawable.checked_icon);
            }

        }else {
            rememberMe = false;
            chbRemeberMe.setImageResource(R.drawable.unchecked_icon);

        }
        lyFbLogin.setOnClickListener(this);
        tvSignIn.setOnClickListener(this);
        lyDontHaveAccount.setOnClickListener(this);
        chbRemeberMe.setOnClickListener(this);
        tvFb.setOnClickListener(this);
        tvForgetPass.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lyFbLogin:
                sSocialType="facebook";
                if (AppUtility.isNetworkAvailable(LoginActivity.this)) {
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));

                } else {
                    AppUtility.showAlertDialog_SingleButton(LoginActivity.this,getResources().getString(R.string.network_error),"Alert!","Ok");

                }
                break;

            case R.id.tvFb:
                rememberMe = false;
                sSocialType="facebook";
                if (AppUtility.isNetworkAvailable(LoginActivity.this)) {
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));

                } else {
                    AppUtility.showAlertDialog_SingleButton(LoginActivity.this,getResources().getString(R.string.network_error),"Alert!","Ok");

                }
                break;
            case R.id.lyDontHaveAccount:
                //startActivity(new Intent(this, SignUpActivity.class));
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                intent.putExtra("rememberMe",rememberMe);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                break;

            case R.id.tvForgetPass:
                Intent intent1 = new Intent(LoginActivity.this, ForgetPassActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                break;

            case R.id.chbRemeberMe:
                if (rememberMe) {
                    rememberMe = false;
                    chbRemeberMe.setImageResource(R.drawable.unchecked_icon);

                } else {
                    rememberMe = true;
                    chbRemeberMe.setImageResource(R.drawable.checked_icon);
                }
                break;

            case R.id.tvSignIn:
                boolean cancel = false;
                sEmail = etLoginEmail.getText().toString().trim();
                sPassword = etLoginPassword.getText().toString().trim();
                mDeviceToken = FirebaseInstanceId.getInstance().getToken();

                if (sEmail.isEmpty()) {
                    cancel = true;
                    etLoginEmail.requestFocus();
                    etLoginEmail.setError(getString(R.string.please_enter_your_email));
                }
                else if (sEmail.contains(" ")) {
                    cancel = true;
                    etLoginEmail.requestFocus();
                    etLoginEmail.setError(getString(R.string.please_enter_your_email));
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                    cancel = true;
                    etLoginEmail.requestFocus();
                    etLoginEmail.setError(getString(R.string.please_enter_a_valid_email));
                }
                else if (sPassword.isEmpty()) {
                    cancel = true;
                    etLoginPassword.requestFocus();
                    etLoginPassword.setError(getString(R.string.please_enter_your_password));
                }
                else if (mDeviceToken==null || mDeviceToken.equals("")) {
                    cancel = true;
                    AppUtility.showAlertDialog_SingleButton(LoginActivity.this,"Something went wrong. google play services not available","Alert!","Ok");
                }
                if (!cancel) {
                    // call login api
                    if (AppUtility.isNetworkAvailable(LoginActivity.this)) {
                        GetUserLoginTask();
                    } else {
                        AppUtility.showAlertDialog_SingleButton(LoginActivity.this,getResources().getString(R.string.network_error),"Alert!","Ok");

                    }

                }

                break;
        }
    }

    public void GetUserLoginTask() {
        final ProgressDialog customDialog = new ProgressDialog(LoginActivity.this);
        customDialog.show();
        mDeviceToken = FirebaseInstanceId.getInstance().getToken();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL+"userLogin",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        JSONObject jsonObject;
                        try {
                            System.out.println(" login response" + response);
                            jsonObject = new JSONObject(response);
                            String status = jsonObject.getString(Constant.STATUS);
                            String message = jsonObject.getString("message");

                            if(status.equals("success")){

                                JSONObject usedetailobject = jsonObject.getJSONObject("userDetails");
                                String isSubscribed,sAuthToken,userId,sEmail,sPhoneNo,fullName,sProfileImage,transactionId,endDate,purchasedBy,sLoginSatus;

                                userId = usedetailobject.getString("id");
                                sEmail = usedetailobject.getString("email");
                                fullName = usedetailobject.getString("fullName");
                                sSocialId = usedetailobject.getString("socialId");
                                sPhoneNo = usedetailobject.getString("contactNo");
                                sAuthToken = usedetailobject.getString(Constant.AUTHTOKEN);
                                sProfileImage = usedetailobject.getString("profilePic");

                                isSubscribed = usedetailobject.getString("isSubscribed");
                                purchasedBy = usedetailobject.getString("purchasedBy");
                                endDate = usedetailobject.getString("endDate");
                                transactionId = usedetailobject.getString("transactionId");

                               /* if (isSubscribed.equals("1"))
                                    sessionManager.setIsSubcribed(true);
                                else
                                    sessionManager.setIsSubcribed(false);*/

                                if (sessionManager.isTempSubcribed() && !sessionManager.getTempTransectionId().equals("")
                                        && isSubscribed.equals("0")){
                                    updateSubscriptionToServer();
                                }

                                if (sessionManager.isTempSubcribed() || isSubscribed.equals("1")){
                                    sessionManager.setIsSubcribed(true);
                                }else {
                                    sessionManager.setIsSubcribed(false);
                                }


                                sessionManager.setPurchasedBy(purchasedBy);
                                sessionManager.setEndDate(endDate);
                                sessionManager.setTransectionId(transactionId);
                                Uhungry.sessionManager.setFirstRecipe("0");
                                sLoginSatus = "1";
                                sessionManager.createSession(userId,sEmail,fullName,sSocialId,sProfileImage,sPhoneNo,sPassword,sLoginSatus,sAuthToken);

                                if (rememberMe){
                                    sessionManager.setIsRememberMe(true);
                                    sessionManager.setPassword(sPassword);
                                    sessionManager.setEmail(sEmail);
                                }else {
                                    sessionManager.setPassword("");
                                    sessionManager.setEmail("");
                                    sessionManager.setPassword("");
                                    sessionManager.setIsRememberMe(false);
                                }

                                Intent intent = new Intent(LoginActivity.this, WellcomeActivity.class);
                                intent.putExtra("message",message);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                            }
                            else {
                                AppUtility.showToast(LoginActivity.this, message, Toast.LENGTH_SHORT);
                            }
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        customDialog.cancel();
                        Toast.makeText(LoginActivity.this, "Ooops! Something went wrong.", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();

                header.put("email", sEmail);
                header.put("password", sPassword);
                header.put("deviceType", "2");
                header.put("deviceToken", mDeviceToken);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,1));
        requestQueue.add(stringRequest);
    }

    public void GetRegistrationTask() {

        final ProgressDialog customDialog = new ProgressDialog(LoginActivity.this);
        customDialog.show();

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,  Constant.BASE_URL+"userRegistration", new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                customDialog.cancel();
                System.out.println("reg resultResponse==="+resultResponse);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(resultResponse);
                    String status = jsonObject.getString(Constant.STATUS);
                    String message = jsonObject.getString("message");

                    if(status.equals("success")){

                        JSONObject usedetailobject = jsonObject.getJSONObject("userDetail");

                        String isSubscribed,sAuthToken,userId,sEmail,fullName,sPhoneNo="",transactionId,sProfileImage,sLoginSatus,endDate,purchasedBy;

                        userId = usedetailobject.getString("id");
                        sEmail = usedetailobject.getString("email");
                        fullName = usedetailobject.getString("fullName");
                        sSocialId = usedetailobject.getString("socialId");
                        sPhoneNo = usedetailobject.getString("contactNo");
                        sAuthToken = usedetailobject.getString(Constant.AUTHTOKEN);
                        sProfileImage = usedetailobject.getString("profilePic");
                        sLoginSatus = "1";
                        isSubscribed = usedetailobject.getString("isSubscribed");
                        purchasedBy = usedetailobject.getString("purchasedBy");
                        endDate = usedetailobject.getString("endDate");
                        transactionId = usedetailobject.getString("transactionId");

                        /*if (isSubscribed.equals("1"))
                            sessionManager.setIsSubcribed(true);
                        else
                            sessionManager.setIsSubcribed(false);*/
                        if (sessionManager.isTempSubcribed() && !sessionManager.getTempTransectionId().equals("")
                                && isSubscribed.equals("0")){
                            updateSubscriptionToServer();
                        }

                        if (sessionManager.isTempSubcribed() || isSubscribed.equals("1")){
                            sessionManager.setIsSubcribed(true);
                        }else {
                            sessionManager.setIsSubcribed(false);
                        }

                        sessionManager.setPurchasedBy(purchasedBy);
                        sessionManager.setEndDate(endDate);
                        sessionManager.setTransectionId(transactionId);

                        sessionManager.createSession(userId,sEmail,fullName,sSocialId,sProfileImage,sPhoneNo,"",sLoginSatus,sAuthToken);

                        sessionManager.setPassword("");
                        sessionManager.setEmail("");
                        sessionManager.setPassword("");
                        sessionManager.setIsRememberMe(false);


                        if (message.equals("User registration successfully done")){
                            Uhungry.sessionManager.setFirstRecipe("NA");
                            sessionManager.setIsSubcribed(false);
                            Intent intent2 = new Intent(LoginActivity.this, HelpActivity.class);
                            intent2.putExtra("message",message);
                            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent2);
                            overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                            finish();
                        }else {
                            Uhungry.sessionManager.setFirstRecipe("0");
                            Intent intent = new Intent(LoginActivity.this, WellcomeActivity.class);
                            intent.putExtra("message",message);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);

                        }

                    }
                    else {
                        AppUtility.showToast(LoginActivity.this, message, Toast.LENGTH_SHORT);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                customDialog.cancel();
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", ""+status);
                        Log.e("Error Message", message);
                        errorMessage = message;

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+"Ooops! Something went wrong,";
                        }else if (networkResponse.statusCode == 300) {
                            errorMessage = message+"Ooops! Something went wrong,";
                        }
                        AppUtility.showToast(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("fullName", fullName);
                params.put("email", sEmail);
                params.put("deviceType", "2");
                params.put("password", sPassword);
                params.put("confirmPassword", "");
                params.put("socialId", sSocialId);
                params.put("socialType", sSocialType);
                params.put("deviceToken", mDeviceToken);
                params.put("profilePic", sUserImageUrl);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                return params;
            }
        };

        VolleySingleton.getInstance(this.getBaseContext()).addToRequestQueue(multipartRequest);
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,1));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void updateSubscriptionToServer() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL_USER+"userSubscription",

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("success")) {
                                Uhungry.sessionManager.setIsSubcribed(true);
                                Uhungry.sessionManager.setEndDate(sessionManager.getTempEndDate());
                                Uhungry.sessionManager.setPurchasedBy("2");
                                Uhungry.sessionManager.setTransectionId(sessionManager.getTempTransectionId());

                                Uhungry.sessionManager.setIsSubcribedTemp(false);
                                Uhungry.sessionManager.setTempEndDate("");
                                Uhungry.sessionManager.setPurchasedBy("2");
                                Uhungry.sessionManager.setTempTransectionId("");

                                //  AppUtility.showAlertDialog_SingleButton(SubcriptionActivity.this,message,"Alert","Ok");
                            }


                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                // header.put("amount", "$1.99");
                header.put("endDate", sessionManager.getTempEndDate());
                //   header.put("transactionId", "");
                header.put("purchasedBy", "2");
                header.put("transactionId", sessionManager.getTempTransectionId());

                return header;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String AuthToken = Uhungry.sessionManager.getAuthToken();
                if (!AuthToken.equals("")){

                    header.put(Constant.AUTHTOKEN, AuthToken);
                }
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        //requestQueue.add(stringRequest);
        Uhungry.getInstance().addToRequestQueue(stringRequest);
    }

}
