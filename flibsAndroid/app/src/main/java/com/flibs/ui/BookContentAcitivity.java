package com.flibs.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.flibs.R;

/**
 * Created by dbudyak on 29.07.16.
 */
public class BookContentAcitivity extends BaseActivity {

    public static String KEY_BOOK_ID = "just_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_content);
        String bookId = getIntent().getExtras().getString(KEY_BOOK_ID);

        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl("http://flibs.efnez.ru/r?b=" + bookId);
        myWebView.setBackgroundResource(android.R.color.white);
        myWebView.setBackgroundColor(Color.TRANSPARENT);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
    }
}
