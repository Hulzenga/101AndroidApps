package com.hulzenga.ioi_apps.app_006;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SimpleCameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    
    private static final String TAG = "CAMERA_PREVIEW";

    private SurfaceHolder       mHolder;
    private Camera              mCamera;
    
    public SimpleCameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        mHolder = getHolder();
        mHolder.addCallback(this);
        
    }   

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Parameters params = mCamera.getParameters();
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "Error setting camera preview: " + e.getMessage());
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        
        //make sure mHolder exists
        if (mHolder.getSurface() == null) {
            return;
        }
         
        
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            Log.e(TAG, "Failed to stop camera preview: " + e.getMessage());
        }                
        
        //this.setX(300.0f);
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "Error starting camera preview: " + e.getMessage());
        }        
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

}
