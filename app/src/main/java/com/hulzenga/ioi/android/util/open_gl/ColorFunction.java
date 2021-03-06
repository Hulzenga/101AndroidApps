package com.hulzenga.ioi.android.util.open_gl;

import com.hulzenga.ioi.android.util.open_gl.vector.Vec3;
import com.hulzenga.ioi.android.util.open_gl.vector.Vec4;

public interface ColorFunction {

  public Vec4 apply(Vec3 position, Vec3 normal);
}
