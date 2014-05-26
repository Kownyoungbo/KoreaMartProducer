package com.koreajt.producer.util;

import com.koreajt.producer.KorMartProducer;
import com.koreajt.producer.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialogAlert extends Dialog{

	Button mOk;
	TextView mTitle, mMessage;
	Context mContext;
	String mTitleStr, mMessageStr;
	KorMartProducer mKorMainProducer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
		lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lpWindow.dimAmount = 0.8f;
		getWindow().setAttributes(lpWindow);

		setContentView(R.layout.custom_dialog_alert);

		setLayout();
		setClickListener(mOkClickListener);
		
	}

	public CustomDialogAlert(Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
	}

	public CustomDialogAlert(Context context, View.OnClickListener OkListener, String title, String message) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		this.mOkClickListener = OkListener;
		this.mTitleStr = title;
		this.mMessageStr = message;
	}

	private View.OnClickListener mOkClickListener;

	private void setLayout() {
		mOk = (Button) findViewById(R.id.ok);
		mTitle = (TextView)findViewById(R.id.alertTitle);
		mMessage = (TextView)findViewById(R.id.alertMessage);
		mTitle.setText(mTitleStr);
		mMessage.setText(mMessageStr);
	}

	private void setClickListener(View.OnClickListener ok) {
		if (mOk != null) {
			mOk.setOnClickListener(ok);
			
		} else {

		}
	}

}
