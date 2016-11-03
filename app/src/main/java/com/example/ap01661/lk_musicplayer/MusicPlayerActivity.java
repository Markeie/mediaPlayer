package com.example.ap01661.lk_musicplayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.LoaderManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableString;
import android.text.Spanned;

import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MusicPlayerActivity extends Activity implements CallBack {
	private ImageView mStartAndPause;
	private ImageView mStop;



	private ImageView mMode;
	private SeekBar mSeekBar;
	private ListView mMusicListView;
	private int mListCount = 0;
	private MusicAdapter sAdapter;
	private AdapterListA mAdapterListA;
	private AdapterListA mAdapterListB;
	private AdapterListA mAdapterListC;
	private ImageView playNext;
	private int testCount = 0;

	private TextView mCurrentTime;
	private TextView mDuration;
	private SeekBar mVolumn;
	private AudioManager audioManager;

	private int mCompare = 0;
	private TextView mMusicLrc;
	private TextView mCurrentList;
	private LinearLayout sLrcScroll;
	private SdScannerReceiver mSdScanReceiver = new SdScannerReceiver();
	private MusicService mService;
	private String mMicLrc = "";
	private int mMusicDuration = 0;
	private int mMusicPosition = 0;
	private int mScrollY = 0;
	private int mlinCout = 0;
	private int lline = 0;
	private int mModeCount = 1;
	private int mFlagSeekBar = 1;
	private boolean isLrcExist = false;
	private final static String TAG = "MainActivity";
	private LyricInfo mLyricInfo = new LyricInfo();
	private TextView mContSize;
	private List<Map<String, Object>> mMusicList = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> mListAll = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> mListA = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> mListB = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> mListC = new ArrayList<Map<String, Object>>();
	private ObservableScrollView mMyScroll;
	private int mPosx = 0;
	private int mPosy = 0;
	private int mCurrentx = 0;
	private int mCurrenty = 0;
	private TextView mCurrentSong;
	private TextView mCurrentAlbum;
	private TextView mCurrentSinger;
	private SharedPreferences pref;
	private SharedPreferences pref2;
	private int mSeekProgress = 0;
	private int mFlagLrc = 2;
	private ListView mListVA;
	private ListView mListVB;
	private ListView mListVC;
	ArrayList<View> viewContainter = new ArrayList<View>();
	ArrayList<String> titleContainer = new ArrayList<String>();
	ViewPager pager = null;
	private int SCAN_FLAY = 1;
	PagerTabStrip tabStrip = null;
	private Cursor mMusicCursor;
	private ImageView mWordSize;
	private long mCompare2 = 0;
	private Bitmap gaussianBlurImg;
	private Bitmap gaussianBlurImg1;
	ImageView albumBack;
	SpannableString ss;
	private PopupWindow mTextPop;
	private int mTextY = 0;
	private int mCurrentSize = 25;
	private MyDataBaaseHelper mMusicHelper;
	private boolean mWork=true;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				mFlagSeekBar = 1;
				mFlagLrc = 2;
				System.out.println("ooooooooooooooooooooooooooo");

				break;

			default:
				break;

			}
		}

	};
	private Handler mHandler2 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (mTextPop != null) {
				mTextPop.dismiss();
				mTextPop = null;
			}
		}

	};
	private Handler mHandler3=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			 if(msg.what==1){
				 mWork=true;
			 }
		}
	};
	Comparator<LineInfo> comparator = new Comparator<LineInfo>() {

		@Override
		public int compare(LineInfo lhs, LineInfo rhs) {
			// TODO Auto-generated method stub

			if (lhs.start > rhs.start)
				return 1;
			else
				return -1;

		}
	};

	/**************************************** 异步刷新 ********************************************/
	class MediaScanner extends AsyncTask<Void, Map<String, Object>, Void> {
		String mScanPath = null;

		void getAllList() {
			mMusicHelper = new MyDataBaaseHelper(MusicPlayerActivity.this,
					"MMM.db", null, 1);
			SQLiteDatabase db = mMusicHelper.getWritableDatabase();
			Cursor cursor = db.query("MusicList", null, null, null, null, null,
					null, null);
			if (cursor.moveToFirst()) {

				do {
 					File f=new File(cursor.getString(cursor.getColumnIndex("data")));
 			if(f.exists()&&f!=null){

						Map<String, Object> map = new HashMap<String, Object>();
						map.put("data",
								cursor.getString(cursor.getColumnIndex("data")));
						map.put("artist",
								cursor.getString(cursor.getColumnIndex("artist")));
						map.put("title",
								cursor.getString(cursor.getColumnIndex("title")));
						map.put("album",
								cursor.getString(cursor.getColumnIndex("album")));
						if (map.get("data").toString().substring(0, 24)
								.equals("/storage/sdcard0/Music/A")) {
							map.put("category", "A");

						} else if (map.get("data").toString().substring(0, 24)
								.equals("/storage/sdcard0/Music/B")) {
							map.put("category", "B");

						} else if (map.get("data").toString().substring(0, 24)
								.equals("/storage/sdcard0/Music/C")) {
							map.put("category", "C");

						} else
							map.put("category", "others");
						publishProgress(map);}else{

				db.delete("MusicList", "data=?", new String[]{cursor.getString(cursor.getColumnIndex("data"))});
				
			}

				} while (cursor.moveToNext());

			}
			cursor.close();
			db.close();

		}

		private long getFileSize(File file) throws Exception {
			long size = 0;
			if (file.exists()) {
				FileInputStream fis = null;
				fis = new FileInputStream(file);
				size = fis.available();
			} else {
				file.createNewFile();
				Log.e("获取文件大小", "文件不存在!");
			}
			return size;
		}

		/**
		 * 这样函数要在doInBackground里面调用，否则可能会出现线程错误
		 * 
		 * @param dirPath
		 */
		void scanFile(String dirPath) {
			File sdcard = new File(dirPath);
			File[] fs = sdcard.listFiles();

			if (isCancelled()) {
				Log.d("TAG", "scan cancel. end at " + dirPath);
				return;
			}
			if (fs == null) {
				return;
			}

			/******************************* 看数据库是不是为空 *****************************************************/

			for (File f : fs) {
				if (SCAN_FLAY == 2) {
					Log.d("TAG", "Activity is onDestory stop Scanfile");
					return;
				}
				if (isCancelled()) {
					Log.d("TAG", "scan cancel. end at " + dirPath
							+ " childfile " + f.getPath());
					return;
				}
				if (f.isFile()) {
					if (f.getName().endsWith(".mp3")) {
						Log.d("Tag", f.getName());

						// try {
						// long blockSize = getFileSize(f);
						// System.out.println(blockSize + "--------------");
						// if (blockSize < 100)
						// return;
						// } catch (Exception e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// }

						String aa = f.getName().substring(0,
								f.getName().length() - 4);
						// Log.d("名字", aa+f.getPath());
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("data", f.getPath());
						map.put("title", aa);
						map.put("album", "test");
						map.put("artist", "test");
						if (f.getPath().substring(0, 24)
								.equals("/storage/sdcard0/Music/A")) {
							map.put("category", "A");
						} else if (f.getPath().substring(0, 24)
								.equals("/storage/sdcard0/Music/B")) {
							map.put("category", "B");

						} else if (f.getPath().substring(0, 24)
								.equals("/storage/sdcard0/Music/C")) {
							map.put("category", "C");

						} else
							map.put("category", "others");
						SQLiteDatabase db = mMusicHelper.getWritableDatabase();
						Cursor cursor = db.rawQuery(
								"select * from MusicList where data=\""
										+ map.get("data").toString() + "\";",
								null);

						/********************************** 检测是否存在 *********************/
						if (cursor.getCount() == 0) {
							ContentValues values2 = new ContentValues();
							values2.put("data", map.get("data").toString());
							values2.put("category", map.get("category")
									.toString());
							values2.put("artist", map.get("artist").toString());
							values2.put("title", map.get("title").toString());
							values2.put("album", map.get("album").toString());
							db.insert("MusicList", null, values2);
							values2.clear();
							publishProgress(map);
							Log.v("oooooooooo",
									"&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
						}
						cursor.close();
                          db.close();
						//

					}

				} else if (!f.getName().startsWith(".")) {
					// 不显示隐藏文件夹，即开头为.的文件夹
					// Log.d("TAG", "scanFile d " + f.getPath());
					scanFile(f.getPath());
				}
			}
		}

		public MediaScanner() {
			// TODO Auto-generated constructor stub
		}

		public MediaScanner(String scanPath) {
			// TODO Auto-generated constructor stub
			mScanPath = scanPath;
		}

		@Override
		protected void onProgressUpdate(Map<String, Object>... values) {
			// TODO Auto-generated method stub

			// System.out.println(values[0]);
			mListAll.add(values[0]);
			sAdapter.notifyDataSetChanged();




			if (values[0].get("category").toString().equals("A")) {
				mListA.add(values[0]);

				mAdapterListA.notifyDataSetChanged();

			} else if (values[0].get("category").toString().equals("B")) {
				mListB.add(values[0]);

				mAdapterListB.notifyDataSetChanged();
			} else if (values[0].get("category").toString().equals("C")) {
				mListC.add(values[0]);

				mAdapterListB.notifyDataSetChanged();
			}

		}

		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			getAllList();
			scanFile(mScanPath);
			return null;
		}
	};

	private ServiceConnection mSerCon = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onServiceConnected");
			MusicService.SimpleBinder mBinder = (MusicService.SimpleBinder) service;
			mService = mBinder.getService();
			mService.setCallback(MusicPlayerActivity.this);

			mService.setmListA(mListA);
			mService.setmListB(mListB);
			mService.setmListC(mListC);
			mService.setmListAll(mListAll);
			Intent stopIntent = new Intent(MusicPlayerActivity.this,
					MusicService.class);
			stopIntent.putExtra("control", "stop2");
			startService(stopIntent);
		}
	};

	@Override
	public LoaderManager getLoaderManager() {
		return super.getLoaderManager();
	}
	@Override
	public void setSelectSong() {
		mCurrentSong.setText("please select a song");

		mMusicLrc.setText("");
		isLrcExist=false;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_music_player);
		/***************************** backgroundset ***************************/
		LinearLayout topOfAlbum = (LinearLayout) findViewById(R.id.topOfAlbum);
		topOfAlbum.getBackground().setAlpha(90);
		Bitmap sampleImg = BitmapFactory.decodeResource(getResources(),
				R.mipmap.bgbggbga); // 获取原图
		gaussianBlurImg = blur(sampleImg, 25f);
		Bitmap sampleImg1 = BitmapFactory.decodeResource(getResources(),
				R.mipmap.cvcvvcv);
		gaussianBlurImg1 = blur(sampleImg1, 25f);
		albumBack = (ImageView) findViewById(R.id.albumBack);
		albumBack.setImageBitmap(gaussianBlurImg);

		/************************** DataBase ***************************/

		/******************* 异步线程 *********************/
		new MediaScanner("/storage/sdcard0")
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//		Button querydata = (Button) findViewById(R.id.querydata);
//		querydata.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				SQLiteDatabase db = mMusicHelper.getWritableDatabase();
//				Cursor cursor = db.query("MusicList", null, null, null, null,
//						null, null, null);
//				int count = cursor.getCount();
//				Log.v("MusicPlayerctivity", count + "-----" + "size");
//				if (cursor.moveToFirst()) {
//					do {
//
//						String title = cursor.getString(cursor
//								.getColumnIndex("artist"));
//						String data = cursor.getString(cursor
//								.getColumnIndex("data"));
//
//						Log.v("MusicPlayerctivity", data);
//					} while (cursor.moveToNext());
//
//				}
//				cursor.close();
//			}
//		});
		/***************************** ViewPager ********************************/

		pager = (ViewPager) findViewById(R.id.viewPager);
		tabStrip = (PagerTabStrip) findViewById(R.id.tabstrip);
		tabStrip.setDrawFullUnderline(false);
		tabStrip.setTextColor(getResources().getColor(R.color.orange));
		// tabStrip.setBackgroundColor(getResources().getColor(R.color.orange));
		tabStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);

		tabStrip.setTabIndicatorColor(getResources().getColor(R.color.red));
		tabStrip.setTextSpacing(2);

		View view1 = LayoutInflater.from(MusicPlayerActivity.this).inflate(
				R.layout.view_1, null);
		View view2 = LayoutInflater.from(MusicPlayerActivity.this).inflate(
				R.layout.view_2, null);
		View view3 = LayoutInflater.from(MusicPlayerActivity.this).inflate(
				R.layout.view_3, null);
		View view4 = LayoutInflater.from(MusicPlayerActivity.this).inflate(
				R.layout.view_4, null);
		viewContainter.add(view1);
		viewContainter.add(view2);
		viewContainter.add(view3);
		viewContainter.add(view4);

		mMusicListView = (ListView) view1.findViewById(R.id.list1);

		sAdapter = new MusicAdapter(MusicPlayerActivity.this, mListAll);

		mMusicListView.setAdapter(sAdapter);

		titleContainer.add("All");
		titleContainer.add("A");
		titleContainer.add("B");
		titleContainer.add("C");

		mMusicListView.setOnItemClickListener(new ListViewListener());
		Log.v(TAG, "ListAll" + mListAll.size());
		Log.v(TAG, "listA" + mListA.size());
		Log.v(TAG, "listB" + mListB.size());
		Log.v(TAG, "listC" + mListC.size());

		mListVA = (ListView) view2.findViewById(R.id.list2);
		mAdapterListA = new AdapterListA(MusicPlayerActivity.this);
		mAdapterListA.setmListA(mListA);
		mListVA.setAdapter(mAdapterListA);
		mListVA.setOnItemClickListener(new itemClickListener());

		mListVB = (ListView) view3.findViewById(R.id.list3);
		mAdapterListB = new AdapterListA(MusicPlayerActivity.this);
		mAdapterListB.setmListA(mListB);
		mListVB.setAdapter(mAdapterListB);
		mListVB.setOnItemClickListener(new itemClickListenerB());

		mListVC = (ListView) view4.findViewById(R.id.list4);
		mAdapterListC = new AdapterListA(MusicPlayerActivity.this);
		mAdapterListC.setmListA(mListC);
		mListVC.setAdapter(mAdapterListC);
		mListVC.setOnItemClickListener(new itemClickListenerC());

		pager.setAdapter(new PagerViewAdapter());
		pager.setOnPageChangeListener(new PageListener());
		System.out.println("--------------------------------------");
		Log.v("size of music", "size:" + mListA.size() + "--" + mListB.size()
				+ "--" + mListC.size());
		/******************************************/
		bindService(new Intent(MusicPlayerActivity.this, MusicService.class),
				mSerCon, BIND_IMPORTANT);
		Log.v("MusicPlayerActivity", "startIntent is passing");
		Intent starIntent = new Intent(MusicPlayerActivity.this,
				MusicService.class);
		starIntent.putExtra("control", "aaa");
		startService(starIntent);
		/******* music_state_change ******************************************************/
		mStartAndPause = (ImageView) findViewById(R.id.staAndPau);
		mStop = (ImageView) findViewById(R.id.stop);
		mStartAndPause.setOnClickListener(new MusicChangeListener());
		mStop.setOnClickListener(new MusicChangeListener());
		/******************************** jindutiao ********************************/
		mSeekBar = (SeekBar) findViewById(R.id.seekBar);
		mSeekBar.setOnSeekBarChangeListener(new SeekBarListener());
		mSeekBar.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
if(mListAll.size()==0)
	return true;
				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					Log.d("SeekBar", "down down down");
					mFlagSeekBar = 2;
					mFlagLrc = 1;

					break;
				case MotionEvent.ACTION_MOVE:

					mFlagSeekBar = 2;
					mFlagLrc = 1;
					setLrcScrollBySeekBar();
					Log.d("SeekBar", "move move move");
					break;
				case MotionEvent.ACTION_UP:

					mFlagSeekBar = 2;
					mFlagLrc = 1;
					setLrcScrollBySeekBar();

					break;
				default:
					break;
				}

				return false;
			}
		});
		/**************************** play_modeʽ ***************************************/

		mMode = (ImageView) findViewById(R.id.state);
		mMode.setOnClickListener(new MusicChangeListener());
		/********************************* textsize ***********************************/
		mWordSize = (ImageView) findViewById(R.id.wordSize);
		mWordSize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				getTextPopwindow();
				mTextPop.setWidth(200);
				mTextPop.setHeight(500);

				mTextPop.showAtLocation(v, Gravity.CENTER, 800, 0);
				mHandler2.removeMessages(0);
				mHandler2.sendEmptyMessageDelayed(0, 5000);

			}
		});

		/********************** play_next_previous&&stopService ****************************************/
		// mStopService = (Button) findViewById(R.id.stopService);
		// mStopService.setOnClickListener(new MusicChangeListener());
		playNext = (ImageView) findViewById(R.id.play_next);
		// playFront = (Button) findViewById(R.id.play_front);
		playNext.setOnClickListener(new MusicChangeListener());
		// playFront.setOnClickListener(new MusicChangeListener());
		/************************ mouted-Scanner *****************************************/
		IntentFilter intentfilter = new IntentFilter(
				Intent.ACTION_MEDIA_SCANNER_STARTED);
		intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentfilter.addDataScheme("file");
		registerReceiver(mSdScanReceiver, intentfilter);
		/**************************** refresh *******************************************/


		/****************************** time ************************************/
		mCurrentTime = (TextView) findViewById(R.id.currentTime);
		mCurrentTime.setText("0:00");
		mDuration = (TextView) findViewById(R.id.duration);
		mDuration.setText("0:00");
		/**************************************************************************/
		mVolumn = (SeekBar) findViewById(R.id.volumn);
		// mCurrentVoice = (TextView) findViewById(R.id.currentVoice);

		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		int MaxSound = audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// ֵ
		// mVoiceMax = (TextView) findViewById(R.id.voiceMax);

		// mVoiceMax.setText(String.valueOf(MaxSound));
		mVolumn.setMax(MaxSound);// ֵ
		int currentSount = audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);//
		mVolumn.setProgress(currentSount);// ֵ
		mVolumn.setOnSeekBarChangeListener(new VoiceSeekBarListener());
		/******************************* lyric*scroll~ ********************************/
		mMusicLrc = (TextView) findViewById(R.id.musicLrc);

		sLrcScroll = (LinearLayout) findViewById(R.id.lrcLinear);
		mContSize = (TextView) findViewById(R.id.contSize);
		mContSize.setVisibility(View.GONE);
		/*********************************************************/
		mMyScroll = (ObservableScrollView) findViewById(R.id.MySroll);
		mMyScroll.setCallback(MusicPlayerActivity.this);

		/************************************************/
		mMyScroll.setScrollListener(new ObservableScrollView.ScrollListener() {


			public void scrollOritention(int x, int y, int oldx, int oldy) {
				// TODO Auto-generated method stub
				mCurrentx = x;
				mCurrenty = y;
			 Log.d("000","scroll1:"+x+"***"+y+"***"+oldx+"***"+oldy);

			}
		});
//		mMyScroll.setScrollListener(new ObservableScrollView.ScrollListener() {
//			@Override
//			public void scrollOritention(int l, int t, int oldl, int oldt) {
//				mCurrentx = l;
// 		mCurrenty = t;
//			}
//		});
		/******************** TouchEvent ***********************************/
		mMyScroll.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (isLrcExist == false) {
					// Log.d("此时没有歌词","木有歌词木有歌词");
					return true;
				}

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					mFlagSeekBar = 2;

					break;
				case MotionEvent.ACTION_MOVE:

					mFlagSeekBar = 2;
					break;
				case MotionEvent.ACTION_UP:

					mFlagSeekBar = 2;

					break;

				}
				return false;

			}
		});
		// startService(new Intent(this, MusicService.class));

		// int size=mLyricInfo.song_lines.size();
		// Log.d("size", size+"");
		// sMusicLrc.post(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// Log.d("M1","measureHeight"+sMusicLrc.getMeasuredHeight());
		// Log.d("M2","MeasureState"+sMusicLrc.getMeasuredState());
		// Log.d("M3","MeasureWidth"+sMusicLrc.getMeasuredWidth());
		// Log.d("M4","width"+sMusicLrc.getWidth());
		// Log.d("M5","height"+sMusicLrc.getHeight());
		// Log.d("M7","height"+sMusicLrc.getHeight());
		// Log.d("linecout", ""+sMusicLrc.getLineCount());
		//
		// //LinearLayout.LayoutParams layoutParams =
		// (LinearLayout.LayoutParams)mLyric.getLayoutParams();
		// //Log.d("M8","LINEARlAYOUT"+layoutParams.height+"**"+layoutParams.width);
		// }
		//
		// });

		/***************** set time songname ****************************************/

		// Intent stopIntent = new Intent(MusicPlayerActivity.this,
		// MusicService.class);
		// stopIntent.putExtra("control", "play");
		// startService(stopIntent);
		mCurrentSong = (TextView) findViewById(R.id.CurrentSong);
		mCurrentSong.setSingleLine();
		// mCurrentSong.getBackground().setAlpha(60);
		mCurrentAlbum = (TextView) findViewById(R.id.album);
		mCurrentSinger = (TextView) findViewById(R.id.singer);
		mCurrentList = (TextView) findViewById(R.id.currentList);
		/****************** 调试 适配器为空 **********************/
		// mCurrentSong.setText(sAdapter.getmMusicList().get(0).get("title")
		// .toString());

		/*
		 * pref2 = getSharedPreferences("list", MODE_PRIVATE); mListCount =
		 * pref2.getInt("mode", 0);
		 */
		Log.v("onCreate", "mode" + mListCount);
		String List = "";
		if (mListCount == 0) {
			List = "All";
			mMusicList = mListAll;
		} else if (mListCount == 1) {
			mMusicList = mListA;
			List = "A";
		} else if (mListCount == 2) {
			List = "B";
			mMusicList = mListB;
		} else if (mListCount == 3) {
			mMusicList = mListC;
			List = "C";
		}

		Log.v("the size of List A B C", mListAll.size() + "--" + mListA.size()
				+ "---" + mListB.size());

	}

	private class itemClickListenerC implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			if(mWork) {
				File file = new File(mListC.get(position).get("data").toString());
				if (file != null && file.exists()) {
					Intent listIntent = new Intent(MusicPlayerActivity.this,
							MusicService.class);
					listIntent.putExtra("control", "listClickC");
					mMusicList = mListC;
					mFlagSeekBar=1;
					mListCount = 3;
					System.out.println("position" + "-------" + position);
					listIntent.putExtra("path", position);
					startService(listIntent);
				} else {
					SQLiteDatabase db = mMusicHelper.getWritableDatabase();
					String pp = mListC.get(position).get("data").toString();
					db.delete("MusicList", "data=?", new String[]{pp});
					db.close();
					mListC.remove(position);
					mService.setmListC(mListC);
					mAdapterListC.notifyDataSetChanged();

					Toast.makeText(MusicPlayerActivity.this, "歌曲不存在或已经被删除",
							Toast.LENGTH_LONG).show();
				}
			}
			mWork=false;
			mHandler.removeMessages(0);
			mHandler.sendEmptyMessageDelayed(0, 300);

		}

	}

	private void getTextPopwindow() {

		if (mTextPop != null) {
			mTextPop.dismiss();
			mTextPop = null;
		} else {
			initTextPopWindow();

		}
	}

	private class itemClickListenerB implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			if(mWork){
			File file = new File(mListB.get(position).get("data").toString());
			if (file != null && file.exists()) {
				mFlagSeekBar=1;
				Intent listIntent = new Intent(MusicPlayerActivity.this,
						MusicService.class);
				listIntent.putExtra("control", "listClickB");
				mMusicList = mListB;
				System.out.println("position" + "-------" + position);
				listIntent.putExtra("path", position);
				mListCount = 2;
				startService(listIntent);
			} else {

				SQLiteDatabase db = mMusicHelper.getWritableDatabase();
				String pp = mListB.get(position).get("data").toString();
				db.delete("MusicList", "data=?", new String[]{pp});
				db.close();
				mListB.remove(position);
				mService.setmListB(mListB);
				mAdapterListB.notifyDataSetChanged();

				Toast.makeText(MusicPlayerActivity.this, "歌曲不存在或已经被删除",
						Toast.LENGTH_LONG).show();
			}
			}
			mWork=false;
			mHandler3.removeMessages(1);
			mHandler3.sendEmptyMessageDelayed(1,300);


		}

	}

	private Bitmap blur(Bitmap bitmap, float radius) {
		Bitmap output = Bitmap.createBitmap(bitmap); // 创建输出图片
		RenderScript rs = RenderScript.create(this); // 构建一个RenderScript对象
		ScriptIntrinsicBlur gaussianBlue = ScriptIntrinsicBlur.create(rs,
				Element.U8_4(rs)); // 创建高斯模糊脚本
		Allocation allIn = Allocation.createFromBitmap(rs, bitmap); // 创建用于输入的脚本类型
		Allocation allOut = Allocation.createFromBitmap(rs, output); // 创建用于输出的脚本类型
		gaussianBlue.setRadius(radius); // 设置模糊半径，范围0f<radius<=25f
		gaussianBlue.setInput(allIn); // 设置输入脚本类型
		gaussianBlue.forEach(allOut); // 执行高斯模糊算法，并将结果填入输出脚本类型中
		allOut.copyTo(output); // 将输出内存编码为Bitmap，图片大小必须注意
		rs.destroy(); // 关闭RenderScript对象，API>=23则使用rs.releaseAllContexts()
		return output;
	}

	private class itemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			if(mWork){
			File file = new File(mListA.get(position).get("data").toString());
			if (file != null && file.exists()) {
				Intent listIntent = new Intent(MusicPlayerActivity.this,
						MusicService.class);
				listIntent.putExtra("control", "listClickA");
				mMusicList = mListA;
				mFlagSeekBar=1;
				mListCount = 1;
				System.out.println("position" + "-------" + position);
				listIntent.putExtra("path", position);
				startService(listIntent);
			} else {

				SQLiteDatabase db = mMusicHelper.getWritableDatabase();
				String pp = mListA.get(position).get("data").toString();
				db.delete("MusicList", "data=?", new String[]{pp});
				db.close();
				mListA.remove(position);
				mService.setmListA(mListA);
				mAdapterListA.notifyDataSetChanged();

				Toast.makeText(MusicPlayerActivity.this, "歌曲不存在或已经被删除",
						Toast.LENGTH_LONG).show();
			}
			}mWork=false;
			mHandler3.removeMessages(1);
			mHandler3.sendEmptyMessageDelayed(1,300);
		}

	}

	private class PageListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			Log.d(TAG, "-------CHANGER" + arg0);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			// Log.d(TAG, "-------scrolled arg0:" + arg0);
			// Log.d(TAG, "-------scrolled arg1:" + arg1);
			// Log.d(TAG, "-------scrolled arg2:" + arg2);

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			// Log.d(TAG, "------selected:" + arg0);
		}

	}

	private class PagerViewAdapter extends PagerAdapter {

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			((ViewPager) container).removeView(viewContainter.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			((ViewPager) container).addView(viewContainter.get(position));
			Log.d(TAG, "instantiateItem ----------------- " + position);
			return viewContainter.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return viewContainter.size();
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			return titleContainer.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

	}

	private class VoiceSeekBarListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if (fromUser) {
				int SeekPosition = seekBar.getProgress();
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						SeekPosition, 0);
			}
			// mCurrentVoice.setText(String.valueOf(progress));

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	protected void onStop() {
		Log.d("on Stop","================");
		super.onStop();
	}

	@Override
	protected void onRestart() {
		Log.d("on Restart","---------------------------");


		mListA=mService.getmListA();
		mListAll=mService.getmListAll();
		mListB=mService.getmListB();
		mListC=mService.getmListC();
		mMusicList=mService.getmMusicList();
		sAdapter.notifyDataSetChanged();
		mAdapterListA.notifyDataSetChanged();
		mAdapterListB.notifyDataSetChanged();
		mAdapterListC.notifyDataSetChanged();


		super.onRestart();
	}

	@Override
	protected void onPause() {
		Intent intent = new Intent("com.likego.broadcast.MUSIC_DESTORY");
		sendBroadcast(intent);
		saveSongInfo();
		SCAN_FLAY = 2;

		//mService.setCallback(null);

			Log.d("on pause","---------------------------");
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		saveSongInfo();
		SCAN_FLAY = 2;
		unregisterReceiver(mSdScanReceiver);
		mService.setCallback(null);
		unbindService(mSerCon);
		Log.d("MainActivity", "onDestory");
		Intent intent = new Intent("com.likego.broadcast.MUSIC_DESTORY");
		sendBroadcast(intent);

		super.onDestroy();
	}

	void saveSongInfo() {
		/********************** SONG INFO ****************/
		if(mListAll.size()==0){
			return;
		}
		SharedPreferences.Editor editor = getSharedPreferences("data",
				MODE_PRIVATE).edit();

		editor.putString("title", mMusicList.get(mService.getmMusicIndex())
				.get("title").toString());
		editor.putInt("index", mService.getmMusicIndex());
		editor.commit();
		/****************** LIST *********************************/
		SharedPreferences.Editor editor2 = getSharedPreferences("list",
				MODE_PRIVATE).edit();
		editor2.putInt("mode", mListCount);
		editor2.commit();

	}

	private void initTextPopWindow() {
		View textPop = getLayoutInflater().inflate(
				R.layout.text_size_set_popwindow, null);

		mTextPop = new PopupWindow(textPop, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		mTextPop.setAnimationStyle(R.style.AnimationFade);
		ImageView add = (ImageView) textPop.findViewById(R.id.add);
		ImageView reduce = (ImageView) textPop.findViewById(R.id.reduce);

		final TextView size = (TextView) textPop.findViewById(R.id.size);
		size.setText(mCurrentSize + "");
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCurrentSize = mCurrentSize + 1;

				size.setText(mCurrentSize + "");
				mMusicLrc
						.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mCurrentSize);
				mContSize
						.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mCurrentSize);
				if(mListAll.size()!=0){
				String path2 = mService.getmMusicList()
						.get(mService.getmMusicIndex()).get("data").toString();
				String lrcPath = path2.substring(0, path2.length() - 4)
						+ ".lrc";
				Log.d("musiclist", mMusicList + "");
				getLrc(lrcPath);}
			}
		});
		reduce.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCurrentSize = mCurrentSize - 1;
				size.setText(mCurrentSize + "");
				mMusicLrc
						.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mCurrentSize);
				mContSize
						.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mCurrentSize);
				if(mListAll.size()!=0){
				String path2 = mService.getmMusicList()
						.get(mService.getmMusicIndex()).get("data").toString();
				String lrcPath = path2.substring(0, path2.length() - 4)
						+ ".lrc";

				getLrc(lrcPath);}
			}
		});

		textPop.setOnTouchListener(new TextPopWinTouchListener());

	}

	private class TextPopWinTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (mTextPop != null & mTextPop.isShowing()) {
				mTextPop.dismiss();
				mTextPop = null;

			}

			return false;
		}

	}

	public class MusicAdapter extends BaseAdapter {

		private List<Map<String, Object>> mListAll = new ArrayList<Map<String, Object>>();

		public Context context;
		private int selectItem = -1;
		private int mUnExist = -1;

		public List<Map<String, Object>> getmListA() {
			return mListA;
		}

		public int getSelectItem() {
			return selectItem;
		}

		public void setSelectItem(int selectItem) {
			this.selectItem = selectItem;
		}

		public MusicAdapter(Context context,
				List<Map<String, Object>> mMusicList) {
			this.context = context;
			this.mListAll = mMusicList;

		}

		public int getmUnExist() {
			return mUnExist;
		}

		public void setmUnExist(int mUnExist) {
			this.mUnExist = mUnExist;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mListAll.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mListAll.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int p, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			int position = p;

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.music_item, null);
				holder.selected = (ImageView) convertView
						.findViewById(R.id.selected);
				holder.artist = (TextView) convertView
						.findViewById(R.id.artist);
				holder.album = (TextView) convertView.findViewById(R.id.album);
				holder.Title = (TextView) convertView.findViewById(R.id.title);
				convertView.setTag(holder);
			} else {

				holder = (ViewHolder) convertView.getTag();

			}

			holder.album
					.setText(mListAll.get(position).get("album").toString());
			holder.Title
					.setText(mListAll.get(position).get("title").toString());
			holder.artist.setText(mListAll.get(position).get("artist")
					.toString());

			if (position == selectItem) {
				// System.out.println("**************item is selected*****************");
				holder.selected.setImageResource(R.mipmap.ac8);
			} else
				holder.selected.setImageResource(R.color.pure);

			if (position % 2 == 0) {

				// convertView.setBackgroundDrawable
				// (context.getResources().getDrawable(R.drawable.popup_backgroup));
				convertView.setBackgroundColor(context.getResources().getColor(
						R.color.list));
				convertView.getBackground().setAlpha(70);
			} else
				convertView.setBackgroundColor(context.getResources().getColor(
						R.color.pure));
			return convertView;
		}

		private class ViewHolder {
			TextView Title;
			TextView artist;
			TextView album;
			ImageView selected;
		}

	}

	private class SdScannerReceiver extends BroadcastReceiver {
		private AlertDialog.Builder builder;
		private AlertDialog ad;

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			String action = intent.getAction();
			if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)) {
				builder = new AlertDialog.Builder(context);
				builder.setMessage("Scansdcard...");
				ad = builder.create();
				ad.show();

			} else if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
				ad.cancel();
				Toast.makeText(context, "Scan_over", Toast.LENGTH_SHORT).show();

				sAdapter = new MusicAdapter(MusicPlayerActivity.this, mListAll);

				mMusicListView.setAdapter(sAdapter);
				sAdapter.notifyDataSetChanged();

				mService.setmListA(mListA);
				mService.setmListB(mListB);
				mService.setmListC(mListC);
				mService.setmListAll(mListAll);
				mAdapterListA.notifyDataSetChanged();
				mAdapterListB.notifyDataSetChanged();
				mAdapterListC.notifyDataSetChanged();

			}

		}

	}

	private class MusicChangeListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(mListAll.size()==0){
				return;
			}

			if(mWork) {
				Log.d("点击", "---------------------------------");
				if (v.equals(mStop)) {
					Intent stopIntent = new Intent(MusicPlayerActivity.this,
							MusicService.class);
					stopIntent.putExtra("control", "stop");
					mCurrentTime.setText("0:00");

					mFlagSeekBar=2;
					mMyScroll.scrollTo(0,0);
					Log.d("stop","scroll 0--------------------------------------------------------------------------------");
					mSeekBar.setProgress(0);
					startService(stopIntent);
				} else if (v.equals(mStartAndPause)) {
					Intent starIntent = new Intent(MusicPlayerActivity.this,
							MusicService.class);
					starIntent.putExtra("control", "play");
					startService(starIntent);
					mFlagSeekBar=1;
				} else if (v.equals(playNext)) {
					Intent starIntent = new Intent(MusicPlayerActivity.this,
							MusicService.class);
					starIntent.putExtra("control", "playNext");
					startService(starIntent);
					mFlagSeekBar=1;
				} else if (v.equals(mMode)) {

					Intent modeIntent = new Intent(MusicPlayerActivity.this,
							MusicService.class);
					mModeCount = mModeCount + 1;
					if (mModeCount > 3)
						mModeCount = 1;
					if (mModeCount == 1) {

						mMode.setImageResource(R.mipmap.cir);
						modeIntent.putExtra("control", "Mode1");
					} else if (mModeCount == 2) {

						mMode.setImageResource(R.mipmap.random);
						modeIntent.putExtra("control", "Mode2");
					} else if (mModeCount == 3) {
						mMode.setImageResource(R.mipmap.sf);
						modeIntent.putExtra("control", "Mode3");
					}

					startService(modeIntent);

				}
			}
			mWork=false;
			mHandler3.removeMessages(1);
			mHandler3.sendEmptyMessageDelayed(1,300);

		}

	}

	private class SeekBarListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			// mFlagSeekBar=2;
			// Log.d("TTTT", "onProgressChanged " + progress);
if(mService==null)
	return;
			mSeekProgress = mSeekBar.getProgress();

				int time1 = mService.getServiceDuration();
			int max1 = mSeekBar.getMax();
			// Log.d("maxxx", "" + max1);
			// mService.setSeekTo(time1, dest1, max1);
			setCurrentPosotion(time1, mSeekProgress, max1);
			// Log.d("SeekBarListener", "onProgressChanged");
			// int size = mLyricInfo.song_lines.size();
			// for (int i = 0; i < size; i++) {
			// LineInfo lineInfo = mLyricInfo.song_lines.get(i);
			// if (lineInfo != null && lineInfo.start >
			// mService.getCurrentPosition()) {
			// mScrollY = lineInfo.cout;
			// break;
			// }

			// }
			// mMyScroll.scrollTo(0, mScrollY * 29);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			mFlagSeekBar = 2;

			Log.v("SeekBarListener",
					"-------onStartTrackingTouch-----------------");
			// Log.d("TTTT", "onStartTrackingTouch " + seekBar.getProgress());
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			Log.d("SeekBarListener", "onStopTrackingTouch" + "此时应为2："
					+ mFlagSeekBar);
			int dest1 = mSeekBar.getProgress();
			int time1 = mService.getServiceDuration();
			int max1 = mSeekBar.getMax();
			// Log.d("maxxx", "" + max1);
			mService.setSeekTo(time1, dest1, max1);
			setCurrentPosotion(time1, dest1, max1);

			mHandler.removeMessages(0);
			mHandler.sendEmptyMessageDelayed(0, 300);
			// Log.d("SeekBarListener", "onStopTrackingTouch" + "此时应为2："
			// + mFlagSeekBar);

		}

	}

	// public float getTextWidth(Context Context, String text, int textSize){
	// TextPaint paint = new TextPaint();
	// float scaledDensity =
	// Context.getResource().getDisplayMetrics().scaledDensity;
	// paint.setTextSize(scaledDensity * textSize);
	// return paint.measureText(text);
	// }

	private class ListViewListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.v("itemclick", "刚刚进入还没开始判断");
			// TODO Auto-generated method stub

  if(mWork) {

//	  testCount = testCount + 1;
//	  if (testCount % 2 == 1) {
//		  albumBack.setImageBitmap(gaussianBlurImg);
//
//	  } else if (testCount % 2 == 0) {
//		  albumBack.setImageBitmap(gaussianBlurImg1);
//	  }
//	  if (testCount == 20)
//		  testCount = 0;

	  File file = new File(mListAll.get(position).get("data").toString());
	  if (file != null && file.exists()) {
		  Intent listIntent = new Intent(MusicPlayerActivity.this,
				  MusicService.class);
		  listIntent.putExtra("control", "listClick");
		  listIntent.putExtra("path", position);
		  mMusicList = mListAll;
          mFlagSeekBar=1;
		  System.out.println("***********" + sAdapter.getSelectItem()
				  + "*********");

		  mListCount = 0;
		  String path = mListAll.get(position).get("data").toString();
		  String lrcPath = path.substring(0, path.length() - 4) + ".lrc";
		  getLrc(lrcPath);
		  startService(listIntent);
	  } else {
		  SQLiteDatabase db = mMusicHelper.getWritableDatabase();
		  String pp = mListAll.get(position).get("data").toString();

		  db.delete("MusicList", "data=?", new String[]{pp});
		  db.close();
		  mListAll.remove(position);
		  mService.setmListAll(mListAll);
		  sAdapter.notifyDataSetChanged();

		  Toast.makeText(MusicPlayerActivity.this, "歌曲不存在****已经被删除",
				  Toast.LENGTH_SHORT).show();
	  }
  }
  mWork=false;
			mHandler3.removeMessages(1);
			mHandler3.sendEmptyMessageDelayed(1,300);

		}

	}

	class LyricInfo {
		List<LineInfo> song_lines;
		String song_artist;
		String song_title;
		String song_album;
		long song_offset;
	}

	class LineInfo {
		String content;
		long start;
		int cout;
		int length;
		int songIndex;

	}

	void setSongName() {
if(mService==null)
	return;
		String currentSong = mService.getmMusicList()
				.get(mService.getmMusicIndex()).get("title").toString();
		mCurrentSong.setText(currentSong);

		String currentAlbum = mService.getmMusicList()
				.get(mService.getmMusicIndex()).get("album").toString();
		String currentSinger = mService.getmMusicList()
				.get(mService.getmMusicIndex()).get("artist").toString();
		mCurrentAlbum.setText("album:" + currentAlbum);
		Log.d("singer", currentSinger);
		mCurrentSinger.setText("singer:" + currentSinger);
		int mode = mService.getmModeFlag();
		if (mode == 0) {
			mMode.setImageResource(R.mipmap.dd);
		}
		if (mode == 1) {
			mMode.setImageResource(R.mipmap.cir);
		}
		if (mode == 2) {
			mMode.setImageResource(R.mipmap.random);
		}
		if (mode == 3) {
			mMode.setImageResource(R.mipmap.sf);
		}

		String List = "";
		if (mListCount == 0) {
			List = "All";

		} else if (mListCount == 1) {

			List = "A";
		} else if (mListCount == 2) {
			List = "B";

		} else if (mListCount == 3) {

			List = "C";
		}
		mCurrentList.setText("list:" + List);
	}

	public void getLrc(String lrcPath) {
		lline = 0;
		mlinCout = 0;
		setSongName();

		// File file=new File("/sdcard/Space Bound.lrc");
		File file = new File(lrcPath);

		if (file != null && file.exists()) {
			try {
				isLrcExist = true;
				StringBuffer stringBuffer = new StringBuffer();
				setupLyricResource(new FileInputStream(file), "UTF-8");

				Collections.sort(mLyricInfo.song_lines, comparator);

				int ccount = 0;
				int clength = 0;
				int lengthAdd = 0;
				if (mLyricInfo != null && mLyricInfo.song_lines != null) {
					int size2 = mLyricInfo.song_lines.size();
					// Log.d("song_line", size+"");

					for (int i = 0; i < size2; i++) {

						String text = mLyricInfo.song_lines.get(i).content;
						/**** length **/
						int songLength = text.length();
						mLyricInfo.song_lines.get(i).length = songLength;
						mContSize.setText(text);

						clength = clength + songLength;
						mLyricInfo.song_lines.get(i).songIndex = lengthAdd
								+ clength;

						lengthAdd = lengthAdd + 1;

						int spec = MeasureSpec.makeMeasureSpec(0,
								MeasureSpec.UNSPECIFIED);
						mContSize.measure(spec, spec);
						int measuredWidth = mContSize.getMeasuredWidth();

						mTextY = mContSize.getMeasuredHeight() - 6;
						if (measuredWidth > 442) {
							ccount = (measuredWidth) / 442 + 1;
						} else
							ccount = 1;
						mlinCout = mlinCout + ccount;
						mLyricInfo.song_lines.get(i).cout = mlinCout;
//						Log.v("count ", mlinCout + "------------------");
						stringBuffer
								.append(mLyricInfo.song_lines.get(i).content
										+ "\n");

						//
					}
					mMicLrc = stringBuffer.toString();
					ss = new SpannableString(mMicLrc);
					mMusicLrc.setText(stringBuffer.toString());


					System.out.println("songline" + mMusicLrc.getLineCount());

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			mMusicLrc.setText(" ");
			isLrcExist = false;
			sLrcScroll.setScrollY(0);
		}

	}

	private void setupLyricResource(InputStream inputStream, String charsetName) {
		if (inputStream != null) {

			try {

				mLyricInfo.song_lines = new ArrayList<LineInfo>();
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream, charsetName);
				BufferedReader reader = new BufferedReader(inputStreamReader);
				String line = null;
				while (((line = reader.readLine()) != null)) {
					// System.out.println("歌词解析line:" + line);
					analyzeLyric(mLyricInfo, line);

				}
				reader.close();
				inputStream.close();
				inputStreamReader.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void analyzeLyric(LyricInfo lyricInfo, String line) {
		int index = line.lastIndexOf("]");
		if (line != null && line.startsWith("[offset:")) {
			String string = line.substring(8, index).trim();
			lyricInfo.song_offset = Long.parseLong(string);
			return;
		}
		if (line != null && line.startsWith("[ti:")) {
			String string = line.substring(4, index).trim();
			lyricInfo.song_title = string;
			return;
		}
		if (line != null && line.startsWith("[ar:")) {
			String string = line.substring(4, index).trim();
			lyricInfo.song_artist = string;
			return;

		}
		if (line != null && line.startsWith("[al:")) {
			String string = line.substring(4, index).trim();
			lyricInfo.song_album = string;
			return;

		}
		if (line != null && line.startsWith("[by:")) {
			return;

		}
		/**
		 * 以上非歌词时间部分必须返回，否则若Index刚好等于9，19，将会导致解析出错，转化时分秒的会出错
		 */
		if (line != null && (index == 9 || index == 19 || index == 29)
				&& line.trim().length() > 10) {

			if (index == 9) {

				LineInfo lineInfo = new LineInfo();
				lineInfo.content = line.substring(10, line.length());
				lineInfo.start = measureStartTimeMillis(line.substring(0, 10));
				lyricInfo.song_lines.add(lineInfo);

			} else if (index == 19) {
				Log.v("In", "index==19");

				LineInfo lineInfo1 = new LineInfo();
				lineInfo1.content = line.substring(20, line.length());
				lineInfo1.start = measureStartTimeMillis(line.substring(0, 10));
				lyricInfo.song_lines.add(lineInfo1);

				LineInfo lineInfo2 = new LineInfo();
				lineInfo2.content = line.substring(20, line.length());
				lineInfo2.start = measureStartTimeMillis(line.substring(10, 20));
				lyricInfo.song_lines.add(lineInfo2);

			} else if (index == 29) {
				Log.v("29", "*****************----********************");
				LineInfo lineInfo1 = new LineInfo();
				lineInfo1.content = line.substring(30, line.length());
				lineInfo1.start = measureStartTimeMillis(line.substring(0, 10));
				lyricInfo.song_lines.add(lineInfo1);

				LineInfo lineInfo2 = new LineInfo();
				lineInfo2.content = line.substring(30, line.length());
				lineInfo2.start = measureStartTimeMillis(line.substring(10, 20));
				lyricInfo.song_lines.add(lineInfo2);

				LineInfo lineInfo3 = new LineInfo();
				lineInfo3.content = line.substring(30, line.length());
				lineInfo3.start = measureStartTimeMillis(line.substring(20, 30));
				lyricInfo.song_lines.add(lineInfo3);

			}

		}

	}

	private long measureStartTimeMillis(String str) {
		long minute = Long.parseLong(str.substring(1, 3));

		long second = Long.parseLong(str.substring(4, 6));

		long millisecond = Long.parseLong(str.substring(7, 9));
		return millisecond + second * 1000 + minute * 60 * 1000;

	}



	public void play() {
		// TODO Auto-generated method stub
		System.out.println("pal");
	}

	void setCurrentPosotion(int time, int dest, int max) {
		int position = time * dest / max;
		int fen = position / 1000 / 60;
		int miao = position / 1000 % 60;
		// Log.d("Seek", position+"");
		if (miao < 10) {
			mCurrentTime.setText(fen + ":" + "0" + miao);
		} else {
			mCurrentTime.setText(fen + ":" + miao);
		}

	}

	@Override
	public void SetSeekBar(int duration, int position) {
		// TODO Auto-generated method stub

		mMusicDuration = duration;
		mMusicPosition = position;
		if (mFlagSeekBar == 1 && mFlagLrc == 2) {
			int max = mSeekBar.getMax();
			mSeekBar.setProgress(mMusicPosition * max / mMusicDuration);
			int fen = mMusicPosition / 1000 / 60;
			int miao = mMusicPosition / 1000 % 60;

			if (miao < 10) {
				mCurrentTime.setText(fen + ":" + "0" + miao);
			} else {
				mCurrentTime.setText(fen + ":" + miao);
			}

			int micMin = duration / 1000 / 60;
			int micSec = duration / 1000 % 60;

			if (micSec < 10) {
				mDuration.setText(micMin + ":" + "0" + micSec);
			} else {
				mDuration.setText(micMin + ":" + micSec);

			}
		}
	}

	@Override
	public void setLrcPosition() {

		System.out.println("999999999999999999999999999999999999999999999999999999999999999999999999999");
//		for(int x=0;x<10;x++) {
 	//mMyScroll.scrollTo(0,0);
//		}
	}


	@Override
	public void setLyric(String lrc) {
		// TODO Auto-generated method stub
		getLrc(lrc);

	}

	@Override
	public void setDuration(int duration) {
		// TODO Auto-generated method stub
		int micMin = duration / 1000 / 60;
		int micSec = duration / 1000 % 60;

		if (micSec < 10) {
			mDuration.setText(micMin + ":" + "0" + micSec);
		} else {
			mDuration.setText(micMin + ":" + micSec);

		}

	}

	void setLrcScrollBySeekBar() {
		/*************** zhushi ******************/
if(mFlagSeekBar==1)
	return;
		int position = mSeekProgress * mService.getServiceDuration()
				/ mSeekBar.getMax();

		if (isLrcExist) {

			int size = mLyricInfo.song_lines.size();

			if(position<mLyricInfo.song_lines.get(0).start){
				mMyScroll.scrollTo(0,0);
				Log.d("setLrcScrollBySeekBar","小于第一句 返回-----------------------------------------------------------------------------");
				return;

			}
			if (position > mLyricInfo.song_lines.get(size - 1).start) {
				mScrollY = mLyricInfo.song_lines.get(size - 1).cout;
				mMyScroll.scrollTo(0, mScrollY * mTextY);
				Log.d("setLrcScrollBySeekBar","0p00000000000---------------------------------------------------------------------------------");
				return;

			}
			for (int i = 0; i < size + 1; i++) {

				LineInfo lineInfo = mLyricInfo.song_lines.get(i);
				if (lineInfo != null && lineInfo.start > position) {
					mScrollY = lineInfo.cout;
					break;
				}
			}
			mMyScroll.scrollTo(0, mScrollY * mTextY);
			Log.d("setLrcScrollBySeekBar","scroll to"+ mScrollY * mTextY+"---------------------------------------------------------------");
		}//mMyScroll.scrollTo(0, mScrollY *


	}

	@Override
	public void setLyric(int position) {
		// TODO Auto-generated method stub
		/*************** zhushi ******************/

		if (isLrcExist && (mFlagSeekBar == 1)) {

			int size = mLyricInfo.song_lines.size();

			if (position >= mLyricInfo.song_lines.get(size - 1).start) {
				mScrollY = mLyricInfo.song_lines.get(size - 1).cout;

				if (mCompare2 != mLyricInfo.song_lines.get(size - 1).start) {
                     mCompare2=mLyricInfo.song_lines.get(size-1).start;
					LineInfo lineInfo2 = mLyricInfo.song_lines.get(size - 1);
                             
					int yy = lineInfo2.length;
					int pp = lineInfo2.songIndex;
					mMyScroll.scrollTo(0, mScrollY * mTextY);
					Log.d("sssss","posiyion");
					Log.d("setLyric(int position)"," mScrollY * mTextY"+ mScrollY * mTextY);
					SpannableString kk = new SpannableString(mMicLrc);
					kk.setSpan(new ForegroundColorSpan(Color.RED), pp - yy, pp,
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					mMusicLrc.setText(kk);
					System.out.println("shehzidhhflsdflskdjflsjflksdjflkdsjflkdsjflsdkfjlksd");

				}

				return;

			}
			int yy = 0;
			int pp = 0;
			int flag = 1;
			int uu = 0;
			for (int i = 1; i < size; i++) {
				LineInfo lineInfo = mLyricInfo.song_lines.get(i);
				if (flag == 1
						&& lineInfo != null
						&&  lineInfo.start > position ) {
					
 

					LineInfo lineInfo2 = mLyricInfo.song_lines.get(i-1);

					mScrollY = lineInfo.cout;
					yy = lineInfo2.length;
					pp = lineInfo2.songIndex;

					if (mCompare != pp) {
						mMyScroll.scrollTo(0, mScrollY * mTextY);
						Log.d("sssss","posiyion2222222222222222222222222");
						Log.d("setLyric(int position)2"," mScrollY * mTextY"+ mScrollY +"----------------------------"+ mTextY);
						SpannableString kk = new SpannableString(mMicLrc);
						kk.setSpan(new ForegroundColorSpan(Color.RED), pp - yy,
								pp, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						mMusicLrc.setText(kk);
						mCompare = pp;


						
						break;
					}

					break;
				}
				// Log.d(TAG, "setLyric.***.");
			}

		}

	}

	@Override
	public void setLyricScroll() {
		// TODO Auto-generated method stub
		if (isLrcExist == false || mFlagSeekBar == 1 || mFlagLrc == 1) {

			return;
		}
		/*************** zhushi ******************/
		int line = mCurrenty / mTextY;
System.out.println("line"+line+"*********************");
		if(line==0){
			mMyScroll.scrollTo(0,0);
			Log.d("sroll()", "000");
			return;

		}
		int size = mLyricInfo.song_lines.size();
		for (int i = 0; i < size; i++) {

			if (mLyricInfo.song_lines.get(i).cout > line) {

				mMusicPosition = (int) mLyricInfo.song_lines.get(i).start;
				mMusicDuration = mService.getServiceDuration();
				int max = mSeekBar.getMax();
				// System.out.println("duration"+mMusicDuration+"-----"+"position"+mMusicPosition);
				mSeekBar.setProgress(mMusicPosition * max / mMusicDuration);
				mService.seeto(mMusicPosition);
				break;

			}
			// System.out.println("函数进行");
		}
		// mFlagSeekBar=1;

	}

	@Override
	public void setBarAndLrcWork() {
		// TODO Auto-generated method stub
		if (mFlagLrc == 1) {
			return;
		} else
			mFlagSeekBar = 1;

		// mFlagLrc=2;
	}

	@Override
	public List<Map<String, Object>> getListAll() {
		return mListAll;
	}

	@Override
	public List<Map<String, Object>> getListA() {
		return mListA;
	}

	@Override
	public List<Map<String, Object>> getListB() {
		return mListB;
	}

	@Override
	public List<Map<String, Object>> getListC() {
		return mListC;
	}


	@Override
	public void setImagePlay() {
		// TODO Auto-generated method stub
		mStartAndPause.setImageResource(R.mipmap.sd);
	}

	@Override
	public void setImagePause() {
		// TODO Auto-generated method stub

		mStartAndPause.setImageResource(R.mipmap.zx);
	}

	@Override
	public void setCurrentSongLog(int listNumber, int position) {
		// TODO Auto-generated method stub
		if (listNumber == 0) {
			sAdapter.setSelectItem(position);
			mAdapterListA.setmSelectItem(-1);
			mAdapterListB.setmSelectItem(-1);
			mAdapterListC.setmSelectItem(-1);
		} else if (listNumber == 1) {
			sAdapter.setSelectItem(-1);
			mAdapterListA.setmSelectItem(position);
			mAdapterListB.setmSelectItem(-1);
			mAdapterListC.setmSelectItem(-1);
		} else if (listNumber == 2) {
			sAdapter.setSelectItem(-1);
			mAdapterListA.setmSelectItem(-1);
			mAdapterListB.setmSelectItem(position);
			mAdapterListC.setmSelectItem(-1);
		} else if (listNumber == 3) {
			sAdapter.setSelectItem(-1);
			mAdapterListA.setmSelectItem(-1);
			mAdapterListB.setmSelectItem(-1);
			mAdapterListC.setmSelectItem(position);

		}
		sAdapter.notifyDataSetChanged();
		mAdapterListA.notifyDataSetChanged();
		mAdapterListB.notifyDataSetChanged();
		mAdapterListC.notifyDataSetChanged();

	}

	@Override
	public void checkExistInService(int listNumber, int position, String path) {
		SQLiteDatabase db=mMusicHelper.getWritableDatabase();
 	db.delete("MusicList", "data=?", new String[]{path});
		db.close();
		System.out.println("在ACtivity删除了它"+path);
		if (listNumber == 0) {
			 mListAll.remove(position);
			mService.setmListAll(mListAll);
			sAdapter.notifyDataSetChanged();
		} else if (listNumber == 1) {
			 mListA.remove(position);
			mService.setmListA(mListA);
			mAdapterListA.notifyDataSetChanged();
		} else if (listNumber == 2) {
			 mListB.remove(position);
			mService.setmListB(mListB);
		} else if (listNumber == 3) {
			 mListC.remove(position);
			mService.setmListC(mListC);
			mAdapterListC.notifyDataSetChanged();

		}


	}


	public void setUnexistSongGrey(int listNumber, int position) {
		// TODO Auto-generated method stub
		if (listNumber == 0) {
			sAdapter.setSelectItem(position);
			mAdapterListA.setmSelectItem(-1);
			mAdapterListB.setmSelectItem(-1);
			mAdapterListC.setmSelectItem(-1);
		} else if (listNumber == 1) {
			sAdapter.setSelectItem(-1);
			mAdapterListA.setmSelectItem(position);
			mAdapterListB.setmSelectItem(-1);
			mAdapterListC.setmSelectItem(-1);
		} else if (listNumber == 2) {
			sAdapter.setSelectItem(-1);
			mAdapterListA.setmSelectItem(-1);
			mAdapterListB.setmSelectItem(position);
			mAdapterListC.setmSelectItem(-1);
		} else if (listNumber == 3) {
			sAdapter.setSelectItem(-1);
			mAdapterListA.setmSelectItem(-1);
			mAdapterListB.setmSelectItem(-1);
			mAdapterListC.setmSelectItem(position);

		}
		sAdapter.notifyDataSetChanged();
		mAdapterListA.notifyDataSetChanged();
		mAdapterListB.notifyDataSetChanged();
		mAdapterListC.notifyDataSetChanged();

	}

}
