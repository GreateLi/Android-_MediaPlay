package com.jc.mediaplayer;

import java.util.LinkedList;

import com.jc.mediaplayer.MainActivity.MovieInfo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

public class MusicInfoController
{
	private static MusicInfoController	mInstance	= null;

	private MusicPlayerApp					pApp		= null;

	public static MusicInfoController getInstance(MusicPlayerApp app)
	{
		if (mInstance == null)
		{
			mInstance = new MusicInfoController(app);
		}
		return mInstance;
	}


	private MusicInfoController(MusicPlayerApp app)
	{
		pApp = app;
	}

	public MusicPlayerApp getMusicPlayer()
	{
		return pApp;
	}

	private Cursor query(Uri uri, String[] prjs, String selections, String[] selectArgs, String order){
		ContentResolver resolver = pApp.getContentResolver();
		if (resolver == null){
			return null;
		}
		return resolver.query(uri, prjs, selections, selectArgs, order);
	}
	public Cursor getAllSongs(){
		return query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
	}
	
	public Cursor getAllVideo()
	{
		return query(  
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,  
                null, null); 
	}
	
	public LinkedList<MovieInfo> getAllMedia(Context context)
	{
		LinkedList<MovieInfo> playList = new LinkedList<MovieInfo>();
	       if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
	        {
	            Cursor cursor =  getAllVideo();
	                
	            int n = cursor.getCount();
	            cursor.moveToFirst();
	            LinkedList<MovieInfo> playList2 = new LinkedList<MovieInfo>();
	            for(int i = 0 ; i != n ; ++i){
	            	MovieInfo mInfo = new MovieInfo();
	            	mInfo.displayName  = cursor
	                        .getString(cursor
	                                .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
	            	mInfo.path = cursor
	                        .getString(cursor
	                                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
	            	mInfo.bAudio = false;
	            	mInfo.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
	            	playList2.add(mInfo);
	            	cursor.moveToNext();
	            }
	            cursor.close();
	            cursor = getAllSongs();
	            n = cursor.getCount();
	            cursor.moveToFirst();
	           // LinkedList<MovieInfo> playList2 = new LinkedList<MovieInfo>();
	            for(int i = 0 ; i != n ; ++i){
	            	MovieInfo mInfo = new MovieInfo();
	            	mInfo.displayName  = cursor
	                        .getString(cursor
	                                .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
	            	mInfo.path = cursor
	                        .getString(cursor
	                                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
	            	mInfo.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
	            	mInfo.bAudio = true;
	            	playList2.add(mInfo);
	            	cursor.moveToNext();
	            }
	            cursor.close();
	            if(playList2.size() > playList.size()){
	            	playList = playList2;
	            } 
	        }
	        else
	        {
	        	Toast.makeText(context, "not find sd card", 0).show();
	        }
	       
	       return playList;
	}
}

