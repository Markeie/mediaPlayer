package com.example.ap01661.lk_musicplayer;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import android.util.Log;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView {

//	private OverScroller mScroller = new OverScroller(getContext());
	private ScrollListener mListener;
	private CallBack mScrollBack = null;

	public static interface ScrollListener {
		public void scrollOritention(int l, int t, int oldl, int oldt);
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				//System.out.println("scroll stop");
				 mScrollBack.setBarAndLrcWork();
				// Log.d("ObservableScrollView", "scroll stop" );
				break;

			default:
				break;
			}
		};
	};
	
	void setCallback(CallBack cb) {
		mScrollBack = cb;
	}

	@Override
	public void fling(int velocityY) {
		// TODO Auto-generated method stub
		super.fling(velocityY);
	}

	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		Log.d("compute", "computeScroll: ");
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageDelayed(0, 100);
		mScrollBack.setLyricScroll();
		Log.d("SeekBar", "move -------------------------- move");
		super.computeScroll();
		
	}

	public ObservableScrollView(Context context, AttributeSet attrs,
			int defStyle) {

		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ObservableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ObservableScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
		if (mListener != null) {
			mListener.scrollOritention(l, t, oldl, oldt);

		}
	}

	public void setScrollListener(ScrollListener l) {
		this.mListener = l;
	}

}
