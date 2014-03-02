package com.hulzenga.ioi_apps.util.open_gl;

import com.hulzenga.ioi_apps.util.vector.Vec3;
import com.hulzenga.ioi_apps.util.vector.Vec4;

public class ColorFunctionFactory {

  private ColorFunctionFactory() {}

  public static ColorFunction createUniform(float r, float g, float b, float a) {
    final Vec4<Float> uniformColor = new Vec4<Float>(r, g, b, a);
    return new ColorFunction() {
      @Override
      public Vec4<Float> apply(Vec3<Float> position, Vec3<Float> normal) {
        return uniformColor;
      }
    };
  }

  public static ColorFunction createRandom() {
    return new ColorFunction() {
      @Override
      public Vec4<Float> apply(Vec3<Float> position, Vec3<Float> normal) {
        return new Vec4<Float>((float) Math.random(), (float) Math.random(), (float) Math.random(), 1.0f);
      }
    };
  }
}
