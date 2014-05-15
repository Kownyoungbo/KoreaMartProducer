package com.koreajt.consumer.gcm;

import java.io.IOException;
import java.net.URI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.koreajt.consumer.KorMainConsumer;

public class RegisterApp extends AsyncTask<Void, Void, String> {
	private static final String TAG = "GCMRelated";
	Context ctx;
	GoogleCloudMessaging gcm;
	String SENDER_ID = "19890708899";
	String regid = null;
	private int appVersion;

	public RegisterApp(Context ctx, GoogleCloudMessaging gcm, int appVersion) {
		this.ctx = ctx;
		this.gcm = gcm;
		this.appVersion = appVersion;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(Void... arg0) {
		String msg = "";
		try {
			if (gcm == null) {
				gcm = GoogleCloudMessaging.getInstance(ctx);
			}
			regid = gcm.register(SENDER_ID);
			msg = "Device registered, registration ID=" + regid;
			sendRegistrationIdToBackend();
			storeRegistrationId(ctx, regid);

		} catch (IOException ex) {
			msg = "Error :" + ex.getMessage();
		}
		return msg;
	}

	private void storeRegistrationId(Context ctx, String regid) {
		final SharedPreferences prefs = ctx
				.getSharedPreferences(
						KorMainConsumer.class.getSimpleName(),
						Context.MODE_PRIVATE);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("registration_id", regid);
		editor.putInt("appVersion", appVersion);
		editor.commit();

	}

	private void sendRegistrationIdToBackend() {
		URI url = null;
		KorMainConsumer.mWebview.loadUrl("javascript:callReg('" + regid + "')");
		
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Log.i("RegisterApp.java",
				"Registration Completed. Now you can see the notifications");
		Log.v(TAG, result);
	}
}
