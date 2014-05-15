package com.koreajt.consumer.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.koreajt.consumer.DummyActivity;
import com.koreajt.consumer.R;

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
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		String[] msgArray = msgSplit(msg);
		
		/* 
		 * 노티피케이션을 터치 하였을때 해당 화면으로 이동 하는 소스
		 * 어플리케이션 중복실행을 막기 위해 더비 맥티비티로 이동
		 */
		Intent intentNotifi = new Intent(getApplicationContext(), DummyActivity.class);
		
		// 인텐트 플레그는 상황에 맞게 수정 요망
		intentNotifi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
                | Intent.FLAG_ACTIVITY_CLEAR_TOP 
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intentNotifi.putExtra("msg", msgArray[0]);
		intentNotifi.putExtra("url", msgArray[1]);

		/*
		 * 차후 요청시 구현
		 * getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_LOCKED //화면 잠겨 있을때 보여주기 
		 * | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //키 잠금 해제하기
		 * | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON //화면 켜기
		 * 
		 */
		
//		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intentNotifi, PendingIntent.FLAG_ONE_SHOT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.icon_app)
				.setContentTitle(getString(R.string.app_name))
				.setStyle(
						new NotificationCompat.BigTextStyle()
								.bigText(msgArray[0]))
				.setContentText(msgArray[0]).setAutoCancel(true)
				.setVibrate(new long[] { 0, 500 });
//		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//		Uri alarmSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.message);
		mBuilder.setContentIntent(contentIntent);
//		mBuilder.setSound(alarmSound);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}

	// 받은 메시지를 나눠주는 메소드
	private String[] msgSplit(String msg) {
		String[] strMsg = null;
		if(msg.indexOf(",") > -1){
			strMsg = msg.split(",");
		}else{
			msg =  msg + "," + getString(R.string.mainUrl);
			strMsg = msg.split(",");
		}
		
		return strMsg;
	}
}