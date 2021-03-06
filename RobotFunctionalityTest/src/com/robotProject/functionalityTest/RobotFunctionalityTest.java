package com.robotProject.functionalityTest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import com.robotProject.robot.*;
import com.robotProject.functionalityTest.R;

public class RobotFunctionalityTest extends Activity {
    private Robot robot;
	private TextView frontSonar;
    private TextView rearSonar;
    private SeekBar speedBar;
    private EditText distanceTextBox;
    
    public void moveForward_Click(View arg)
    {
    	robot.MoveForward(speedBar.getProgress());
    }
    
    public void moveForwardDist_Click(View arg)
    {
    	robot.MoveForward(speedBar.getProgress(), Integer.parseInt(distanceTextBox.getText().toString()));
    }
    
    public void moveBackward_Click(View arg)
    {
    	robot.MoveBackward(speedBar.getProgress());
    }
    
    public void moveBackwardDist_Click(View arg)
    {
    	robot.MoveBackward(speedBar.getProgress(), Integer.parseInt(distanceTextBox.getText().toString()));
    }
    
    public void spinLeft_Click(View arg)
    {
    	robot.SpinLeft(speedBar.getProgress());
    }
    
    public void spinLeftDist_Click(View arg)
    {
    	robot.SpinLeft(speedBar.getProgress(), Integer.parseInt(distanceTextBox.getText().toString()));
    }
    
    public void spinRight_Click(View arg)
    {
    	robot.SpinRight(speedBar.getProgress());
    }
    
    public void spinRightDist_Click(View arg)
    {
    	robot.SpinRight(speedBar.getProgress(), Integer.parseInt(distanceTextBox.getText().toString()));
    }
    
    public void stop_Click(View arg)
    {
    	robot.Stop();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        robot = new Robot(this);
        robot.addEventListener(frontSonarListener);
        robot.addEventListener(rearSonarListener);
        setContentView(R.layout.activity_main);
        frontSonar = (TextView) findViewById(R.id.frontSonar);
        rearSonar = (TextView) findViewById(R.id.rearSonar);
        speedBar = (SeekBar) findViewById(R.id.speedBar);
        distanceTextBox = (EditText) findViewById(R.id.distanceTextBox);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        robot.resume();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        robot.pause();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        robot.destroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void updateFrontSonarText(final String text){
    	this.runOnUiThread(new Runnable(){
    		public void run() {
    			frontSonar.setText("FRONT SONAR: " + text);
    		}
    	});
    }
    
    public void updateRearSonarText(final String text){
    	this.runOnUiThread(new Runnable(){
    		public void run() {
    			rearSonar.setText("REAR SONAR: " + text);
    		}
    	});
    }
    
    private final RobotListener frontSonarListener = new RobotListener(RobotEventType.EvtFrontSonar){
    	@Override
    	public void robotSignalReceived(RobotEvent event){
    		updateFrontSonarText(Integer.toString(event.getValue()));
		}
    };
    
    private final RobotListener rearSonarListener = new RobotListener(RobotEventType.EvtRearSonar){
    	@Override
    	public void robotSignalReceived(RobotEvent event){
    		updateRearSonarText(Integer.toString(event.getValue()));
		}
    };
}
