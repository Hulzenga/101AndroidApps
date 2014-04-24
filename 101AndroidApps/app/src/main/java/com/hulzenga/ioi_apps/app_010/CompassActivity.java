package com.hulzenga.ioi_apps.app_010;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import com.hulzenga.ioi_apps.DemoActivity;
import com.hulzenga.ioi_apps.R;

/**
 * Created by jouke on 24-4-14.
 */
public class CompassActivity extends DemoActivity{

  private CompassView   mCompassView;
  private SensorManager mSensorManager;
  private SensorEventListener mSensorEventListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      mCompassView.updateOrientation(event.values);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.app_010_activity_compass);

    mCompassView = (CompassView) findViewById(R.id.app_010_compassView);
    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
  }

  @Override
  protected void onPause() {
    super.onPause();
    mSensorManager.unregisterListener(mSensorEventListener);
  }
}
