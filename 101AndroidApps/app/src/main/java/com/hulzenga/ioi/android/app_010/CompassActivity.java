package com.hulzenga.ioi.android.app_010;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.hulzenga.ioi.android.DemoActivity;
import com.hulzenga.ioi.android.R;

import java.util.Arrays;

/**
 * Created by jouke on 24-4-14.
 */
public class CompassActivity extends DemoActivity {

  private static final float MOVING_AVERAGE_FACTOR = 0.2f;

  private CompassView         mCompassView;
  private SensorManager       mSensorManager;
  private SensorEventListener mGravityListener;
  private SensorEventListener mMagneticFieldListener;

  private boolean mFilteringSensorData = false;

  private float[] mGravity       = {0.0f, 0.0f, 9.8f};
  private float[] mMagneticField = {0.0f, 0.0f, 0.0f};
  private SequentialMeasurementsView mFieldMeasurementsView;
  private int mUnlockedScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_USER;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.app_010_activity_compass);

    mCompassView = (CompassView) findViewById(R.id.app_010_compassView);
    mFieldMeasurementsView = (SequentialMeasurementsView) findViewById(R.id.app_010_fieldMeasurementsView);

    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    mGravityListener = new SensorEventListener() {
      @Override
      public void onSensorChanged(SensorEvent event) {
        mGravity = Arrays.copyOf(event.values, event.values.length);
      }

      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {

      }
    };

    mMagneticFieldListener = new SensorEventListener() {
      @Override
      public void onSensorChanged(SensorEvent event) {
        float[] R = new float[16];
        float[] eulerAngles = new float[3];

        if (mSensorManager.getRotationMatrix(R, null, mGravity, event.values)) {
          mSensorManager.getOrientation(R, eulerAngles);
          mCompassView.updateOrientation(eulerAngles);

          mFieldMeasurementsView.addMeasurement((float) Math.sqrt(
                  event.values[0] * event.values[0] +
                      event.values[1] * event.values[1] +
                      event.values[2] * event.values[2]
              )
          );
        }
      }

      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {

      }
    };
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case R.id.app_010_action_lock_rotation:
        int screenOrientation = getRequestedOrientation();
        if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LOCKED) {
          mUnlockedScreenOrientation = screenOrientation;
          setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
          item.setIcon(getResources().getDrawable(android.R.drawable.ic_lock_lock));
        } else {
          setRequestedOrientation(mUnlockedScreenOrientation);
          item.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_always_landscape_portrait));
        }
        return true;
      default:
        return super.onOptionsItemSelected(item);

    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    mSensorManager.registerListener(mGravityListener, mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_UI);
    mSensorManager.registerListener(mMagneticFieldListener, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
  }

  @Override
  protected void onPause() {
    super.onPause();

    mSensorManager.unregisterListener(mMagneticFieldListener);
    mSensorManager.unregisterListener(mGravityListener);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.app_010_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }
}
