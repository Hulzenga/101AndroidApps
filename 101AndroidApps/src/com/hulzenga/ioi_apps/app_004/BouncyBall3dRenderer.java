package com.hulzenga.ioi_apps.app_004;

import static android.opengl.GLES20.*;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.hulzenga.ioi_apps.util.open_gl.SceneObject;
import com.hulzenga.ioi_apps.util.open_gl.ShaderTools;
import com.hulzenga.ioi_apps.util.open_gl.ShapeFactory;

public class BouncyBall3dRenderer implements GLSurfaceView.Renderer {

    private static final String TAG                  = "BOUNCY_BALL_3D_RENDERER";
    private Context             context;

    private List<SceneObject>   sceneObjects         = new ArrayList<SceneObject>();

    private volatile float      mDeltaX              = 0.0f;
    private volatile float      mDeltaY              = 0.0f;
    private volatile float      mDistance            = 0.0f;

    private float[]             mViewTranslateMatrix = new float[16];
    private float[]             mViewRotateMatrix    = new float[16];
    private float[]             mViewBaseMatrix      = new float[16];
    private float[]             mViewMatrix          = new float[16];

    private float[]             mProjectionMatrix    = new float[16];
    private float[]             mMVMatrix            = new float[16];
    private float[]             mMVPMatrix           = new float[16];

    private int                 mMVPMatrixHandle;
    private int                 mMVMatrixHandle;

    private int                 mPositionHandle;
    private int                 mColorHandle;
    private int                 mNormalHandle;

    private float[]             mLightPosition       = { 3.0f, 2.0f, 2.0f, 1.0f };
    private float[]             mLightEyePosition    = new float[4];
    private int                 mLightPositionHandle;

    public BouncyBall3dRenderer(Context context) {
        this.context = context;

        initScene();
        setupCamera();
    }

    private void initScene() {
        // sceneObjects.add(new SceneObject(null, ShapeFactory.box(1.0f, 1.0f,
        // 1.0f)));

        float[] modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, -1.0f, 0.0f, 0.0f);
        sceneObjects.add(new SceneObject(modelMatrix, ShapeFactory.box(10.0f, 0.5f, 10.0f)));

        modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 1.0f, 0.5f, 0.0f);
        sceneObjects.add(new SceneObject(modelMatrix, ShapeFactory.box(0.5f, 1.5f, 0.5f)));
    }

    public void setupCamera() {

        // eye position
        final float eyeX = 0.0f;
        final float eyeY = 2.0f;
        final float eyeZ = 4.0f;

        // look at vector
        final float lookX = 0.0f;
        final float lookY = 1.0f;
        final float lookZ = 0.0f;

        // up vector
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        Matrix.setIdentityM(mViewRotateMatrix, 0);
        Matrix.setIdentityM(mViewTranslateMatrix, 0);

        // set base view matrix; the view before camera rotation and zoom
        // translation
        Matrix.setLookAtM(mViewBaseMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        updateViewMatrix();
    }

    public void updateViewMatrix() {
        Matrix.rotateM(mViewRotateMatrix, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
       // Matrix.rotateM(mViewRotateMatrix, 0, mDeltaY, 1.0f, 0.0f, 0.0f);
        

        mDeltaX = 0.0f;
        mDeltaY = 0.0f;

        Matrix.setIdentityM(mViewTranslateMatrix, 0);
        Matrix.translateM(mViewTranslateMatrix, 0, 0.0f, 0.0f, mDistance);
        
        Matrix.multiplyMM(mViewMatrix, 0, mViewRotateMatrix, 0, mViewTranslateMatrix, 0);
        Matrix.multiplyMM(mViewMatrix, 0, mViewBaseMatrix, 0, mViewMatrix, 0);

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

        Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        // Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near,
        // far);
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        // enable depth testing
        glEnable(GL_DEPTH_TEST);

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

        final double time = (double) (SystemClock.uptimeMillis() % 10000L);
        final float delta = (float) Math.sin(2.0 * Math.PI * time / 10000.0);

        for (SceneObject obj : sceneObjects) {
            obj.getRenderObject().getVertexBuffer().position(0);
            glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, obj.getRenderObject().getStride(), obj
                    .getRenderObject().getVertexBuffer());
            glEnableVertexAttribArray(mPositionHandle);

            obj.getRenderObject().getVertexBuffer().position(obj.getRenderObject().getColorOffset());
            glVertexAttribPointer(mColorHandle, 4, GL_FLOAT, false, obj.getRenderObject().getStride(), obj
                    .getRenderObject().getVertexBuffer());
            glEnableVertexAttribArray(mColorHandle);

            obj.getRenderObject().getVertexBuffer().position(obj.getRenderObject().getNormalOffset());
            glVertexAttribPointer(mNormalHandle, 3, GL_FLOAT, false, obj.getRenderObject().getStride(), obj
                    .getRenderObject().getVertexBuffer());
            glEnableVertexAttribArray(mNormalHandle);

            Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, obj.getModelMatrix(), 0);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);

            glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0);
            glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

            Matrix.multiplyMV(mLightEyePosition, 0, mMVMatrix, 0, mLightPosition, 0);
            glUniform3f(mLightPositionHandle, mLightEyePosition[0], mLightEyePosition[1], mLightEyePosition[2]);

            obj.getRenderObject().getIndexBuffer().position(0);
            glDrawElements(GL_TRIANGLES, obj.getRenderObject().getNumberOfIndices(), GL_UNSIGNED_SHORT, obj
                    .getRenderObject().getIndexBuffer());
        }
    }

    public void addDelta(float dx, float dy) {
        mDeltaX += dx;
        mDeltaY += dy;

        updateViewMatrix();
    }

}
