package com.koreajt.producer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

public class TestInputActivity extends Activity {

	Button btnNext;
	ViewFlipper viewFlipper;
	EditText pgge02Input01, pgge02Input02;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_page_01);
		

		btnNext = (Button) findViewById(R.id.page01Next);
		viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
		pgge02Input01 = (EditText)findViewById(R.id.page02Input01);
		pgge02Input02 = (EditText)findViewById(R.id.page02Input02);
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(TestInputActivity.this, R.anim.push_left_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(TestInputActivity.this, R.anim.push_left_out));
		btnNext.setText("다음");

		btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				switch (viewFlipper.getDisplayedChild()) {
				case 0:
					btnNext.setText("다음");
					viewFlipper.showNext();
					break;
				case 1:
					btnNext.setText("다음");
					viewFlipper.showNext();
					break;
				case 2:
					btnNext.setText("상품등록완료");
					viewFlipper.showNext();
					break;
				case 3:
					btnNext.setText("다음");
					viewFlipper.setDisplayedChild(0);
					break;
				default:
					break;
				}
			}
		});

	}
	public String getEditViewVal(EditText editText){
		
		String str = "";
		str = editText.getText().toString();
		
		return str;
	}

}
