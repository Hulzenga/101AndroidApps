package com.hulzenga.ioi.android.app_010;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.hulzenga.ioi.android.AppActivity;
import com.hulzenga.ioi.android.R;

/**
 * Created by jouke on 24-4-14.
 */
public class CompassActivity extends AppActivity {

  private CompassView         mCompassView;
  private CompassDetailsView  mCompassDetailsView;
  private SensorManager       mSensorManager;
  private SensorEventListener mGravityListener;
  private SensorEventListener mMagneticFieldListener;
  private LocationManager     mLocationManager;
  private LocationListener    mLocationListener;

  private boolean showingDetails;

  private float[] mGravity                   = {0.0f, 0.0f, 9.8f};
  private int     mUnlockedScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_USER;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.app_010_activity_compass);

    //register views
    mCompassView = (CompassView) findViewById(R.id.app_010_compassView);
    mCompassDetailsView = (CompassDetailsView) findViewById(R.id.app_010_detailsView);

    //register managers
    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    //build listeners
    mGravityListener = new SensorEventListener() {
      @Override
      public void onSensorChanged(SensorEvent event) {

        System.arraycopy(event.values, 0, mGravity, 0, mGravity.length);
      }

      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    mMagneticFieldListener = new SensorEventListener() {

      final float[] R = new float[16];
      final float[] eulerAngles = new float[3];

      @Override
      public void onSensorChanged(SensorEvent event) {
        if (SensorManager.getRotationMatrix(R, null, mGravity, event.values)) {
          SensorManager.getOrientation(R, eulerAngles);
          mCompassView.updateOrientation(eulerAngles);

          mCompassDetailsView.updateOrientation(eulerAngles);
          mCompassDetailsView.addMeasurement((float) Math.sqrt(
                  event.values[0] * event.values[0] +
                      event.values[1] * event.values[1] +
                      event.values[2] * event.values[2]
              )
          );
        }
      }

      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    mLocationListener = new LocationListener() {
      @Override
      public void onLocationChanged(Location location) {
        final GeomagneticField field = new GeomagneticField(
            (float)location.getLatitude(),
            (float)location.getLongitude(),
            (float)location.getAltitude(),
            System.currentTimeMillis());

        //GeomagneticField gives field strength in nanoTesla while SensorManager uses microTesla
        //so divide by 1000.0f to take care of conversion
        mCompassDetailsView.setEstimatedFieldStrength(field.getFieldStrength()/1000.0f);
      }

      @Override
      public void onStatusChanged(String provider, int status, Bundle extras) {

      }

      @Override
      public void onProviderEnabled(String provider) {

      }

      @Override
      public void onProviderDisabled(String provider) {

      }
    };
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case R.id.app_010_action_show_details:

        if (showingDetails) {
          mCompassDetailsView.setVisibility(View.GONE);
          showingDetails = false;
        } else {
          mCompassDetailsView.setVisibility(View.VISIBLE);
          showingDetails = true;
        }

        return true;
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
    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000l, 1000.0f, mLocationListener);

  }

  @Override
  protected void onPause() {
    super.onPause();

    mSensorManager.unregisterListener(mMagneticFieldListener);
    mSensorManager.unregisterListener(mGravityListener);
    mLocationManager.removeUpdates(mLocationListener);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.app_010_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }
}
