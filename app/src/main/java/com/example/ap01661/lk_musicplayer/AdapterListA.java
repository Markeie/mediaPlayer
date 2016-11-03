package com.example.ap01661.lk_musicplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

 

public class AdapterListA extends BaseAdapter {

	 
	 private List<Map<String,Object>>mListA=new ArrayList<Map<String,Object>>();
		 public List<Map<String, Object>> getmListA() {
		return mListA;
	}

	public int getmSelectItem() {
			return mSelectItem;
		}

		public void setmSelectItem(int mSelectItem) {
			this.mSelectItem = mSelectItem;
		}

	public void setmListA(List<Map<String, Object>> mListA) {
		this.mListA = mListA;
	}
	private int  mSelectItem=-1;

		private Context context;//用于接收传递过来的Context对象
        public AdapterListA(Context context) {
            super();
            this.context = context;
        }

		  public int getCount() {
			// TODO Auto-generated method stub
			return mListA.size();
		}
	     
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mListA.get(position);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int p, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			int position = p;
			ViewHolder holder=null;
			if(convertView==null){
				holder=new ViewHolder();
				convertView=LayoutInflater.from(context).inflate(R.layout.music_list_one,null);
			    holder.selected=(ImageView)convertView.findViewById(R.id.selected);
		       holder.artist=(TextView)convertView.findViewById(R.id.artist);
				holder.album=(TextView)convertView.findViewById(R.id.album); 
				holder.Title=(TextView)convertView.findViewById(R.id.title);
			    convertView.setTag(holder);
			}else{
				holder=(ViewHolder)convertView.getTag();
				
			}
				
                holder.artist.setText(mListA.get(position).get("artist").toString());
				holder.album.setText(mListA.get(position).get("album").toString()); 
				holder.Title.setText(mListA.get(position).get("title").toString());
				if(position==mSelectItem){
					holder.selected.setImageResource(R.mipmap.ac8);
				}else holder.selected.setImageResource(R.color.pure);
				
				if(position%2==0){
	 convertView.setBackgroundColor(context.getResources().getColor(R.color.list));	
	 convertView.getBackground().setAlpha(70);
	}else 
		convertView.setBackgroundColor(context.getResources().getColor(R.color.pure));
				
				return convertView;
			}
			
			private class ViewHolder{
				TextView Title;
				TextView artist;
				 ImageView selected;
				TextView album;
			}
			
			
	 
}
