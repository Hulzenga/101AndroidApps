package com.hulzenga.ioi_apps.util.open_gl;

public class SceneObject {

    private float[]      mModelMatrix = new float[16];

    private RenderObject mRenderObject;

    public SceneObject(float[] modelMatrix, RenderObject renderObject) {
        mModelMatrix = modelMatrix;
        mRenderObject = renderObject;
    }
    
    public float[] getModelMatrix() {
        return mModelMatrix;
    }
    
    public void setModelMatrix(float[] modelMatrix) {
        mModelMatrix = modelMatrix;
    }
    
    public RenderObject getRenderObject() {
        return mRenderObject;
    }
}
