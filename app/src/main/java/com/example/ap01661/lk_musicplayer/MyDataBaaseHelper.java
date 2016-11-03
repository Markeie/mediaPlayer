package com.example.ap01661.lk_musicplayer;

 
 

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDataBaaseHelper extends SQLiteOpenHelper{

	 
  private Context mContext;
 
  
  public static final String CREATE_MUSICLIST="create table MusicList("+"id integer primary key autoincrement,"+"title text,"+"album text,"+ "data text,"+"category text,"+"artist text)";
  
			
			
	public MyDataBaaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext=context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
 
	   db.execSQL(CREATE_MUSICLIST);
	//	Toast.makeText(mContext, "create succeeded MusicList", Toast.LENGTH_SHORT).show();
		 
	
	
	
	
	
	
	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
 
		 
		Toast.makeText(mContext, "updata successful", Toast.LENGTH_SHORT).show();
	}

}
