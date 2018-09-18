package com.uhungry.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uhungry.R;
import com.uhungry.custom_widget.ProgressDialog;
import com.uhungry.helper.Constant;
import com.uhungry.utils.AppUtility;
import com.uhungry.utils.Uhungry;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgetPassActivity extends AppCompatActivity {
    EditText etForgetPassEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        etForgetPassEmail = (EditText) findViewById(R.id.etForgetPassEmail);

        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        ImageView actionbar_btton_back = (ImageView) findViewById(R.id.actionbar_btton_back);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etForgetPassEmail.getText().toString();
                if (!email.equals("")){
                    if(AppUtility.isNetworkAvailable(ForgetPassActivity.this)) {
                        ForgetPassApi(email);
                    }
                    else {
                        AppUtility.showAlertDialog_SingleButton(ForgetPassActivity.this,getResources().getString(R.string.network_error),"Alert!","Ok");
                    }
                }else {
                    AppUtility.showToast(ForgetPassActivity.this,"Enter your email id.",1);
                }
            }
        });

        actionbar_btton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);

            }
        });
    }

    private void ForgetPassApi(final String email) {
        final ProgressDialog customDialog = new ProgressDialog(ForgetPassActivity.this);
        customDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL+"forgotPassword",

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customDialog.cancel();
                        System.out.println("response" + response);
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if (status.equalsIgnoreCase("success")) {
                                AppUtility.showToast(ForgetPassActivity.this,message,1);
                                finish();
                                overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                            }
                            else {
                                AppUtility.showAlertDialog_SingleButton(ForgetPassActivity.this,message,"Error!","Ok");
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        customDialog.cancel();
                        AppUtility.showAlertDialog_SingleButton(ForgetPassActivity.this,"Something went wrong, please check after some time.","Error!","Ok");
                    }
                }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();

                header.put("email", email);

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

        RequestQueue requestQueue = Volley.newRequestQueue(ForgetPassActivity.this);
        requestQueue.add(stringRequest);
    }

}
