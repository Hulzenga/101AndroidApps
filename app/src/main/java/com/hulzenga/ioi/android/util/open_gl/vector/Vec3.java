package com.hulzenga.ioi.android.util.open_gl.vector;

public class Vec3 {

  public float x;
  public float y;
  public float z;

  public Vec3(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Vec3 copy() {
    return new Vec3(x, y, z);
  }

  public Vec3 add(Vec3 operand) {
    x += operand.x;
    y += operand.y;
    z += operand.z;

    return this;
  }

  public Vec3 subtract(Vec3 operand) {

    x -= operand.x;
    y -= operand.y;
    z -= operand.z;

    return this;
  }

  public Vec3 cross(Vec3 operand) {
    float nx = y * operand.z - operand.y * z;
    float ny = z * operand.x - operand.z * x;
    z = x * operand.y - operand.x * y;
    y = ny;
    x = nx;
    return this;
  }

  public Vec3 normalize() {
    float norm = (float) Math.sqrt(x * x + y * y + z * z);

    x /= norm;
    y /= norm;
    z /= norm;

    return this;
  }

  public Vec3 invert() {
    x = -x;
    y = -y;
    z = -z;

    return this;
  }
}
