package com.uhungry.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.image.cropper.CropImage;
import com.squareup.picasso.Picasso;
import com.uhungry.R;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.model.RecipeSteps;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import vollyemultipart.AppHelper;
import vollyemultipart.VolleyMultipartRequest;
import vollyemultipart.VolleySingleton;

import static com.uhungry.utils.Uhungry.sessionManager;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView ivProfilePic,ivEdit;
    private  boolean isEdit = false,isEmailPass = false;
    private EditText etProfileContact,etProfileEmail,etProfileName;
    private Bitmap profileImageBitmap;
    private String sFullName,sEmail,sContact,sPass;
    private Button btnProfileUpdate;
    private LinearLayout lyProfilePass;
    private TextView etProfPass;
    private RelativeLayout lyProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        bindView();
    }
    private void bindView(){
        TextView tvTitle = (TextView) findViewById(R.id.actionbarLayout_title);
        ImageView actionbar_btton_back = (ImageView) findViewById(R.id.actionbar_btton_back);
        ivEdit = (ImageView) findViewById(R.id.ivEdit);
        actionbar_btton_back.setVisibility(View.VISIBLE);
        lyProfilePass = (LinearLayout) findViewById(R.id.lyProfilePass);
        lyProfilePic = (RelativeLayout) findViewById(R.id.lyProfilePic);
        lyProfilePass.setEnabled(false);
        tvTitle.setText(getString(R.string.title_setting));

        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        btnProfileUpdate = (Button) findViewById(R.id.btnProfileUpdate);

        etProfileContact = (EditText) findViewById(R.id.etProfileContact);
        etProfileEmail = (EditText) findViewById(R.id.etProfileEmail);
        etProfileName = (EditText) findViewById(R.id.etProfileName);
        etProfPass = (TextView) findViewById(R.id.etProfPass);

        if (!sessionManager.getProfileImage().equals("") || !sessionManager.getProfileImage().isEmpty()) {
            Picasso.with(SettingActivity.this)
                    .load(sessionManager.getProfileImage()).into(ivProfilePic);
        }
        etProfileEmail.clearFocus();
        if (!sessionManager.getUserEmail().equals(""))
            etProfileEmail.setText(sessionManager.getUserEmail());
        else
            etProfileEmail.setText("NA");

        if (!sessionManager.getFullName().equals(""))
            etProfileName.setText(sessionManager.getFullName());

        if (!sessionManager.getContactNo().equals(""))
            etProfileContact.setText(sessionManager.getContactNo());

        ivEdit.setVisibility(View.VISIBLE);
        lyProfilePic.setEnabled(false);

        ivEdit.setOnClickListener(this);
        actionbar_btton_back.setOnClickListener(this);
        lyProfilePass.setOnClickListener(this);
        btnProfileUpdate.setOnClickListener(this);
        lyProfilePic.setOnClickListener(this);
        etProfPass.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.actionbar_btton_back :
                Intent intent = new Intent(SettingActivity.this, WellcomeActivity.class);
                intent.putExtra("message","SettingActivity");
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.exit_to_right,R.anim.enter_from_left);
                break;

            case R.id.lyProfilePic :
                selectImage();
                break;

            case R.id.lyProfilePass :
                showDialog();
                break;

            case R.id.etProfPass :
                showDialog();
                break;

            case R.id.btnProfileUpdate :
                boolean cancel = false;
                sEmail = etProfileEmail.getText().toString().trim();
                sFullName = etProfileName.getText().toString().trim();
                sContact = etProfileContact.getText().toString().trim();
                sPass = etProfPass.getText().toString().trim();

                if (sFullName.isEmpty()) {
                    cancel = true;
                    etProfileName.requestFocus();
                    etProfileName.setError(getString(R.string.please_enter_your_fullname));

                } else if (sEmail.isEmpty()) {
                    cancel = true;
                    etProfileEmail.requestFocus();
                    etProfileEmail.setError(getString(R.string.please_enter_your_email));

                } else if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                    cancel = true;
                    etProfileEmail.requestFocus();
                    etProfileEmail.setError(getString(R.string.please_enter_a_valid_email));

                }else if (!sContact.isEmpty()) {
                    if (sContact.length()<=9){
                        cancel = true;
                        etProfileContact.requestFocus();
                        etProfileContact.setError(getString(R.string.please_enter_a_valid_phone));
                    }
                }
                if (!cancel) {
                    if (AppUtility.isNetworkAvailable(SettingActivity.this)) {
                        //  if (sessionManager.getSocialId().equals("")||sessionManager.getSocialId().isEmpty()){
                        if (!sEmail.equals(sessionManager.getUserEmail())) {

                            showAlertDialog("Email modification will expire your current session","Are you sure");

                            /*AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingActivity.this);
                            builder1.setTitle("Are you sure");
                            builder1.setMessage("Email modification will expire your current session");
                            builder1.setCancelable(true);
                            builder1.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            isEmailPass = true;
                                            if (AppUtility.isNetworkAvailable(SettingActivity.this)) {
                                                updateProfileTask();
                                            } else {
                                                AppUtility.showToast(SettingActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT);
                                            }
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();*/
                        } else {
                            // isEmailPass = false;
                            updateProfileTask();
                        }
                        // }
                    }else {
                        AppUtility.showToast(SettingActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT);
                    }
                }

                break;

            case R.id.ivEdit :
                if (isEdit){
                    isEdit = false;
                    setComponentsEnableDisable();
                }else {
                    isEdit = true;
                    setComponentsEnableDisable();
                }
                break;

        }
    }

    public void showAlertDialog(String msg, String title) {
        View DialogView = View.inflate(SettingActivity.this, R.layout.dialog_recipe_layout, null);

        final Dialog alertDailog = new Dialog(SettingActivity.this, android.R.style.Theme_Light);
        alertDailog.setCanceledOnTouchOutside(false);
        alertDailog.setCancelable(false);

        alertDailog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDailog.getWindow().getAttributes().windowAnimations = R.style.InOutAnimation;
        alertDailog.setContentView(DialogView);

        Button btnYes = (Button) DialogView.findViewById(R.id.btnYes);
        Button btnNo = (Button) DialogView.findViewById(R.id.btnNo);
        ImageView ivCross = (ImageView) DialogView.findViewById(R.id.ivCross);
        TextView tv_msg = (TextView) DialogView.findViewById(R.id.tv_msg);
        TextView tv_title = (TextView) DialogView.findViewById(R.id.tv_title);

        tv_msg.setText(msg);
        tv_title.setText(title);
        btnYes.setText("OK");
        btnNo.setVisibility(View.GONE);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDailog.cancel();
                isEmailPass = true;
                if (AppUtility.isNetworkAvailable(SettingActivity.this)) {
                    updateProfileTask();
                } else {
                    AppUtility.showToast(SettingActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT);
                }
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDailog.cancel();
                showDialog();
            }
        });

        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDailog.cancel();
            }
        });

        alertDailog.show();
    }


    private void setComponentsEnableDisable() {
        if (!isEdit) {
            ivEdit.setAlpha(1.0f);
            etProfileEmail.setEnabled(false);
            etProfileContact.setEnabled(false);
            etProfileName.setEnabled(false);
            ivProfilePic.setEnabled(false);
            ivProfilePic.setOnClickListener(null);
            lyProfilePic.setEnabled(false);
            etProfPass.setEnabled(false);
            lyProfilePass.setEnabled(false);
            lyProfilePass.setClickable(false);
            btnProfileUpdate.setVisibility(View.GONE);

        }else {
            btnProfileUpdate.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(SettingActivity.this, R.anim.move_top);
            btnProfileUpdate.startAnimation(anim);
            ivEdit.setAlpha(0.5f);
            etProfileEmail.setEnabled(true);
            etProfileContact.setEnabled(true);
            etProfPass.setEnabled(true);
            etProfileName.setEnabled(true);
            ivProfilePic.setEnabled(true);
            ivProfilePic.setOnClickListener(this);
            lyProfilePass.setEnabled(true);
            lyProfilePass.setClickable(true);
            lyProfilePic.setEnabled(true);

        }
    }

    public  void showDialog() {
        View DialogView = View.inflate(SettingActivity.this, R.layout.dialog_change_pass_layout, null);

        final Dialog alertDailog = new Dialog(SettingActivity.this, android.R.style.Theme_Light);
        alertDailog.setCanceledOnTouchOutside(true);
        alertDailog.setCancelable(true);

        alertDailog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDailog.getWindow().getAttributes().windowAnimations = R.style.InOutAnimation;
        alertDailog.setContentView(DialogView);

        Button btnDone = (Button) DialogView.findViewById(R.id.btnDone);
        LinearLayout ivCross = (LinearLayout) DialogView.findViewById(R.id.ivCross);
        final EditText etOldPassword = (EditText) DialogView.findViewById(R.id.etOldPassword);
        final EditText etNewPassword = (EditText) DialogView.findViewById(R.id.etNewPassword);
        final EditText etNewConfirmPass = (EditText) DialogView.findViewById(R.id.etNewConfirmPass);

        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDailog.cancel();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean cancel = false;

                String sOldPassword = etOldPassword.getText().toString().trim();
                final String sNewPassword = etNewPassword.getText().toString().trim();
                String sNewConfPassword = etNewConfirmPass.getText().toString().trim();
                String oldpass = sessionManager.getUserPassword();
                if (sOldPassword.isEmpty()) {
                    cancel = true;
                    etOldPassword.requestFocus();
                    etOldPassword.setError("Please enter old password");

                }else if (!sOldPassword.equals(sessionManager.getUserPassword())) {
                    cancel = true;
                    etOldPassword.requestFocus();
                    etOldPassword.setError(getString(R.string.please_enter_correct_old_password));

                }else if (sNewPassword.isEmpty()){
                    cancel = true;
                    etNewPassword.requestFocus();
                    etNewPassword.setError("Please enter new password");
                }
                else if (sNewPassword.length()<=5){
                    cancel = true;
                    etNewPassword.requestFocus();
                    etNewPassword.setError(getString(R.string.please_enter_six_digit_pass));
                }else if (sNewConfPassword.isEmpty()) {
                    cancel = true;
                    etNewConfirmPass.requestFocus();
                    etNewConfirmPass.setError(getString(R.string.please_enter_confirm_password));
                }
                else if (!sNewConfPassword.equals(sNewPassword)){
                    cancel = true;
                    etNewConfirmPass.requestFocus();
                    etNewConfirmPass.setError(getString(R.string.please_enter_correct_confirm_pass));
                }
                if (!cancel) {

                    View DialogView = View.inflate(SettingActivity.this, R.layout.dialog_recipe_layout, null);

                    final Dialog dialog = new Dialog(SettingActivity.this, android.R.style.Theme_Light);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);

                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = R.style.InOutAnimation;
                    dialog.setContentView(DialogView);

                    Button btnYes = (Button) DialogView.findViewById(R.id.btnYes);
                    Button btnNo = (Button) DialogView.findViewById(R.id.btnNo);
                    ImageView ivCross = (ImageView) DialogView.findViewById(R.id.ivCross);
                    TextView tv_msg = (TextView) DialogView.findViewById(R.id.tv_msg);
                    TextView tv_title = (TextView) DialogView.findViewById(R.id.tv_title);

                    tv_msg.setText("Password modification will expire your current session");
                    tv_title.setText("Are you sure");
                    btnYes.setText("OK");
                    btnNo.setVisibility(View.GONE);

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDailog.cancel();
                            dialog.cancel();
                            isEmailPass = true;
                            etProfPass.setText(sNewPassword);
                        }
                    });

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDailog.cancel();
                            showDialog();
                        }
                    });

                    ivCross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDailog.cancel();
                        }
                    });

                    dialog.show();

                    //   if (!sNewPassword.equals(sessionManager.getUserPassword())) {
                  /*  AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingActivity.this);
                    builder1.setTitle("Are you sure");
                    builder1.setMessage("Password modification will expire your current session");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    alertDailog.cancel();
                                    dialog.cancel();
                                    isEmailPass = true;
                                    etProfPass.setText(sNewPassword);
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();*/
                    // }
                }
            }
        });

        alertDailog.show();
    }

    public void updateProfileTask() {

        final ProgressDialog customDialog = new ProgressDialog(SettingActivity.this);
        customDialog.show();

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,  Constant.BASE_URL_USER+"updateProfile", new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                customDialog.cancel();
                System.out.println("profile resultResponse==="+resultResponse);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String Status = result.getString("status");
                    String message = result.getString("message");

                    if(Status.equals("success")) {

                        JSONObject usedetailobject = result.getJSONObject("userInfo");
                        String sAuthToken,userId,email,sPhoneNo="",fullName,sProfileImage,sLoginSatus,sSocialId;
                        AppUtility.showToast(SettingActivity.this,message,0);
                        userId = usedetailobject.getString("userId");
                        email = usedetailobject.getString("email");
                        fullName = usedetailobject.getString("fullName");
                        sPhoneNo = usedetailobject.getString("contactNo");
                        sAuthToken = usedetailobject.getString(Constant.AUTHTOKEN);
                        sProfileImage = usedetailobject.getString("profilePic");

                        if (isEmailPass) {
                            //    sessionManager.setEmail(sEmail);
                            sessionManager.logout();
                            SettingActivity.this.overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                        }else {
                            sLoginSatus = "1";
                            sessionManager.createSession(userId,sEmail,fullName,"",sProfileImage,sPhoneNo,sPass,sLoginSatus,sAuthToken);

                        }
                        isEdit = false;
                        ivEdit.setAlpha(0.5f);
                        setComponentsEnableDisable();

                    }

                    else {
                        AppUtility.showToast(SettingActivity.this, message, Toast.LENGTH_SHORT);

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
                        AppUtility.showToast(SettingActivity.this, message, Toast.LENGTH_SHORT);

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

                params.put("fullName",sFullName);
                params.put("email",sEmail);
                params.put("password",sPass);
                params.put("contactNo",sContact);

                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                String AuthToken = sessionManager.getAuthToken();
                if (!AuthToken.equals("")){
                    header.put(Constant.AUTHTOKEN, AuthToken);
                }
                return header;
            }
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (profileImageBitmap!=null){
                    params.put("profilePic", new DataPart("profilePic.jpg", AppHelper.getFileDataFromDrawable(profileImageBitmap), "profilePic/jpeg"));

                }
                return params;
            }
        };

        VolleySingleton.getInstance(SettingActivity.this).addToRequestQueue(multipartRequest);
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,1));

    }

    private void selectImage() {

        final CharSequence[] items = {getString(R.string.text_take_photo), getString(R.string.text_chose_gellery), getString(R.string.text_cancel)};
        AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
        alert.setTitle(getString(R.string.text_add_photo));
        alert.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.text_take_photo))) {

                    if(Build.VERSION.SDK_INT >= 23) {
                        if (SettingActivity.this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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

                        if (SettingActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imageBitmap = null;
            if (requestCode == Constant.REQUEST_CAMERA) {
                imageBitmap = (Bitmap) data.getExtras().get("data");
                Uri selectedImageUri =  ImageUtil.getImageUri(SettingActivity.this,imageBitmap);

                if (selectedImageUri != null) {
                    CropImage.activity(selectedImageUri).setAspectRatio(400,400).start(SettingActivity.this);
                }

            } else if (requestCode == Constant.SELECT_FILE) {

                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    CropImage.activity(selectedImageUri).setAspectRatio(400,400).start(SettingActivity.this);
                }

            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                try {

                    if(result!=null) {
                        imageBitmap = MediaStore.Images.Media.getBitmap(SettingActivity.this.getContentResolver(), result.getUri());
                    }

                    if (imageBitmap != null) {
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        profileImageBitmap = imageBitmap;
                        ivProfilePic.setImageBitmap(profileImageBitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
                    Toast.makeText(SettingActivity.this, "YOU DENIED PERMISSION CANNOT SELECT IMAGE", Toast.LENGTH_LONG).show();
                }
            }break;

            case  Constant.MY_PERMISSIONS_REQUEST_CAMERA:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                } else {
                    Toast.makeText(SettingActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }
            } break;
        }
    }
}
