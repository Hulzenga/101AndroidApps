package com.hulzenga.ioi.android.util.open_gl.geometry;

import com.hulzenga.ioi.android.util.open_gl.vector.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Geometry {

  private static final int VERTICES_PER_TRIANGLE = 3;

  private List<Vec3>  vertices           = new ArrayList<Vec3>();
  private List<Vec3>  normals            = new ArrayList<Vec3>();
  private List<Vec3>  textureCoordinates = new ArrayList<Vec3>();
  private List<Short> indices            = new ArrayList<Short>();

  public List<Vec3> getVertices() {
    return vertices;
  }

  public List<Vec3> getNormals() {
    return normals;
  }

  public List<Vec3> getTextureCoordinates() {
    return textureCoordinates;
  }

  public List<Short> getIndices() {
    return indices;
  }

  public void makeFaceted() {

    if (isFaceted()) {
      return;
    }

    List<Vec3> newVertices = new ArrayList<Vec3>();
    List<Vec3> newNormals = new ArrayList<Vec3>();
    List<Vec3> newTextureCoordinates = new ArrayList<Vec3>();
    List<Short> newIndices = new ArrayList<Short>();

    for (int i = 0; i < indices.size(); i++) {
      newVertices.add(vertices.get(indices.get(i)));
      newNormals.add(normals.get(indices.get(i)));
      newTextureCoordinates.add(textureCoordinates.get(indices.get(i)));
      newIndices.add((short) i);
    }

    vertices = newVertices;
    normals = newNormals;
    textureCoordinates = newTextureCoordinates;
    indices = newIndices;

    int numberOfTriangles = indices.size() / VERTICES_PER_TRIANGLE;

    for (int i = 0; i < numberOfTriangles; i++) {
      Vec3 a = vertices.get(i * VERTICES_PER_TRIANGLE).copy().
          subtract(vertices.get(i * VERTICES_PER_TRIANGLE + 1));
      Vec3 b = vertices.get(i * VERTICES_PER_TRIANGLE + 1).copy().
          subtract(vertices.get(i * VERTICES_PER_TRIANGLE + 2));
      a.cross(b).normalize();
      normals.set(i * VERTICES_PER_TRIANGLE , a);
      normals.set(i * VERTICES_PER_TRIANGLE + 1, a);
      normals.set(i * VERTICES_PER_TRIANGLE + 2, a);
    }
  }

  public boolean isFaceted() {
    return vertices.size() == indices.size();
  }

  public void discardSelectedVertices(VertexSelector selector) {

    List<Integer> removalList = new ArrayList<Integer>();

    for (int i = vertices.size()-1; i >= 0; i--) {
      if (selector.isSelected(vertices.get(i))) {
        removalList.add(i);
      }
    }

    for (int i = 0; i < indices.size();) {
      if (removalList.contains(indices.get(i)) ||
          removalList.contains(indices.get(i+1)) ||
          removalList.contains(indices.get(i+2))) {
        indices.remove(i+2);
        indices.remove(i+1);
        indices.remove(i);
      } else {
        i += 3;
      }
    }

    for (int i : removalList) {
      vertices.remove(i);
      normals.remove(i);

      //TODO: add this once all geometries have texture coordinates
      //textureCoordinates.remove(i);
    }
  }

  /**
   * Estimates normals based on neighboring vertex locations. Dubious how well this works
   */
  public void estimateNormals() {
    for (short i = 0; i < vertices.size(); i++) {
      Vec3 newNormal = new Vec3(0.0f, 0.0f, 0.0f);
      for (Vec3 connectedVertex : findConnectedVertices(i)) {
        newNormal.add(connectedVertex.copy().subtract(vertices.get(i)));
      }
      normals.set(i, newNormal.normalize().invert());
    }
  }

  private List<Vec3> findConnectedVertices(short vertexIndex) {
    List<Vec3> connectedVertices = new ArrayList<Vec3>();
    Set<Short> connectedIndices = new HashSet<Short>();

    int numberOfTriangles = indices.size() / VERTICES_PER_TRIANGLE;
    Short s1, s2, s3;
    for (int i = 0; i < numberOfTriangles; i++) {
      s1 = indices.get(i * 3);
      s2 = indices.get(i * 3 + 1);
      s3 = indices.get(i * 3 + 2);

      if (s1 == vertexIndex || s2 == vertexIndex || s3 == vertexIndex) {
        connectedIndices.add(s1);
        connectedIndices.add(s2);
        connectedIndices.add(s3);
      }
    }

    for (Short index : connectedIndices) {
      connectedVertices.add(vertices.get(index));
    }
    return connectedVertices;
  }

  public interface VertexSelector {
    public boolean isSelected(Vec3 vertex);
  }
}
