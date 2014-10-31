package com.mahekmshah.swachhbharat;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mahekmshah.swachhbharat.NetworkOperations.JSONCallback;
import com.mahekmshah.swachhbharat.SwachDialog.DoubleButtonCallback;
import com.mahekmshah.swachhbharat.SwachTwitter.TwitterCallback;
import com.mahekmshah.swachhbharat.reusable.SwachIconFont;
import com.mahekmshah.swachhbharat.tools.NetworkUtils;

public class MainActivity extends FragmentActivity implements OnClickListener, SwachConstants {

	private SwachBharatApplication mApp;

	private LinearLayout mComplaintLayout;

	private final int ACTIVITY_TWITTERLOGIN = 0;
	private final int ACTIVITY_CAMERA = 1;

	private Uri imageUri;

	private String mTweetCategory;
	private SwachLocationService appLocationService;

	private NetworkOperations mNetwork;

	private ProgressDialog progress;

	private LinearLayout mapReports;
	private LinearLayout hashSwach;
	private LinearLayout atSwach;
	private LinearLayout fbPage;
	private LinearLayout website;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		mApp = (SwachBharatApplication) getApplication();
		appLocationService = new SwachLocationService(MainActivity.this);

		initComplaints();
		initLinks();

		SharedPreferences pref = getSharedPreferences("com.mahekmshah.swachhbharat", MODE_PRIVATE);
		String token = pref.getString("com.mahekmshah.swachhbharat.token", null);
		String secret = pref.getString("com.mahekmshah.swachhbharat.secret", null);
		String username = pref.getString("com.mahekmshah.swachhbharat.username", null);
		String screen = pref.getString("com.mahekmshah.swachhbharat.screenname", null);

		if ((token == null) || (secret == null)) {
			Intent i = new Intent(MainActivity.this, LoginActivity.class);
			startActivityForResult(i, ACTIVITY_TWITTERLOGIN);
		} else {
			mApp.setTwitterToken(token);
			mApp.setTwitterTokenSecret(secret);
			mApp.setUserName(username);
			mApp.setScreenName(screen);
		}

		mNetwork = new NetworkOperations();
	}

	private void initComplaints() {
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		String[] complaints = getResources().getStringArray(R.array.complaints);
		String[] complaints_icons = getResources().getStringArray(R.array.complaints_icons);
		mComplaintLayout = (LinearLayout) findViewById(R.id.complainticonsscroll);
		for (int i = 0; i < complaints.length; i++) {
			View v = mInflater.inflate(R.layout.view_complaint_items, null);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(30, 30, 30, 30);
			v.setLayoutParams(layoutParams);
			v.setTag(complaints[i]);
			// v.setBackgroundResource(R.drawable.button_selector);
			v.setOnClickListener(this);

			SwachIconFont icon = (SwachIconFont) v.findViewById(R.id.comicons);
			TextView text = (TextView) v.findViewById(R.id.comiconstext);

			icon.setText(complaints_icons[i]);
			text.setText(complaints[i]);

			mComplaintLayout.addView(v);
		}
	}

	private void initLinks() {
		mapReports = (LinearLayout) findViewById(R.id.map_reports);
		hashSwach = (LinearLayout) findViewById(R.id.hash_swach);
		atSwach = (LinearLayout) findViewById(R.id.at_swach);
		fbPage = (LinearLayout) findViewById(R.id.fb_page);
		website = (LinearLayout) findViewById(R.id.website);

		mapReports.setOnClickListener(this);
		hashSwach.setOnClickListener(this);
		atSwach.setOnClickListener(this);
		fbPage.setOnClickListener(this);
		website.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ACTIVITY_CAMERA) {
			if (resultCode == RESULT_OK) {
				prepareTweet();
			} else {
				// CustomDialog.singleActionDialogue(MainActivity.this, getResources().getString(R.string.dialog_alert), getResources().getString(R.string.dialog_error_camera_failure), getResources()
				// .getString(R.string.dialog_ok), null);
			}
		}
	}

	private void prepareTweet() {
		if (!NetworkUtils.isOnline(this)) {
			SwachDialog.singleActionDialogue(this, getResources().getString(R.string.dialog_network_alert), getResources().getString(R.string.dialog_network_unavailable),
					getResources().getString(R.string.dialog_ok), null);
			return;
		}

		Location location = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			location = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (location == null) {
			SwachDialog.doubleActionDialogue(MainActivity.this, getResources().getString(R.string.dialog_report_dirty), getResources().getString(R.string.dialog_error_unable_gps), getResources()
					.getString(R.string.dialog_button_settings), getResources().getString(R.string.dialog_button_cancel), new DoubleButtonCallback() {
				@Override
				public void setPositiveButton() {
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}

				@Override
				public void setNegativeButton() {

				}
			});
		}

		// #swachhbharat [TAG eg: Hospital] @pmoindia at [GPS/map url], Please fix this! . Reported by [twitter username] via [@swachhbharatapp]
		if (location != null) {

			StringBuilder builder = new StringBuilder();
			builder.append("http://maps.google.com/maps?z=12&t=m&q=loc:");
			builder.append(location.getLatitude());
			builder.append("+");
			builder.append(location.getLongitude());

			String payload = "{\"longUrl\": \"" + builder.toString() + "\"}";

			final double lat = location.getLatitude();
			final double lon = location.getLongitude();
			mNetwork.postSwachData(API_SHORT_URL, payload, new JSONCallback() {
				@Override
				public void getJsonData(byte error, JSONObject data) {
					try {
						String shortUrl = data.getString("id");
						String longUrl = data.getString("longUrl");
						final String tweetText = "Please fix this at " + shortUrl + "! #swachhbharat @pmoindia. Reported via @swachhbharatapp Tag:" + mTweetCategory;
						setTweetConfirmationDialog(tweetText, lat, lon, shortUrl);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

		}
	}

	public void setTweetConfirmationDialog(final String tweetText, final double lat, final double lon, final String mapUrl) {
		SwachDialog.doubleActionDialogue(MainActivity.this, getResources().getString(R.string.dialog_report_dirty), tweetText, getResources().getString(R.string.dialog_tweet), getResources()
				.getString(R.string.dialog_button_cancel), new DoubleButtonCallback() {
			@Override
			public void setPositiveButton() {
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progress = new ProgressDialog(MainActivity.this);
						progress.setMessage(getResources().getString(R.string.dialog_tweeting));
						progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						progress.setCancelable(false);
						progress.show();
					}
				});

				File f = convertImageUriToFile(imageUri);
				mApp.getTwitter().postMedia(CONSUMER_KEY, CONSUMER_SECREY, mApp.getTwitterToken(), mApp.getTwitterTokenSecret(), tweetText, f, new TwitterCallback() {
					@Override
					public void getMessage(final int error, String url, String token, String secret) {
						MainActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								progress.cancel();
								if (error == 0) {
									Toast.makeText(MainActivity.this, getResources().getString(R.string.dialog_tweeted_success), Toast.LENGTH_LONG).show();
									try {
										JSONObject jObj = new JSONObject();
										jObj.put("method", "addTweet");
										jObj.put("Username", mApp.getScreenName());
										jObj.put("Name", mTweetCategory);
										jObj.put("Latitude", lat);
										jObj.put("Longitude", lon);
										jObj.put("MapUrl", mapUrl);
										jObj.put("TweetUrl", "");
										mNetwork.getSwachData(API_BASE + Uri.encode(jObj.toString()), null);
									} catch (JSONException e) {
										e.printStackTrace();
									}
								} else {
									Toast.makeText(MainActivity.this, getResources().getString(R.string.dialog_tweeted_failure), Toast.LENGTH_LONG).show();
								}
							}
						});
					}
				});
			}

			@Override
			public void setNegativeButton() {

			}
		});
	}

	@Override
	public void onClick(View v) {
		Intent feed;
		switch (v.getId()) {
		case R.id.map_reports:
			feed = new Intent(MainActivity.this, FeedsActivity.class);
			feed.putExtra("URL_DIFFERENTIATOR", 0);
			startActivity(feed);
			break;
		case R.id.hash_swach:
			feed = new Intent(MainActivity.this, FeedsActivity.class);
			feed.putExtra("URL_DIFFERENTIATOR", 1);
			startActivity(feed);
			break;
		case R.id.at_swach:
			feed = new Intent(MainActivity.this, FeedsActivity.class);
			feed.putExtra("URL_DIFFERENTIATOR", 2);
			startActivity(feed);
			break;
		case R.id.fb_page:
			feed = new Intent(Intent.ACTION_VIEW);
			feed.setData(Uri.parse(getResources().getString(R.string.url_fb)));
			startActivity(feed);
			break;
		case R.id.website:
			feed = new Intent(Intent.ACTION_VIEW);
			feed.setData(Uri.parse(getResources().getString(R.string.credits_fotomobile_link)));
			startActivity(feed);
			break;
		default:
			mTweetCategory = v.getTag().toString();
			launchCamera();
			break;
		}

	}

	private void launchCamera() {
		String fileName = "SwachhBharat" + System.currentTimeMillis() + ".jpg";
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, fileName);
		values.put(MediaStore.Images.Media.DESCRIPTION, "Pic for Swachh Bharat");
		imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(intent, ACTIVITY_CAMERA);
	}

	public File convertImageUriToFile(Uri imageUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION };
			cursor = getContentResolver().query(imageUri, proj, null, null, null);
			int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			int orientation_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);
			if (cursor.moveToFirst()) {
				String orientation = cursor.getString(orientation_ColumnIndex);
				return new File(cursor.getString(file_ColumnIndex));
			}
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
}
