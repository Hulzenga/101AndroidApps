package com.hulzenga.ioi_apps.util.open_gl;


public class ShapeFactory {

    // cannot be instantiated
    private ShapeFactory() {
    }

    public static RenderObject sphere(float radius, int segments, int slices) {


        final int fpv = 3; //floats per vertex
        

        float[] vert = new float[fpv*(segments+1)];

        vert[0] = 0.0f;
        vert[1] = radius;
        vert[2] = 0.0f;
        
        for (int i = 0; i < segments; i++) {            
            vert[fpv*(i+1)+0] = radius * ((float) Math.sin(i * 2.0 * Math.PI / ((double) segments)));
            vert[fpv*(i+1)+1] = 0.0f;
            vert[fpv*(i+1)+2] = radius * ((float) Math.cos(i * 2.0 * Math.PI / ((double) segments)));
        }
        
        /*
        for (int i = 0; i < segments; i++) {
            vert[i * floatsPerSegment + 0] = radius * ((float) Math.sin(i * 2.0 * Math.PI / ((double) segments)));
            vert[i * floatsPerSegment + 1] = 0.0f;
            vert[i * floatsPerSegment + 2] = radius * ((float) Math.cos(i * 2.0 * Math.PI / ((double) segments)));

            vert[i * floatsPerSegment + 3] = radius * ((float) Math.sin((i + 1) * 2.0 * Math.PI / ((double) segments)));
            vert[i * floatsPerSegment + 4] = 0.0f;
            vert[i * floatsPerSegment + 5] = radius * ((float) Math.cos((i + 1) * 2.0 * Math.PI / ((double) segments)));

            vert[i * floatsPerSegment + 6] = radius * ((float) Math.sin(i * 2.0 * Math.PI / ((double) segments)));
            vert[i * floatsPerSegment + 7] = 0.5f;
            vert[i * floatsPerSegment + 8] = radius * ((float) Math.cos(i * 2.0 * Math.PI / ((double) segments)));
            
            
            vert[i * floatsPerSegment + 9] = radius * ((float) Math.sin((i + 1) * 2.0 * Math.PI / ((double) segments)));
            vert[i * floatsPerSegment +10] = 0.0f;
            vert[i * floatsPerSegment +11] = radius * ((float) Math.cos((i + 1) * 2.0 * Math.PI / ((double) segments)));

            vert[i * floatsPerSegment +12] = radius * ((float) Math.sin((i + 1) * 2.0 * Math.PI / ((double) segments)));
            vert[i * floatsPerSegment +13] = 0.5f;
            vert[i * floatsPerSegment +14] = radius * ((float) Math.cos((i + 1) * 2.0 * Math.PI / ((double) segments)));

            vert[i * floatsPerSegment +15] = radius * ((float) Math.sin(i * 2.0 * Math.PI / ((double) segments)));
            vert[i * floatsPerSegment +16] = 0.5f;
            vert[i * floatsPerSegment +17] = radius * ((float) Math.cos(i * 2.0 * Math.PI / ((double) segments)));
        }*/
        
        short[] indices = new short[segments*3];
        
        for(int i = 0; i < segments-1; i++) {
            indices[i*3+0] = (short) i;
            indices[i*3+1] = (short) (i+1);
            indices[i*3+2] = (short) 0;
        }
        
        indices[(segments-1)*3+0] = (short) segments;
        indices[(segments-1)*3+1] = (short) 1;
        indices[(segments-1)*3+2] = (short) 0;
                
        
        RenderObject sphere = new RenderObject(vert, null, null, null, indices);
        
        return sphere;
    }
}
