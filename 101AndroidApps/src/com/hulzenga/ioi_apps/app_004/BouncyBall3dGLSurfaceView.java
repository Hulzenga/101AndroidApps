package com.hulzenga.ioi_apps.app_004;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class BouncyBall3dGLSurfaceView extends GLSurfaceView {

    private BouncyBall3dRenderer mRenderer;

    public BouncyBall3dGLSurfaceView(Context context) {
        super(context);

        mRenderer = new BouncyBall3dRenderer(context);
        
        setEGLContextClientVersion(2);
        
        setRenderer(mRenderer);
    }

    private float              mPreviousX         = 0.0f;
    private float              mPreviousY         = 0.0f;

    private static final float TOUCH_SCALE_FACTOR = 180.0f / 320;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                  dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                  dy = dy * -1 ;
                }

                mRenderer.setAngle(
                        mRenderer.getAngle() +
                        ((dx + dy) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320                
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
}
