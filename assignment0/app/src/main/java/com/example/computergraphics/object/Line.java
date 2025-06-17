package com.example.computergraphics.object;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.example.computergraphics.utils.MaterialFileHandle;
import com.example.computergraphics.utils.MatrixUtils;

public class Line extends GraphicObject {
    static float []  defaultSource = {
            -2.0f, 0.0f, -2.0f
    };
    static float [] defaultDirection = {
            0.1f, 0.0f, 0.1f
    };
    static int defaultMaxLength = 40;
    public final int typ = 1;
    public float [] source;
    public float [] direction;
    public float length;
    public float [] color = {0f, 0f, 1f, 1f};
    public Line(float [] Source, float [] Direction, float Length, Context context){
        this(Source,
            MatrixUtils.add(Source,
                MatrixUtils.mul(Direction, Length > 0f ? Length : defaultMaxLength)),
            context);
    }
    public Line(Context context){
        this(defaultSource, defaultDirection, defaultMaxLength, context);
    }
    public Line(float [] start, float [] end, Context context){
        super(
            new float[] {start[0], start[1], start[2], end[0], end[1], end[2]},
            null,
            null,
            GLES20.glCreateProgram(),
            context
        );
        source = start;
        float [] ray = MatrixUtils.sub(end, start);
        length = MatrixUtils.norm(ray);
        direction = new float[] {ray[0] / length, ray[1] / length, ray[2] / length};
    }
    public void draw(float [] vMatrix, float[] pMatrix, float[] rotationMatrix, float [] eye) {
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
        int colorHandle = GLES20.glGetUniformLocation(program, "uColor");
        int viewPositionHandle = GLES20.glGetUniformLocation(program, "uViewPosition");

        int positionHandle = GLES20.glGetAttribLocation(program, "aPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        modelMatrix = this.getModelMatrix();
        Matrix.multiplyMM(modelMatrix, 0, rotationMatrix, 0, modelMatrix, 0);
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

        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexData.length / COORDS_PER_VERTEX);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}

