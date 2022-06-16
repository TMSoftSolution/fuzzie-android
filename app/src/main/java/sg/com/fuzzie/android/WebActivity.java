package sg.com.fuzzie.android;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sg.com.fuzzie.android.core.BaseActivity;

/**
 * Created by nurimanizam on 15/12/16.
 */

@EActivity(R.layout.activity_web)
public class WebActivity extends BaseActivity {

    @Extra
    boolean showLoading;

    @Extra
    String urlExtra;

    @Extra
    String titleExtra;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.webView)
    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @AfterViews
    public void calledAfterViewInjection() {

        if (titleExtra != null) {
            tvTitle.setText(titleExtra);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (showLoading) {
                    showLoader();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoader();
                showLoading = false;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                hideLoader();
                showLoading = false;
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                hideLoader();
                showLoading = false;
            }
        });
        if (urlExtra != null) {
            webView.loadUrl(urlExtra);
        }

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

}
