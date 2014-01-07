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

    private static final String TAG               = "BOUNCY_BALL_3D_RENDERER";
    private Context             context;

    private float[]             mModelMatrix      = new float[16];
    private float[]             mViewMatrix       = new float[16];
    private float[]             mProjectionMatrix = new float[16];
    private float[]             mMVMatrix         = new float[16];
    private float[]             mMVPMatrix        = new float[16];

    private int                 mMVPMatrixHandle;
    private int                 mMVMatrixHandle;

    private int                 mPositionHandle;
    private int                 mColorHandle;
    private int                 mNormalHandle;

    private float[]             mLightPosition    = { 3.0f, 2.0f, 0.0f, 1.0f };
    private float[]             mLightEyePosition = new float[4];
    private int                 mLightPositionHandle;

    private RenderObject        sphere            = ShapeFactory.sphere(1.0f, 16, 16);

    public BouncyBall3dRenderer(Context context) {
        this.context = context;
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
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {

        // glEnable(GL_CULL_FACE);
        // glCullFace(GL_BACK);

        // enable depth testing
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
            glBindAttribLocation(programHandle, 1, "a_Color");
            glBindAttribLocation(programHandle, 2, "a_Normal");

            glLinkProgram(programHandle);

            final int[] linkStatus = new int[1];
            glGetProgramiv(programHandle, GL_LINK_STATUS, linkStatus, 0);

            if (linkStatus[0] == 0) {
                glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        mMVMatrixHandle = glGetUniformLocation(programHandle, "u_MVMatrix");
        mMVPMatrixHandle = glGetUniformLocation(programHandle, "u_MVPMatrix");

        mPositionHandle = glGetAttribLocation(programHandle, "a_Position");
        mColorHandle = glGetAttribLocation(programHandle, "a_Color");
        mNormalHandle = glGetAttribLocation(programHandle, "a_Normal");

        mLightPositionHandle = glGetUniformLocation(programHandle, "u_LightPosition");

        glUseProgram(programHandle);
    }

    @Override
    public void onDrawFrame(GL10 arg0) {

        glClearColor(0.5f, 0.5f, 1.0f, 1.0f);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        sphere.getVertexBuffer().position(0);
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, sphere.getStride(), sphere.getVertexBuffer());
        glEnableVertexAttribArray(mPositionHandle);

        sphere.getVertexBuffer().position(sphere.getColorOffset());
        glVertexAttribPointer(mColorHandle, 4, GL_FLOAT, false, sphere.getStride(), sphere.getVertexBuffer());
        glEnableVertexAttribArray(mColorHandle);

        sphere.getVertexBuffer().position(sphere.getNormalOffset());
        glVertexAttribPointer(mNormalHandle, 3, GL_FLOAT, false, sphere.getStride(), sphere.getVertexBuffer());
        glEnableVertexAttribArray(mNormalHandle);

        final double time = (double) (SystemClock.uptimeMillis() % 10000L);
        final float delta = (float) Math.sin(2.0 * Math.PI * time / 10000.0);
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, delta, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, mAngle, 1.0f, 0.0f, 0.0f);
        Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);

        glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0);
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        Matrix.multiplyMV(mLightEyePosition, 0, mMVMatrix, 0, mLightPosition, 0);
        glUniform3f(mLightPositionHandle, mLightEyePosition[0], mLightEyePosition[1], mLightEyePosition[2]);

        sphere.getIndexBuffer().position(0);
        glDrawElements(GL_TRIANGLES, sphere.getNumberOfIndices(), GL_UNSIGNED_SHORT, sphere.getIndexBuffer());

    }

    private volatile float mAngle;

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        this.mAngle = angle;
    }

}
