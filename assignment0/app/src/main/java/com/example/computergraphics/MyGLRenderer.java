package com.example.computergraphics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.content.res.AssetManager;
import android.content.Context;
import android.util.Log;

import com.example.computergraphics.object.*;
import com.example.computergraphics.utils.MatrixUtils;
import com.example.computergraphics.utils.Utility;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private Context context;
    private float [] userInputLineSource;
    private float [] userInputLineDirection;
    private GraphicObject object;
    private Cube cube;
    private GraphicObject model;
    private Line line;
    private Cube [] cubes;
    private Line [] lines;
    private GroupedGraphicObjects groupedObjects;
    private float [] vpMatrix = new float[16];
    private float [] projectionMatrix = new float[16];
    private float [] viewMatrix = new float [16];
    private float [] rotationMatrix = new float[16];
    private float [] lightModelMatrix = new float[16];
    public float [] eye = { 0.0f, 2.0f, 3.0f };
    MyGLRenderer(float [] source, float [] direction, Context context) {
        // Constructor
        this.context = context;
        this.userInputLineSource = source;
        this.userInputLineDirection = direction;
    }
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
		GLES30.glEnable(GLES30.GL_CULL_FACE);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glLineWidth(10.0f);

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
        Random random = new Random();
        cubes = new Cube[10];
        lines = new Line[100];
        for(int i=0; i<cubes.length; i++){
            cubes[i] = new Cube(context);
            cubes[i].setTranslation(
                new float[] {
                    2 * random.nextFloat() - 1f,
                    2 * random.nextFloat() - 1f,
                    2 * random.nextFloat() - 1f,
                }
            );
        }
        for (int i=0; i<lines.length; i++){
            float[] source = new float[] {
                2 * random.nextFloat() - 1f,
                2 * random.nextFloat() - 1f,
                2 * random.nextFloat() - 1f,
            };
            float[] direction = new float[] {
                2 * random.nextFloat() - 1f,
                2 * random.nextFloat() - 1f,
                2 * random.nextFloat() - 1f,
            };
            lines[i] = new Line(source, direction, -1, context);
        }
        int copies = 100;
        float[] instancedModelMatrices = new float[copies * 16];
        for(int i=0; i<copies; i++){
            float[] transalation = MatrixUtils.add(
                MatrixUtils.mul(
                    MatrixUtils.generateRandomVector(3),
                    2
                ), new float[] {-1f, -1f, -1f}
            );
            float[] scale = new float[] {0.5f, 0.5f, 0.5f};
            float[] rotation = MatrixUtils.generateRandomVector(3);
            float[] modelMatrix = Utility.getModelMatrix(transalation, rotation, scale);
            System.arraycopy(modelMatrix, 0, instancedModelMatrices, i * 16, 16);
        }
        groupedObjects = new GroupedGraphicObjects(context, copies, instancedModelMatrices);
    }
    private long lastTime = System.nanoTime();
    private int frames;
    public void onDrawFrame(GL10 unused) {
        frames ++;
        long currentTime = System.nanoTime();
        if (currentTime - lastTime > 1_000_000_000L){
            lastTime = currentTime;
            Log.d("FPS", "FPS " + frames);
            frames = 0;
        }

        // Redraw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
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
//        for(Cube c : cubes){
//            c.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//        }
//        for(Line l : lines){
//            l.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//        }
//        for(int i=0; i<cubes.length; i++){
//            for(int j=0; j<lines.length; j++){
//                List<float[][]> intersectionList = cubes[i].getIntersectionsWithLine(lines[j]);
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
        groupedObjects.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
    }
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
    }
    public static int loadShader(int type, String shaderCode){
        int shader = GLES30.glCreateShader(type);
        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

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