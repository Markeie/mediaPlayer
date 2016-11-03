package com.example.ap01661.lk_musicplayer;

import java.util.List;
import java.util.Map;

public interface CallBack {
 
	void play();
	void SetSeekBar(int duration, int position);
  void setLrcPosition();
	 void setUnexistSongGrey(int listNumber, int position);
	void setLyric(String lrc);
	void setDuration(int duration); 
	void setLyric(int position);
	void setLyricScroll();
	void setBarAndLrcWork();
	List<Map<String, Object>> getListAll();
	List<Map<String, Object>> getListA();
	List<Map<String, Object>> getListB();
	List<Map<String, Object>> getListC();
    void setSelectSong();
	void getLrc(String lrc);
     void setImagePlay();
     void setImagePause();
     void setCurrentSongLog(int listNumber, int position);
   void checkExistInService(int listNumber, int position,String path);
}
