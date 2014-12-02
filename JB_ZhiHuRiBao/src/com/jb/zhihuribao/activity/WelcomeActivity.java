package com.jb.zhihuribao.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;

import com.jb.zhihuribao.R;
import com.jb.zhihuribao.common.NetUtil;
import com.loopj.android.image.SmartImageView;

public class WelcomeActivity extends Activity {
	
	private static final String WELCOME_URL = "http://news-at.zhihu.com/api/3/start-image/480*728";
	private SmartImageView bgImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
		init();
	}

	private void init() {
		bgImage = (SmartImageView) findViewById(R.id.welcome_bg_smart);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String json = NetUtil.getJson(WELCOME_URL);
				try {
					JSONObject jsonObject = new JSONObject(json);
					final String imgUrl = jsonObject.getString("img");
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						
						@Override
						public void run() {
							bgImage.setImageUrl(imgUrl);
							
							// ���嶯�����ӱ����С�Ŵ�1.2���������Ŀ�ʼ����
							Animation animation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
							
							// ��������ʱ��
							animation.setDuration(2000);
							
							// ����������ͣ���ڽ�����λ��
							animation.setFillAfter(true);
							

							// ��Ӷ�������
							animation.setAnimationListener(new AnimationListener() {
								
								@Override
								public void onAnimationStart(Animation animation) {
								}
								
								@Override
								public void onAnimationRepeat(Animation animation) {
								}
								
								@Override
								public void onAnimationEnd(Animation animation) {
									startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
								}
							});
							
							// ��������
							bgImage.startAnimation(animation);
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
