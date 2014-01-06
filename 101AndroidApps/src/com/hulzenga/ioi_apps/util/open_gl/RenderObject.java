package com.hulzenga.ioi_apps.util.open_gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.util.Log;

public class RenderObject {

    private static final String TAG                           = "RENDER_OBJECT";
    private static final int    FLOATS_PER_VERTEX             = 3;
    private static final int    FLOATS_PER_COLOR              = 4;
    private static final int    FLOATS_PER_NORMAL             = 3;
    private static final int    FLOATS_PER_TEXTURE_COORDINATE = 2;

    public static final int     BYTES_PER_FLOAT               = 4;
    public static final int     BYTES_PER_SHORT               = 2;
    
    public static final int     INDICES_PER_TRIANGLE          = 3;              

    private FloatBuffer         mVertexBuffer;
    private ShortBuffer         mIndexBuffer;
    private int                 mNumberOfVertices;

    private boolean             mColored;
    private boolean             mNormal;
    private boolean             mTextured;

    private int                 mVertexOffset;
    private int                 mColorOffset;
    private int                 mNormalOffset;
    private int                 mTextureOffset;
    private int                 mStride;

    public RenderObject(float[] vertices, float[] colors, float[] normals, float[] textureCoords, short[] indices) {

        /*
         * check input validity and set RenderObject state accordingly
         */
        if (vertices == null || vertices.length % FLOATS_PER_VERTEX != 0) {
            Log.e(TAG, "vertex array has invalid length or does not exist");
            return;
        } else {
            mNumberOfVertices = vertices.length / FLOATS_PER_VERTEX;
        }

        if (colors == null) {
            mColored = false;
        } else if (colors.length % FLOATS_PER_COLOR != 0 || colors.length / FLOATS_PER_COLOR != mNumberOfVertices) {
            Log.e(TAG, "color array has invalid length");
            return;
        } else {
            mColored = true;
        }

        if (normals == null) {
            mNormal = false;
        } else if (normals.length % FLOATS_PER_NORMAL != 0 || normals.length / FLOATS_PER_NORMAL != mNumberOfVertices) {
            Log.e(TAG, "normal array has invalid length");
            return;
        } else {
            mNormal = true;
        }

        if (indices == null || indices.length % INDICES_PER_TRIANGLE != 0) {
            Log.e(TAG, "indices array has invalid length");
            return;
        }         

        if (textureCoords == null) {
            mTextured = false;
        } else if (textureCoords.length % FLOATS_PER_TEXTURE_COORDINATE != 0
                || textureCoords.length / FLOATS_PER_TEXTURE_COORDINATE != mNumberOfVertices) {
            Log.e(TAG, "texture coordinate array has invalid length");
            return;
        } else {
            mTextured = true;            
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

        

        /*
         * allocate and fill mVertexBuffer
         */

        mVertexBuffer = ByteBuffer.allocateDirect(mNumberOfVertices * mStride * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.position(0);

        for (int i = 0; i < mNumberOfVertices; i++) {
            {// braces for style
                mVertexBuffer.put(vertices, i * floatStride + mVertexOffset, FLOATS_PER_VERTEX);
            }
            if (mColored) {
                mVertexBuffer.put(vertices, i * floatStride + mColorOffset, FLOATS_PER_COLOR);
            }
            if (mNormal) {
                mVertexBuffer.put(vertices, i * floatStride + mNormalOffset, FLOATS_PER_NORMAL);
            }
            if (mTextured) {
                mVertexBuffer.put(vertices, i * floatStride + mTextureOffset, FLOATS_PER_TEXTURE_COORDINATE);
            }
        }

        // add on the index buffer
        mIndexBuffer = ByteBuffer.allocateDirect(indices.length * BYTES_PER_SHORT).order(ByteOrder.nativeOrder())
                .asShortBuffer();
        mIndexBuffer.put(indices);
        
        //setup stride (very confusing that this has to be in bytes while the rest is in floats)
        mStride = floatStride*BYTES_PER_FLOAT; 
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
