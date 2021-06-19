package com.example.crockpot3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class UserGuideActivity extends AppCompatActivity { // simple class for displaying the HTML usage guide to the user
    private WebView guideWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_webguide);

        guideWebView = findViewById(R.id.webview);
        guideWebView.getSettings().setAllowContentAccess(true);
        guideWebView.getSettings().setAllowFileAccess(true);
        guideWebView.loadUrl("file:///android_asset/webfiles/index.html");
    }
}