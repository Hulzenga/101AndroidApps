package com.hulzenga.ioi_apps.util.open_gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ShapeFactory {

    public static final int BYTES_PER_FLOAT = 4;

    // cannot be instantiated
    private ShapeFactory() {
    }

    public static FloatBuffer sphere(float radius, int segments, int slices) {

        final int trianglesPerSegment = 2;
        final int verticesPerTriangle = 3;
        final int floatsPerVertex = 3;
        final int floatsPerSegment = floatsPerVertex * verticesPerTriangle * trianglesPerSegment;
        final int floatsInBuffer = floatsPerSegment * segments;

        float[] tr = new float[floatsInBuffer];

        for (int i = 0; i < segments; i++) {
            tr[i * floatsPerSegment + 0] = radius * ((float) Math.sin(i * 2.0 * Math.PI / ((double) segments)));
            tr[i * floatsPerSegment + 1] = 0.0f;
            tr[i * floatsPerSegment + 2] = radius * ((float) Math.cos(i * 2.0 * Math.PI / ((double) segments)));

            tr[i * floatsPerSegment + 3] = radius * ((float) Math.sin((i + 1) * 2.0 * Math.PI / ((double) segments)));
            tr[i * floatsPerSegment + 4] = 0.0f;
            tr[i * floatsPerSegment + 5] = radius * ((float) Math.cos((i + 1) * 2.0 * Math.PI / ((double) segments)));

            tr[i * floatsPerSegment + 6] = radius * ((float) Math.sin(i * 2.0 * Math.PI / ((double) segments)));
            tr[i * floatsPerSegment + 7] = 0.5f;
            tr[i * floatsPerSegment + 8] = radius * ((float) Math.cos(i * 2.0 * Math.PI / ((double) segments)));
            
            
            tr[i * floatsPerSegment + 9] = radius * ((float) Math.sin((i + 1) * 2.0 * Math.PI / ((double) segments)));
            tr[i * floatsPerSegment +10] = 0.0f;
            tr[i * floatsPerSegment +11] = radius * ((float) Math.cos((i + 1) * 2.0 * Math.PI / ((double) segments)));

            tr[i * floatsPerSegment +12] = radius * ((float) Math.sin((i + 1) * 2.0 * Math.PI / ((double) segments)));
            tr[i * floatsPerSegment +13] = 0.5f;
            tr[i * floatsPerSegment +14] = radius * ((float) Math.cos((i + 1) * 2.0 * Math.PI / ((double) segments)));

            tr[i * floatsPerSegment +15] = radius * ((float) Math.sin(i * 2.0 * Math.PI / ((double) segments)));
            tr[i * floatsPerSegment +16] = 0.5f;
            tr[i * floatsPerSegment +17] = radius * ((float) Math.cos(i * 2.0 * Math.PI / ((double) segments)));
        }
        
        FloatBuffer sphereBuffer = ByteBuffer.allocateDirect(floatsInBuffer * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        
        sphereBuffer.put(tr);
        
        return sphereBuffer;
    }
}
