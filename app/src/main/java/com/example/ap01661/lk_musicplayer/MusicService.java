package com.example.ap01661.lk_musicplayer;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.FileHandler;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
public class MusicService extends Service {
	private int mMusicIndex = 0;
	private List<Map<String, Object>> mMusicList;
	private List<Map<String, Object>> mListAll;
	private List<Map<String, Object>> mListA;
	private List<Map<String, Object>> mListB;
	private List<Map<String, Object>> mListC;
	private int mListCount = 0;
	private MyDataBaaseHelper mMusicHelper;

	public List<Map<String, Object>> getmListAll() {
		return mListAll;
	}

	public void setmListAll(List<Map<String, Object>> mListAll) {
		this.mListAll = mListAll;
	}

	public List<Map<String, Object>> getmListA() {
		return mListA;
	}

	public void setmListA(List<Map<String, Object>> mListA) {
		this.mListA = mListA;
	}

	public List<Map<String, Object>> getmListB() {
		return mListB;
	}

	public void setmListB(List<Map<String, Object>> mListB) {
		this.mListB = mListB;
	}

	public List<Map<String, Object>> getmListC() {
		return mListC;
	}

	public void setmListC(List<Map<String, Object>> mListC) {
		this.mListC = mListC;
	}

	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private int mSeekFlag = 3;
	private int once = 1;
	private int aa = 2;
	int kk = 1;
	int dd = 1;
	private int noListFlag=1;
	private IntentFilter mMusicfFilter;
	private MusicPlayerReceiver mMusicPlayerReceiver;
	// private String mMusicPath = Environment.getExternalStorageDirectory() +
	// "/"
	// + "Carly Rae Jepsen - I Really Like You.mp3";
	private String mMusicPath = "";
	private CallBack mCallBack = null;
	private int mModeFlag = 1;

	public int getmModeFlag() {
		return mModeFlag;
	}

	public void setmModeFlag(int mModeFlag) {
		this.mModeFlag = mModeFlag;
	}

	public List<Map<String, Object>> getmMusicList() {
		return mMusicList;
	}

	public int getmMusicIndex() {
		return mMusicIndex;
	}

	public void setmMusicIndex(int mMusicIndex) {
		this.mMusicIndex = mMusicIndex;
	}

	public String getmMusicPath() {
		return mMusicPath;
	}

	public void setmMusicPath(String mMusicPath) {
		this.mMusicPath = mMusicPath;
	}

	public void setmMusicList(List<Map<String, Object>> mMusicList) {
		this.mMusicList = mMusicList;
	}

	void setCallback(CallBack cb) {
		mCallBack = cb;
	}

	public void seeto(int time) {
		mMediaPlayer.seekTo(time);
	}

	private Handler mHandler2 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 1:

				if (mCallBack == null) {
					// mMediaPlayer.reset();

					stopSelf();
				}

				break;

			default:
				break;
			}
		}

	};


	boolean checkFileExist(String path){
		File f=new File(path);
		return f.exists()&&f!=null;
	}
	void removeSong( ){
		if(mMusicList.size()==0){

			if(mCallBack!=null){
              mCallBack.setSelectSong();
			}
			return;}

   System.out.println("进入了REMOVESONG"+mMusicPath+"size:"+mMusicList.size()+"index"+mMusicIndex);
		mMusicPath=mMusicList.get(mMusicIndex).get("data").toString();
        if(checkFileExist(mMusicPath)){
			mMediaPlayer.reset();
			initMediaPlayer();
			mMediaPlayer.start();
			String lrcpath=mMusicPath.substring(0,mMusicPath.length()-4)+".lrc";
			if(mCallBack!=null){
			mCallBack.getLrc(lrcpath);}
			System.out.println("在REMOVE里面加载了"+mMusicPath+"size:"+mMusicList.size()+"index"+mMusicIndex);
            return;
		}else{SQLiteDatabase db=mMusicHelper.getWritableDatabase();
			db.delete("MusicList", "data=?", new String[]{mMusicPath});
			int op=0;
			if(mMusicIndex==mMusicList.size()-1){
				op=0;
			}else op=mMusicIndex;
			mMusicList.remove(mMusicIndex);
			System.out.println("进入了REMOVESONG"+mMusicPath+"size:"+mMusicList.size()+"index"+mMusicIndex);
			 mMusicIndex=op;

			removeSong();
		}
	}
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 1:

				/*******************判断问价是否存在********************/

if(checkFileExist(mMusicPath)==false){

 mMediaPlayer.stop();
	mMediaPlayer.reset();

	 removeSong();

	if(mCallBack!=null) {
		Log.d("88","aaaaaaaaaaaaaaaaaaaaaaaaa");
		mCallBack.SetSeekBar(1,
				0);
		mCallBack.setLyric(0);


			mCallBack.setImagePause();


	}

}
                                if(mMusicList.size()==0){
									Log.d("88","bbbbbbbbbbbbbbbbb");
									mSeekFlag=3;
									return;
								}
				/******************退出重新进入的时候加载歌词****************************/
				if (aa == 1 && mCallBack != null && mMediaPlayer != null) {
					aa = 2;

					Log.d("MusicActivityIn", "inininininininini");

					mListAll=mCallBack.getListAll();
					mListB=mCallBack.getListB();
					mListA=mCallBack.getListA();
					mListC=mCallBack.getListC();
				switch (mListCount){
					case 0:mMusicList=mListAll;break;
					case 1:mMusicList=mListA;break;
					case 2:mMusicList=mListB;break;
					case 3:mMusicList=mListC;break;
					default:break;

				}
					if(mMusicList.size()==0){
						Log.d("88","cccccccccccccccccccc");
						mMediaPlayer.stop();
						mMediaPlayer=new MediaPlayer();
						noListFlag=2;
						mCallBack.SetSeekBar(1,
								0);
						mCallBack.setLyric(0);
						mCallBack.setImagePlay();
System.out.println("重新设置000000000000000000000000000000000");
					}else {
						noListFlag = 1;
						String lrcPath = mMusicPath.substring(0,mMusicPath.length() - 4)
								+ ".lrc";
						Log.d("88","dddddddddddddddddddddddddddd");
						mCallBack.setLyric(lrcPath);
					}
				}
				/************进度条 歌词条刷新*********************************************/
				if (mCallBack != null && mMediaPlayer != null&&noListFlag==1) {

					mCallBack.SetSeekBar(mMediaPlayer.getDuration(),
							mMediaPlayer.getCurrentPosition());
					mCallBack.setLyric(mMediaPlayer.getCurrentPosition());


				}
				/***************时间到切换歌曲************************/
				if (noListFlag==1&&mMediaPlayer != null
						&& (mMediaPlayer.getDuration() < mMediaPlayer
								.getCurrentPosition() + 1000)) {
					Log.d("播放模式切换", "切切切");
                         File f=new File(mMusicPath);
								 if(f.exists()&&f!=null) {
						if (mModeFlag == 1) {
							circleRepet();
						} else if (mModeFlag == 2) {
							randomPlay();
						} else if (mModeFlag == 3) {
							songLooping();
						}

						if (mCallBack != null) {
							/*****************歌曲切换的时候显示当前播放歌曲以及更新歌词**************************/
							mCallBack.setCurrentSongLog(mListCount, mMusicIndex);
							String lrcPath = mMusicPath.substring(0,
									mMusicPath.length() - 4)
									+ ".lrc";
							mCallBack.setLyric(lrcPath);
						}
					}else {
									 mMediaPlayer.stop();
									 noListFlag=2;
									 if (mCallBack != null) {
										 mCallBack.SetSeekBar(1,
												 0);
										 mCallBack.setLyric(0);
									 }

								 }
				}

				break;
			case 2:
				if (mCallBack == null) {
					// mMediaPlayer.reset();

					stopSelf();
				}
				break;
			default:
				break;
			}
		}

	};

	public class SimpleBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}
	}

	public SimpleBinder mBinder;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d("bindService", "service is binded");
		return mBinder;
	}

	void stop() {
		if (mMediaPlayer.isPlaying()) {
			Log.d("MusicService", "stop");
			// mMediaPlayer.pause();
			mMediaPlayer.reset();
			// mSeekFlag = 3;
		}

	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		mCallBack = null;

		Log.d("bindService", "service is onUnbind");
		return super.onUnbind(intent);
	}

	public void setSeekTo(int time, int dest, int max) {
		// Log.d("TTTT", "   cup " + mMediaPlayer.getCurrentPosition());

		mMediaPlayer.seekTo(time * dest / max);
		// Log.d("TTTT", " dest " + dest + " seek " + (time * dest / max) +
		// "   cup " + mMediaPlayer.getCurrentPosition());
	}

	public int getServiceDuration() {

		return mMediaPlayer.getDuration();
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		//
		// initMediaPlayer();

		// MusicPlayerActivity.sMode.setOnClickListener(new ModeListener());

		Log.d("Service onCreate", "Service is Created");
		//
		mMusicHelper=new MyDataBaaseHelper(MusicService.this, "MMM.db", null, 1);
 	 
		mBinder = new SimpleBinder();
		mMusicfFilter = new IntentFilter();
		mMusicfFilter.addAction("com.likego.broadcast.MUSIC_DESTORY");
		mMusicPlayerReceiver = new MusicPlayerReceiver();
		registerReceiver(mMusicPlayerReceiver, mMusicfFilter);
		Log.d("MusicService", "OnCreate:" + "mMusicIndex is" + mMusicIndex);
		Log.d("MusicService", "OnCreate:" + "mMusicPath is" + mMusicPath);

		super.onCreate();

	}

	public class MusicPlayerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			System.out.println("广播接收到");
			aa = 1;
			Log.d("aa", aa + "");
		}

	}
void loadSharPreference(){

	SharedPreferences.Editor editor = getSharedPreferences("data",
			MODE_PRIVATE).edit();

	editor.putString("title","");
	editor.putInt("index", 0);
	editor.commit();
	/****************** LIST *********************************/
	SharedPreferences.Editor editor2 = getSharedPreferences("list",
			MODE_PRIVATE).edit();
	editor2.putInt("mode", 0);
	editor2.commit();

}
// 	void setFirstTime()
//		{
//			SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
//			mMusicIndex = pref.getInt("index", 0);
//
//			SharedPreferences pref2 = getSharedPreferences("list", MODE_PRIVATE);
//			mListCount = pref2.getInt("mode", 0);
//			System.out.println("此时第一次进入服务查询Sharepreference"+"--index:"+mMusicIndex+"--listCount:"+mListCount);
//			Log.d("OnStartCommand","mListAll size:"+mListAll.size());
//			/********************** 模式选择 *******************/
//			if (mListCount == 0) {
//				mMusicList = mListAll;
//			} else if (mListCount == 1) {
//				mMusicList = mListA;
//			} else if (mListCount == 2) {
//				mMusicList = mListB;
//			} else if (mListCount == 3) {
//				mMusicList = mListC;
//			}
//			if(mMusicIndex>mMusicList.size()-1)
//				return ;
//			mMusicPath = mMusicList.get(mMusicIndex).get("data").toString();
//			System.out.println("once里面"+mMusicPath);
//			File f=new File(mMusicPath);
//			if(f.exists()&&f!=null){
//				System.out.println("once 文件存在");
//				mMediaPlayer.reset();
//				initMediaPlayer();
//				once = 2;}else {
//                    once=2;
//
//				System.out.println("文件不存在");
//				//mCallBack.checkExistInService(mListCount,mMusicIndex,mMusicPath);
//
//
//
//			}
//
//			Log.d("MusicService", "Service is work firstTime");
//		}
// 	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		Log.d("MusicService", "onStartCommond最前面进入");
		if (intent.getExtras().getString("control").equals("aaa")) {
			Log.d("OnStartCommand", "接收到AAA Intent");

			return START_STICKY;

		}
		if(mListAll.size()==0){
			loadSharPreference();
			System.out.println("进入服务，此时集合为空，在onStartCommand");
			return START_STICKY;
		}


		if (once == 1) {
			SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
			mMusicIndex = pref.getInt("index", 0);

			SharedPreferences pref2 = getSharedPreferences("list", MODE_PRIVATE);
			mListCount = pref2.getInt("mode", 0);
				System.out.println("此时第一次进入服务查询Sharepreference"+"--index:"+mMusicIndex+"--listCount:"+mListCount);
			Log.d("OnStartCommand","mListAll size:"+mListAll.size());
			/********************** 模式选择 *******************/
			if (mListCount == 0) {
				mMusicList = mListAll;
			} else if (mListCount == 1) {
				mMusicList = mListA;
			} else if (mListCount == 2) {
				mMusicList = mListB;
			} else if (mListCount == 3) {
				mMusicList = mListC;
			}
			Log.d("onStartCommand size:",mMusicList.size()+"");
               if(mMusicIndex>mMusicList.size()-1)
			   {mMusicIndex=0;
				   return START_STICKY;}
			mMusicPath = mMusicList.get(mMusicIndex).get("data").toString();
			System.out.println("once里面"+mMusicPath);
			File f=new File(mMusicPath);
			if(f.exists()&&f!=null){
				System.out.println("once 文件存在");
			mMediaPlayer.reset();
			initMediaPlayer();
			once = 2;}else {

				System.out.println("文件不存在");
					return START_STICKY;



			}

			Log.d("MusicService", "Service is work firstTime");
		}
		String playFlag = intent.getExtras().getString("control");
		Log.v("on Startconmand 2", "按键触发，正式进入操作");
		if ("play".equals(playFlag)) {

			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();

				mSeekFlag = 1;
			} else if (!mMediaPlayer.isPlaying()) {

				mSeekFlag = 1;
				if (mMediaPlayer == null) {
					initMediaPlayer();
				}
				mMediaPlayer.start();

			}

		} else if ("stop".equals(playFlag)) {
			mMediaPlayer.reset();
			initMediaPlayer();

			mSeekFlag = 3;

		} else if ("listClick".equals(playFlag)) {
			mMusicIndex = intent.getExtras().getInt("path");
			mMusicList = mListAll;
			mMusicPath = mMusicList.get(mMusicIndex).get("data").toString();

			mListCount = 0;


			mMediaPlayer.reset();
			initMediaPlayer();
			mMediaPlayer.start();
			mSeekFlag = 1;

		} else if ("listClickA".equals(playFlag)) {
			mMusicIndex = intent.getExtras().getInt("path");
			mMusicList = mListA;
			mListCount = 1;
			mMusicPath = mMusicList.get(mMusicIndex).get("data").toString();
			mMediaPlayer.reset();
			initMediaPlayer();
			mMediaPlayer.start();
			String path2 = mMusicList.get(mMusicIndex).get("data").toString();
			String lrcPath = path2.substring(0, path2.length() - 4) + ".lrc";
			Log.d("musiclist", mMusicList + "");
			mCallBack.getLrc(lrcPath);
			mSeekFlag = 1;

		} else if ("listClickB".equals(playFlag)) {
			mListCount = 2;
			mMusicIndex = intent.getExtras().getInt("path");
			System.out
					.println("music index is :" + mMusicIndex + "***********");
			mMusicList = mListB;
			mMusicPath = mMusicList.get(mMusicIndex).get("data").toString();
			mMediaPlayer.reset();
			initMediaPlayer();
			mMediaPlayer.start();
			String path2 = mMusicList.get(mMusicIndex).get("data").toString();
			String lrcPath = path2.substring(0, path2.length() - 4) + ".lrc";

			mCallBack.getLrc(lrcPath);
			mSeekFlag = 1;

		} else if ("listClickC".equals(playFlag)) {
			mListCount = 3;
			mMusicIndex = intent.getExtras().getInt("path");
			System.out
					.println("music index is :" + mMusicIndex + "***********");
			mMusicList = mListC;
			mMusicPath = mMusicList.get(mMusicIndex).get("data").toString();
			mMediaPlayer.reset();
			initMediaPlayer();
			mMediaPlayer.start();
			String path2 = mMusicList.get(mMusicIndex).get("data").toString();
			String lrcPath = path2.substring(0, path2.length() - 4) + ".lrc";
			// Log.d("musiclist", mMusicList+"");
			mCallBack.getLrc(lrcPath);
			mSeekFlag = 1;

		} else if ("playNext".equals(playFlag)) {
			System.out.println("播放下一首");
			PlayNext();
			mSeekFlag = 1;
		}  else if ("Mode1".equals(playFlag)) {
			mModeFlag = 1;
		} else if ("Mode2".equals(playFlag)) {
			mModeFlag = 2;
		} else if ("Mode3".equals(playFlag)) {
			mModeFlag = 3;
		} else if ("stop2".equals(playFlag)) {
			// mSeekFlag=3;
		}

		mCallBack.setDuration(mMediaPlayer.getDuration());

		mCallBack.setLyric(mMusicPath.substring(0, mMusicPath.length() - 4)
				+ ".lrc");
		Log.d("onStartCommand","播放时间设置&歌词设置");
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						sleep(300);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (mSeekFlag == 1)
						mHandler.sendEmptyMessage(1);
					else if (mSeekFlag == 3)
						mHandler.sendEmptyMessage(2);
					else
						break;
				}
			}
		}.start();

		Log.d("MsEEKfLAG", mSeekFlag + "");
 		mCallBack.setCurrentSongLog(mListCount, mMusicIndex);
		if (mMediaPlayer.isPlaying())
			mCallBack.setImagePlay();
		else
			mCallBack.setImagePause();

		return START_STICKY;
	}

	public void play() {
		mMediaPlayer.start();
	}


	/************************ Play next song ******************************/
	private void PlayNext() {
		Log.d("nextSong", mMusicPath);
		Log.d("MUSIC INDEX", mMusicIndex + "");
           if(mMusicList.size()==0){
			mMediaPlayer.stop();
	               return;	}
		if (mModeFlag == 2) {
			randomPlay();

		}
		if (mModeFlag == 1 || mModeFlag == 3) {
				circleRepet();

		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("MusicService", "on Destory");
		unregisterReceiver(mMusicPlayerReceiver);
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;

		}
	}

	/************* singer song circling ***************************/
	void songLooping() {

		mMediaPlayer.start();
		mMediaPlayer.setLooping(true);
	}

	/*************** song random play *************************************/
	void randomPlay() {

		Random ran = new Random(System.currentTimeMillis());
		System.out.println(ran.nextInt(mMusicList.size()));

		mMusicIndex = ran.nextInt(mMusicList.size());
		mMusicPath = mMusicList.get(mMusicIndex).get("data").toString();
		File f = new File(mMusicPath);
		if (f != null && f.exists()) {
			mMediaPlayer.reset();
			initMediaPlayer();
			mMediaPlayer.start();
		} else {
			mMusicList.remove(mMusicIndex);
			System.out.println("随机模式 删除了"+mMusicList.size()+mMusicPath+"index"+mMusicIndex);
           SQLiteDatabase db=mMusicHelper.getWritableDatabase();
           db.delete("MusicList", "data=?", new String[]{mMusicPath});
//			switch (mListCount) {
//			case 0:
//				mListAll.remove(mMusicIndex);
//				break;
//			case 1:
//				mListA.remove(mMusicIndex);
//				break;
//			case 2:
//				mListB.remove(mMusicIndex);
//				break;
//			case 3:
//				mListC.remove(mMusicIndex);
//				break;
//			default:
//				break;
//			}
mMusicIndex = mMusicIndex - 1;
			randomPlay();
		}
	}

	 


	/*********************** list circle ****************************/

	void circleRepet() {
if(mMusicList.size()==0)
{
	mMediaPlayer.stop();
	noListFlag=2;
	mSeekFlag=3;
	return;
}
		if (mMusicIndex + 1 < mMusicList.size()) {
			mMusicIndex = mMusicIndex + 1;
			mMusicPath = mMusicList.get(mMusicIndex).get("data").toString();
			Log.d("nextSong", mMusicPath);
			Log.d("MUSIC INDEX2", mMusicIndex + "");
			File f = new File(mMusicPath);
			if (f.exists() && f != null) {
				mMediaPlayer.reset();
				initMediaPlayer();
				mMediaPlayer.start();
			} else {
				SQLiteDatabase db=mMusicHelper.getWritableDatabase();
				db.delete("MusicList", "data=?", new String[]{mMusicPath});
				System.out.println("列表循环不是最后一个 删除了"+mMusicList.size()+mMusicPath+"index"+mMusicIndex);
				mMusicList.remove(mMusicIndex);
//				switch (mListCount) {
//					case 0:
//
//					//	mListAll.remove(mMusicIndex);
//						System.out.println("列表循环不是最后一个 switch 删除了"+mListAll.size()+mMusicPath+"index"+mMusicIndex);
//
//						break;
//					case 1:
//						mListA.remove(mMusicIndex);
//						break;
//					case 2:
//						mListB.remove(mMusicIndex);
//						break;
//					case 3:
//						mListC.remove(mMusicIndex);
//						break;
//					default:
//						break;
//
//				}
				mMusicIndex = mMusicIndex - 1;
				circleRepet();
			}
		} else {
			mMusicIndex = 0;

			mMusicPath = mMusicList.get(mMusicIndex).get("data").toString();

			File f = new File(mMusicPath);
			if (f.exists() && f != null) {

				mMediaPlayer.reset();

				// mCallBack.setCurrentSongLog(mListCount, mMusicIndex);
				mMusicPath = mMusicList.get(mMusicIndex).get("data").toString();
				Log.d("nextSong", mMusicPath);
				Log.d("MUSIC INDEX2", mMusicIndex + "");
				initMediaPlayer();
				mMediaPlayer.start();
			}else{
				System.out.println("最后一个 删除了"+mMusicList.size()+mMusicPath+"index"+mMusicIndex);
				SQLiteDatabase db=mMusicHelper.getWritableDatabase();
				db.delete("MusicList", "data=?", new String[]{mMusicPath});
				mMusicList.remove(mMusicIndex);
				switch (mListCount) {
					case 0:
						System.out.println("最后一个 删除了"+mMusicList.size()+mMusicPath+"index"+mMusicIndex);
						mListAll.remove(mMusicIndex);
						break;
					case 1:
						mListA.remove(mMusicIndex);
						break;
					case 2:
						mListB.remove(mMusicIndex);
						break;
					case 3:
						mListC.remove(mMusicIndex);
						break;
					default:
						break;

				}
				mMusicIndex = mMusicIndex - 1;
				circleRepet();

			}
		}

	}
	/****************** get mediaplay currenttime ******************/
	public int getCurrentPosition() {
		if (mMediaPlayer != null)
			return mMediaPlayer.getCurrentPosition();
		else
			return 0;
	}




	/********************** init media player ********************************/
	public void initMediaPlayer() {
		try {

			mMediaPlayer.setDataSource(mMusicPath);
			Log.d("init", mMusicPath);

			mMediaPlayer.prepare();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
