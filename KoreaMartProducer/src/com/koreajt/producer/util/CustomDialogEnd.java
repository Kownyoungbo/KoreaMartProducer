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

public class CustomDialogEnd extends Dialog{

	Button mOk, mCancle;
	Context mContext;
	KorMartProducer mKorMainProducer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
		lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lpWindow.dimAmount = 0.8f;
		getWindow().setAttributes(lpWindow);

		setContentView(R.layout.custom_dialog_end);

		setLayout();
		setClickListener(mOkClickListener, mCancleClickListener);
		
	}

	public CustomDialogEnd(Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
	}

	public CustomDialogEnd(Context context, View.OnClickListener OkListener, View.OnClickListener CancleListener) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		this.mOkClickListener = OkListener;
		this.mCancleClickListener = CancleListener;
	}

	private View.OnClickListener mOkClickListener;
	private View.OnClickListener mCancleClickListener;

	private void setLayout() {
		mOk = (Button) findViewById(R.id.endOk);
		mCancle = (Button) findViewById(R.id.endCancle);
		
	}

	private void setClickListener(View.OnClickListener ok, View.OnClickListener cancle) {
		if (mOk != null && mCancle != null) {
			mOk.setOnClickListener(ok);
			mCancle.setOnClickListener(cancle);
		} else {

		}
	}

}
