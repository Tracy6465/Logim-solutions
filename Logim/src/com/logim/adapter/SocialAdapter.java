package com.logim.adapter;

import java.util.ArrayList;

import com.logim.main.R;
import com.logim.vo.SocialVo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SocialAdapter extends BaseAdapter {
	
	Context context;
	ArrayList<SocialVo> list;
	
	public SocialAdapter(Context context, ArrayList<SocialVo> list) {
		super();
		
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SocialVo item = list.get(position);
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.item_social, null);
		
		ImageView logo = (ImageView)view.findViewById(R.id.social_logo);
		TextView title = (TextView)view.findViewById(R.id.social_title);
		TextView summary = (TextView)view.findViewById(R.id.social_summary);
		ImageView isused = (ImageView)view.findViewById(R.id.social_isused);
		
		logo.setImageResource(item.getLogo());
		title.setText(item.getTitle());
		summary.setText(item.getSummary());
		
		if (item.getIsused().equals("true")) {
			isused.setVisibility(View.VISIBLE);
		}
		
		return view;
	}

}
