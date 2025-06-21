package com.example.computergraphics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.computergraphics.shape.*;
import com.example.computergraphics.utils.*;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.util.*;
import java.io.*;
import android.content.res.AssetManager;
import android.content.Context;
import android.util.Log;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private final Context context;

    private Shape triangle;
    private Cube cube;
    private Shape model;
    private Line line;
    private Sphere sphere;

    private float [] vpMatrix = new float[16];
    private float [] projectionMatrix = new float[16];
    private float [] viewMatrix = new float [16];
    private float [] rotationMatrix = new float[16];
    private float [] lightModelMatrix = new float[16];

    public float [] eye = { 0.0f, 0.0f, 4.0f };
    public float [] source;
    public float [] direction;

    public MyGLRenderer(Context context, float [] source, float [] direction) {
        // Constructor
        this.context = context;
        this.source = source;
        this.direction = direction;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glLineWidth(10.0f);

        triangle = new Shape(this.context);
        cube = new Cube(this.context);
        line = new Line(source, direction, 40, context);
        try {
            sphere = new Sphere(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            AssetManager assetManager = this.context.getAssets();
            InputStream objInputStream = assetManager.open("sphere.obj");
            model = new Shape(objInputStream, this.context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(viewMatrix, 0, eye[0], eye[1], eye[2], 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.setRotateM(rotationMatrix, 0, mAngle, 0f, 1.0f, 0.0f);

//        triangle.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//        cube.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//        model.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
        line.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
        sphere.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//        MyComposite com = Utility.getIntersection(model, line, context);
//        float [][] intersects = com.intersections;
//        for (int i=0; i<intersects.length; i++){
//            Point p = new Point(new float [] {
//                intersects[i][0],
//                intersects[i][1],
//                intersects[i][2],
//            }, context);
//            p.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//        }
//        Line [] lines = com.lines;
//        for (int i=0; i<lines.length; i++){
//            lines[i].color = new float[] {1f, 1f, 1f, 1f} ;
//            lines[i].draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//        }
        float [][] intersections = Utility.getIntersectionLineSphere(line, sphere);
        for (float [] intersection : intersections){
            Log.d("Tag", Arrays.toString(intersection));
            Point p = new Point(intersection, context);
            p.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
        }
    }
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 2, 7);
    }
    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
    public volatile float mAngle;
    public float getAngle() {
        return this.mAngle;
    }
    public void setAngle(float angle) {
        this.mAngle = angle;
    }
}

