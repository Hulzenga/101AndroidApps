package com.hulzenga.ioi_apps.util.open_gl;

import com.hulzenga.ioi_apps.util.vector.Vec3;
import com.hulzenga.ioi_apps.util.vector.Vec4;

public interface ColorFunction {

  public Vec4<Float> apply(Vec3<Float> position, Vec3<Float> normal);
}
