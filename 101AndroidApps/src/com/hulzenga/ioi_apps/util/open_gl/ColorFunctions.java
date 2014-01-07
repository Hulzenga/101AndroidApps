package com.hulzenga.ioi_apps.util.open_gl;

import com.hulzenga.ioi_apps.util.vector.Vec3;
import com.hulzenga.ioi_apps.util.vector.Vec4;

public class ColorFunctions {

    public interface ColorFunction {

        public Vec4<Float> colorMap(Vec3<Float> position, Vec3<Float> normal);
    }
    
    public class Uniform implements ColorFunction {

        Vec4<Float> uniformColor;
        
        public Uniform(Vec4<Float> uniformColor) {
            this.uniformColor = uniformColor;
        }
        
        @Override
        public Vec4<Float> colorMap(Vec3<Float> position, Vec3<Float> normal) {
            return uniformColor;
        }        
    }    
}
