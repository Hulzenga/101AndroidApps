package com.hulzenga.ioi_apps.util.open_gl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

public class ShaderTool {
    private static final String TAG                   = "SHADER_LOADER";

    private static final String SHADER_BASE_DIRECTORY = "shaders/";

    // ShaderTool cannot be instantiated
    private ShaderTool() {
    }

    public static String readShader(Context context, String file) {
        String shader = "";

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open(
                    SHADER_BASE_DIRECTORY + file));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {

                sb.append(line + "\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            inputStreamReader.close();
            shader = sb.toString();
        } catch (IOException e) {
            Log.e(TAG, "IOException: failed to read " + file);
        }

        return shader;
    }

    //TODO: implement some more sophisticated exception handling
    public static boolean compileShader(int shaderHandle, String shader) {

        if (shaderHandle != 0) {
            // Pass in the shader source.
            GLES20.glShaderSource(shaderHandle, shader);

            // Compile the shader.
            GLES20.glCompileShader(shaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(shaderHandle);
                return false;
            }

            // compilation successful return true
            return true;
        }
        return false;
    }
}
