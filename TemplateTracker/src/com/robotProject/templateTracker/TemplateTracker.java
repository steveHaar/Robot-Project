package com.robotProject.templateTracker;

import java.text.DecimalFormat;

import org.opencv.android.*;

import com.robotProject.templateTracker.R;
import com.robotProject.video.cameraPreview.*;
import com.robotProject.video.cameraPreview.*;
import com.robotProject.video.events.template.*;
import com.robotProject.video.events.test.*;
import com.robotProject.video.listeners.template.*;
import com.robotProject.video.processors.*;
import com.robotProject.video.processors.template.*;
import android.graphics.*;
import android.graphics.PorterDuff.Mode;
import android.graphics.Paint.*;
import android.graphics.Rect;
import android.hardware.*;
import android.hardware.Camera.*;
import android.os.*;
import android.app.*;
import android.util.*;
import android.view.*;
import android.view.SurfaceHolder.Callback;
import android.widget.*;

@SuppressWarnings("unused")
public class TemplateTracker extends Activity implements ICameraPreviewCallback, ITemplateFrameProcessedListener, Callback {
	private static final String TAG = "CameraTest::MainActivity";
	
	private TemplateFrameProcessor mFrameProcessor;
	private CameraPreview mPreview;
	private SurfaceView mCameraOverlay;
	private FrameLayout mFrameLayout;
	
	private Button mTrackButton;
	private TextView mFpsIndicator;
	private long mTotalTime = 0;
	private int mFpsIteration = 0;
	private int mFpsStep = 10;
	private int mDrawIteration = 0;
	private int mDrawStep = 1;
	
	private Paint mRectPaint = new Paint();
	private DecimalFormat mDecimalFormat = new DecimalFormat("#.##");

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mFrameProcessor = new TemplateFrameProcessor(this);
        mPreview = new CameraPreview(this, mFrameProcessor);
        
        mTrackButton = (Button)findViewById(R.id.trackButton);
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
    
    public void trackButton_Click(View arg) {
    	mTrackButton.setVisibility(View.GONE);
    	mFrameProcessor.setViewMode(1);
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
    	
    	mCameraOverlay.getHolder().addCallback(this);
    }
    
    public void onFrameProcessed(TemplateFrameProcessedEvent event) {
    	mDrawIteration++;
    	mFpsIteration++;
    	mTotalTime += event.time;
    	
		if (mFpsIteration == mFpsStep) {
    		double fps = (1000.0 * mFpsStep) / mTotalTime;
    		mFpsIndicator.setText(mDecimalFormat.format(fps));
    		mTotalTime = mFpsIteration = 0;
    	}
		
		if (mDrawIteration % mDrawStep == 0) {
			String fps = mDecimalFormat.format((mDrawIteration * 1000.0) / event.time);
			Canvas canvas = mCameraOverlay.getHolder().lockCanvas();
			if (canvas != null) {
				canvas.drawColor(0, Mode.CLEAR);
				canvas.drawRect(event.rect, mRectPaint);
				mCameraOverlay.getHolder().unlockCanvasAndPost(canvas);
			}
			mDrawIteration = 0;
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		double cameraAspectRatio = (double)mFrameProcessor.getFrameSize().width / mFrameProcessor.getFrameSize().height;
		double screenAspectRatio = (double)mFrameLayout.getWidth() / mFrameLayout.getHeight();
		Log.i("Camera: " + mFrameProcessor.getFrameSize().width + " x " + mFrameProcessor.getFrameSize().height, TAG);
		Log.i("Screen: " + mFrameLayout.getWidth() + " x " + mFrameLayout.getHeight(), TAG);
		if (cameraAspectRatio < screenAspectRatio) {
			int newWidth = (int)Math.floor(mFrameLayout.getHeight() * cameraAspectRatio);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(newWidth, mFrameLayout.getHeight());
			lp.leftMargin = (mFrameLayout.getWidth() - newWidth) / 2;
			mFrameLayout.setLayoutParams(lp);
		}
		else if (cameraAspectRatio > screenAspectRatio) {
			int newHeight = (int)Math.floor(mFrameLayout.getWidth() / cameraAspectRatio);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mFrameLayout.getWidth(), newHeight);
			lp.topMargin = (mFrameLayout.getHeight() - newHeight) / 2;
			mFrameLayout.setLayoutParams(lp);
		}
		mCameraOverlay.getHolder().setFixedSize(mFrameProcessor.getFrameSize().width, mFrameProcessor.getFrameSize().height);
		mCameraOverlay.invalidate();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
}
