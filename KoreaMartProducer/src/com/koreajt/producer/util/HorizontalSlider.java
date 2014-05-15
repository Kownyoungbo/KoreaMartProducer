package com.koreajt.producer.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

public class HorizontalSlider extends ProgressBar {

	public interface OnProgressChangeListener {
		void onProgressChanged(View v, int progress);
	}
	public HorizontalSlider(Context context, AttributeSet attrs) {
		super(context, attrs, android.R.attr.progressBarStyleHorizontal);
	}
}
