package com.example.computergraphics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import java.io.*;
import java.util.List;
import java.util.Random;

import android.content.res.AssetManager;
import android.content.Context;
import com.example.computergraphics.object.*;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private Context context;
    private float [] userInputLineSource;
    private float [] userInputLineDirection;
    private GraphicObject object;
    private Cube cube;
    private GraphicObject model;
    private Line line;
    private float [] vpMatrix = new float[16];
    private float [] projectionMatrix = new float[16];
    private float [] viewMatrix = new float [16];
    private float [] rotationMatrix = new float[16];
    private float [] lightModelMatrix = new float[16];
    public float [] eye = { 0.0f, 2.0f, 5.0f };
    MyGLRenderer(float [] source, float [] direction, Context context) {
        // Constructor
        this.context = context;
        this.userInputLineSource = source;
        this.userInputLineDirection = direction;
    }
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glLineWidth(10.0f);

        object = new GraphicObject(this.context);
        cube = new Cube(context);
        line = new Line(userInputLineSource, userInputLineDirection, -1, context);
        try {
            AssetManager assetManager = this.context.getAssets();
            InputStream objInputStream = assetManager.open("capsule.obj");
            model = new GraphicObject(objInputStream, this.context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(viewMatrix, 0, eye[0], eye[1], eye[2], 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.setRotateM(rotationMatrix, 0, mAngle, 0f, 1.0f, 0.0f);

//        object.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//        cube.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//        model.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//        line.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//        List<float[][]> intersectionsList = cube.getIntersectionsWithLine(line);
//        for (float [][] intersections : intersectionsList){
//            if(intersections.length == 1){
//                Point p = new Point(intersections[0], context);
//                p.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//            }
//            else if(intersections.length == 2){
//                Line l = new Line(intersections[0], intersections[1], context);
//                l.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//            }
//        }
//        Random random = new Random();
//        Cube [] cubes = new Cube[10];
//        Line [] lines = new Line[10];
//        for(int i=0; i<cubes.length; i++){
//            cubes[i] = new Cube(context);
////            cubes[i].setTranslation(
////                new float[] {random.nextFloat(), random.nextFloat(), random.nextFloat()}
////            );
//            cubes[i].draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//        }
//        for (int i=0; i<lines.length; i++){
//            float[] source = new float[] {random.nextFloat(), random.nextFloat(), random.nextFloat()};
//            float[] direction = new float[] {random.nextFloat(), random.nextFloat(), random.nextFloat()};
//            lines[i] = new Line(source, direction, -1, context);
//            lines[i].draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//        }
//        for(int i=0; i<cubes.length; i++){
//            for(int j=0; j<lines.length; j++){
//                List<float[][]> intersectionList = cubes[i].getIntersectionsWithLine(lines[i]);
//                for(float[][] intersections : intersectionList){
//                    if(intersections.length == 1){
//                        Point p = new Point(intersections[0], context);
//                        p.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//                    }
//                    else if(intersections.length == 2){
//                        Line l = new Line(intersections[0], intersections[1], context);
//                        l.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//                    }
//                }
//            }
//        }
    }
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
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