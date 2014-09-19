package net.gree.unitywebview;

import com.unity3d.player.UnityPlayer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewDialog extends Dialog
{
	private WebView mWebView;
	
	public WebViewDialog(Context context, final String gameObject) {
		super(context, android.R.style.Theme);

		getWindow ().setGravity (Gravity.NO_GRAVITY);
		getWindow ().requestFeature (Window.FEATURE_NO_TITLE);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		// Set the background to an empty drawable to make it transparent:
		ColorDrawable emptyDrawable = new ColorDrawable (0);
		getWindow ().setBackgroundDrawable (emptyDrawable);

		// Don't dim the view behind the dialog:
		getWindow ().clearFlags (WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		
		mWebView = new WebView(context);
		mWebView.setVisibility(View.GONE);
		mWebView.setFocusable(true);
		mWebView.setFocusableInTouchMode(true);

		setContentView(mWebView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		mWebView.setWebChromeClient(new WebChromeClient() {
			public boolean onConsoleMessage(android.webkit.ConsoleMessage cm) {
				Log.d("Webview", cm.message());
				return true;
			}
		});
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("http://") || url.startsWith("https://") || 
						url.startsWith("file://") || url.startsWith("javascript:")) {
					// Let webview handle the URL
					return false;
				}
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				view.getContext().startActivity(intent);
				return true;
			}
		});
		mWebView.addJavascriptInterface(
			new WebViewPluginInterface(gameObject), "Unity");

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setSupportZoom(false);
		webSettings.setJavaScriptEnabled(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			Log.i("WebViewPlugin", "Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT);
			webSettings.setAllowUniversalAccessFromFileURLs(true);
		}
		webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		String databasePath = mWebView.getContext().getDir("databases", Context.MODE_PRIVATE).getPath(); 
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
		webSettings.setDatabasePath(databasePath); 

	}

	public WebView getWebView()
	{
		return mWebView;
	}

}
