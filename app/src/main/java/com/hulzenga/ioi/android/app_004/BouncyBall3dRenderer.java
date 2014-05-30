package com.hulzenga.ioi.android.app_004;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.hulzenga.ioi.android.util.Constrain;
import com.hulzenga.ioi.android.util.open_gl.ShaderTools;
import com.hulzenga.ioi.android.util.open_gl.engine.SceneGraph;
import com.hulzenga.ioi.android.util.open_gl.engine.SceneNode;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BACK;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindAttribLocation;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glCullFace;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

class BouncyBall3dRenderer implements GLSurfaceView.Renderer {

  private static final String TAG         = "BOUNCY_BALL_3D_RENDERER";
  private static final float  MIN_DELTA_Y = 0.0f;
  private volatile     float  mDeltaY     = MIN_DELTA_Y;
  private static final float  MAX_DELTA_Y = 90.0f;
  private Context    context;
  private SceneGraph mSceneGraph;
  private volatile float mDeltaX   = 0.0f;
  private volatile float mDistance = 0.0f;

  private float[] mViewTranslateMatrix = new float[16];
  private float[] mViewRotateMatrix    = new float[16];
  private float[] mViewBaseMatrix      = new float[16];
  private float[] mViewMatrix          = new float[16];

  private float[] mProjectionMatrix = new float[16];
  private float[] mMVMatrix         = new float[16];
  private float[] mMVPMatrix        = new float[16];

  private int mMVPMatrixHandle;
  private int mMVMatrixHandle;

  private int mPositionHandle;
  private int mColorHandle;
  private int mNormalHandle;

  private float[] mLightPosition    = {4.0f, 4.0f, 3.0f, 1.0f};
  private float[] mLightEyePosition = new float[4];
  private int   mLightPositionHandle;
  private float mWidth;
  private float mHeight;

  public BouncyBall3dRenderer(Context context, SceneGraph sceneGraph) {
    this.context = context;
    mSceneGraph = sceneGraph;

    setupCamera();
  }

  private void setupCamera() {

    // eye position
    final float eyeX = 0.0f;
    final float eyeY = 2.0f;
    final float eyeZ = 6.0f;

    // look at vector
    final float lookX = mSceneGraph.getLookAtX();
    final float lookY = mSceneGraph.getLookAtY();
    final float lookZ = mSceneGraph.getLookAtZ();

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

  private void updateViewMatrix() {
    Matrix.setIdentityM(mViewRotateMatrix, 0);
    Matrix.rotateM(mViewRotateMatrix, 0, mDeltaY, 1.0f, 0.0f, 0.0f);
    Matrix.rotateM(mViewRotateMatrix, 0, mDeltaX, 0.0f, 1.0f, 0.0f);

    Matrix.setIdentityM(mViewTranslateMatrix, 0);
    Matrix.translateM(mViewTranslateMatrix, 0, 0.0f, 0.0f, mDistance);

    Matrix.multiplyMM(mViewMatrix, 0, mViewRotateMatrix, 0, mViewTranslateMatrix, 0);
    Matrix.multiplyMM(mViewMatrix, 0, mViewBaseMatrix, 0, mViewMatrix, 0);

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
  public void onSurfaceChanged(GL10 arg0, int width, int height) {

    mWidth = (float) width;
    mHeight = (float) height;

    final float ratio = ((float) width) / ((float) height);
    final float left = -ratio;
    final float right = ratio;
    final float bottom = -1.0f;
    final float top = 1.0f;
    final float near = 1.0f;
    final float far = 14.0f;

    Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
  }

  @Override
  public void onDrawFrame(GL10 arg0) {

    glClearColor(0.5f, 0.5f, 1.0f, 1.0f);
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    for (SceneNode node : mSceneGraph) {

      node.runControllers();

      node.getVertexBuffer().position(0);
      glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, node.getStride(), node.getVertexBuffer());
      glEnableVertexAttribArray(mPositionHandle);

      node.getVertexBuffer().position(node.getColorOffset());
      glVertexAttribPointer(mColorHandle, 4, GL_FLOAT, false, node.getStride(), node.getVertexBuffer());
      glEnableVertexAttribArray(mColorHandle);

      node.getVertexBuffer().position(node.getNormalOffset());
      glVertexAttribPointer(mNormalHandle, 3, GL_FLOAT, false, node.getStride(), node.getVertexBuffer());
      glEnableVertexAttribArray(mNormalHandle);

      Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, node.getModelMatrix(), 0);
      Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);

      glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0);
      glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

      Matrix.multiplyMV(mLightEyePosition, 0, mViewMatrix, 0, mLightPosition, 0);
      glUniform3f(mLightPositionHandle, mLightEyePosition[0], mLightEyePosition[1], mLightEyePosition[2]);

      node.getIndexBuffer().position(0);
      glDrawElements(GL_TRIANGLES, node.getNumberOfIndices(), GL_UNSIGNED_SHORT, node.getIndexBuffer());
    }
  }

  public void touchMove(float dx, float dy) {
    mDeltaX += dx;
    mDeltaY = Constrain.doubleBound(MIN_DELTA_Y, mDeltaY + dy, MAX_DELTA_Y);

    updateViewMatrix();
  }


}
