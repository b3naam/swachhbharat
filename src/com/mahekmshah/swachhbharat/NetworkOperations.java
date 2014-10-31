package com.mahekmshah.swachhbharat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class NetworkOperations {
	public final int CONNECTION_TIMEOUT = 30000; // half Minute

	public interface JSONCallback {
		public void getJsonData(byte error, JSONObject data);
	}

	public void getSwachData(String url, JSONCallback callback) {
		new GetSwachData().execute(url, callback);
	}

	public void postSwachData(String url, String payload, JSONCallback callback) {
		new PostSwachData().execute(url, payload, callback);
	}

	private class GetSwachData extends AsyncTask<Object, Integer, Byte> {
		private JSONCallback jsonCallback;
		private HttpClient client;
		private HttpGet mHttpGet = null;
		private String respData;

		@Override
		protected void onPostExecute(Byte result) {
			super.onPostExecute(result);
			try {
				if (jsonCallback != null) {
					if (respData != null) {
						JSONObject jObj = new JSONObject(respData);
						jsonCallback.getJsonData(result.byteValue(), jObj);
					} else {
						jsonCallback.getJsonData(result.byteValue(), null);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected Byte doInBackground(Object... params) {
			String url = (String) params[0];
			jsonCallback = (JSONCallback) params[1];
			try {
				client = new DefaultHttpClient();
				mHttpGet = new HttpGet(url);
				HttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
				HttpConnectionParams.setSoTimeout(httpParameters, CONNECTION_TIMEOUT);

				HttpResponse response = client.execute(mHttpGet);
				respData = getHttpResponceAsString(response);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return -1;
			} catch (IOException e) {
				e.printStackTrace();
				return -2;
			}
			return 0;
		}

	}

	protected class PostSwachData extends AsyncTask<Object, Integer, Byte> {
		private JSONCallback jsonCallback;
		private HttpResponse response = null;
		private String respData;
		private String jsonStr = null;

		@Override
		protected void onPostExecute(Byte result) {
			try {
				if (jsonCallback != null) {
					if (respData != null) {
						JSONObject jObj = new JSONObject(respData);
						jsonCallback.getJsonData(result.byteValue(), jObj);
					} else {
						jsonCallback.getJsonData(result.byteValue(), null);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected Byte doInBackground(Object... params) {
			String url = (String) params[0];
			jsonStr = (String) params[1];
			jsonCallback = (JSONCallback) params[2];
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(url);
				HttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
				HttpConnectionParams.setSoTimeout(httpParameters, CONNECTION_TIMEOUT);

				post.addHeader("Content-Type", "application/json");
				if (jsonStr != null) {
					StringEntity entity = new StringEntity(jsonStr);
					post.setEntity(entity);
				}
				response = client.execute(post);
				respData = getHttpResponceAsString(response);
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
			return 0;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	}

	private String getHttpResponceAsString(HttpResponse response) {
		try {
			if (response != null) {
				InputStream is = response.getEntity().getContent();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				StringBuilder builder = new StringBuilder();
				String inputStr;
				while ((inputStr = br.readLine()) != null) {
					builder.append(inputStr);
				}
				br.close();
				isr.close();
				is.close();
				return builder.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
