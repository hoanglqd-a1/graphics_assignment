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
        
        GLES20.glLineWidth(5.0f);

        triangle = new Shape(this.context);
        cube = new Cube(this.context);
        line = new Line(source, direction, 40, context);
        try {
            AssetManager assetManager = this.context.getAssets();
            InputStream objInputStream = assetManager.open("capsule.obj");
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
        cube.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
//        model.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
        line.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
        MyComposite com = this.getIntersection(cube, line);
        float [][] intersects = com.intersections;
        for (int i=0; i<intersects.length; i++){
            Point p = new Point(new float [] {
                intersects[i][0],
                intersects[i][1],
                intersects[i][2],
            }, context);
            p.draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
        }
        Line [] lines = com.lines;
        for (int i=0; i<lines.length; i++){
            lines[i].color = new float[] {1f, 1f, 1f, 1f} ;
            lines[i].draw(viewMatrix, projectionMatrix, rotationMatrix, eye);
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
    public MyComposite getIntersection(Shape s, Line l){
        List<float[]> intersects = new ArrayList<>();
        List<Line> lines = new ArrayList<>();
        for (int i=0; i<s.vertexData.length; i+= 3 * s.COORDS_PER_VERTEX){
            float [] normal = {s.normalData[i], s.normalData[i+1], s.normalData[i+2]};
            float [] v0 = {s.vertexData[i  ], s.vertexData[i+1], s.vertexData[i+2]};
            float [] v1 = {s.vertexData[i+3], s.vertexData[i+4], s.vertexData[i+5]};
            float [] v2 = {s.vertexData[i+6], s.vertexData[i+7], s.vertexData[i+8]};
            // Log.d("tag", "v "   + v0[0] + " " + v0[1] + " " + v0[2] + " "
            //                     + v1[0] + " " + v1[1] + " " + v1[2] + " "
            //                     + v2[0] + " " + v2[1] + " " + v2[2] + " ");
            float D = - MyMatrixLib.MM(normal, v0);
            float t = 0;
            float NR = MyMatrixLib.MM(normal, l.direction);
            if (Math.abs(NR) < 1e-6){
                Line l0 = new Line(v1, v2, null, context);
                Line l1 = new Line(v2, v0, null, context);
                Line l2 = new Line(v0, v1, null, context);
                float [] p0 = getIntersectionLine(l, l0);
                float [] p1 = getIntersectionLine(l, l1);
                float [] p2 = getIntersectionLine(l, l2);
                List<float[]> points = new ArrayList<>();
                if (p0 != null){
                    Log.d("Tag", "P0 " + p0[0] + " " + p0[1] + " " + p0[2]);
                }
                if (p1 != null){
                    Log.d("Tag", "P0 " + p1[0] + " " + p1[1] + " " + p1[2]);
                }
                if (p2 != null){
                    Log.d("Tag", "P2 " + p2[0] + " " + p2[1] + " " + p2[2]);
                }
                if (p0 != null && (p0[0] - v1[0]) * (p0[0] - v2[0]) - 1e-6 < 0){
                    points.add(p0);
                }
                if (p1 != null && (p1[0] - v2[0]) * (p1[0] - v0[0]) - 1e-6 < 0){
                    points.add(p1);
                }
                if (p2 != null && (p2[0] - v0[0]) * (p2[0] - v1[0]) - 1e-6 < 0){
                    points.add(p2);
                }
                if (points.size() < 2) continue;
                float [] h1, h2;
                int idx1 = 0;
                int idx2 = 0;
                for (int j=0; j<points.size(); j++){
                    if (points.get(j)[0] > points.get(idx2)[0]){
                        idx2 = j;
                    }
                    if (points.get(j)[0] < points.get(idx1)[0]){
                        idx1 = j;
                    }
                }
                lines.add(new Line(points.get(idx1), points.get(idx2), null, context));
            }
            else {
                t = - (MyMatrixLib.MM(normal, l.source) + D) / NR;
                if (t < 0){
                    continue;
                }
                float p [] = MyMatrixLib.add(l.source, MyMatrixLib.mul(l.direction, t));
                float v0v1 [] = MyMatrixLib.sub(v1, v0);
                float v1v2 [] = MyMatrixLib.sub(v2, v1);
                float v2v0 [] = MyMatrixLib.sub(v0, v2);
                float v0p [] = MyMatrixLib.sub(p, v0);
                float v1p [] = MyMatrixLib.sub(p, v1);
                float v2p [] = MyMatrixLib.sub(p, v2);
                float n1 = MyMatrixLib.MM(normal, MyMatrixLib.crossProduct(v0v1, v0p));
                float n2 = MyMatrixLib.MM(normal, MyMatrixLib.crossProduct(v1v2, v1p));
                float n3 = MyMatrixLib.MM(normal, MyMatrixLib.crossProduct(v2v0, v2p));
                if (n1 * n2 >= 0 && n1 * n3 >= 0){
                    intersects.add(new float [] {p[0], p[1], p[2], normal[0], normal[1], normal[2]});
                }
            }
        }
        return new MyComposite(
            intersects.toArray(new float[0][]),
            lines.toArray(new Line[0]));
    }
    public float [] getIntersectionLine(Line u, Line v){
        float [] s1 = u.source;
        float [] s2 = v.source;
        float [] d1 = u.direction;
        float [] d2 = v.direction;
        float [] r = MyMatrixLib.sub(s1, s2);
        float a = MyMatrixLib.MM(d1, d1);
        float b = MyMatrixLib.MM(d1, d2);
        float c = MyMatrixLib.MM(d2, d2);
        float d = MyMatrixLib.MM(d1, r);
        float e = MyMatrixLib.MM(d2, r);
        float denom = a*c - b*b;
        if (Math.abs(denom) < 1e-6){
            return null;
        }
        float t = (b*e - c*d)/denom;
        float s = (a*e - b*d)/denom;

        float [] p1 = new float [] {
            s1[0] + t*d1[0], s1[1] + t*d1[1], s1[2] + t*d1[2],
        };
        float [] p2 = new float [] {
            s2[0] + s*d2[0], s2[1] + s*d2[1], s2[2] + s*d2[2],
        };
        float [] dis = MyMatrixLib.sub(p1, p2);
        if (dis[0] * dis[0] + dis[1] * dis[1] + dis[2] * dis[2] > 1e-6){
            return null;
        }
        return p1;
    }
}

