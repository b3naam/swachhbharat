package com.mahekmshah.swachhbharat;

import java.io.File;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.net.Uri;

public class SwachTwitter {
	private String twitterConsumerKey;
	private String twitterConsumerSecret;
	private Twitter twitter;
	private RequestToken requestToken;

	private String userName;
	private String screenName;

	public static final String TWITTER_CALLBACK_URL = "http://about.me/mahek";
	public final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";

	public SwachTwitter(String twitterConsumerKey, String twitterConsumerSecret) {
		this.twitterConsumerKey = twitterConsumerKey;
		this.twitterConsumerSecret = twitterConsumerSecret;
	}

	public interface TwitterCallback {
		public void getMessage(int error, String url, String token, String secret);
	}

	public void askOAuth(final TwitterCallback callback) {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(twitterConsumerKey);
		configurationBuilder.setOAuthConsumerSecret(twitterConsumerSecret);
		Configuration configuration = configurationBuilder.build();
		twitter = new TwitterFactory(configuration).getInstance();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
					if (callback != null) {
						callback.getMessage(0, requestToken.getAuthenticationURL(), null, null);
					}
				} catch (Exception e) {
					final String errorString = e.toString();
					if (callback != null) {
						callback.getMessage(-1, errorString, null, null);
					}
				}
			}
		}).start();
	}

	public void getAccessTokenandSecret(final String url, final TwitterCallback callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Uri uri = Uri.parse(url);
					String verifier = uri.getQueryParameter(IEXTRA_OAUTH_VERIFIER);
					AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

					long userID = accessToken.getUserId();
					User user = twitter.showUser(userID);
					userName = user.getName();
					screenName = user.getScreenName();
					if (callback != null) {
						callback.getMessage(0, null, accessToken.getToken(), accessToken.getTokenSecret());
					}
				} catch (Exception e) {
					if (callback != null) {
						callback.getMessage(-1, null, null, null);
					}
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void postMessage(String twitterConsumerKey, String twitterConsumerSecret, String token, String tokenSecret, final String message, final TwitterCallback callback) {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(twitterConsumerKey);
		configurationBuilder.setOAuthConsumerSecret(twitterConsumerSecret);
		configurationBuilder.setOAuthAccessToken(token);
		configurationBuilder.setOAuthAccessTokenSecret(tokenSecret);
		Configuration configuration = configurationBuilder.build();
		final Twitter twitter = new TwitterFactory(configuration).getInstance();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					twitter.updateStatus(message);
					if (callback != null) {
						callback.getMessage(0, null, null, null);
					}
				} catch (TwitterException e) {
					e.printStackTrace();
					if (callback != null) {
						callback.getMessage(-1, null, null, null);
					}
				}
			}
		}).start();
	}

	public void postMedia(String twitterConsumerKey, String twitterConsumerSecret, String token, String tokenSecret, final String message, final File file, final TwitterCallback callback) {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(twitterConsumerKey);
		configurationBuilder.setOAuthConsumerSecret(twitterConsumerSecret);
		configurationBuilder.setOAuthAccessToken(token);
		configurationBuilder.setOAuthAccessTokenSecret(tokenSecret);
		Configuration configuration = configurationBuilder.build();
		final Twitter twitter = new TwitterFactory(configuration).getInstance();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					StatusUpdate status = new StatusUpdate(message);
					status.setMedia(file);
					twitter.updateStatus(status);

					// twitter.uploadMedia(file);
					if (callback != null) {
						callback.getMessage(0, null, null, null);
					}
				} catch (TwitterException e) {
					e.printStackTrace();
					if (callback != null) {
						callback.getMessage(-1, null, null, null);
					}
				}
			}
		}).start();
	}

	public String getUserName() {
		return userName;
	}

	public String getScreenName() {
		return screenName;
	}

}
