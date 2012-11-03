package com.example.cameratest;

import org.opencv.android.*;
import com.robotProject.video.cameraPreview.*;
import com.robotProject.video.cameraPreview.*;
import com.robotProject.video.events.template.*;
import com.robotProject.video.events.test.*;
import com.robotProject.video.listeners.template.*;
import com.robotProject.video.processors.*;
import com.robotProject.video.processors.template.*;
import android.graphics.*;
import android.graphics.Paint.*;
import android.graphics.Rect;
import android.hardware.*;
import android.hardware.Camera.*;
import android.os.*;
import android.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;

@SuppressWarnings("unused")
public class MainActivity extends Activity implements ICameraPreviewCallback, ITemplateFrameProcessedListener {
	private static final String TAG = "CameraTest::MainActivity";
	
	private TemplateFrameProcessor frameProcessor;
	private CameraPreview mPreview;
	private SurfaceView mCameraOverlay;
	private FrameLayout mFrameLayout;
	
	private TextView mFpsIndicator;
	private long mTotalTime = 0;
	private long mIteration = 0;
	private Paint mRectPaint = new Paint();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        frameProcessor = new TemplateFrameProcessor(this);
        mPreview = new CameraPreview(this, frameProcessor);
        
        mFpsIndicator = (TextView)findViewById(R.id.timeIndicator);
        mFpsIndicator.setTextColor(Color.RED);
        mRectPaint.setARGB(255, 255, 0, 0);
        mRectPaint.setStyle(Style.STROKE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mPreview.resume();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        mPreview.pause();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        mPreview.destroy();
    }
    
    public void cameraPreviewLoaded() {
    	mCameraOverlay = new SurfaceView(this);
        mCameraOverlay.getHolder().setFormat(PixelFormat.TRANSPARENT);
    	
        mFrameLayout = (FrameLayout)findViewById(R.id.camera_preview);
    	mFrameLayout.addView(mCameraOverlay);
    	mFrameLayout.addView(mPreview);
    }
    
    public void onFrameProcessed(TemplateFrameProcessedEvent event) {
    	mIteration++;
		mTotalTime += event.time;
		if (mIteration == 100) {
    		long fps = 100000 / mTotalTime;
    		mIteration = mTotalTime = 0;
    		mFpsIndicator.setText(Long.toString(fps));
    	}
		Canvas canvas = mCameraOverlay.getHolder().lockCanvas();
		if (canvas != null) {
			canvas.drawRect(event.rect, mRectPaint);
			mCameraOverlay.getHolder().unlockCanvasAndPost(canvas);
		}
	}
}