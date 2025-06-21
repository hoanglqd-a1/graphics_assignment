package com.example.computergraphics.shape;

import android.content.Context;

public class Point extends Shape {
    public Point(float [] coordinate, Context context){
        super(context);
        typ = 0;
        color = new float[] {1f, 0f, 0f, 1f};
        vertexData = coordinate;
        createBuffer();
    }
}
