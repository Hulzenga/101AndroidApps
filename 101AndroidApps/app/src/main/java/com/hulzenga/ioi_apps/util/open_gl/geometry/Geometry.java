package com.hulzenga.ioi_apps.util.open_gl.geometry;

import com.hulzenga.ioi_apps.util.vector.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class Geometry {

  private final List<Vec3<Float>> vertices           = new ArrayList<Vec3<Float>>();
  private final List<Vec3<Float>> normals            = new ArrayList<Vec3<Float>>();
  private final List<Vec3<Float>> textureCoordinates = new ArrayList<Vec3<Float>>();
  private final List<Short>       indices            = new ArrayList<Short>();

  public List<Vec3<Float>> getVertices() {
    return vertices;
  }

  public List<Vec3<Float>> getNormals() {
    return normals;
  }

  public List<Vec3<Float>> getTextureCoordinates() {
    return textureCoordinates;
  }

  public List<Short> getIndices() {
    return indices;
  }
}
