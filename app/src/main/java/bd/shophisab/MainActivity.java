package bd.shophisab;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

	private WebView web;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		web = new WebView(this);
		web.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		WebSettings s = web.getSettings();
		s.setJavaScriptEnabled(true);
		s.setDomStorageEnabled(true);
		s.setDatabaseEnabled(true);
		s.setAllowFileAccess(true);
		s.setAllowContentAccess(true);
		s.setCacheMode(WebSettings.LOAD_DEFAULT);
		s.setUseWideViewPort(true);
		s.setLoadWithOverviewMode(false);
		s.setSupportZoom(false);
		s.setBuiltInZoomControls(false);
		s.setTextZoom(100);

		web.setWebViewClient(new WebViewClient());
		web.loadUrl("file:///android_asset/index.html");

		setContentView(web);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && web != null && web.canGoBack()) {
			web.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
