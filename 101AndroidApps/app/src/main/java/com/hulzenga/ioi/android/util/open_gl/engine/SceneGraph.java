package com.hulzenga.ioi.android.util.open_gl.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SceneGraph implements Iterable<SceneNode> {

  private List<SceneNode> mNodes = new ArrayList<SceneNode>();
  private float mLookatX = 0.0f;
  private float mLookatY = 0.0f;
  private float mLookatZ = 0.0f;

  public void setLookAt(float x, float y, float z) {
    mLookatX = x;
    mLookatY = y;
    mLookatZ = z;
  }

  public float getLookAtX() {
    return mLookatX;
  }

  public float getLookAtY() {
    return mLookatY;
  }

  public float getLookAtZ() {
    return mLookatZ;
  }

  public void addNode(SceneNode node) {
    mNodes.add(node);
  }

  @Override
  public Iterator<SceneNode> iterator() {
    return mNodes.iterator();
  }
}
