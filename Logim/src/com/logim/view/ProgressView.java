package com.logim.view;

import com.logim.main.R;

import android.app.ProgressDialog;
import android.content.Context;


public class ProgressView extends ProgressDialog {

	public ProgressView(Context context, int theme) {
		super(context, theme);
		init();
	}

	public ProgressView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		setProgressStyle(ProgressDialog.STYLE_SPINNER);
		setMessage(getContext().getString(R.string.dialog_processing));
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		setMax(100);
	}
}
