package com.hulzenga.ioi.android.util.open_gl.geometry;

import com.hulzenga.ioi.android.util.open_gl.vector.Vec3;

import java.util.List;

public class Box extends Geometry {

  //TODO add texture coordinates to Box
  public Box(float x, float y, float z) {
    List<Vec3> vertices = getVertices();
    List<Vec3> normals = getNormals();
    List<Vec3> textureCoordinates = getTextureCoordinates();
    List<Short> indices = getIndices();

    /*
     * assign vertices and normals
     */
    // front face
    vertices.add(new Vec3(-x / 2, -y / 2, z / 2));
    vertices.add(new Vec3(+x / 2, -y / 2, z / 2));
    vertices.add(new Vec3(+x / 2, +y / 2, z / 2));
    vertices.add(new Vec3(-x / 2, +y / 2, z / 2));

    normals.add(new Vec3(0.0f, 0.0f, 1.0f));
    normals.add(new Vec3(0.0f, 0.0f, 1.0f));
    normals.add(new Vec3(0.0f, 0.0f, 1.0f));
    normals.add(new Vec3(0.0f, 0.0f, 1.0f));

    indices.add((short) 0);
    indices.add((short) 1);
    indices.add((short) 2);
    indices.add((short) 2);
    indices.add((short) 3);
    indices.add((short) 0);

    // left face
    vertices.add(new Vec3(-x / 2, -y / 2, -z / 2));
    vertices.add(new Vec3(-x / 2, -y / 2, +z / 2));
    vertices.add(new Vec3(-x / 2, +y / 2, +z / 2));
    vertices.add(new Vec3(-x / 2, +y / 2, -z / 2));

    normals.add(new Vec3(-1.0f, 0.0f, 0.0f));
    normals.add(new Vec3(-1.0f, 0.0f, 0.0f));
    normals.add(new Vec3(-1.0f, 0.0f, 0.0f));
    normals.add(new Vec3(-1.0f, 0.0f, 0.0f));

    indices.add((short) 4);
    indices.add((short) 5);
    indices.add((short) 6);
    indices.add((short) 6);
    indices.add((short) 7);
    indices.add((short) 4);

    // right face
    vertices.add(new Vec3(+x / 2, -y / 2, -z / 2));
    vertices.add(new Vec3(+x / 2, -y / 2, +z / 2));
    vertices.add(new Vec3(+x / 2, +y / 2, +z / 2));
    vertices.add(new Vec3(+x / 2, +y / 2, -z / 2));

    normals.add(new Vec3(1.0f, 0.0f, 0.0f));
    normals.add(new Vec3(1.0f, 0.0f, 0.0f));
    normals.add(new Vec3(1.0f, 0.0f, 0.0f));
    normals.add(new Vec3(1.0f, 0.0f, 0.0f));

    indices.add((short) 10);
    indices.add((short) 9);
    indices.add((short) 8);
    indices.add((short) 8);
    indices.add((short) 11);
    indices.add((short) 10);

    // back face
    vertices.add(new Vec3(-x / 2, -y / 2, -z / 2));
    vertices.add(new Vec3(+x / 2, -y / 2, -z / 2));
    vertices.add(new Vec3(+x / 2, +y / 2, -z / 2));
    vertices.add(new Vec3(-x / 2, +y / 2, -z / 2));

    normals.add(new Vec3(0.0f, 0.0f, -1.0f));
    normals.add(new Vec3(0.0f, 0.0f, -1.0f));
    normals.add(new Vec3(0.0f, 0.0f, -1.0f));
    normals.add(new Vec3(0.0f, 0.0f, -1.0f));

    indices.add((short) 14);
    indices.add((short) 13);
    indices.add((short) 12);
    indices.add((short) 15);
    indices.add((short) 14);
    indices.add((short) 12);

    //top face
    vertices.add(new Vec3(-x / 2, +y / 2, +z / 2));
    vertices.add(new Vec3(+x / 2, +y / 2, +z / 2));
    vertices.add(new Vec3(+x / 2, +y / 2, -z / 2));
    vertices.add(new Vec3(-x / 2, +y / 2, -z / 2));

    normals.add(new Vec3(0.0f, 1.0f, 0.0f));
    normals.add(new Vec3(0.0f, 1.0f, 0.0f));
    normals.add(new Vec3(0.0f, 1.0f, 0.0f));
    normals.add(new Vec3(0.0f, 1.0f, 0.0f));

    indices.add((short) 16);
    indices.add((short) 17);
    indices.add((short) 18);
    indices.add((short) 16);
    indices.add((short) 18);
    indices.add((short) 19);


    //bottom face
    vertices.add(new Vec3(-x / 2, -y / 2, +z / 2));
    vertices.add(new Vec3(+x / 2, -y / 2, +z / 2));
    vertices.add(new Vec3(+x / 2, -y / 2, -z / 2));
    vertices.add(new Vec3(-x / 2, -y / 2, -z / 2));

    normals.add(new Vec3(0.0f, -1.0f, 0.0f));
    normals.add(new Vec3(0.0f, -1.0f, 0.0f));
    normals.add(new Vec3(0.0f, -1.0f, 0.0f));
    normals.add(new Vec3(0.0f, -1.0f, 0.0f));

    indices.add((short) 20);
    indices.add((short) 22);
    indices.add((short) 21);
    indices.add((short) 20);
    indices.add((short) 23);
    indices.add((short) 22);
  }
}
