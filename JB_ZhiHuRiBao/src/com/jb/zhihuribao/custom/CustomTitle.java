package com.jb.zhihuribao.custom;

import com.jb.zhihuribao.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CustomTitle extends FrameLayout {
	private TextView tvTitle;
	
	public CustomTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater.from(context).inflate(R.layout.custom_title, this);
		init();
	}


	private void init() {
		tvTitle = (TextView) findViewById(R.id.tx_title);
	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}
}
