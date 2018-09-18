package com.uhungry.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.image.cropper.CropImage;
import com.uhungry.R;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.ImageUtil;
import com.uhungry.utils.Uhungry;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import vollyemultipart.AppHelper;
import vollyemultipart.VolleyMultipartRequest;
import vollyemultipart.VolleySingleton;

import static com.uhungry.utils.Uhungry.sessionManager;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etRegName,etRegEmail,etRegPhoneNo,etRegPassword,etRegConfirmPass;
    private TextView tvSignUp;
    private Bitmap profileImageBitmap;
    private ImageView ivRegProfilePic;
    private boolean rememberMe = false;
    private String mDeviceToken = "", sEmail = "",sPhoneNo="", sPassword = "",fullName,sConfirmPass,sSocialId="",sSocialType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        bindViews();
    }

    private void bindViews(){

        Intent i = getIntent();
        rememberMe = i.getBooleanExtra("rememberMe",false);
        etRegName = (EditText) findViewById(R.id.etRegName);
        etRegPhoneNo = (EditText) findViewById(R.id.etRegPhoneNo);
        etRegEmail = (EditText) findViewById(R.id.etRegEmail);
        etRegPassword = (EditText) findViewById(R.id.etRegPassword);
        etRegConfirmPass = (EditText) findViewById(R.id.etRegConfirmPass);
        TextView tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        ivRegProfilePic = (ImageView) findViewById(R.id.ivRegProfilePic);
        RelativeLayout lyProfilePic = (RelativeLayout) findViewById(R.id.lyProfilePic);
        LinearLayout lyAlreadyHaveAccount = (LinearLayout) findViewById(R.id.lyAlreadyHaveAccount);

        lyAlreadyHaveAccount.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        //  ivRegProfilePic.setOnClickListener(this);
        lyProfilePic.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lyProfilePic:
                selectImage();
                break;

            case R.id.lyAlreadyHaveAccount:
                finish();
                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                break;

            case R.id.tvSignUp:
                boolean cancel = false;
                mDeviceToken = FirebaseInstanceId.getInstance().getToken();
                fullName = etRegName.getText().toString().trim();
                sEmail = etRegEmail.getText().toString().trim();
                sPhoneNo = etRegPhoneNo.getText().toString().trim();
                sPassword = etRegPassword.getText().toString().trim();
                sConfirmPass = etRegConfirmPass.getText().toString().trim();

                if (fullName.isEmpty()) {
                    cancel = true;
                    etRegName.requestFocus();
                    etRegName.setError(getString(R.string.please_enter_your_fullname));

                }else if (fullName.length()<=2){
                    cancel = true;
                    etRegName.requestFocus();
                    etRegName.setError(getString(R.string.please_enter_valid_fullname));
                }
                else if (sEmail.isEmpty()) {
                    cancel = true;
                    etRegEmail.requestFocus();
                    etRegEmail.setError(getString(R.string.please_enter_your_email));
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                    cancel = true;
                    etRegEmail.requestFocus();
                    etRegEmail.setError(getString(R.string.please_enter_a_valid_email));

                }else if (!sPhoneNo.isEmpty()) {
                    if (sPhoneNo.length()<=9){
                        cancel = true;
                        etRegPhoneNo.requestFocus();
                        etRegPhoneNo.setError(getString(R.string.please_enter_a_valid_phone));
                    }
                }
                else if (sPassword.isEmpty()) {
                    cancel = true;
                    etRegPassword.requestFocus();
                    etRegPassword.setError(getString(R.string.please_enter_your_password));
                }
                else if (!sPassword.isEmpty()) {
                    if (sPassword.length()<=5){
                        cancel = true;
                        etRegPassword.requestFocus();
                        etRegPassword.setError(getString(R.string.please_enter_six_digit_pass));
                    }
                }
                else if (sConfirmPass.isEmpty()) {
                    cancel = true;
                    etRegConfirmPass.setError(getString(R.string.please_enter_confirm_password));

                }
                else if (!sConfirmPass.equals(sPassword)){
                    cancel = true;
                    etRegConfirmPass.setError(getString(R.string.please_enter_correct_confirm_pass));
                }
               /* else if (profileImageBitmap==null){
                    cancel = true;
                    AppUtility.showAlertDialog_SingleButton(this,"Please select your profile image","","ok");
                }*/
                if (!cancel) {
                    // call reg api
                    mDeviceToken = FirebaseInstanceId.getInstance().getToken();

                    if (AppUtility.isNetworkAvailable(SignUpActivity.this)) {

                        GetRegistrationTask();
                    } else {
                        AppUtility.showAlertDialog_SingleButton(SignUpActivity.this, getString(R.string.network_error), "ALert","Ok");

                    }

                    break;
                }
        }
    }

    private void selectImage() {

        final CharSequence[] items = {getString(R.string.text_take_photo), getString(R.string.text_chose_gellery), getString(R.string.text_cancel)};
        AlertDialog.Builder alert = new AlertDialog.Builder(SignUpActivity.this);
        alert.setTitle(getString(R.string.text_add_photo));
        alert.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.text_take_photo))) {

                    if(Build.VERSION.SDK_INT >= 23) {
                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(
                                    new String[]{Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    Constant.MY_PERMISSIONS_REQUEST_CAMERA);
                        }
                        else {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, Constant.REQUEST_CAMERA);
                        }
                    }else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, Constant.REQUEST_CAMERA);
                    }


                } else if (items[item].equals(getString(R.string.text_chose_gellery))) {

                    if(Build.VERSION.SDK_INT >= 23){

                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, Constant.SELECT_FILE);
                        }
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, Constant.SELECT_FILE);
                    }


                } else if (items[item].equals(getString(R.string.text_cancel))) {
                    dialog.dismiss();
                }
            }
        });
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constant.SELECT_FILE);
                } else {
                    Toast.makeText(SignUpActivity.this, "YOU DENIED PERMISSION CANNOT SELECT IMAGE", Toast.LENGTH_LONG).show();
                }
            }break;

            case  Constant.MY_PERMISSIONS_REQUEST_CAMERA:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                } else {
                    Toast.makeText(SignUpActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }
            } break;
        }
    }

    public void GetRegistrationTask() {

        final ProgressDialog customDialog = new ProgressDialog(SignUpActivity.this);
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

                        String sAuthToken,userId,sEmail,fullName,sProfileImage,sLoginSatus;

                        userId = usedetailobject.getString("id");
                        sEmail = usedetailobject.getString("email");
                        fullName = usedetailobject.getString("fullName");
                        sSocialId = usedetailobject.getString("socialId");
                        sPhoneNo = usedetailobject.getString("contactNo");
                        sAuthToken = usedetailobject.getString(Constant.AUTHTOKEN);
                        sProfileImage = usedetailobject.getString("profilePic");
                        sLoginSatus = "1";

                        String isSubscribed = usedetailobject.getString("isSubscribed");
                        String  purchasedBy = usedetailobject.getString("purchasedBy");
                        String endDate = usedetailobject.getString("endDate");
                        String transactionId = usedetailobject.getString("transactionId");

                        if (isSubscribed.equals("1"))
                            sessionManager.setIsSubcribed(true);
                        else
                            sessionManager.setIsSubcribed(false);

                        sessionManager.setPurchasedBy(purchasedBy);
                        sessionManager.setFirstRecipe("NA");
                        sessionManager.setEndDate(endDate);
                        sessionManager.setTransectionId(transactionId);

                        sessionManager.setIsSubcribedTemp(false);
                        sessionManager.setTempEndDate(endDate);
                        sessionManager.setPurchasedBy(purchasedBy);
                        sessionManager.setTempTransectionId(transactionId);

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
                        //Intent intent = new Intent(SignUpActivity.this, WellcomeActivity.class);
                        Intent intent = new Intent(SignUpActivity.this, HelpActivity.class);
                        intent.putExtra("message",message);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                        finish();

                    }
                    else {
                        AppUtility.showToast(SignUpActivity.this, message, Toast.LENGTH_SHORT);

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
                        AppUtility.showToast(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT);

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
                params.put("confirmPassword", sConfirmPass);
                params.put("socialId", sSocialId);
                params.put("socialType", sSocialType);
                params.put("contactNo", sPhoneNo);
                params.put("deviceToken", mDeviceToken);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (profileImageBitmap!=null){
                    params.put("profilePic", new DataPart("profileImage.jpg", AppHelper.getFileDataFromDrawable(profileImageBitmap), "image/jpeg"));

                }

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
            Bitmap imageBitmap = null;

            if (requestCode == Constant.REQUEST_CAMERA) {
                imageBitmap = (Bitmap) data.getExtras().get("data");
                Uri selectedImageUri =  ImageUtil.getImageUri(SignUpActivity.this,imageBitmap);

                if (selectedImageUri != null) {
                    CropImage.activity(selectedImageUri).setAspectRatio(400,400).start(SignUpActivity.this);
                }

            } else if (requestCode == Constant.SELECT_FILE) {

                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    CropImage.activity(selectedImageUri).setAspectRatio(400,400).start(SignUpActivity.this);
                }

            }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                try {

                    if(result!=null) {
                        imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                    }

                    if (imageBitmap != null) {
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        profileImageBitmap = imageBitmap;
                        ivRegProfilePic.setImageBitmap(profileImageBitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
