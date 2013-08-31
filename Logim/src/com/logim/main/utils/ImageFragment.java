package com.logim.main.utils;

import com.logim.main.R;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageFragment extends Fragment {

	// int color = Color.GREEN;
	View view = null;
	ImageView imageview;
	TextView textview;
	int resourceId;
	String textoInicial;
	Bitmap image;

	public ImageFragment(int id, String text) {
		super();
		this.resourceId = id;
		this.textoInicial = text;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.image_layout, null);
		
		imageview = (ImageView) view.findViewById(R.id.fotoInicial);
		imageview.setImageResource(resourceId);
		
		textview = (TextView) view.findViewById(R.id.textoInicial);
		textview.setText(textoInicial);
		
		return view;
	}

}
