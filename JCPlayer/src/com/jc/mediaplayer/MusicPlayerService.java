package com.jc.mediaplayer;

import java.io.IOException;
 
import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.RelativeLayout;

public class MusicPlayerService extends Service implements OnVideoSizeChangedListener 
,MediaPlayerControl
{
	private final IBinder mBinder = new LocalBinder();
    
    private MediaPlayer mMediaPlayer = null;
    private int         mVideoWidth;
    private int         mVideoHeight;
    private int         mSurfaceWidth;
    private int         mSurfaceHeight;
    private int         mSeekWhenPrepared ;
    private int          mDuration;
    private boolean     mStartWhenPrepared = false;
    private boolean     mPlayPause = false;
    private boolean     mIsPrepared = false;
    private OnCompletionListener mOnCompletionListener;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private String mMediaPath;
    private MyScreenSizeChangeListener mMyscreenSizeChangeListener;
           
    
    public   interface MyScreenSizeChangeListener
    {
    	public void doChangeSurViewSize(MediaPlayer mp);
    }
    
    public void setScreenSizeChange(MyScreenSizeChangeListener listener)
    {
    	mMyscreenSizeChangeListener = listener;
    }
    /**
    * Register a callback to be invoked when the media file
    * is loaded and ready to go.
    *
    * @param l The callback that will be run
    */
   public void setOnPreparedListener(MediaPlayer.OnPreparedListener Listener)
   {
       mOnPreparedListener = Listener;
   }

   /**
    * Register a callback to be invoked when the end of a media file
    * has been reached during playback.
    *
    * @param l The callback that will be run
    */
   public void setOnCompletionListener(OnCompletionListener Listener)
   {
       mOnCompletionListener = Listener;
   }
   
   MediaPlayer.OnCompletionListener mCompleteListener = new MediaPlayer.OnCompletionListener() 
   {
       public void onCompletion(MediaPlayer mp) 
       {
          // broadcastEvent(PLAY_COMPLETED);
           if (mOnCompletionListener != null) {
               mOnCompletionListener.onCompletion(mMediaPlayer);
           }
       }
   };
   
   MediaPlayer.OnPreparedListener mPrepareListener = new MediaPlayer.OnPreparedListener() 
   {
       public void onPrepared(MediaPlayer mp) 
       {   
    	   mIsPrepared = true;
          // broadcastEvent(PLAYER_PREPARE_END);
           if (mOnPreparedListener != null) {
               mOnPreparedListener.onPrepared(mMediaPlayer);
           }
           
           mVideoWidth = mp.getVideoWidth();
           mVideoHeight = mp.getVideoHeight();
           if (mVideoWidth != 0 && mVideoHeight != 0) {
               //Log.i("@@@@", "video size: " + mVideoWidth +"/"+ mVideoHeight);
        	  // MainActivity.surHolder.setFixedSize(mVideoWidth, mVideoHeight);
               if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight)
               {
                   // We didn't actually change the size (it was already at the size
                   // we need), so we won't get a "surface changed" callback, so
                   // start the video here instead of in the callback.
                   if (mSeekWhenPrepared != 0) {
                       mMediaPlayer.seekTo(mSeekWhenPrepared);
                       mSeekWhenPrepared = 0;
                   }
                   if (mStartWhenPrepared) {
                       mMediaPlayer.start();
                       mStartWhenPrepared = false;
           
                   } 
               }
           } else 
           {
               // We don't know the video size yet, but should start anyway.
               // The video size might be reported to us later.
               if (mSeekWhenPrepared != 0) {
                   mMediaPlayer.seekTo(mSeekWhenPrepared);
                   mSeekWhenPrepared = 0;
               }
               if (mStartWhenPrepared) {
                   mMediaPlayer.start();
                   mStartWhenPrepared = false;
               }
           }
       }
   };
   

	public void onCreate()
	{
		super.onCreate();
		initVideoView();
	}
	
    private void initVideoView() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        MainActivity.surHolder.addCallback(mSHCallback);
       // openMediaPlayer();
    }
    

	public void openMediaPlayer()
	{
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnPreparedListener(mPrepareListener);
		mMediaPlayer.setOnCompletionListener(mCompleteListener);
		mMediaPlayer.setOnVideoSizeChangedListener(this);
		mIsPrepared = false;
		mDuration =-1;
		if(null != mMediaPath)
		setDataSource(mMediaPath);
	}
	
	public class LocalBinder extends Binder
	{
		public MusicPlayerService getService()
		{
			return MusicPlayerService.this;
		}
	}


	public IBinder onBind(Intent intent)
	{
		return mBinder;
	}

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback()
    {
        public void surfaceChanged(SurfaceHolder holder, int format,
                                    int w, int h)
        {
            mSurfaceWidth = w;
            mSurfaceHeight = h;
            if (mMediaPlayer != null && mIsPrepared && mVideoWidth == w && mVideoHeight == h)
            {
                if (mSeekWhenPrepared != 0) 
                {
                    mMediaPlayer.seekTo(mSeekWhenPrepared);
                    mSeekWhenPrepared = 0;
                }
                mMediaPlayer.start();
 
            }
        }

        public void surfaceCreated(SurfaceHolder holder)
        {
            openMediaPlayer();
        }

        public void surfaceDestroyed(SurfaceHolder holder)
        {
            // after we return from this we can't use the surface any more
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    };
    

	public void setDataSource(String path)
	{

		try
		{
			mMediaPath = path;
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.setDisplay(MainActivity.surHolder);
			mMediaPlayer.prepare();
			MainActivity.surView.requestLayout();
			MainActivity.surView.invalidate();
			//mMediaPlayer.prepareAsync();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			return;
		}
	}


	public void start()
	{
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            mStartWhenPrepared = false;
    } else {
        mStartWhenPrepared = true;
    }
		 
	}


	public void stop()
	{
		mMediaPlayer.stop();
	}


	public void pause()
	{
        if (mMediaPlayer != null && mIsPrepared) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
        mStartWhenPrepared = false;
	}


	public boolean isPlaying()
	{
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.isPlaying();
        }
        return false;
	}


	public int getDuration()
	{
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.getDuration();
        }
        mDuration = -1;
        return mDuration;
 
	}


	public int getPosition()
	{
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
		//return mMediaPlayer.getCurrentPosition();
	}


	public long seek(long whereto)
	{
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.seekTo((int)whereto);
        } else {
            mSeekWhenPrepared = (int)whereto;
        }
		//mMediaPlayer.seekTo((int) whereto);
		return whereto;
	}


	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		// TODO Auto-generated method stub
        mVideoWidth = mp.getVideoWidth();
        mVideoHeight = mp.getVideoHeight();
        if(null != mMyscreenSizeChangeListener)
        {
        	mMyscreenSizeChangeListener.doChangeSurViewSize(mp);
        }
        
//        if (mVideoWidth != 0 && mVideoHeight != 0) {
//        	MainActivity.surHolder.setFixedSize(mVideoWidth, mVideoHeight);
//        }
        
//
//			int videoWidth =  getVideoWidth();
//			int videoHeight =  getVideoHeight();
//			int mWidth = MainActivity.screenWidth;
//			int mHeight = MainActivity.screenHeight - 15;
//			
//			if (videoWidth > 0 && videoHeight > 0) {
//	            if ( videoWidth * mHeight  > mWidth * videoHeight ) {
//	                //Log.i("@@@", "image too tall, correcting");
//	            	mHeight = mWidth * videoHeight / videoWidth;
//	            } else if ( videoWidth * mHeight  < mWidth * videoHeight ) {
//	                //Log.i("@@@", "image too wide, correcting");
//	            	mWidth = mHeight * videoWidth / videoHeight;
//	            } else {
//	                
//	            }
//	        }
//			
//			setVideoScale(mWidth, mHeight);
//
// 			//getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
// 
//        if (mVideoWidth != 0 && mVideoHeight != 0) {
//        	MainActivity.surHolder.setFixedSize(mVideoWidth, mVideoHeight);
//        	
//        	if(mVideoWidth > mVideoHeight) 
//        	{
//                int h = MainActivity.screenWidth * height / width;
//                int margin = (MainActivity.screenHeight - h) / 2;
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.MATCH_PARENT);
//                  lp.setMargins(0, margin, 0, margin);
//               MainActivity.mInstance.surView.setLayoutParams(lp);
//        	}
//        	else
//        	{
//        		//
//	            int w = MainActivity.screenHeight * width / height;
//	            int margin = (MainActivity.screenWidth - w) / 2;
//	            //Logger.d(TAG, "margin:" + margin);
//	            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//	                    RelativeLayout.LayoutParams.MATCH_PARENT,
//	                    RelativeLayout.LayoutParams.MATCH_PARENT);
//	            lp.setMargins(margin, 0, margin, 0);
//	            MainActivity.mInstance.surView.setLayoutParams(lp);
//        	}
//        }
		
		
		
//		   if (width == 0 || height == 0) {
//	            Log.e("12", "invalid video width(" + width + ") or height(" + height
//	                    + ")");
//	            return;
//	        }
//	        //Logger.d(TAG, "onVideoSizeChanged width:" + width + " height:" + height);
	       // mIsVideoSizeKnown = true;

	}

    public  void setVideoScale(int width , int height){
    	LayoutParams lp = MainActivity.mInstance.surView.getLayoutParams();
    	lp.height = height;
		lp.width = width;
		MainActivity.mInstance.surView.setLayoutParams(lp);
    }
	@Override
	public boolean canPause() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean canSeekBackward() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void seekTo(int pos) {
		// TODO Auto-generated method stub
		
	}
	
	public int getVideoWidth()
	{
		return mVideoWidth;
	}
	
	public int getVideoHeight()
	{
		return mVideoHeight;
	}

}

