package com.mahekmshah.swachhbharat;

import android.app.Application;

import com.parse.Parse;
import com.parse.PushService;

public class SwachBharatApplication extends Application implements SwachConstants {

	private SwachTwitter mTwitter;

	private String mTwitterToken;
	private String mTwitterTokenSecret;
	private String mUserName;
	private String mScreenName;

	@Override
	public void onCreate() {
		super.onCreate();
		Parse.enableLocalDatastore(this);
		Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_ID);
		PushService.setDefaultPushCallback(this, MainActivity.class);
	}

	public SwachTwitter getTwitter() {
		if (mTwitter == null) {
			mTwitter = new SwachTwitter(CONSUMER_KEY, CONSUMER_SECREY);
		}
		return mTwitter;
	}

	public String getTwitterToken() {
		return mTwitterToken;
	}

	public void setTwitterToken(String mTwitterToken) {
		this.mTwitterToken = mTwitterToken;
	}

	public String getTwitterTokenSecret() {
		return mTwitterTokenSecret;
	}

	public void setTwitterTokenSecret(String mTwitterTokenSecret) {
		this.mTwitterTokenSecret = mTwitterTokenSecret;
	}

	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String userName) {
		this.mUserName = userName;
	}

	public String getScreenName() {
		return mScreenName;
	}

	public void setScreenName(String screenName) {
		this.mScreenName = screenName;
	}
}
