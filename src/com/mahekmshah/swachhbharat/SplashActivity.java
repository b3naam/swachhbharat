package com.mahekmshah.swachhbharat;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.mahekmshah.swachhbharat.tools.NetworkUtils;

public class SplashActivity extends Activity implements OnClickListener {
	private Timer mTimer;
	private MyTimerTask mTimerTask;

	private ImageView mTwitterLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_splash);

		mTwitterLogin = (ImageView) findViewById(R.id.twitterLogin);

		mTimer = new Timer();
		mTimerTask = new MyTimerTask();
		mTimer.schedule(mTimerTask, 5000);
	}

	class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				public void run() {
					mTimer.cancel();
					mTwitterLogin.setVisibility(View.VISIBLE);
					mTwitterLogin.setOnClickListener(SplashActivity.this);
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.twitterLogin) {
			if (NetworkUtils.isOnline(this)) {
				finish();
				Intent i = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(i);
			} else {
				SwachDialog.singleActionDialogue(this, getResources().getString(R.string.dialog_network_alert), getResources().getString(R.string.dialog_network_unavailable), getResources()
						.getString(R.string.dialog_ok), null);
			}
		}
	}

}
