package com.hulzenga.ioi_apps.app_006;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.hulzenga.ioi_apps.DemoActivity;
import com.hulzenga.ioi_apps.R;

public class SimpleCameraActivity extends DemoActivity implements SettingChangeListener {

    private static final String  TAG                = "SIMPLE_CAM_ACTIVITY";

    private Camera               mCamera;
    private SimpleCameraPreview  mPreview;

    private int                  mSelectedCamera    = 0;

    private Fragment             mActiveFragment;
    private CameraFragment       mCameraFragment;
    private ColorEffectsFragment mFilterFragment;
    private IntensityFragment    mIntensityFragment = new IntensityFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // App needs the camera feature, if it doesn't exist exit
        if (!hasCameraFeature(this)) {
            // TODO: do something fancier than this
            Log.d(TAG, "does not have camera feature");
            finish();
        }

        // hide action bar
        getActionBar().hide();

        // hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.app_006_activity_simple_cam);

        // linkup all views that need to be
        mPreview = (SimpleCameraPreview) findViewById(R.id.app_006_cameraPreview);

        // discover cameras
        discoverCameras();

    }

    private boolean hasCameraFeature(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)
                || context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            return true;
        } else {
            return false;
        }
    }

    private void discoverCameras() {
        int[] facing = new int[Camera.getNumberOfCameras()];
        CameraInfo info = new CameraInfo();

        for (int i = 0; i < facing.length; i++) {
            Camera.getCameraInfo(i, info);
            facing[i] = info.facing;
        }
        mCameraFragment = CameraFragment.newInstance(this, facing);
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectCamera(mSelectedCamera);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
            // mPreview.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void toggleCameraFragment(View view) {

        toggleActiveFragment(mCameraFragment, R.id.app_006_sideFragmentContainer);
    }

    public void toggleFilterFragment(View view) {
        toggleActiveFragment(mFilterFragment, R.id.app_006_sideFragmentContainer);
    }

    public void toggleIntensityFragment(View view) {
        toggleActiveFragment(mIntensityFragment, R.id.app_006_bottomFragmentContainer);
    }

    public void toggleActiveFragment(Fragment fragment, int container) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (mActiveFragment == fragment) {
            fragmentTransaction.remove(fragment);
            mActiveFragment = null;
        } else {
            if (mActiveFragment != null) {
                fragmentTransaction.remove(mActiveFragment);
            }
            fragmentTransaction.add(container, fragment);
            mActiveFragment = fragment;
        }
        fragmentTransaction.commit();
    }

    private class OpenCameraTask extends AsyncTask<Integer, Integer, Camera> {

        @Override
        protected Camera doInBackground(Integer... params) {
            Camera camera = null;
            try {
                camera = Camera.open(params[0]);
            } catch (Exception e) {
                Log.e(TAG, "Failed to open camera: " + e.getMessage());
            }
            mSelectedCamera = params[0];
            return camera;
        }

        @Override
        protected void onPostExecute(Camera result) {
            mCamera = result;

            /*
             * create the option fragments based on opened camera parameters
             */
            Parameters params = mCamera.getParameters();

            mFilterFragment = ColorEffectsFragment.newInstance(SimpleCameraActivity.this,
                    params.getSupportedColorEffects());
            
            mPreview.loadCamera(mCamera);
        }

    }

    @Override
    public void selectCamera(int cameraId) {
        if (mCamera != null) {
            mCamera.release();
        }
        new OpenCameraTask().execute(cameraId);

        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);

        mIntensityFragment = new IntensityFragment();
    }

    @Override
    public void changeSetting(SettingType type, String newSetting) {
        switch (type) {
        case COLOR_EFFECT:
           
            Parameters params = mCamera.getParameters();
            params.setColorEffect(newSetting);
            mCamera.setParameters(params);
            break;
        }
    }
}
