package com.uhungry.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uhungry.R;

public class TermsAndConditionActivity extends AppCompatActivity {
private WebView webView;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);

        TextView tvTitle = (TextView) findViewById(R.id.actionbarLayout_title);
        ImageView actionbar_btton_back = (ImageView) findViewById(R.id.actionbar_btton_back);
        actionbar_btton_back.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.title_termsAndConditions);

        webView = (WebView) findViewById(R.id.webView);
        dialog = new ProgressDialog(TermsAndConditionActivity.this);
        dialog.setMessage("Loading please wait.....");
        initWebView("http://192.232.197.144/dev/themes/TC/TermsConditions.pdf");

        actionbar_btton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void initWebView(String url) {
        webView.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(url);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowContentAccess(true);
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url="+url);
    }

    private class MyWebViewClient extends WebViewClient {

        private MyWebViewClient() {
        }


        @Override
        public void onPageFinished(WebView view, String url) {
//            progressBar.setProgress(100);
//            progressBar.setVisibility(View.GONE);
            dialog.dismiss();
            super.onPageFinished(view, url);

        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            progressBar.setProgress(0);
            dialog.show();
            super.onPageStarted(view, url, favicon);

        }


        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }
}
