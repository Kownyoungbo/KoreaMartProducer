package com.koreajt.producer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.koreajt.producer.util.TagValuse;

public class DummyActivity extends Activity {
	
	/**
	 * @user 권영보 140324
	 * 노티피케이션 실행 시 어플 중복으로 보여주는것을 방지하는 더미 액티비티
	 */
	UrlSharedPreference pref = new UrlSharedPreference(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Intent intent;
		intent = getIntent();
//		String str = getString(R.string.mainServerUrl) + intent.getStringExtra("url");
		String str = intent.getStringExtra("url");
		Log.e("pushUrl", str);
		
		intent = new Intent(DummyActivity.this, KorMartProducer.class);
		pref.put(TagValuse.NOTIFIURL, str);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP 
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		startActivity(intent);

		this.finish();
	}
}
