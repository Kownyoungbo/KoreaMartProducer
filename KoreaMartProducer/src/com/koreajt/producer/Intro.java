package com.koreajt.producer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Intro extends Activity {

	private Display display;

	private WindowManager wm;

	private DisplayMetrics metrics;

	int display_width, display_height = 0;

	boolean isWifiAvail, isWifiConn, isMobileAvail, isMobileConn;

	WebView introWebview;

	private String deviceId = "";

	LinearLayout introLayout;

	NetworkInfo ni1;

	NetworkInfo ni2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);

		DefultValues();
		SetData();
		Loading();
	}

	private void DefultValues() {

		wm = getWindowManager();
		display = wm.getDefaultDisplay();
		metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		display_width = display.getWidth();
		display_height = display.getHeight();

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		ni1 = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		ni2 = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		isWifiAvail = ni1.isAvailable();
		isWifiConn = ni1.isConnected();
		isMobileAvail = ni2.isAvailable();
		isMobileConn = ni2.isConnected();
	}

	private void SetData() {
		// prefEditor.putInt("width", display_width);
		// prefEditor.putInt("height", display_height);
		// prefEditor.commit();

		// if (display_width == 720 && display_height == 1280) {
		// introLayout.setBackgroundResource(R.drawable.intro_720_1280);
		// } else {
		// introLayout.setBackgroundResource(R.drawable.intro_800_1280);
		// }
		if (isWifiConn == false && isMobileConn == false) {
			DialogNotInternet();
		}
	}

	private void callActivity() {
		Intent it = new Intent(Intro.this, KorMainProducer.class);
		startActivity(it);
		finish();
	}

	private void Loading() {
		// TODO Auto-generated method stub
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				callActivity();

			}
		};
		handler.sendEmptyMessageDelayed(0, 2000);
	}

	public void onBackPressed() {
		System.exit(0);
	}

	private void DialogNotInternet() {
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setMessage("네트워크가 연결되어 있지 않습니다. \n 어플리케이션을 종료 합니다.")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});
		AlertDialog alert = alt_bld.create();
		alert.show();
	}

}
