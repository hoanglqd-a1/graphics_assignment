package com.example.computergraphics;

import java.lang.Math;
public class MyMatrixLib {
    static float MM(float [] v1, float [] v2){
        float ans = 0.0f;
        for (int i=0; i<v1.length; i++){
            ans += v1[i] * v2[i];
        }
        return ans;
    }
    static float [] add(float [] v1, float [] v2){
        float [] ans = new float[v1.length];
        for (int i=0; i<v1.length; i++){
            ans[i] = v1[i] + v2[i];
        }
        return ans;
    }
    static float [] sub(float [] v1, float [] v2){
        float [] ans = new float[v1.length];
        for (int i=0; i<v1.length; i++){
            ans[i] = v1[i] - v2[i];
        }
        return ans;
    }
    static float [] mul(float [] v, float scalar) {
        float a [] = new float[v.length];
        for (int i=0; i<v.length; i++){
            a[i] = v[i] * scalar;
        }
        return a;
    }
    static float [] crossProduct (float [] v1, float [] v2){
        return new float [] {v1[1]*v2[2] - v1[2]*v2[1], v1[2]*v2[0] - v1[0]*v2[2], v1[0]*v2[1] - v1[1]*v2[0]};
    }
    static float[] normalize(float[] v) {
        float mag = (float)Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]);
        return new float[]{ v[0]/mag, v[1]/mag, v[2]/mag };
    }
}
