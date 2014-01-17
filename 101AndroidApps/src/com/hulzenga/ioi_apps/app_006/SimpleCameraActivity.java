package com.hulzenga.ioi_apps.app_006;

import java.util.HashMap;
import java.util.Map;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.hulzenga.ioi_apps.DemoActivity;
import com.hulzenga.ioi_apps.R;

public class SimpleCameraActivity extends DemoActivity {

    private static final String    TAG                   = "SIMPLE_CAM_ACTIVITY";

    private Camera                 mCamera;
    private SimpleCameraPreview    mPreview;

    private Fragment               mActiveFragment;
    private CameraFragment         mCameraFragment       = new CameraFragment();
    private FilterFragment         mFilterFragment       = new FilterFragment();
    private IntensityFragment      mIntensityFragment    = new IntensityFragment();

    private Map<Fragment, Integer> mFragmentContainerMap = new HashMap<Fragment, Integer>();
    {
        mFragmentContainerMap.put(mCameraFragment, R.id.app_006_cameraFragmentContainer);
        mFragmentContainerMap.put(mFilterFragment, R.id.app_006_filterFragmentContainer);
        mFragmentContainerMap.put(mIntensityFragment, R.id.app_006_intensityFragmentContainer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide action bar
        getActionBar().hide();

        // hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.app_006_activity_simple_cam);

        // App needs the camera feature, if it doesn't exist exit
        if (!hasCameraFeature(this)) {
            // TODO: do something fancier than this
            Log.d(TAG, "does not have camera feature");
            finish();
        }

        mCamera = getCameraInstance();

        mPreview = new SimpleCameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.app_006_cameraPreview);
        preview.addView(mPreview);
    }

    private boolean hasCameraFeature(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            return true;
        } else {
            return false;
        }
    }

    private static Camera getCameraInstance() {
        Camera camera = null;
        Log.d(TAG, "Number of cameras = " + Camera.getNumberOfCameras());
        try {
            camera = Camera.open(0);
        } catch (Exception e) {
            Log.e(TAG, "Could not open camera");
        }

        return camera;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
    
    public void toggleCameraFragment(View view) {
        toggleActiveFragment(mCameraFragment);
    }
    
    public void toggleFilterFragment(View view) {
        toggleActiveFragment(mFilterFragment);
    }

    public void toggleIntensityFragment(View view) {
        toggleActiveFragment(mIntensityFragment);
    }
    
    public void toggleActiveFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (mActiveFragment == fragment) {
            fragmentTransaction.remove(fragment);
            mActiveFragment = null;
        } else {

            if (mActiveFragment != null) {
                fragmentTransaction.remove(mActiveFragment);
            }

            fragmentTransaction.add(mFragmentContainerMap.get(fragment), fragment);
            mActiveFragment = fragment;
        }

        fragmentTransaction.commit();
    }
    
    
    
}
