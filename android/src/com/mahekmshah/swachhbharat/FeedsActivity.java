package com.mahekmshah.swachhbharat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

public class FeedsActivity extends Activity {
	private WebView feed;

	private int differentator = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_feeds);
		if ((getIntent() != null) && (getIntent().getExtras() != null)) {
			differentator = (int) getIntent().getExtras().get("URL_DIFFERENTIATOR");
		}

		feed = (WebView) findViewById(R.id.creditsfeed);
		feed.getSettings().setJavaScriptEnabled(true);
		feed.setPadding(0, 0, 0, 0);
		feed.setInitialScale(getScale());
		switch (differentator) {
		case 0:
			feed.loadUrl("file:///android_asset/map.html");
			break;
		case 1:
			feed.loadUrl("file:///android_asset/twitterwidgethome.html");
			break;
		case 2:
			feed.loadUrl("file:///android_asset/twitterwidgetmenu.html");
			break;
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	private int getScale() {
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int width = display.getWidth();
		Double val = new Double(width) / new Double(300);
		val = val * 100d;
		return val.intValue();
	}
}
