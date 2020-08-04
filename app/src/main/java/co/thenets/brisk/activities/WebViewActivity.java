package co.thenets.brisk.activities;

import android.content.Context;
import android.net.MailTo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import co.thenets.brisk.R;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;

public class WebViewActivity extends BaseActivity
{
    private String mUrl;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        mContext = this;
        setView();
    }

    private void setView()
    {
        mUrl = getIntent().getStringExtra(Params.URL_TO_OPEN);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(new WebClient());
        mWebView.loadUrl(mUrl);
    }

    private class WebClient extends WebViewClient
    {
        @Override
        public void onPageFinished(WebView view, String url)
        {
            mProgressBar.setVisibility(View.INVISIBLE);
            mWebView.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
            view.clearCache(true);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            if (MailTo.isMailTo(url))
            {
                Utils.sendMail(mContext, Params.SUPPORT_EMAIL, getString(R.string.contact_us), "");
                view.reload();
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}
