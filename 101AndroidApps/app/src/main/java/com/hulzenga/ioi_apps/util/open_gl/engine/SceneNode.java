package com.hulzenga.ioi_apps.util.open_gl.engine;

import android.opengl.Matrix;
import android.util.Log;

import com.hulzenga.ioi_apps.util.open_gl.ColorFunction;
import com.hulzenga.ioi_apps.util.open_gl.geometry.Geometry;
import com.hulzenga.ioi_apps.util.open_gl.vector.Vec3;
import com.hulzenga.ioi_apps.util.open_gl.vector.Vec4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

public class SceneNode {

  public static final  int    BYTES_PER_FLOAT               = 4;
  public static final  int    BYTES_PER_SHORT               = 2;
  public static final  int    VERTICES_PER_TRIANGLE         = 3;
  private static final String TAG                           = "RENDER_OBJECT";
  private static final int    FLOATS_PER_VERTEX             = 3;
  private static final int    FLOATS_PER_COLOR              = 4;
  private static final int    FLOATS_PER_NORMAL             = 3;
  private static final int    FLOATS_PER_TEXTURE_COORDINATE = 2;
  private FloatBuffer mVertexBuffer;
  private ShortBuffer mIndexBuffer;
  private int         mNumberOfVertices;
  private int         mNumberOfIndices;

  private boolean mColored;
  private boolean mNormal;
  private boolean mTextured;

  private int mVertexOffset;
  private int mColorOffset;
  private int mNormalOffset;
  private int mTextureOffset;
  private int mStride;

  private float[] mModelMatrix       = new float[16];
  private float[] mTranslationMatrix = new float[16];
  private float[] mRotationMatrix    = new float[16];

  {
    Matrix.setIdentityM(mModelMatrix, 0);
    Matrix.setIdentityM(mTranslationMatrix, 0);
    Matrix.setIdentityM(mRotationMatrix, 0);
  }

  private List<NodeController> mNodeControllers = new ArrayList<NodeController>();

  //TODO: implement alternate constructor which would accept geoemtry + texture
  public SceneNode(Geometry geometry, ColorFunction colorFunction) {
    List<Vec4> colors = new ArrayList<Vec4>();

    List<Vec3> vertices = geometry.getVertices();
    List<Vec3> normals = geometry.getNormals();

    for (int i = 0; i < vertices.size(); i++) {
      colors.add(colorFunction.apply(vertices.get(i), normals.get(i)));
    }

    allocateBuffers(vertices, colors, normals, geometry.getTextureCoordinates(), geometry.getIndices());
  }

  private void allocateBuffers(List<Vec3> vertices, List<Vec4> colors, List<Vec3> normals,
                               List<Vec3> textureCoords, List<Short> indices) {

    /*
     * check input validity and set SceneNode state accordingly
     * TODO: this needs to go elsewhere
     */

    if (vertices == null || vertices.size() == 0) {
      Log.e(TAG, "input vertex list is either null or contains no vertices");
      return;
    } else {
      mNumberOfVertices = vertices.size();
    }

    if (colors == null) {
      mColored = false;
    } else if (colors.size() != mNumberOfVertices) {
      Log.e(TAG, "the list of colors does not have the same length as the list of vertices");
      return;
    } else {
      mColored = true;
    }

    if (normals == null) {
      mNormal = false;
    } else if (normals.size() != mNumberOfVertices) {
      Log.e(TAG, " the list of vertex normals does not have the same length as the list of vertices");
      return;
    } else {
      mNormal = true;
    }

    if (textureCoords.size() == 0) {
      mTextured = false;
    } else if (textureCoords.size() != mNumberOfVertices) {
      Log.e(TAG, "the list of texture coordinates does not have the same length as the list of vertices");
      return;
    } else {
      mTextured = true;
    }

    if (indices == null || indices.size() % VERTICES_PER_TRIANGLE != 0) {
      Log.e(TAG, "indices array has invalid length");
      return;
    } else {
      mNumberOfIndices = indices.size();
    }

    /*
     * check each possible property and set their offset and contribution to
     * the mStride
     */

    int floatStride = 0;

    { // braces for style
      mVertexOffset = floatStride;
      floatStride += FLOATS_PER_VERTEX;
    }
    if (mColored) {
      mColorOffset = floatStride;
      floatStride += FLOATS_PER_COLOR;
    }
    if (mNormal) {
      mNormalOffset = floatStride;
      floatStride += FLOATS_PER_NORMAL;
    }
    if (mTextured) {
      mTextureOffset = floatStride;
      floatStride += FLOATS_PER_TEXTURE_COORDINATE;
    }

    // setup stride (very confusing that this has to be in bytes while the
    // rest is in floats)
    mStride = floatStride * BYTES_PER_FLOAT;

    /*
     * allocate and fill mVertexBuffer
     */

    mVertexBuffer = ByteBuffer.allocateDirect(mNumberOfVertices * mStride).order(ByteOrder.nativeOrder())
        .asFloatBuffer();
    mVertexBuffer.position(0);

    for (int i = 0; i < mNumberOfVertices; i++) {
      {// braces for style
        mVertexBuffer.put(vertices.get(i).x);
        mVertexBuffer.put(vertices.get(i).y);
        mVertexBuffer.put(vertices.get(i).z);
      }
      if (mColored) {
        mVertexBuffer.put(colors.get(i).x);
        mVertexBuffer.put(colors.get(i).y);
        mVertexBuffer.put(colors.get(i).z);
        mVertexBuffer.put(colors.get(i).w);
      }
      if (mNormal) {
        mVertexBuffer.put(normals.get(i).x);
        mVertexBuffer.put(normals.get(i).y);
        mVertexBuffer.put(normals.get(i).z);
      }
      if (mTextured) {
        mVertexBuffer.put(textureCoords.get(i).x);
        mVertexBuffer.put(textureCoords.get(i).x);
      }
    }

    // add on the index buffer
    mIndexBuffer = ByteBuffer.allocateDirect(indices.size() * BYTES_PER_SHORT).order(ByteOrder.nativeOrder())
        .asShortBuffer();

    for (short s : indices) {
      mIndexBuffer.put(s);
    }

  }

  public void subscribe(NodeController controller) {
    mNodeControllers.add(controller);
  }

  public void runControllers() {
    for (NodeController controller : mNodeControllers) {
      controller.update(this);
    }
  }

  public void setTranslation(float x, float y, float z) {
    Matrix.setIdentityM(mTranslationMatrix, 0);
    Matrix.translateM(mTranslationMatrix, 0, x, y, z);
    updateModelMatrix();
  }

  private void updateModelMatrix() {
    Matrix.setIdentityM(mModelMatrix, 0);
    Matrix.multiplyMM(mModelMatrix, 0, mTranslationMatrix, 0, mRotationMatrix, 0);
  }

  /**
   * set rotation matrix, rotates in y->x->z order
   *
   * @param x
   * @param y
   * @param z
   */
  public void setRotation(float x, float y, float z) {
    float[] tmpMatrix = new float[16];

    Matrix.setRotateM(mRotationMatrix, 0, y, 0.0f, 1.0f, 0.0f);
    Matrix.setRotateM(tmpMatrix, 0, x, 1.0f, 0.0f, 0.0f);
    Matrix.multiplyMM(mRotationMatrix, 0,tmpMatrix , 0, mRotationMatrix, 0);
    Matrix.setRotateM(tmpMatrix, 0, z, 0.0f, 0.0f, 1.0f);
    Matrix.multiplyMM(mRotationMatrix, 0, tmpMatrix, 0, mRotationMatrix, 0);
    updateModelMatrix();
  }

  public float[] getModelMatrix() {
    return mModelMatrix;
  }

  public boolean hasColor() {
    return mColored;
  }

  public boolean hasNormal() {
    return mNormal;
  }

  public boolean hasTexture() {
    return mTextured;
  }

  public FloatBuffer getVertexBuffer() {
    return mVertexBuffer;
  }

  public ShortBuffer getIndexBuffer() {
    return mIndexBuffer;
  }

  public int getNumberOfVertices() {
    return mNumberOfVertices;
  }

  public int getNumberOfIndices() {
    return mNumberOfIndices;
  }

  public int getVertexOffset() {
    return mVertexOffset;
  }

  public int getColorOffset() {
    return mColorOffset;
  }

  public int getNormalOffset() {
    return mNormalOffset;
  }

  public int getTextureOffset() {
    return mTextureOffset;
  }

  public int getStride() {
    return mStride;
  }


}
