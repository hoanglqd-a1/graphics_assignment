package com.example.computergraphics.utils;


import android.content.Context;
import android.opengl.Matrix;

import java.io.*;

public class Utility {
    public static String readRawTextFile(Context context, int resId) {
        InputStream inputStream = context.getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
    public static float[] getModelMatrix(float[] translation, float[] rotation, float[] scale){
        float[] modelMatrix = new float[16];
        // Reset the model matrix to identity
        Matrix.setIdentityM(modelMatrix, 0);
        // Apply translation
        Matrix.translateM(modelMatrix, 0, translation[0], translation[1], translation[2]);
        // Apply rotation
        Matrix.rotateM(modelMatrix, 0, rotation[0], 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, rotation[1], 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, rotation[2], 0.0f, 0.0f, 1.0f);
        // Apply scaling
        Matrix.scaleM(modelMatrix, 0, scale[0], scale[1], scale[2]);
        return modelMatrix;
    }
}
