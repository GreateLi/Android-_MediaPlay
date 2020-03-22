package com.jc.mediaplayer;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

public class VideoView extends SurfaceView {

    private int         mVideoWidth;
    private int         mVideoHeight;
    private int         mSurfaceWidth;
    private int         mSurfaceHeight;
    
	public VideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

    public int getVideoWidth(){
    	return mVideoWidth;
    }
    
    public int getVideoHeight(){
    	return mVideoHeight;
    }
    
	@Override
	public SurfaceHolder getHolder() {
		// TODO Auto-generated method stub
		return super.getHolder();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
       
        setMeasuredDimension(width,height);
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
    public void setVideoScale(int width , int height){
    	LayoutParams lp = getLayoutParams();
    	lp.height = height;
		lp.width = width;
		setLayoutParams(lp);
    }
    
    private void initVideoView() {
        mVideoWidth = 0;
        mVideoHeight = 0;
 
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }

}
