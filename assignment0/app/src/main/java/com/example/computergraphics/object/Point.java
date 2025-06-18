package com.example.computergraphics.object;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Point extends GraphicObject {
    public final int typ = 0;
    public float [] color = new float [] {1f, 0f, 0f, 1f};
    public Point(float [] coordinate, Context context){
        super(coordinate, null, null, GLES20.glCreateProgram(), context);
    }
    public void draw(float [] vMatrix, float[] pMatrix, float[] worldRotationMatrix, float [] eye) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(program);

        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            // Linking failed
            Log.e("Shader", "Program linking failed.");
            Log.e("Shader", GLES20.glGetProgramInfoLog(program));
        }

        int mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        int mvMatrixHandle = GLES20.glGetUniformLocation(program, "uMVMatrix");
        int useTexture = GLES20.glGetUniformLocation(program, "uUseTexture");
        int useNormal = GLES20.glGetUniformLocation(program, "uUseNormal");
        int textureUniformHandle = GLES20.glGetUniformLocation(program, "uTexture");
        int colorHandle = GLES20.glGetUniformLocation(program, "uColor");
        int lightPositionHandle = GLES20.glGetUniformLocation(program, "uLightPosition");
        int viewPositionHandle = GLES20.glGetUniformLocation(program, "uViewPosition");
        int ambientHandle = GLES20.glGetUniformLocation(program, "Ka");
        int diffuseHandle = GLES20.glGetUniformLocation(program, "Kd");
        int specularHandle = GLES20.glGetUniformLocation(program, "Ks");

        int positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        int normalHandle = GLES20.glGetAttribLocation(program, "aNormal");
        int textureCoordinateHandle = GLES20.glGetAttribLocation(program, "aTextureCoordinate");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        modelMatrix = this.getModelMatrix();
        Matrix.multiplyMM(modelMatrix, 0, worldRotationMatrix, 0, modelMatrix, 0);
        float [] vpMatrix = new float[16];
        Matrix.multiplyMM(vpMatrix, 0, pMatrix, 0, vMatrix, 0);
        float [] mvpMatrix = new float[16];
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        float [] mvMatrix = new float[16];
        Matrix.multiplyMM(mvMatrix, 0, vMatrix, 0, modelMatrix, 0);
        GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, mvMatrix, 0);

        GLES20.glUniform3fv(viewPositionHandle, 1, eye, 0);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexData.length / COORDS_PER_VERTEX);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
