package com.example.ap01661.lk_musicplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

public class HorseRunningText extends TextView{
 

	public HorseRunningText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public HorseRunningText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public HorseRunningText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		// TODO Auto-generated method stub

		return true;
	}
	

	
}
