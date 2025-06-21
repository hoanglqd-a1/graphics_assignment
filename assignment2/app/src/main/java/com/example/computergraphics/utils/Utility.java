package com.example.computergraphics.utils;

import com.example.computergraphics.shape.Line;
import com.example.computergraphics.shape.Shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.util.Random;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

public class Utility {
    public static float[] getModelMatrix(float[] translation, float[] rotation, float[] scale) {
        float[] scaleMatrix = new float[16];
        float[] rotateMatrix = new float[16];
        float[] tempMatrix = new float[16];
        float[] modelMatrix = new float[16];

        // Start with identity matrices
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.setIdentityM(rotateMatrix, 0);
        Matrix.setIdentityM(modelMatrix, 0);

        // Scale
        Matrix.scaleM(scaleMatrix, 0, scale[0], scale[1], scale[2]);

        // Rotation
        Matrix.setIdentityM(rotateMatrix, 0);
        Matrix.rotateM(rotateMatrix, 0, rotation[0], 1.0f, 0.0f, 0.0f); // X
        Matrix.rotateM(rotateMatrix, 0, rotation[1], 0.0f, 1.0f, 0.0f); // Y
        Matrix.rotateM(rotateMatrix, 0, rotation[2], 0.0f, 0.0f, 1.0f); // Z

        // modelMatrix = rotate * scale
        Matrix.multiplyMM(tempMatrix, 0, rotateMatrix, 0, scaleMatrix, 0);

        // Translation
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, translation[0], translation[1], translation[2]);

        // modelMatrix = translate * (rotate * scale)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, tempMatrix, 0);

        return modelMatrix;
    }

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
    public static MyComposite getIntersection(Shape s, Line l, Context context){
        List<float[]> intersects = new ArrayList<>();
        List<Line> lines = new ArrayList<>();
        for (int i=0; i<s.vertexData.length; i+= 3 * Shape.COORDS_PER_VERTEX){
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
                float [] p = MyMatrixLib.add(l.source, MyMatrixLib.mul(l.direction, t));
                float [] v0v1 = MyMatrixLib.sub(v1, v0);
                float [] v1v2 = MyMatrixLib.sub(v2, v1);
                float [] v2v0 = MyMatrixLib.sub(v0, v2);
                float [] v0p = MyMatrixLib.sub(p, v0);
                float [] v1p = MyMatrixLib.sub(p, v1);
                float [] v2p = MyMatrixLib.sub(p, v2);
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
    public static float [] getIntersectionLine(Line u, Line v){
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
