package com.mahekmshah.swachhbharat;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mahekmshah.swachhbharat.SwachTwitter.TwitterCallback;
import com.mahekmshah.swachhbharat.tools.NetworkUtils;

public class LoginActivity extends Activity implements SwachConstants {

	private SwachBharatApplication mApp;

	private NetworkOperations nWhandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_twitter_login);

		mApp = (SwachBharatApplication) getApplication();

		nWhandler = new NetworkOperations();

		mApp.getTwitter().askOAuth(new TwitterCallback() {
			@Override
			public void getMessage(int error, final String url, String token, String secret) {
				if (error == 0) {
					LoginActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							openTwitterLogin(url);
						}
					});
				}
			}
		});
	}

	public void openTwitterLogin(String url) {
		WebView mWebView = (WebView) findViewById(R.id.twitter_login_web_view);
		mWebView.loadUrl(url);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.contains(SwachTwitter.TWITTER_CALLBACK_URL)) {
					mApp.getTwitter().getAccessTokenandSecret(url, new TwitterCallback() {
						@Override
						public void getMessage(int error, String url, String token, String secret) {
							if (error == 0) {
								SharedPreferences.Editor editor = getSharedPreferences("com.mahekmshah.swachhbharat", MODE_PRIVATE).edit();
								editor.putString("com.mahekmshah.swachhbharat.token", token);
								editor.putString("com.mahekmshah.swachhbharat.secret", secret);
								editor.putString("com.mahekmshah.swachhbharat.username", mApp.getTwitter().getUserName());
								editor.putString("com.mahekmshah.swachhbharat.screenname", mApp.getTwitter().getScreenName());
								editor.commit();

								mApp.setTwitterToken(token);
								mApp.setTwitterTokenSecret(secret);
								mApp.setUserName(mApp.getTwitter().getUserName());
								mApp.setScreenName(mApp.getTwitter().getScreenName());

								try {
									JSONObject jObj = new JSONObject();
									jObj.put("method", "addUser");
									jObj.put("Name", mApp.getUserName());
									jObj.put("Email", "");
									jObj.put("Handle", "@" + mApp.getScreenName());
									jObj.put("DeviceId", NetworkUtils.getSecureID(LoginActivity.this));
									nWhandler.getSwachData(API_BASE + Uri.encode(jObj.toString()), null);
									finish();
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}
					});
					return true;
				}
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}
		});
	}

	@Override
	public void onBackPressed() {

	}
}
