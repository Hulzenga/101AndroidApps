package com.hulzenga.ioi_apps.app_004;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.hulzenga.ioi_apps.DemoActivity;

public class BouncyBall3dActivity extends DemoActivity {

    private BouncyBall3dGLSurfaceView mGLSurfaceView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mGLSurfaceView = new BouncyBall3dGLSurfaceView(this);
        //mGLSurfaceView.setEGLContextClientVersion(2);
        

        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }
    
    

}
