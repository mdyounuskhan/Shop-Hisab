package bd.shophisab;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class MainActivity extends Activity {

	private WebView web;
	private ValueCallback<Uri[]> filePathCallback;
	private static final int FILE_PICK = 1001;

	@SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
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
		s.setAllowFileAccessFromFileURLs(true);
		s.setAllowUniversalAccessFromFileURLs(true);
		s.setCacheMode(WebSettings.LOAD_DEFAULT);
		s.setUseWideViewPort(true);
		s.setLoadWithOverviewMode(false);
		s.setSupportZoom(false);
		s.setBuiltInZoomControls(false);
		s.setTextZoom(100);

		web.setWebViewClient(new WebViewClient());

		// Needed so that alert/confirm work and so that <input type="file"> opens the picker.
		web.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onShowFileChooser(WebView view,
					ValueCallback<Uri[]> callback,
					FileChooserParams params) {
				if (filePathCallback != null) {
					filePathCallback.onReceiveValue(null);
				}
				filePathCallback = callback;
				try {
					Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
					pick.addCategory(Intent.CATEGORY_OPENABLE);
					pick.setType("*/*");
					startActivityForResult(Intent.createChooser(pick, "Choose backup file"), FILE_PICK);
					return true;
				} catch (Exception e) {
					filePathCallback = null;
					toast("No file manager found");
					return false;
				}
			}
		});

		// Lets the page save real files (blob downloads do not work inside a WebView).
		web.addJavascriptInterface(new Bridge(), "Android");

		web.loadUrl("file:///android_asset/index.html");

		setContentView(web);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FILE_PICK) {
			Uri[] result = null;
			if (resultCode == RESULT_OK && data != null && data.getData() != null) {
				result = new Uri[]{ data.getData() };
			}
			if (filePathCallback != null) {
				filePathCallback.onReceiveValue(result);
			}
			filePathCallback = null;
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void toast(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	private class Bridge {

		@JavascriptInterface
		public String saveFile(String name, String content, String mime) {
			try {
				if (name == null || name.length() == 0) name = "shop-hisab.txt";
				if (mime == null || mime.length() == 0) mime = "text/plain";
				byte[] bytes = content.getBytes(Charset.forName("UTF-8"));

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
					ContentValues v = new ContentValues();
					v.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
					v.put(MediaStore.MediaColumns.MIME_TYPE, mime);
					v.put(MediaStore.MediaColumns.RELATIVE_PATH,
							Environment.DIRECTORY_DOWNLOADS + "/ShopHisab");
					Uri uri = getContentResolver().insert(
							MediaStore.Downloads.EXTERNAL_CONTENT_URI, v);
					if (uri == null) {
						toast("Could not create the file");
						return "error";
					}
					OutputStream os = getContentResolver().openOutputStream(uri);
					os.write(bytes);
					os.flush();
					os.close();
					toast("Saved: Downloads/ShopHisab/" + name);
				} else {
					File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
					if (dir == null) dir = getFilesDir();
					if (!dir.exists()) dir.mkdirs();
					File out = new File(dir, name);
					FileOutputStream fos = new FileOutputStream(out);
					fos.write(bytes);
					fos.flush();
					fos.close();
					toast("Saved: " + out.getAbsolutePath());
				}
				return "ok";
			} catch (Exception e) {
				toast("Save failed: " + e.getMessage());
				return "error";
			}
		}
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
