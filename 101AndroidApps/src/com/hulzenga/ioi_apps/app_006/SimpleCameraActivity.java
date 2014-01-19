package com.hulzenga.ioi_apps.app_006;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.hulzenga.ioi_apps.DemoActivity;
import com.hulzenga.ioi_apps.R;
import com.hulzenga.ioi_apps.app_006.CameraFragment.CameraSelectionListener;

public class SimpleCameraActivity extends DemoActivity implements CameraSelectionListener {

    private static final String TAG                = "SIMPLE_CAM_ACTIVITY";

    private Camera              mCamera;
    private SimpleCameraPreview mPreview;

    private Fragment            mActiveFragment;
    private CameraFragment      mCameraFragment;
    private FilterFragment      mFilterFragment    = new FilterFragment();
    private IntensityFragment   mIntensityFragment = new IntensityFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(this, "cameras = " + Camera.getNumberOfCameras(), Toast.LENGTH_LONG).show();
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
        discoverCameras();

        mPreview = (SimpleCameraPreview) findViewById(R.id.app_006_cameraPreview);
        selectCamera(0);
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
        mCameraFragment = CameraFragment.newInstance(facing);
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
            mPreview.stopPreview();
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

    private int mSelectedCamera = -1;
    
    public void selectCamera(int camera) {
        if (mCamera != null) {
            mCamera.release();
        }
        new OpenCameraTask().execute(camera);
    }
    
    private void previewOpenCamera(Camera camera) {
        mCamera = camera;
        mPreview.loadCamera(mCamera);
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
            previewOpenCamera(result);
        }

    }
}
