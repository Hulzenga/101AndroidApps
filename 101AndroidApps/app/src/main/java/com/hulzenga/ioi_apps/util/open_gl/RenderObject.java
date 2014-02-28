package com.hulzenga.ioi_apps.util.open_gl;

import android.util.Log;

import com.hulzenga.ioi_apps.util.vector.Vec3;
import com.hulzenga.ioi_apps.util.vector.Vec4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

public class RenderObject {

  private static final String TAG                           = "RENDER_OBJECT";
  private static final int    FLOATS_PER_VERTEX             = 3;
  private static final int    FLOATS_PER_COLOR              = 4;
  private static final int    FLOATS_PER_NORMAL             = 3;
  private static final int    FLOATS_PER_TEXTURE_COORDINATE = 2;

  public static final int BYTES_PER_FLOAT = 4;
  public static final int BYTES_PER_SHORT = 2;

  public static final int VERTICES_PER_TRIANGLE = 3;

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

  // TODO rewrite use integers for indices list
  public RenderObject(List<Vec3<Float>> vertices, List<Vec4<Float>> colors, List<Vec3<Float>> normals,
                      List<Vec3<Float>> textureCoords, List<Short> indices) {

        /*
         * check input validity and set RenderObject state accordingly
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

    if (textureCoords == null) {
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
