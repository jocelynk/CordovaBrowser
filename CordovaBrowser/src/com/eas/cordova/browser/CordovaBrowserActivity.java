package com.eas.cordova.browser;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.widget.Toast;

import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.DroidGap;

public class CordovaBrowserActivity extends DroidGap {
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String defaultUrl = "file:///android_asset/www/index.html";
		String url = getUrlFromIntent(getIntent(), defaultUrl);
		super.loadUrl(url);
		super.appView.clearCache(false);

	}


	public String getUrlFromIntent(Intent i, String defaultUrl){
		String webURI = i.getStringExtra(Intent.EXTRA_TEXT);
		if (webURI != null)
			return webURI;
		else 
			return defaultUrl;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.home){
			super.loadUrl("file:///android_asset/www/index.html");
		}
		else if (item.getItemId() == R.id.refresh){
			super.appView.clearCache(false);
			super.appView.reload();
		}
		return true;
	}

	private void toastConsoleMessage(String message, int lineNumber, String sourceID){
		if (message != null && sourceID != null &&
				!message.contains("JSCallback Error: Request failed with status 0")){
			Toast.makeText(this,
					sourceID + ": Line " + lineNumber + " : " + message, 
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void init() {
		CordovaWebView webView = new CordovaWebView(this);
		CordovaWebViewClient webViewClient;
		webViewClient = new CordovaWebViewClient(this, webView);
		this.init(webView, webViewClient, new CordovaChromeClient(this, webView){
			@Override
			public void onConsoleMessage(String message, int lineNumber, String sourceID){
				toastConsoleMessage(message, lineNumber, sourceID);
				super.onConsoleMessage(message, lineNumber, sourceID);
			}

			@TargetApi(8)
			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage){
				toastConsoleMessage(consoleMessage.message(),
						consoleMessage.lineNumber(),
						consoleMessage.sourceId());
				return super.onConsoleMessage(consoleMessage);
			}
		});
	}
}