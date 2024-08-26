package org.koishi.launcher.h2co3.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;

import java.io.IOException;

public class MicrosoftLoginActivity extends H2CO3Activity {

    public static final int AUTHENTICATE_MICROSOFT_REQUEST = 2000;
    public static final int CANCEL_REQUEST = 3000;

    private WebView webView;
    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private Runnable retryRunnable;
    private static final int RETRY_DELAY = 2000;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microsoft_login);
        showMessageListView();
        progressBar = findViewById(R.id.web_loading_progress);
        webView = findViewById(R.id.microsoft_login_webview);
        initWebView();
        loadMicrosoftLoginUrl();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        webView.setWebViewClient(new MicrosoftLoginWebViewClient());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    private void loadMicrosoftLoginUrl() {
        String microsoftLoginUrl = "https://login.live.com/oauth20_authorize.srf" +
                "?client_id=00000000402b5328" +
                "&response_type=code" +
                "&scope=service%3A%3Auser.auth.xboxlive.com%3A%3AMBI_SSL" +
                "&redirect_url=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf";
        webView.loadUrl(microsoftLoginUrl);
    }

    class MicrosoftLoginWebViewClient extends WebViewClient {

        private static final String MICROSOFT_LOGIN_PREFIX = "ms-xal-00000000402b5328";
        private static final String CANCEL_URL = "res=cancel";

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (url.startsWith(MICROSOFT_LOGIN_PREFIX)) {
                Intent data = new Intent();
                data.setData(Uri.parse(url));
                progressBar.setVisibility(View.GONE);
                setResult(Activity.RESULT_OK, data);
                try {
                    clearWebViewCache(MicrosoftLoginActivity.this);
                } catch (IOException e) {
                    H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
                }
                finish();
                return true;
            }

            if (url.contains(CANCEL_URL)) {
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
            }

            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            handler.removeCallbacks(retryRunnable);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            progressBar.setVisibility(View.GONE);

            retryRunnable = () -> {
                progressBar.setVisibility(View.VISIBLE);
                view.loadUrl(view.getUrl());
            };
            handler.postDelayed(retryRunnable, RETRY_DELAY);
        }
    }
}