package com.hulzenga.ioi_apps.util.open_gl;

import java.util.ArrayList;
import java.util.List;

import com.hulzenga.ioi_apps.util.vector.Vec3;
import com.hulzenga.ioi_apps.util.vector.Vec4;

public class ShapeFactory {

    // cannot be instantiated
    private ShapeFactory() {
    }

    public static RenderObject sphere(float radius, int segments, int slices) {

        final int numberOfVertices = (slices * segments + 2);

        List<Vec3<Float>> vertices = new ArrayList<Vec3<Float>>();
        List<Vec4<Float>> colors = new ArrayList<Vec4<Float>>();
        List<Vec3<Float>> normals = new ArrayList<Vec3<Float>>();
        List<Short> indices = new ArrayList<Short>();

        /*
         * calculate vertex locations
         */

        // add top vertex
        vertices.add(new Vec3<Float>(0.0f, radius, 0.0f));

        for (int i = 0; i < slices; i++) {

            final float rScale = (float) Math.sin((i + 1) * Math.PI / (slices + 1));
            final float segmentHeight = (float) Math.cos((i + 1) * Math.PI / (slices + 1));

            for (int j = 0; j < segments; j++) {
                vertices.add(new Vec3<Float>(
                        rScale * radius * ((float) Math.sin(j * 2.0 * Math.PI / segments)),
                        segmentHeight,
                        rScale * radius * ((float) Math.cos(j * 2.0 * Math.PI / segments))
                        ));
            }
        }

        // add bottom vertex
        vertices.add(new Vec3<Float>(0.0f, -radius, 0.0f));

        /*
         * assign color
         */
        Vec4<Float> uniform = new Vec4<Float>(1.0f, 0.5f, 0.5f, 1.0f);
        for (int i = 0; i < numberOfVertices; i++) {
            colors.add(uniform);
        }

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

        RenderObject sphere = new RenderObject(vertices, colors, normals, null, indices);

        return sphere;
    }
    
    public static RenderObject box(float x, float y, float z) {
        List<Vec3<Float>> vertices = new ArrayList<Vec3<Float>>();
        List<Vec4<Float>> colors = new ArrayList<Vec4<Float>>();
        List<Vec3<Float>> normals = new ArrayList<Vec3<Float>>();
        List<Short> indices = new ArrayList<Short>();
        
        return new RenderObject(vertices, colors, normals, null, indices);

    }
}
