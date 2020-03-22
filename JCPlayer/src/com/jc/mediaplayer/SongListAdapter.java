package com.jc.mediaplayer;

import java.util.LinkedList;

import com.jc.mediaplayer.MainActivity.MovieInfo;

import android.content.Context;
import android.graphics.Color;
import android.provider.MediaStore.Audio.Playlists;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class SongListAdapter extends BaseAdapter {

	private Context mContext ;
	private LinkedList<MovieInfo> mPlayList;
	private LayoutInflater mLayoutInflater;
	private int selectItem;
	static class ViewHolder {
		TextView songName;
		TextView singer;
	}
	
	public  SongListAdapter(Context context,LinkedList<MovieInfo> playList)
	{
		mContext = context;
		mPlayList = playList;
		mLayoutInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mPlayList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mPlayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder  holder = null;
		if(convertView == null)
		{
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.list, null);
			holder.songName = (TextView)convertView.findViewById(R.id.nametext);
			holder.singer = (TextView)convertView.findViewById(R.id.singer_text);
			convertView.setTag(holder);
		}
		else
		{
			holder =(ViewHolder)convertView.getTag();
		}
		
		MovieInfo info = mPlayList.get(position);
		holder.songName.setText(info.displayName);
		holder.singer.setText(info.singer);
		
        if (position == selectItem) {  
        	holder.songName.setTextColor(Color.parseColor("#0099ff"));  
        	holder.singer.setTextColor(Color.parseColor("#0099ff")); 
                              
        } else {  
        	holder.songName.setTextColor(Color.BLACK);  
        	holder.singer.setTextColor(Color.GRAY);  
        }
 
		// TODO Auto-generated method stub
		return convertView;
	}
	public void setSelectItem(int selectItem) {    
        this.selectItem = selectItem;    
     }    
}
