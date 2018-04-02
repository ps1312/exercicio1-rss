package br.ufpe.cin.if1001.rss;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebviewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Pega os dados vindos do intent e joga na webview para carregar
        setContentView(R.layout.activity_webview);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient());
        Intent intentContent = getIntent();
        myWebView.loadUrl(intentContent.getStringExtra("url"));
    }
}
