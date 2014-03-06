package com.hulzenga.ioi_apps.util.open_gl;

import com.hulzenga.ioi_apps.util.open_gl.vector.Vec3;
import com.hulzenga.ioi_apps.util.open_gl.vector.Vec4;

public class ColorFunctionFactory {

  private ColorFunctionFactory() {}

  public static ColorFunction createUniform(float r, float g, float b, float a) {
    final Vec4 uniformColor = new Vec4(r, g, b, a);
    return new ColorFunction() {
      @Override
      public Vec4 apply(Vec3 position, Vec3 normal) {
        return uniformColor;
      }
    };
  }

  public static ColorFunction createRandomBound(final float rLower, final float rUpper,
                                                final float gLower, final float gUpper,
                                                final float bLower, final float bUpper) {
    return new ColorFunction() {
      @Override
      public Vec4 apply(Vec3 position, Vec3 normal) {
        return new Vec4(
            (float) (Math.random() * (rUpper - rLower) + rLower),
            (float) (Math.random() * (gUpper - gLower) + gLower),
            (float) (Math.random() * (bUpper - bLower) + bLower),
            1.0f);
      }
    };
  }

  public static ColorFunction createRandom() {
    return new ColorFunction() {
      @Override
      public Vec4 apply(Vec3 position, Vec3 normal) {
        return new Vec4((float) Math.random(), (float) Math.random(), (float) Math.random(), 1.0f);
      }
    };
  }
}
