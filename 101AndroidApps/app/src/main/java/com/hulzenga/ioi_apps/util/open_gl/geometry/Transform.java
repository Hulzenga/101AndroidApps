package com.hulzenga.ioi_apps.util.open_gl.geometry;

import com.hulzenga.ioi_apps.util.vector.Vec3;

public class Transform {

  public static Geometry jiggle(Geometry geometry, float x, float y, float z) {
    for (Vec3<Float> vertex : geometry.getVertices()) {
      vertex.x += 2.0f*((float)Math.random()-0.5f)*x;
      vertex.y += 2.0f*((float)Math.random()-0.5f)*y;
      vertex.z += 2.0f*((float)Math.random()-0.5f)*z;
    }
    //TODO: add normal transforms

    return geometry;
  }
}
