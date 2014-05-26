package com.koreajt.producer.util;

import com.koreajt.producer.KorMartProducer;
import com.koreajt.producer.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

public class CustomDialogMovie extends Dialog {

	Button mGal, mCam;
	Context mContext;
	KorMartProducer mKorMainProducer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
		lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lpWindow.dimAmount = 0.8f;
		getWindow().setAttributes(lpWindow);

		setContentView(R.layout.custom_dialog_movie);

		setLayout();
		setClickListener(mLeftClickListener, mRightClickListener);
	}

	public CustomDialogMovie(Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
	}

	public CustomDialogMovie(Context context, View.OnClickListener leftListener,
			View.OnClickListener rightListener) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		this.mLeftClickListener = leftListener;
		this.mRightClickListener = rightListener;
	}

	private View.OnClickListener mLeftClickListener;
	private View.OnClickListener mRightClickListener;

	private void setLayout() {
		mGal = (Button) findViewById(R.id.m_gal);
		mCam = (Button) findViewById(R.id.m_cam);
	}

	private void setClickListener(View.OnClickListener left, View.OnClickListener right) {
		if (left != null && right != null) {
			mGal.setOnClickListener(left);
			mCam.setOnClickListener(right);
		} else {

		}
	}

}
