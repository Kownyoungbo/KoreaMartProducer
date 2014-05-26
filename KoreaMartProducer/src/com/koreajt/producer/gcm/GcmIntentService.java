package com.koreajt.producer.gcm;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.koreajt.producer.DummyActivity;
import com.koreajt.producer.KorMartPushMessage;
import com.koreajt.producer.R;

/**
 * @author 권영보 140321 GCM 리시버에서 받은 메시지를 처리하는 서비스 차후 수신한 푸시를 외부 서버에 저장 예정
 */
public class GcmIntentService extends IntentService {
	public static final int NOTIFICATION_ID = 1;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);

		// 메시지를 받아서 정상적으로 수신이 되었는지 확인
		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				/*
				 * intnet 안에 있는 String 문자열을 가지고오는 부분 Notice 테그안에 문자열이 들어 있음 형식은
				 * 메시지/Url 형식으로 구성하여 메시지와 Url을 분리후 사용
				 */
				String msg = intent.getStringExtra("Notice");

				sendNotification(msg);
				Log.i("GcmIntentService.java | onHandleIntent", "Received: "
						+ extras.toString());
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// 메시지를 받아서 노티피케이션에 띄워주는 메소드
	private void sendNotification(String msg) {

		String[] msgArray = null;

		msgArray = msgSplit(msg);

		ActivityManager am = (ActivityManager) getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runList = am.getRunningTasks(10);
		ComponentName name = runList.get(0).topActivity;
		String className = name.getClassName();
		boolean isAppRunning = false;
		if (className.contains("com.koreajt.producer")) {
			isAppRunning = true;
		}
		if (isAppRunning == true && isScreenOn(getApplicationContext())) {
			pushSend(msgArray[0], msgArray[1]);
		}else if(isAppRunning == false && isScreenOn(getApplicationContext())){
			pushSend(msgArray[0], msgArray[1]);
		}else {
			pushSend(msgArray[0], msgArray[1]);
			Intent pushMessage = new Intent(GcmIntentService.this,
					KorMartPushMessage.class);
			pushMessage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			pushMessage.putExtra("pushMessage", msgArray[0]);
			pushMessage.putExtra("pushUrl", msgArray[1]);
			startActivity(pushMessage);
		}
		msgArray = null;
	}

	public void pushSend(String msg, String url) {

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		/*
		 * 노티피케이션을 터치 하였을때 해당 화면으로 이동 하는 소스 어플리케이션 중복실행을 막기 위해 더비 맥티비티로 이동
		 */

		Intent intentNotifi = new Intent(getApplicationContext(),
				DummyActivity.class);

		// 인텐트 플레그는 상황에 맞게 수정 요망
		intentNotifi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intentNotifi.putExtra("msg", msg);
		intentNotifi.putExtra("url", url);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intentNotifi, PendingIntent.FLAG_ONE_SHOT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.icon_app_02)
				.setContentTitle(getString(R.string.app_name)).setTicker(msg)
				.setContentText(msg).setAutoCancel(true)
				.setVibrate(new long[] { 0, 1500 });
		Uri alarmSound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mBuilder.setContentIntent(contentIntent);
		mBuilder.setSound(alarmSound);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

	}

	public static boolean isScreenOn(Context context) {
		return ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
				.isScreenOn();
	}

	// 받은 메시지를 나눠주는 메소드
	private String[] msgSplit(String msg) {
		String[] strMsg = null;
		if (msg.indexOf(",") > -1) {
			strMsg = msg.split(",");
		} else {
			msg = msg + "," + getString(R.string.mainUrl);
			strMsg = msg.split(",");
		}

		return strMsg;
	}

}