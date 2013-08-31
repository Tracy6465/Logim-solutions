package com.logim.activity;

import com.logim.main.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TutorialsCameraActivity extends Activity {
	
	TextView tutorials_text;
	RelativeLayout layout;
	int click = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorials_text);
		
		tutorials_text = (TextView)findViewById(R.id.tutorials_text);
		tutorials_text.setText(
				getString(R.string.text_tutorials_camera_1));
		
		layout = (RelativeLayout)findViewById(R.id.tutorials_layout);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (click) {
				case 0:
					tutorials_text.setText(
							getString(R.string.text_tutorials_camera_2));
					click++;
					break;
				case 1:
					finish();
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					break;
				}
			}
		});
	}

}
