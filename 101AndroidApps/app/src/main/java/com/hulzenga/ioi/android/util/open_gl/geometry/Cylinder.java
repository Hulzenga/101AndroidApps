package com.hulzenga.ioi.android.util.open_gl.geometry;

import com.hulzenga.ioi.android.util.open_gl.vector.Vec3;

import java.util.List;

public class Cylinder extends Geometry {

  //TODO: add texture coordinates
  public Cylinder(float radius, float height, int segments) {
    List<Vec3> vertices = getVertices();
    List<Vec3> normals = getNormals();
    List<Vec3> textureCoordinates = getTextureCoordinates();
    List<Short> indices = getIndices();

    float halfHeight = height / 2.0f;

    // add top and bottom disk
    vertices.add(new Vec3(0.0f, halfHeight, 0.0f));
    normals.add(new Vec3(0.0f, 1.0f, 0.0f));
    vertices.add(new Vec3(0.0f, -halfHeight, 0.0f));
    normals.add(new Vec3(0.0f, -1.0f, 0.0f));

    double angleStep = 2.0 * Math.PI / (double) segments;

    for (int i = 0; i < segments; i++) {
      vertices.add(new Vec3(radius*(float)Math.sin(i * angleStep), halfHeight, radius*(float) Math.cos(i * angleStep)));
      normals.add(new Vec3(0.0f, 1.0f, 0.0f));
      vertices.add(new Vec3(radius*(float) Math.sin(i * angleStep), -halfHeight, radius*(float) Math.cos(i * angleStep)));
      normals.add(new Vec3(0.0f, -1.0f, 0.0f));
    }

    //add rim vertices
    for (int i = 0; i < segments; i++) {
      vertices.add(new Vec3(radius*(float) Math.sin(i * angleStep), halfHeight, radius*(float) Math.cos(i * angleStep)));
      normals.add(new Vec3((float) Math.sin(i * angleStep), 0.0f, (float) Math.cos(i * angleStep)));
      vertices.add(new Vec3(radius*(float) Math.sin(i * angleStep), -halfHeight, radius*(float) Math.cos(i * angleStep)));
      normals.add(new Vec3((float) Math.sin(i * angleStep), 0.0f, (float) Math.cos(i * angleStep)));
    }


    //build index list
    for (int i = 0; i < segments - 1; i++) {
      indices.add((short) 0);
      indices.add((short) (2 + 2 * i));
      indices.add((short) (4 + 2 * i));

      indices.add((short) 1);
      indices.add((short) (5 + 2 * i));
      indices.add((short) (3 + 2 * i));
    }

    indices.add((short) 0);
    indices.add((short) (2 + 2 * (segments - 1)));
    indices.add((short) (2));

    indices.add((short) 1);
    indices.add((short) (3));
    indices.add((short) (3 + 2 * (segments - 1)));


    for (int i = 2 * segments + 2; i < 4 * segments; i += 2) {
      indices.add((short) (i + 0));
      indices.add((short) (i + 1));
      indices.add((short) (i + 3));

      indices.add((short) (i + 0));
      indices.add((short) (i + 3));
      indices.add((short) (i + 2));
    }

    int index = 4 * segments;
    indices.add((short) (4 * segments + 0));
    indices.add((short) (4 * segments + 1));
    indices.add((short) (2 * segments + 2));

    indices.add((short) (4 * segments + 1));
    indices.add((short) (2 * segments + 3));
    indices.add((short) (2 * segments + 2));
  }
}
