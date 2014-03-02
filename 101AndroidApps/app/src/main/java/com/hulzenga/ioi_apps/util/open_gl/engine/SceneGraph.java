package com.hulzenga.ioi_apps.util.open_gl.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SceneGraph implements Iterable<SceneNode> {

  private List<SceneNode> mNodes = new ArrayList<SceneNode>();


  public void addNode(SceneNode node) {
    mNodes.add(node);
  }

  @Override
  public Iterator<SceneNode> iterator() {
    return mNodes.iterator();
  }
}
