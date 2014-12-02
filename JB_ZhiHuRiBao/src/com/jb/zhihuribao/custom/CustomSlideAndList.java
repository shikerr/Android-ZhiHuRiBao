package com.jb.zhihuribao.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jb.zhihuribao.R;
import com.jb.zhihuribao.common.NetUtil;

import entity.Latest;
import entity.Story;
import entity.TopStory;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;


/**
 * 自定义幻灯
 * @author Lusifer
 *
 * 2014年12月1日下午2:32:41
 */
public class CustomSlideAndList extends FrameLayout {
	private Context context;
	private ViewPager vpSlide;
	private List<ImageView> imageViews;
	private LinearLayout dotsGroup;
	private TextView txTitle;
	private ListView lvNews;
	private ScrollView svNews;
	
	// 数据相关
	private Latest latest;

	public CustomSlideAndList(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.custom_slide_list, this);
		
		initView();
	}
	
	/**
	 * 初始化控件
	 */
	private void initView() {
		vpSlide = (ViewPager) findViewById(R.id.vp_slide);
		dotsGroup = (LinearLayout) findViewById(R.id.dots_group);
		txTitle = (TextView) findViewById(R.id.tx_title);
		lvNews = (ListView) findViewById(R.id.lv_news);
		svNews = (ScrollView) findViewById(R.id.sv_news);
	}
	
	/**
	 * 初始化数据
	 */
	public void init(String uri) {
		MyAsyncTask task = new MyAsyncTask();
		task.execute(uri);
	}
	
	/**
	 * 异步任务
	 * @author Administrator
	 *
	 */
	class MyAsyncTask extends AsyncTask<String, Void, Latest> implements OnPageChangeListener {
		private int item; // ViewPager的Postion
		private Handler pageChangeHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				vpSlide.setCurrentItem(item);
				if (item == imageViews.size() - 1) {
					item = 0;
				} else {
					item++;
				}
			}
		};
		
		public MyAsyncTask() {
			vpSlide.setOnPageChangeListener(this);
		}

		/**
		 * 在后台执行
		 */
		@Override
		protected Latest doInBackground(String... params) {
			Latest latest = null;
			List<Story> stories = null;
			List<TopStory> topStories = null;
			
			String json = NetUtil.getJson(params[0]);
			
			try {
				if (json != null) {
					JSONObject jsonObject = new JSONObject(json);
					
					// 解析Latest
					latest = new Latest();
					latest.setDate(jsonObject.getString("date"));
					
					// 封装Story
					JSONArray arrayStories = jsonObject.getJSONArray("stories");
					if (arrayStories != null && arrayStories.length() > 0) {
						stories = new ArrayList<Story>();
						for (int i = 0 ; i < arrayStories.length() ; i++) {
							JSONObject obj = arrayStories.getJSONObject(i);
							Story story = new Story();
							story.setGa_prefix(obj.getString("ga_prefix"));
							story.setId(obj.getLong("id"));
							
							// 图片数组
							JSONArray array = obj.getJSONArray("images");
							if (array != null && array.length() > 0) {
								String[] images = new String[array.length()];
								for (int x = 0 ; x < array.length() ; x++) {
									images[x] = array.getString(x);
								}
								story.setImages(images);
							}
							
							story.setShare_url(obj.getString("share_url"));
							story.setTitle(obj.getString("title"));
							story.setType(obj.getInt("type"));
							if (story.getImages() != null && story.getImages().length > 0) {
								story.setBitmap(NetUtil.getBitmap(story.getImages()[0]));
							}
							stories.add(story);
						}
					}
					
					// 封装TopStory
					JSONArray arrayTopStories = jsonObject.getJSONArray("top_stories");
					if (arrayTopStories != null && arrayTopStories.length() > 0) {
						topStories = new ArrayList<TopStory>();
						for (int i = 0 ; i < arrayTopStories.length() ; i++) {
							JSONObject obj = arrayTopStories.getJSONObject(i);
							TopStory topStory = new TopStory();
							topStory.setGa_prefix(obj.getString("ga_prefix"));
							topStory.setId(obj.getLong("id"));
							topStory.setImage(obj.getString("image"));
							topStory.setShare_url(obj.getString("share_url"));
							topStory.setTitle(obj.getString("title"));
							topStory.setType(obj.getInt("type"));
							topStory.setBitmap(NetUtil.getBitmap(topStory.getImage()));
							topStories.add(topStory);
						}
					}
					
					latest.setStories(stories);
					latest.setTopStories(topStories);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return latest;
		}
		
		/**
		 * 在主线程中操作UI
		 */
		@Override
		protected void onPostExecute(Latest result) {
			if (result != null) {
				latest = result;
				
				// 初始化幻灯
				initSlide();
				// 初始化ListView
				initListView();
			}
		}

		/**
		 * 初始化幻灯
		 */
		private void initSlide() {
			// 初始化ImageViews
			imageViews = new ArrayList<ImageView>();
			for (int i = 0 ; i < latest.getTopStories().size() ; i++) {
				TopStory topStory = latest.getTopStories().get(i);
				ImageView imageView = new ImageView(context);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageView.setImageBitmap(topStory.getBitmap());
				imageViews.add(imageView);
			}
			
			// ViewPager赋值
			MyPagerAdapter pagerAdapter = new MyPagerAdapter();
			vpSlide.setAdapter(pagerAdapter);
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					pageChangeHandler.sendEmptyMessage(0);
				}
			}, 3000, 3000);
			
			// 初始化小圆点
			initSmallDot(0);
			
			// 初始化标题
			txTitle.setText(latest.getTopStories().get(0).getTitle());
		}
		
		/**
		 * 初始化小圆点
		 * @param index
		 */
		private void initSmallDot(int index) {
			dotsGroup.removeAllViews();
			
			for (int i = 0 ; i < imageViews.size() ; i++) {
				ImageView imageView = new ImageView(context);
				imageView.setImageResource(R.drawable.dot_default);
				imageView.setPadding(5, 0, 5, 0);
				
				dotsGroup.addView(imageView);
			}
			
			// 设置选中项
			((ImageView)dotsGroup.getChildAt(index)).setImageResource(R.drawable.dot_selected);
		}
		
		/**
		 * 初始化ListView
		 */
		private void initListView() {
			MyBaseAdapter adapter = new MyBaseAdapter();
			lvNews.setAdapter(adapter);
			
			// 将ScrollView置顶
			svNews.smoothScrollTo(0, 0);
		}
		
		// PageChangeListener
		
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			
		}

		@Override
		public void onPageSelected(int position) {
			initSmallDot(position);
			item = position;
			
			txTitle.setText(latest.getTopStories().get(position).getTitle());
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			
		}
		
	}
	
	/**
	 * ViewPager的适配器（幻灯）
	 * @author Lusifer
	 *
	 * 2014年12月1日下午3:36:20
	 */
	class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageViews.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(imageViews.get(position));
			return imageViews.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(imageViews.get(position));
		}
		
	}
	
	/**
	 * ListView
	 * @author Lusifer
	 *
	 * 2014年12月1日下午3:36:13
	 */
	class MyBaseAdapter extends BaseAdapter {
		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			return latest.getStories().size();
		}

		@Override
		public Object getItem(int position) {
			return latest.getStories().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.list_news, parent, false);
				
				viewHolder = new ViewHolder();
				viewHolder.txTitle = (TextView) convertView.findViewById(R.id.tx_title);
				viewHolder.imgThumb = (ImageView) convertView.findViewById(R.id.img_thumb);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			Story story = latest.getStories().get(position);
			viewHolder.txTitle.setText(story.getTitle());
			viewHolder.imgThumb.setImageBitmap(story.getBitmap());
			
			return convertView;
		}
		
		class ViewHolder {
			public TextView txTitle;
			public ImageView imgThumb;
		}
	}
}
