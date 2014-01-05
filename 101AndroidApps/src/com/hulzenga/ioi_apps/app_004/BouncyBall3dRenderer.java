package com.hulzenga.ioi_apps.app_004;

import static android.opengl.GLES20.*;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.hulzenga.ioi_apps.util.open_gl.RenderObject;
import com.hulzenga.ioi_apps.util.open_gl.ShaderTools;
import com.hulzenga.ioi_apps.util.open_gl.ShapeFactory;

public class BouncyBall3dRenderer implements GLSurfaceView.Renderer {

    private Context     context;

    private float[]     mModelMatrix      = new float[16];
    private float[]     mViewMatrix       = new float[16];
    private float[]     mProjectionMatrix = new float[16];
    private float[]     mVPMatrix         = new float[16];
    private float[]     mMVPMatrix        = new float[16];

    private int         mMVPMatrixHandle;
    private int         mPositionHandle;    
    
    private RenderObject sphere = ShapeFactory.sphere(1.0f, 16, 0);
    
    
    public BouncyBall3dRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
        
        //glEnable(GL_CULL_FACE);
        //glCullFace(GL_BACK);
        
        //enable depth testing
        glEnable(GL_DEPTH_TEST);
        
        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 2.0f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -1.0f;

        // Set our up vector. This is where our head would be pointing were we
        // holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix represents the camera position.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        final String vertexShader = ShaderTools.readShader(context, "app_004/simple.vert");
        final String fragmentShader = ShaderTools.readShader(context, "app_004/simple.frag");

        int vertexShaderHandle = glCreateShader(GL_VERTEX_SHADER);
        int fragmentShaderHandle = glCreateShader(GL_FRAGMENT_SHADER);

        ShaderTools.compileShader(vertexShaderHandle, vertexShader);
        ShaderTools.compileShader(fragmentShaderHandle, fragmentShader);

        int programHandle = glCreateProgram();

        if (programHandle != 0) {

            // attach shaders
            glAttachShader(programHandle, vertexShaderHandle);
            glAttachShader(programHandle, fragmentShaderHandle);

            // bind attributes
            glBindAttribLocation(programHandle, 0, "a_Position");

            glLinkProgram(programHandle);

            final int[] linkStatus = new int[1];
            glGetProgramiv(programHandle, GL_LINK_STATUS, linkStatus, 0);

            if (linkStatus[0] == 0) {
                glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        mMVPMatrixHandle = glGetUniformLocation(programHandle, "u_MVPMatrix");
        mPositionHandle = glGetAttribLocation(programHandle, "a_Position");

        glUseProgram(programHandle);
    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {

        final float ratio = ((float) width) / ((float) height);
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 arg0) {

        glClearColor(0.5f, 0.5f, 1.0f, 2.0f);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        sphere.getVertexBuffer().position(0);
        
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, 0, sphere.getVertexBuffer());
        glEnableVertexAttribArray(mPositionHandle);
        
        final double time = (double) (SystemClock.uptimeMillis() % 10000L);
        final float delta = (float) Math.sin(2.0*Math.PI*time/10000.0);
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, delta, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, mAngle, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mVPMatrix, 0);
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        glDrawArrays(GL_TRIANGLES, 0, sphere.getNumberOfVertices());

        
    }
    
    private volatile float mAngle;

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        this.mAngle = angle;
    }
    
}