package co.abarbazar;

import android.Manifest;
import android.annotation.SuppressLint;
import co.ronash.pushe.Pushe;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AbsPermission {
    private static final int REQUEST_PERMISSION = 10;
    public Handler handler = new Handler();
    public SwipeRefreshLayout swtRefresh;
    public static boolean exit = false;
    private WebView mWebView;
    public String catche = null;
    public Button TryAgain;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Pushe.initialize(this, true);


        requestAppPermission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                R.string.msg, REQUEST_PERMISSION);



        init();
    }

    @Override
    public void onPermissiosGranted(int requestCode) {
        //here is not anything
    }

    public void init() {
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.webview);
        mWebView.requestFocus();
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if (catche == null) {
            mWebView.loadUrl("https://www.abarbazar.com/");
        } else {
            mWebView.loadUrl(catche);
        }

        CookieManager.getInstance().setAcceptCookie(true);

                mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view = mWebView;
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                swtRefresh.setRefreshing(false);
            }

            @Override
            public void onReceivedError(WebView view,
                                        WebResourceRequest request,
                                        WebResourceError error) {

                catche = mWebView.getUrl().toString();
                show();
            }

        });


        swtRefresh = findViewById(R.id.swipreRefresh);

        swtRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String n = mWebView.getUrl();

                swtRefresh.setRefreshing(true);
                mWebView.loadUrl(n);
                swtRefresh.setRefreshing(false);
            }

        });

    }

    private void show() {
        setContentView(R.layout.activity_error);
        TryAgain = findViewById(R.id.Again);
        TryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((isNetworkAvailable())) {
                    setContentView(R.layout.activity_main);
                    init();
                } else {
                    Toast.makeText(MainActivity.this,
                            "عدم اتصال به اینترنت", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(MainActivity.this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else if (exit) {
            System.exit(0);
        } else {
            exit = true;
            Toast.makeText(this, "برای خروج دو بار کلیک کنید", Toast.LENGTH_SHORT).show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 2000);
        }
    }
}