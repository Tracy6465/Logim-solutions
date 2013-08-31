package com.logim.activity;

import com.logim.main.R;
import com.logim.view.ProgressView;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.MediaController;
import android.widget.VideoView;

public class TutorialsVideoActivity extends Activity{
	
	String path = "http://s3-us-west-2.amazonaws.com/logimmedia/tutorial.mp4"; 
	static int i = 0;
	
	VideoView videoView;
	ProgressView pDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorials_video);
		
		pDialog = new ProgressView(this);
		pDialog.show();
		
		MediaController controller = new MediaController(this);
        videoView = (VideoView)this.findViewById(R.id.video_view);  
        videoView.setVideoPath(path);  
        videoView.setMediaController(controller);  
        videoView.start();  
        videoView.requestFocus();
        
        videoView.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				pDialog.cancel();
			}
		});
        videoView.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				goBack();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		videoView.seekTo(i);
		videoView.start(); 
	}
	
	@Override
	protected void onStop() { 
        super.onStop(); 
        videoView.pause(); 
        i = videoView.getCurrentPosition(); 
    } 
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK: {
			goBack();
			return true;
		}
		default:
			return super.onKeyUp(keyCode, event);
		}
	}
	
	/**
	 * 
	 * Return to the previous View
	 */
	void goBack() {
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
}
