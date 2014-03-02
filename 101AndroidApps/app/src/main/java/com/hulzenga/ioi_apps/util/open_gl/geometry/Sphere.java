package com.hulzenga.ioi_apps.util.open_gl.geometry;

import com.hulzenga.ioi_apps.util.vector.Vec3;

import java.util.List;

public class Sphere extends Geometry {

  public Sphere(float radius, int segments, int slices) {

    final int numberOfVertices = (slices * segments + 2);

    List<Vec3<Float>> vertices = getVertices();
    List<Vec3<Float>> normals = getNormals();
    List<Vec3<Float>> textureCoordinates = getTextureCoordinates();
    List<Short> indices = getIndices();

    /*
     * calculate vertex locations
     */

    // add top vertex
    vertices.add(new Vec3<Float>(0.0f, radius, 0.0f));

    for (int i = 0; i < slices; i++) {

      final float rScale = (float) (radius * Math.sin((i + 1) * Math.PI / (slices + 1)));
      final float segmentHeight = (float) (radius * Math.cos((i + 1) * Math.PI / (slices + 1)));

      for (int j = 0; j < segments; j++) {
        vertices.add(new Vec3<Float>(
            rScale * ((float) Math.sin(j * 2.0 * Math.PI / segments)),
            segmentHeight,
            rScale * ((float) Math.cos(j * 2.0 * Math.PI / segments))
        ));
      }
    }

    // add bottom vertex
    vertices.add(new Vec3<Float>(0.0f, -radius, 0.0f));


    /*
     * calculate normals
     */
    for (int i = 0; i < numberOfVertices; i++) {
      // length = sqrt(x*x + y*y + z*z)
      float length = (float) Math.sqrt(
          vertices.get(i).x * vertices.get(i).x +
              vertices.get(i).y * vertices.get(i).y +
              vertices.get(i).z * vertices.get(i).z);

      normals.add(new Vec3<Float>(vertices.get(i).x / length, vertices.get(i).y / length, vertices.get(i).z
          / length));
    }

    /*
     * TODO: assign texture coordinates (if I dare)
     */

    /*
     * assign indices
     */

    // top
    for (int i = 0; i < segments - 1; i++) {
      indices.add((short) (i + 1));
      indices.add((short) (i + 2));
      indices.add((short) 0);
    }

    indices.add((short) segments);
    indices.add((short) 1);
    indices.add((short) 0);

    // body
    for (int i = 1; i < slices; i++) {
      for (int j = 0; j < segments - 1; j++) {

        indices.add((short) (i * segments + j + 1));
        indices.add((short) (i * segments + j + 2));
        indices.add((short) ((i - 1) * segments + j + 1));

        indices.add((short) (i * segments + j + 2));
        indices.add((short) ((i - 1) * segments + j + 2));
        indices.add((short) ((i - 1) * segments + j + 1));

      }
      indices.add((short) (i * segments + segments));
      indices.add((short) (i * segments + 1));
      indices.add((short) ((i - 1) * segments + segments));

      indices.add((short) (i * segments + 1));
      indices.add((short) ((i - 1) * segments + 1));
      indices.add((short) ((i - 1) * segments + segments));
    }

    // bottom
    for (int i = segments * (slices - 1); i < segments * slices - 1; i++) {
      indices.add((short) (i + 1));
      indices.add((short) (i + 2));
      indices.add((short) (numberOfVertices - 1));
    }

    indices.add((short) (segments * slices));
    indices.add((short) (segments * (slices - 1) + 1));
    indices.add((short) (numberOfVertices - 1));
  }
}
