package com.jb.zhihuribao.activity;

import com.jb.zhihuribao.R;
import com.jb.zhihuribao.custom.CustomSlideAndList;
import com.jb.zhihuribao.custom.CustomTitle;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	private static final String LATEST_URL = "http://news-at.zhihu.com/api/3/stories/latest";
	
	private CustomTitle title;
	private CustomSlideAndList cSlide;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
	}

	private void init() {
		title = (CustomTitle) findViewById(R.id.custom_title);
		title.setTitle("Å£±Æ");
		
		cSlide = (CustomSlideAndList) findViewById(R.id.custom_slide_list);
		cSlide.init(LATEST_URL);
	}
}
