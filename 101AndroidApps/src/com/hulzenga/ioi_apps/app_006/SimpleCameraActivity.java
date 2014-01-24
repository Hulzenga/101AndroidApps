package com.hulzenga.ioi_apps.app_006;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hulzenga.ioi_apps.DemoActivity;
import com.hulzenga.ioi_apps.R;
import com.hulzenga.ioi_apps.app_006.PickIconFragment.ParameterGroup;
import com.hulzenga.ioi_apps.util.DeveloperTools;
import com.hulzenga.ioi_apps.util.DeveloperTools.Statefulness;
import com.hulzenga.ioi_apps.util.FileManager;

public class SimpleCameraActivity extends DemoActivity implements SettingChangeListener {

    private static final String TAG             = "SIMPLE_CAM_ACTIVITY";

    private Camera              mCamera;
    private PictureCallback     mPicture;

    private SimpleCameraPreview mPreview;

    private List<ImageButton>   mImageButtons;
    private ImageButton         mCameraSelectButton;
    private ImageButton         mFlashButton;
    private ImageButton         mColorEffectButton;
    private ImageButton         mExposureButton;
    private ImageButton         mSettingButton;
    private ImageButton         mCaptureButton;

    private Drawable            mCaptureButtonStart;
    private Drawable            mCaptureButtonPause;

    private int                 mSelectedCamera = 0;

    private Fragment            mActiveFragment;
    private Fragment            mActiveLevel2MenuFragment;
    private PickIconFragment    mCameraFragment;
    private PickIconFragment    mFlashFragment;
    private PickIconFragment    mColorEffectFragment;
    private ExposureFragment    mExposureFragment;
    private SettingMenuFragment mSettingMenuFragment;
    private EmptyFragment       mEmptyFragment  = EmptyFragment.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // App needs the camera feature, if it doesn't exist exit
        if (!hasCameraFeature(this)) {
            Toast.makeText(this, "No camera available", Toast.LENGTH_LONG).show();
            Log.d(TAG, "does not have camera feature");
            finish();
        }

        // hide action bar
        getActionBar().hide();

        // hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // inflate main activity view
        setContentView(R.layout.app_006_activity_simple_cam);

        // acquire required view references
        mPreview = (SimpleCameraPreview) findViewById(R.id.app_006_cameraPreview);

        mCameraSelectButton = (ImageButton) findViewById(R.id.app_006_cameraSelectButton);
        mFlashButton = (ImageButton) findViewById(R.id.app_006_flashButton);
        mColorEffectButton = (ImageButton) findViewById(R.id.app_006_colorEffectButton);
        mExposureButton = (ImageButton) findViewById(R.id.app_006_exposureButton);
        mSettingButton = (ImageButton) findViewById(R.id.app_006_settingsButton);
        mCaptureButton = (ImageButton) findViewById(R.id.app_006_captureButton);

        // add all image buttons together in a list for easy access
        mImageButtons = new ArrayList<ImageButton>();
        mImageButtons.add(mCameraSelectButton);
        mImageButtons.add(mFlashButton);
        mImageButtons.add(mColorEffectButton);
        mImageButtons.add(mExposureButton);
        mImageButtons.add(mSettingButton);
        mImageButtons.add(mCaptureButton);

        // make image buttons stateful for a clearer user experience
        for (ImageButton imageButton : mImageButtons) {
            DeveloperTools.makeImageButtonStateful(imageButton, Statefulness.greyWhenDisabled);
        }

        // get drawables for the capture button so these can be controlled
        // programmatically
        mCaptureButtonStart = mCaptureButton.getDrawable();
        mCaptureButtonPause = getResources().getDrawable(R.drawable.app_006_pause_capture);

        // setup camera callbacks
        mPicture = new PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                File picture = FileManager.GetOutputMediaFile(FileManager.MEDIA_TYPE_IMAGE);
                if (picture == null) {
                    Log.d(TAG, "Failed to create image file, check permissions");
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(picture);
                    fos.write(data);
                    fos.close();
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                }

                mPreview.startPreview();
            }
        };

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

        mCameraFragment = PickIconFragment
                .newInstance(this, cameraFacings.get(0), cameraFacings, ParameterGroup.CAMERA);
    }

    /**
     * lock all buttons to ensure there is no input during critical operations
     * such as opening a camera or taking a picture
     */
    private void lock() {
        for (ImageButton imageButton : mImageButtons) {
            imageButton.setEnabled(false);
        }
    }

    /**
     * releases those buttons which can be used by the current camera
     */
    private void release() {
        mCameraSelectButton.setEnabled(true);
        Parameters params = mCamera.getParameters();
        if (params.getSupportedFlashModes() != null) {
            mFlashButton.setEnabled(true);
        }

        if (params.getSupportedColorEffects() != null) {
            mColorEffectButton.setEnabled(true);
        }

        if (params.getMinExposureCompensation() != params.getMaxExposureCompensation()) {
            mExposureButton.setEnabled(true);
        }

        mSettingButton.setEnabled(true);
        mCaptureButton.setEnabled(true);
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

    public void toggleColorEffectFragment(View view) {
        toggleActiveFragment(mColorEffectFragment, R.id.app_006_centerFragmentContainer);
    }

    public void toggleExposureFragment(View view) {
        toggleActiveFragment(mExposureFragment, R.id.app_006_bottomFragmentContainer);
    }

    public void toggleActiveFragment(Fragment fragment, int container) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        if (mActiveFragment == fragment) {
            // fragmentManager.popBackStack("app_006.level1",
            // FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.remove(fragment);
            mActiveFragment = null;
        } else {
            if (mActiveFragment != null) {
                // the setting menu requires additional deletion of the sub menu
                if (mActiveFragment == mSettingMenuFragment) {
                    if (mActiveLevel2MenuFragment != null) {
                        ft.remove(mActiveLevel2MenuFragment);
                        mActiveLevel2MenuFragment = null;
                        mOpenSubMenu = null;
                    } else {
                        ft.remove(mEmptyFragment);
                    }
                }
                ft.remove(mActiveFragment);
            }
            ft.add(container, fragment);
            // fragmentTransaction.addToBackStack("app_006.level1");
            mActiveFragment = fragment;
        }

        ft.commit();
    }

    public void toggleSettingsFragment(View view) {
        // toggleActiveFragment(mSettingMenuFragment,
        // R.id.app_006_settingMenuContainer1);

        FragmentManager fm = getFragmentManager();

        if (mActiveFragment == mSettingMenuFragment) {
            if (mActiveLevel2MenuFragment != null) {
                fm.beginTransaction().remove(mActiveLevel2MenuFragment).remove(mSettingMenuFragment).commit();
            } else {
                fm.beginTransaction().remove(mEmptyFragment).remove(mSettingMenuFragment).commit();
            }
            mOpenSubMenu = null;
            mActiveLevel2MenuFragment = null;
            mActiveFragment = null;
        } else {
            if (mActiveFragment != null) {
                fm.beginTransaction().remove(mActiveFragment).commit();
            }
            fm.beginTransaction().add(R.id.app_006_settingMenuContainer1, mSettingMenuFragment)
                    .add(R.id.app_006_settingMenuContainer1, mEmptyFragment).commit();
            mActiveFragment = mSettingMenuFragment;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // mActiveFragment = null;
    }

    private boolean mVideoMode = false;

    public void toggleVideoOrPicture(View view) {

        // toggle videoMode
        mVideoMode = !mVideoMode;
    }

    private boolean mFilming = false;

    public void takeCapture(View view) {

        if (!mVideoMode) {
            mCamera.takePicture(null, null, mPicture);
        } else {
            if (!mFilming) {
                mFilming = true;
                mCaptureButton.setImageDrawable(mCaptureButtonPause);
            } else {
                mFilming = false;
                mCaptureButton.setImageDrawable(mCaptureButtonStart);
            }
        }
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

            mFlashFragment = PickIconFragment.newInstance(SimpleCameraActivity.this, params.getFlashMode(),
                    params.getSupportedFlashModes(), ParameterGroup.FLASH);
            mColorEffectFragment = PickIconFragment.newInstance(SimpleCameraActivity.this, params.getColorEffect(),
                    params.getSupportedColorEffects(), ParameterGroup.EFFECTS);

            mExposureFragment = ExposureFragment.newInstance(SimpleCameraActivity.this,
                    params.getMinExposureCompensation(), params.getExposureCompensation(),
                    params.getMaxExposureCompensation());

            mSettingMenuFragment = SettingMenuFragment.newInstance(params);
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
    public void changeSetting(ChangeType type, Object newSetting) {
        Parameters params = mCamera.getParameters();

        Log.d(TAG, "Setting Change requested: " + String.valueOf(newSetting));
        switch (type) {
        case CAMERA:
            selectCamera((Integer) newSetting);
            toggleCameraFragment(null);
            break;
        case FLASH:
            mPreview.stopPreview();
            params.setFlashMode((String) newSetting);
            mCamera.setParameters(params);
            mPreview.startPreview();
            toggleFlashFragment(null);
            break;
        case COLOR_EFFECT:
            mPreview.stopPreview();
            params.setColorEffect((String) newSetting);
            mCamera.setParameters(params);
            mPreview.startPreview();
            toggleColorEffectFragment(null);
            break;
        case EXPOSURE:
            mPreview.stopPreview();
            params.setExposureCompensation((Integer) newSetting);
            mCamera.setParameters(params);
            mPreview.startPreview();
            break;
        case FOCUS:
            mPreview.stopPreview();
            params.setFocusMode((String) newSetting);
            mCamera.setParameters(params);
            mPreview.startPreview();
            break;
        case SCENE_MODE:
            mPreview.stopPreview();
            params.setSceneMode((String) newSetting);
            mCamera.setParameters(params);
            mPreview.startPreview();
            break;
        case ISO:
            mPreview.stopPreview();
            params.set("iso", (String) newSetting);
            mCamera.setParameters(params);
            mPreview.startPreview();
            break;
        case WHITE_BALANCE:
            mPreview.stopPreview();
            params.setSceneMode((String) newSetting);
            mCamera.setParameters(params);
            mPreview.startPreview();
            break;
        default:
            Log.w(TAG,
                    "changeSeting was called with an unknown type: " + String.valueOf(type) + ", "
                            + String.valueOf(newSetting));
            break;
        }
    }

    private ChangeType mOpenSubMenu;

    @Override
    public void open2ndLevelSetting(ChangeType type) {

        Parameters params = mCamera.getParameters();
        FragmentManager fm = getFragmentManager();

        // First remove existing level 2 fragment
        if (mActiveLevel2MenuFragment != null) {
            fm.beginTransaction().remove(mActiveLevel2MenuFragment).commit();
        } else {
            fm.beginTransaction().remove(mEmptyFragment).commit();
        }

        FragmentTransaction ft = fm.beginTransaction();
        // check if this is meant to close the already opened sub menu
        if (type == mOpenSubMenu) {
            // close the opened sub menu
            mActiveLevel2MenuFragment = null;
            mOpenSubMenu = null;
            ft.add(R.id.app_006_settingMenuContainer1, mEmptyFragment);
        } else {
            // open a new sub menu
            switch (type) {
            case IMAGE_SIZE:
                // mActiveLevel2MenuFragment =
                // SettingMenuLvl2Fragment.newInstance(ChangeType.IMAGE_SIZE,
                // params.getPictureSize(),
                // params.getSupportedPictureSizes());
                break;
            case FOCUS:
                mActiveLevel2MenuFragment = SettingMenuLvl2Fragment.newInstance(ChangeType.FOCUS,
                        params.getFocusMode(), params.getSupportedFocusModes());
                break;
            case SCENE_MODE:
                mActiveLevel2MenuFragment = SettingMenuLvl2Fragment.newInstance(ChangeType.SCENE_MODE,
                        params.getSceneMode(), params.getSupportedSceneModes());
                break;
            case ISO:
                String current = "100";
                List<String> iso = new ArrayList<String>();
                iso.add("auto");
                iso.add("100");
                iso.add("200");
                iso.add("400");
                iso.add("800");
                iso.add("1600");
                mActiveLevel2MenuFragment = SettingMenuLvl2Fragment.newInstance(ChangeType.ISO, current, iso);
                break;
            case WHITE_BALANCE:
                mActiveLevel2MenuFragment = SettingMenuLvl2Fragment.newInstance(ChangeType.WHITE_BALANCE,
                        params.getWhiteBalance(), params.getSupportedWhiteBalance());
                break;
            default:
                Log.w(TAG, "A level 2 menu setting call was made with an invalid (or not yet implemented) type "
                        + String.valueOf(type));
                break;
            }
            mOpenSubMenu = type;
            ft.add(R.id.app_006_settingMenuContainer1, mActiveLevel2MenuFragment);
        }

        ft.commit();
    }
}
