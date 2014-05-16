package com.hulzenga.ioi.android.util.open_gl.geometry;

import com.hulzenga.ioi.android.util.open_gl.vector.Vec3;

public class Transform {

  public static Geometry jiggle(Geometry geometry, float x, float y, float z) {
    for (Vec3 vertex : geometry.getVertices()) {
      vertex.x += 2.0f*((float)Math.random()-0.5f)*x;
      vertex.y += 2.0f*((float)Math.random()-0.5f)*y;
      vertex.z += 2.0f*((float)Math.random()-0.5f)*z;
    }
    //TODO: add normal transforms

    return geometry;
  }

  public static Geometry jiggleEveryOther(Geometry geometry, float x, float y, float z) {

    int numberOfVertices = geometry.getVertices().size();
    Vec3 vertex;

    for (int i = 0; i < numberOfVertices-1; i += 2) {
      vertex = geometry.getVertices().get(i);

      vertex.x += 2.0f*((float)Math.random()-0.5f)*x;
      vertex.y += 2.0f*((float)Math.random()-0.5f)*y;
      vertex.z += 2.0f*((float)Math.random()-0.5f)*z;
    }

    return geometry;
  }
}
