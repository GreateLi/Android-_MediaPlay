package com.jc.mediaplayer;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import com.jc.mediaplayer.MusicPlayerService.MyScreenSizeChangeListener;
import com.jc.mediaplayer.R;
//import com.admob.android.ads.AdView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;

import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
 
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
 
public class MainActivity extends Activity implements OnClickListener,
OnItemClickListener, OnDismissListener{
	public static MainActivity	mInstance;
	private final static String TAG = "MainActivityActivity";
	private int mCurPlayMode = 0;
	private boolean mCurScreenMode = false;
	
	private final static int AUDIO_MODE =0;
	private final static int VIDEO_MODE =1;
    private final static int SCREEN_FULL = 1;
    private final static int SCREEN_DEFAULT = 0;
	
	
    private MusicPlayerService mMusicPlayerService = null;
    private MusicInfoController mMusicInfoController = null;
	
	public static LinkedList<MovieInfo> playList; //= new LinkedList<MovieInfo>();
	public static class MovieInfo{
		String displayName;  
		String path;
		String singer;
		boolean bAudio;
	}
	private final static int TIME = 3818;
	private static int position =0 ;
	 private boolean     mIsPrepared = false;
	private int playedTime =0;
	private boolean bFirstPlay = true;
	private boolean bFirstStart = true;//fist start//第一次启动
	private boolean bResumeStart = false;
	
	private SongListAdapter mListAdapter;
	private SeekBar seekBar = null;  
	private TextView durationTextView = null;
	private TextView playedTextView = null;
	
	private ImageButton list_Btn = null;
	private ImageButton last_Btn = null;
	private ImageButton play_Btn = null;
	private ImageButton next_Btn = null;
	//private ImageButton full_Btn = null;
	private TextView background_Image;
	private LinearLayout fill_view;
	private GestureDetector mGestureDetector;
	private View toolLayout ;
	
	private View songlistView = null;
	private View backgroundView = null;
	private PopupWindow menuPopup = null;
	private PopupWindow backgroundPopup = null;
	private ListView songList; 
	
	public static int screenWidth = 0;
	public static int screenHeight = 0;

	private Context context;
	public static SurfaceView surView;
	public static SurfaceHolder surHolder;
	public Intent mIntentService;
	
	// 再按一次退出程序
	private Boolean isExit = false;
	private Boolean hasTask = false;
	Timer tExit = new Timer();
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			isExit = false;
			hasTask = true;
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
        if (mIsPrepared &&
                keyCode != KeyEvent.KEYCODE_BACK &&
                keyCode != KeyEvent.KEYCODE_VOLUME_UP &&
                keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
                keyCode != KeyEvent.KEYCODE_MENU &&
                keyCode != KeyEvent.KEYCODE_CALL &&
                keyCode != KeyEvent.KEYCODE_ENDCALL &&
                		mMusicPlayerService != null ) 
                 {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                if (mMusicPlayerService.isPlaying()) {
                	mMusicPlayerService.pause();
                     
                } else {
                	mMusicPlayerService.start();
                    
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                    && mMusicPlayerService.isPlaying()) {
            	mMusicPlayerService.pause();
                 
            }  
        }

		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (isExit == false) {
				isExit = true;
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				if (!hasTask) {
					tExit.schedule(task, 2000);
				}
			} else {

				finish();
				//System.exit(0);
			}
			return true;
		}
		
		if(KeyEvent.KEYCODE_HOME ==keyCode )
		{
			
		}
         return super.onKeyDown(keyCode, event);
	}
	
    private ServiceConnection mPlaybackConnection = new ServiceConnection() 
    {
        public void onServiceConnected(ComponentName className, IBinder service) 
        {  
        	mMusicPlayerService = ((MusicPlayerService.LocalBinder)service).getService();
        	
            mMusicPlayerService.setOnPreparedListener(new OnPreparedListener(){

    			@Override
    			public void onPrepared(MediaPlayer mp) {
    				// TODO Auto-generated method stub
    				
	    			int vWidth = mp.getVideoWidth();
	    			int vHeight = mp.getVideoHeight();
	    			
    	           	    mIsPrepared = true;
    					int i = mMusicPlayerService.getDuration();
    					Log.d("onCompletion", ""+i);
    					seekBar.setMax(i);
    					i/=1000;
    					int minute = i/60;
    					int hour = minute/60;
    					int second = i%60;
    					minute %= 60;
    					durationTextView.setText(String.format("%02d:%02d:%02d", hour,minute,second));
    					
    					play_Btn.setImageResource(R.drawable.btn_pause_normal);
    					myHandler.sendEmptyMessage(PROGRESS_CHANGED);  
    	    			
    			        //首先取得video的宽和高     
    			       // vWidth = player.getVideoWidth();    
    			       // vHeight = player.getVideoHeight();    
    			            
//    			        if(vWidth > screenWidth || vHeight > screenHeight){    
//    			            //如果video的宽或者高超出了当前屏幕的大小，则要进行缩放     
//    			            float wRatio = (float)vWidth/(float)screenWidth;    
//    			            float hRatio = (float)vHeight/(float)screenHeight;    
//    			                
//    			            //选择大的一个进行缩放     
//    			            float ratio = Math.max(wRatio, hRatio);    
//    			                
//    			            vWidth = (int)Math.ceil((float)vWidth/ratio);    
//    			            vHeight = (int)Math.ceil((float)vHeight/ratio);    
//    			                
//    			            //设置surfaceView的布局参数     
//    			            surView.setLayoutParams(new LinearLayout.LayoutParams(vWidth, vHeight)); 
//    			        }
    				    mMusicPlayerService.start();  
    				    
    			}	
            });
            
            mMusicPlayerService.setOnCompletionListener(new OnCompletionListener(){

        		@Override
        		public void onCompletion(MediaPlayer arg0) {
        			// TODO Auto-generated method stub
        			loopPlay();
        		}
        	});
            
            mMusicPlayerService.setScreenSizeChange(new MyScreenSizeChangeListener() {
				
				@Override
				public void doChangeSurViewSize(MediaPlayer mp) {
					// TODO Auto-generated method stub
	    			int videoWidth = mp.getVideoWidth();
	    			int videoHeight = mp.getVideoHeight();
			        int wid = mp.getVideoWidth();
			        int hig = mp.getVideoHeight();
			         // if audio
			        if (wid == 0 || hig == 0) 
			        {
			            return;
			        }
		//          
			     // 根据视频的属性调整其显示的模式
			        if (wid > hig) {
			            if ( getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			            	 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			            	 getDisPlayMetric();
			            }
			        } else {
			            if ( getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			            	 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			            	 getDisPlayMetric();
			            }
			        }
   	
			        //surView.invalidate();
				}
			});
         
        }
        public void onServiceDisconnected(ComponentName className) 
        {
        	mMusicPlayerService = null;
        }
    };
    
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		
		getDisPlayMetric();
		super.onConfigurationChanged(newConfig);
	}
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.main);
        context = this;
        mInstance = this;
        Log.d("OnCreate", getIntent().toString());

        MusicPlayerApp musicPlayerApp=(MusicPlayerApp)getApplication();
        mMusicInfoController = (musicPlayerApp).getMusicInfoController();
        
        // bind playback service
        mIntentService = new Intent(this,MusicPlayerService.class);
        startService(mIntentService);        
        bindService(mIntentService, mPlaybackConnection, Context.BIND_AUTO_CREATE);
 
        
        getDisPlayMetric();

        fill_view = (LinearLayout)findViewById(R.id.fill_view);
        backgroundView = getLayoutInflater().inflate(R.layout.audio_background,null);
        background_Image = (TextView)backgroundView.findViewById(R.id.background_image);
        songlistView = getLayoutInflater().inflate(R.layout.song_list, null);
        songList = (ListView)songlistView.findViewById(R.id.song_listview);
        songList.setOnItemClickListener(this);
        songList.setFocusableInTouchMode(true);
        menuPopup = new PopupWindow(songlistView);
        backgroundPopup = new PopupWindow(backgroundView);
        menuPopup.setFocusable(true);
        menuPopup.setOnDismissListener(this);
        
        playList = mMusicInfoController.getAllMedia( context);
        mListAdapter = new SongListAdapter(context, playList);
		songList.setAdapter(mListAdapter);
		//songList.notifyDataSetChanged();
        durationTextView = (TextView) findViewById(R.id.duration);
        playedTextView = (TextView) findViewById(R.id.has_played);
        
        mGestureDetector = new GestureDetector(this, new MyOnGestureListener());
        
        list_Btn = (ImageButton) findViewById(R.id.list_btn);
        last_Btn = (ImageButton) findViewById(R.id.last_btn);
        play_Btn = (ImageButton) findViewById(R.id.play_btn);
        next_Btn = (ImageButton) findViewById(R.id.next_btn);
       // full_Btn = (ImageButton ) findViewById(R.id.full_screen_btn);
        surView = (SurfaceView)findViewById(R.id.surView);
        surHolder = surView.getHolder();
        toolLayout=(View)findViewById(R.id.toolbar);
        surView.setOnTouchListener( new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				mGestureDetector.onTouchEvent(event);
				return true;
			}
		});
        
        list_Btn.setOnClickListener(this);
        next_Btn.setOnClickListener(this);
        play_Btn.setOnClickListener(this);  
       // full_Btn.setOnClickListener(this);
        //last one;
        last_Btn.setOnClickListener(this);        
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

				@Override
				public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
					// TODO Auto-generated method stub
					if(fromUser){

						mMusicPlayerService.seek(progress);
					}
				}
	
				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub
					
				}
	
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
				
				}
        	});
 
  
   }
    
    class MyOnGestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.i(getClass().getName(), "onSingleTapUp-----" + getActionName(e.getAction()));
            if(!mCurScreenMode)
            {
            	toolLayout.setVisibility(View.VISIBLE);
            	myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
            }
            else
            {
            	toolLayout.setVisibility(View.GONE);
            }
            mCurScreenMode = !mCurScreenMode;
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i(getClass().getName(), "onLongPress-----" + getActionName(e.getAction()));
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i(getClass().getName(),
                    "onScroll-----" + getActionName(e2.getAction()) + ",(" + e1.getX() + "," + e1.getY() + ") ,("
                            + e2.getX() + "," + e2.getY() + ")");
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.i(getClass().getName(),
                    "onFling-----" + getActionName(e2.getAction()) + ",(" + e1.getX() + "," + e1.getY() + ") ,("
                            + e2.getX() + "," + e2.getY() + ")");
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.i(getClass().getName(), "onShowPress-----" + getActionName(e.getAction()));
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.i(getClass().getName(), "onDown-----" + getActionName(e.getAction()));
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(getClass().getName(), "onDoubleTap-----" + getActionName(e.getAction()));
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.i(getClass().getName(), "onDoubleTapEvent-----" + getActionName(e.getAction()));
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i(getClass().getName(), "onSingleTapConfirmed-----" + getActionName(e.getAction()));
            return false;
        }
    }
    

 
    private String getActionName(int action) {
        String name = "";
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                name = "ACTION_DOWN";
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                name = "ACTION_MOVE";
                break;
            }
            case MotionEvent.ACTION_UP: {
                name = "ACTION_UP";
                break;
            }
            default:
            break;
        }
        return name;
    }

    public void loopPlay()//next one
    {
    	int n = playList.size();

    	if(++position==n)
    	{
    		position =0;
    	}
 
		setPlayDataSource();
    }
    
    public void leftLoopPlay()
    {
    	int n = playList.size();
		if(--position<0)
		{
			position =n-1;
		}
		setPlayDataSource();
    }

	private final static int PROGRESS_CHANGED = 0;
	private final static int HIDE_CONTROLER = 1;
    Handler myHandler = new Handler(){
    
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch(msg.what){
			
				case PROGRESS_CHANGED:
					
					int i = mMusicPlayerService.getPosition();
					seekBar.setProgress(i);
					seekBar.setSecondaryProgress(0);
 
					i/=1000;
					int minute = i/60;
					int hour = minute/60;
					int second = i%60;
					minute %= 60;
					playedTextView.setText(String.format("%02d:%02d:%02d", hour,minute,second));
					
					sendEmptyMessageDelayed(PROGRESS_CHANGED, 100);
					break;
				case HIDE_CONTROLER:
					if(toolLayout.getVisibility() == View.VISIBLE && mCurPlayMode == VIDEO_MODE)
					{				
						toolLayout.setVisibility(View.GONE);
						mCurScreenMode = false;
					}

					break;
			}
			super.handleMessage(msg);
		}	
    };
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub

			if(!playList.get(position).bAudio &&null != mMusicPlayerService && mMusicPlayerService.isPlaying())
			{
				playedTime = mMusicPlayerService.getPosition();
				mMusicPlayerService.pause();
				play_Btn.setImageResource(R.drawable.btn_play_normal);
				bResumeStart = true;
			}

		super.onPause();  
	}
	@Override
	protected void onResume() 
	{
		if(playList.size()>0 &&!playList.get(position).bAudio && 0 !=playedTime){
			 
			mMusicPlayerService.seek(playedTime);
			mMusicPlayerService.start(); 
		} 
 
		super.onResume();
	}

    
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
 
		if(menuPopup.isShowing()){
			menuPopup.dismiss();
		}
		if(backgroundPopup.isShowing())
		{
			backgroundPopup.dismiss();
		}
		myHandler.removeMessages(PROGRESS_CHANGED);
		myHandler.removeMessages(HIDE_CONTROLER);
		
		if(mMusicPlayerService.isPlaying()){
			mMusicPlayerService.stop();
		}
		
		if(mMusicPlayerService!=null )
		{
			unbindService(mPlaybackConnection);
			stopService(new Intent(this, MusicPlayerService.class));
			
		}
		
		playList.clear();		
		super.onDestroy();
	}     

 
	@Override
	public void onClick(View v) {
		
		switch(v.getId())
		{
		case R.id.list_btn:
		{
			popupMenuWindow();
		}
		break;
		case R.id.last_btn:
		{
			leftLoopPlay();
		}
		break;
		case R.id.play_btn:
		{
            if (mMusicPlayerService != null && mMusicPlayerService.isPlaying()) {
            	mMusicPlayerService.pause();
				 
				play_Btn.setImageResource(R.drawable.btn_play_normal);
            } else if (mMusicPlayerService != null){
            	if(bFirstPlay)
            	{
            		position = 0;
            		setPlayDataSource();
            		bFirstPlay = false;
            	}
            	else{
                	mMusicPlayerService.start();
    				play_Btn.setImageResource(R.drawable.btn_pause_normal);
            	}
            }  
		}
		break;
		case R.id.next_btn:
		{
			loopPlay();
		}
		break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		position = arg2;
		setPlayDataSource();
	}
	
	private void popupMenuWindow()
	{
		if(menuPopup != null){
			if(!menuPopup.isShowing())
			{
				
				ColorDrawable cd = new ColorDrawable(-0000);
				menuPopup.setBackgroundDrawable(cd);
				menuPopup.showAtLocation(toolLayout, Gravity.LEFT|Gravity.TOP, 0, 0);
				menuPopup.update(15,0,screenWidth,screenHeight-toolLayout.getHeight());
			}
			else
			{
				menuPopup.dismiss();
			}
		}
	}
	
	public void setPlayDataSource()
	{
		MovieInfo info = playList.get(position);
        //surView.setVisibility(View.VISIBLE);
        fill_view.setVisibility(View.GONE);
        
		if(info.bAudio)
		{
	        if ( getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
	          	 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	             getDisPlayMetric();
	          }
	 
			toolLayout.setVisibility(View.VISIBLE);
			if(!menuPopup.isShowing())
				backgroundPopupWidow();
			mCurPlayMode = AUDIO_MODE;
		}
		else
		{
			background_Image.setVisibility(View.GONE);
			backgroundPopup.dismiss();
			menuPopup.dismiss();
			toolLayout.setVisibility(View.GONE);
			mCurPlayMode = VIDEO_MODE;
		}

		mListAdapter.setSelectItem(position);//
		((BaseAdapter) mListAdapter).notifyDataSetInvalidated();
		mMusicPlayerService.setDataSource(info.path);
	}
	
	public void getDisPlayMetric()
	{
		DisplayMetrics metric = new DisplayMetrics();
		 getWindowManager().getDefaultDisplay().getMetrics(metric); 
//		System.out.println(metric.toString());
		 screenWidth = metric.widthPixels;  // 屏幕宽度（像素） 
		 screenHeight = metric.heightPixels;  // 屏幕高度（像素） 
	}


    private void backgroundPopupWidow()
    {
		if(!backgroundPopup.isShowing())
		{
			View toolLayout=(View)findViewById(R.id.toolbar);
			ColorDrawable cd = new ColorDrawable(-0000);
			backgroundPopup.setBackgroundDrawable(cd);
			backgroundPopup.showAtLocation(toolLayout, Gravity.LEFT|Gravity.TOP, 0, 0);
			backgroundPopup.update(15,0,screenWidth,screenHeight-toolLayout.getHeight());
		}

		background_Image.setVisibility(View.VISIBLE);
		background_Image.setText(playList.get(position).displayName);
    }
	@Override
	public void onDismiss() {
		// TODO Auto-generated method stub
		if(playList.get(position).bAudio)
		  backgroundPopupWidow();
	}
}