package com.hulzenga.ioi_apps.app_006;

import java.util.ArrayList;
import java.util.List;

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
import com.hulzenga.ioi_apps.app_006.SettingIconFragment.ParameterGroup;

public class SimpleCameraActivity extends DemoActivity implements SettingChangeListener {

    private static final String TAG             = "SIMPLE_CAM_ACTIVITY";

    private Camera              mCamera;
    private SimpleCameraPreview mPreview;

    private int                 mSelectedCamera = 0;

    private Fragment            mActiveFragment;
    private SettingIconFragment mCameraFragment;
    private SettingIconFragment mFlashFragment;
    private SettingIconFragment mColorEffectFragment;
    private ExposureFragment    mExposureFragment;

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
        List<String> cameraFacings = new ArrayList<String>();
        CameraInfo info = new CameraInfo();

        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, info);
            cameraFacings.add(String.valueOf(info.facing));
        }

        mCameraFragment = SettingIconFragment.newInstance(this, cameraFacings, ParameterGroup.CAMERA);
    }

    private void lock() {

    }

    private void release() {

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
        toggleActiveFragment(mCameraFragment, R.id.app_006_centerFragmentContainer);
    }

    public void toggleFlashFragment(View view) {
        toggleActiveFragment(mFlashFragment, R.id.app_006_centerFragmentContainer);
    }

    public void toggleFilterFragment(View view) {
        toggleActiveFragment(mColorEffectFragment, R.id.app_006_centerFragmentContainer);
    }

    public void toggleIntensityFragment(View view) {
        toggleActiveFragment(mExposureFragment, R.id.app_006_bottomFragmentContainer);
    }

    public void toggleSettingsFragment(View view) {

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

            mFlashFragment = SettingIconFragment.newInstance(SimpleCameraActivity.this,
                    params.getSupportedFlashModes(), ParameterGroup.FLASH);
            mColorEffectFragment = SettingIconFragment.newInstance(SimpleCameraActivity.this,
                    params.getSupportedColorEffects(), ParameterGroup.EFFECTS);

            mExposureFragment = ExposureFragment.newInstance(SimpleCameraActivity.this,
                    params.getMinExposureCompensation(), params.getExposureCompensation(),
                    params.getMaxExposureCompensation());

            mPreview.loadCamera(mCamera);

            // release buttons back to the user
            release();
        }

    }

    private void selectCamera(int cameraId) {
        if (mCamera != null) {
            mCamera.release();
        }

        // no input allowed while opening a new camera
        lock();
        new OpenCameraTask().execute(cameraId);

        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
    }

    @Override
    public void changeSetting(int type, Object newSetting) {
        Parameters params = mCamera.getParameters();

        switch (type) {
        case CAMERA:
            selectCamera((Integer) newSetting);
            break;
        case COLOR_EFFECT:
            mPreview.stopPreview();
            params.setColorEffect((String) newSetting);
            mCamera.setParameters(params);
            String effect = mCamera.getParameters().getColorEffect();
            mPreview.startPreview();
            break;
        case EXPOSURE:
            mPreview.stopPreview();
            params.setExposureCompensation((Integer) newSetting);
            mCamera.setParameters(params);
            mPreview.startPreview();
            break;
        }
    }
}
