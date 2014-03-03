package com.hulzenga.ioi_apps.util.open_gl.geometry;

import com.hulzenga.ioi_apps.util.vector.Vec3;

import java.util.List;

public class Grid extends Geometry {

  public Grid(float x, float z, int xSteps, int zSteps) {
    List<Vec3<Float>> vertices = getVertices();
    List<Vec3<Float>> normals = getNormals();
    List<Vec3<Float>> textureCoordinates = getTextureCoordinates();
    List<Short> indices = getIndices();

    float xStep = x / ((float) xSteps);
    float zStep = z / ((float) zSteps);

    //define vertices, normals and texture coordinates
    for (int i = 0; i <= xSteps; i++) {
      for (int j = 0; j <= zSteps; j++) {
        vertices.add(new Vec3<Float>(-x / 2.0f + i * xStep, 0.0f, -z / 2.0f + j * zStep));
        normals.add(new Vec3<Float>(0.0f, 1.0f, 0.0f));
        textureCoordinates.add(new Vec3<Float>(((float) i) / ((float) xSteps), 1.0f, ((float) j) / ((float) zSteps)));
      }
    }

    //add indices
    for (int i = 0; i < xSteps; i++) {
      for (int j = 0; j < zSteps; j++) {
        indices.add((short) (i * (zSteps + 1) + j + 0));
        indices.add((short) (i * (zSteps + 1) + j + 1));
        indices.add((short) ((i + 1) * (zSteps + 1) + j));

        indices.add((short) ((i + 1) * (zSteps + 1) + j));
        indices.add((short) (i * (zSteps + 1) + j + 1));
        indices.add((short) ((i + 1) * (zSteps + 1) + j + 1));

      }
    }


  }
}
